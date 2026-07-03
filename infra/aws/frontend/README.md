# DeliverAI Frontend — AWS Static Hosting (S3 + CloudFront)

Terraform infrastructure to deploy **only the frontend** (`ui-design/dist`) on AWS. The Spring Boot backend remains deployed outside AWS (Azure Web App) and is not touched from here.

## Architecture

```
User ──HTTPS──> CloudFront ──┬── default (/*)             ──> private S3 (React SPA, via OAC)
                             └── /order*, /order-items*,
                                 /products*, /user*,
                                 /v3/*, /swagger-ui*       ──> external backend (Azure, HTTPS)
```

### Why S3 + CloudFront?

- Vite produces pure static assets (`ui-design/dist`): no SSR and no Node process at runtime, so an S3 bucket is enough as storage.
- CloudFront provides HTTPS, global CDN, caching, invalidation and the SPA fallback (403/404 → `index.html` 200) that React Router needs.
- The bucket is **private**: only CloudFront can read it via Origin Access Control (OAC, SigV4), conditioned on the distribution ARN.
- API proxying through CloudFront: backend paths are routed to the external origin, so the SPA calls its own origin and CORS/mixed-content are avoided.

### Why NOT EC2/ECS/ECR?

- EC2/ECS mean servers/containers running 24/7 to serve static files: unnecessary cost and operations.
- ECR is only an image registry, not hosting; an Nginx image (see `ui-design/Dockerfile`) is for local/demo parity, not the AWS deploy path.

### Region: `us-east-1`

- Customer in Colombia/Bogotá: CloudFront is global and `PriceClass_All` (default) includes South American edge locations.
- The backend is on Azure (Canada Central); `us-east-1` is stable, default/non-opt-in and usually good latency towards North American backends.
- If the backend ever moves to US West, `us-west-2` would be an alternative. If it moved to São Paulo, evaluate `sa-east-1`.

## Resources created

- Private S3 bucket (`<project_name>-<account_id>`), full public access block, ownership enforced, optional versioning.
- CloudFront distribution: S3 origin (OAC) + external backend origin, API behaviors with caching disabled / all methods / query strings and headers forwarded (`AllViewerExceptHostHeader` policy), SPA fallback 403/404 → `/index.html` 200.
- IAM OIDC provider for GitHub Actions (`create_github_oidc_provider=false` if it already exists in the account — there can only be one).
- IAM role for GitHub Actions deploys with trust limited to `tulio3101/DeliverAI` branch `develop` and a minimal policy: `s3:ListBucket`, `s3:PutObject`, `s3:DeleteObject`, `cloudfront:CreateInvalidation`.

> **SPA fallback note:** `custom_error_response` is distribution-wide; a real backend 403/404 is also rewritten to `index.html` 200 for the caller. Documented in `main.tf`.

## Authentication

### Local Terraform

Uses the AWS provider's standard credential chain (env vars, `~/.aws/config`, SSO). Recommended:

```bash
export AWS_PROFILE=deliverai
```

No access keys are ever stored in the repo. Terraform state is **local** (no remote backend): `*.tfstate`, `.terraform/` and `terraform.tfvars` are gitignored — do not commit them.

### GitHub Actions (OIDC, no keys)

The `frontend-deploy.yml` workflow assumes the role via OIDC:

- `permissions: id-token: write, contents: read`
- `aws-actions/configure-aws-credentials` with `role-to-assume: ${{ vars.AWS_ROLE_ARN }}`
- Trust policy limited to `repo:tulio3101/DeliverAI:ref:refs/heads/develop`

## Required GitHub Variables

Configure in Settings → Secrets and variables → Actions → **Variables** (values come from `terraform output`; `init-setup.sh` prints them):

| Variable | Value |
|---|---|
| `AWS_REGION` | output `aws_region` (e.g. `us-east-1`) |
| `AWS_ROLE_ARN` | output `github_actions_deploy_role_arn` |
| `S3_BUCKET` | output `s3_bucket_name` |
| `CLOUDFRONT_DISTRIBUTION_ID` | output `cloudfront_distribution_id` |
| `VITE_API_BASE_URL` | `https://<cloudfront_domain_name>` (API proxied by CloudFront) or the backend's direct URL |

No GitHub Secrets are needed for AWS (OIDC replaces long-lived keys).

## Operations

### First time

```bash
cd infra/aws/frontend
cp terraform.tfvars.example terraform.tfvars   # fill in the real backend_origin_domain
AWS_PROFILE=deliverai ./scripts/init-setup.sh
```

Validates credentials (`aws sts get-caller-identity`), runs `terraform init/plan/apply` and prints the GitHub Variables ready to copy. Then run the first asset deploy: `./scripts/update-frontend.sh` or push to `develop`.

### Update infrastructure

```bash
./scripts/update-infra.sh    # plan + confirmation + apply
```

### Update only the frontend (no Terraform)

```bash
./scripts/update-frontend.sh
```

Build (`VITE_MOCK_DATA=false`, `VITE_API_BASE_URL` from the output or the env), `aws s3 sync ui-design/dist --delete`, CloudFront `/*` invalidation. Same as what the workflow does on every push to `develop`.

### Destroy demo

```bash
./scripts/destroy-demo.sh
```

**Irreversible.** Requires typed confirmation (bucket name or `destroy`), empties the bucket (including versions) and runs `terraform destroy`. Deletes the distribution, bucket, IAM role and (if created by this module) the OIDC provider.

### Manual rollback

There is no automatic rollback pipeline. Options:

1. **Redeploy a previous commit:** `git checkout <good-commit> -- ui-design` (or checkout the commit) and `./scripts/update-frontend.sh`, or re-run the `frontend-deploy` workflow from that commit (`workflow_dispatch`).
2. **Resync a previous build:** if a previous `dist/` is kept (local or Actions artifact), `aws s3 sync <previous-dist> s3://<bucket> --delete` + invalidation.
3. With `enable_bucket_versioning=true`, restoring previous object versions is also possible.

## Related workflows

- `.github/workflows/frontend-ci.yml`: checks (Biome, tsc, build) on PRs to `develop`. No AWS credentials.
- `.github/workflows/frontend-deploy.yml`: push to `develop` / manual → checks → real build → OIDC → S3 sync → CloudFront invalidation.
