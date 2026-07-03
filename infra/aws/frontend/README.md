# DeliverAI Frontend — AWS Static Hosting (S3 + CloudFront)

Infraestructura Terraform para desplegar **solo el frontend** (`ui-design/dist`) en AWS. El backend Spring Boot sigue desplegado fuera de AWS (Azure Web App) y no se toca desde aquí.

## Arquitectura

```
Usuario ──HTTPS──> CloudFront ──┬── default (/*)            ──> S3 privado (React SPA, via OAC)
                                └── /order*, /order-items*,
                                    /products*, /user*,
                                    /v3/*, /swagger-ui*      ──> Backend externo (Azure, HTTPS)
```

### ¿Por qué S3 + CloudFront?

- Vite genera assets estáticos puros (`ui-design/dist`): no hay SSR ni proceso Node en runtime, así que un bucket S3 basta como almacenamiento.
- CloudFront aporta HTTPS, CDN global, cache, invalidación y el fallback SPA (403/404 → `index.html` 200) que React Router necesita.
- El bucket es **privado**: solo CloudFront puede leerlo vía Origin Access Control (OAC, SigV4), condicionado al ARN de la distribución.
- Proxy de API por CloudFront: los paths del backend se enrutan al origin externo, así el SPA llama a su mismo origen y se evitan CORS y mixed-content.

### ¿Por qué NO EC2/ECS/ECR?

- EC2/ECS implican servidores/containers corriendo 24/7 para servir archivos estáticos: costo y operación innecesarios.
- ECR es solo un registry de imágenes, no hosting; una imagen Nginx (ver `ui-design/Dockerfile`) sirve para paridad local/demo, no como path de deploy en AWS.

### Región: `us-east-1`

- Cliente en Colombia/Bogotá: CloudFront es global y `PriceClass_All` (default) incluye edge locations en Sudamérica.
- Backend está en Azure (probablemente USA); `us-east-1` es estable, default/no opt-in y usualmente buena latencia hacia backends en USA.
- Si se confirma backend en US West, `us-west-2` sería alternativa. Si el backend real estuviera en São Paulo, evaluar `sa-east-1` a futuro.

## Recursos creados

- S3 bucket privado (`<project_name>-<account_id>`), public access block completo, ownership enforced, versioning opcional.
- CloudFront distribution: origin S3 (OAC) + origin backend externo, behaviors API con cache deshabilitado / todos los métodos / query strings y headers forwarded (policy `AllViewerExceptHostHeader`), fallback SPA 403/404 → `/index.html` 200.
- IAM OIDC provider para GitHub Actions (`create_github_oidc_provider=false` si ya existe en la cuenta — solo puede haber uno).
- IAM role para deploy desde GitHub Actions con trust limitado a `tulio3101/DeliverAI` rama `develop` y política mínima: `s3:ListBucket`, `s3:PutObject`, `s3:DeleteObject`, `cloudfront:CreateInvalidation`.

> **Nota fallback SPA:** `custom_error_response` es global a la distribución; un 403/404 real del backend también se reescribe a `index.html` 200 para el caller. Documentado en `main.tf`.

## Autenticación

### Terraform local

Usa el credential chain estándar del provider AWS (env vars, `~/.aws/config`, SSO). Recomendado:

```bash
export AWS_PROFILE=deliverai
```

Nunca se guardan access keys en el repo. Estado Terraform es **local** (sin backend remoto): `*.tfstate`, `.terraform/` y `terraform.tfvars` están gitignored — no commitearlos.

### GitHub Actions (OIDC, sin keys)

El workflow `frontend-deploy.yml` asume el role vía OIDC:

- `permissions: id-token: write, contents: read`
- `aws-actions/configure-aws-credentials` con `role-to-assume: ${{ vars.AWS_ROLE_ARN }}`
- Trust policy limitado a `repo:tulio3101/DeliverAI:ref:refs/heads/develop`

## GitHub Variables requeridas

Configurar en Settings → Secrets and variables → Actions → **Variables** (los valores salen de `terraform output`, `init-setup.sh` los imprime):

| Variable | Valor |
|---|---|
| `AWS_REGION` | output `aws_region` (ej. `us-east-1`) |
| `AWS_ROLE_ARN` | output `github_actions_deploy_role_arn` |
| `S3_BUCKET` | output `s3_bucket_name` |
| `CLOUDFRONT_DISTRIBUTION_ID` | output `cloudfront_distribution_id` |
| `VITE_API_BASE_URL` | `https://<cloudfront_domain_name>` (API proxyada por CloudFront) o URL directa del backend |

No se necesitan GitHub Secrets para AWS (OIDC reemplaza keys long-lived).

## Operación

### Primera vez

```bash
cd infra/aws/frontend
cp terraform.tfvars.example terraform.tfvars   # llenar backend_origin_domain real
AWS_PROFILE=deliverai ./scripts/init-setup.sh
```

Valida credenciales (`aws sts get-caller-identity`), corre `terraform init/plan/apply` e imprime las GitHub Variables listas para copiar. Luego el primer deploy de assets: `./scripts/update-frontend.sh` o push a `develop`.

### Actualizar infraestructura

```bash
./scripts/update-infra.sh    # plan + confirmación + apply
```

### Actualizar solo el frontend (sin Terraform)

```bash
./scripts/update-frontend.sh
```

Build (`VITE_MOCK_DATA=false`, `VITE_API_BASE_URL` del output o del env), `aws s3 sync ui-design/dist --delete`, invalidación CloudFront `/*`. Es lo mismo que hace el workflow en cada push a `develop`.

### Destruir demo

```bash
./scripts/destroy-demo.sh
```

**Irreversible.** Pide confirmación tipeada (nombre del bucket o `destroy`), vacía el bucket (incluyendo versiones) y corre `terraform destroy`. Elimina distribución, bucket, role IAM y (si lo creó este módulo) el OIDC provider.

### Rollback manual

No hay pipeline de rollback automático. Opciones:

1. **Redeploy de commit previo:** `git checkout <commit-bueno> -- ui-design` (o checkout del commit) y `./scripts/update-frontend.sh`, o re-run del workflow `frontend-deploy` desde ese commit (`workflow_dispatch`).
2. **Resync de build previo:** si se conserva un `dist/` anterior (artifact local o de Actions), `aws s3 sync <dist-previo> s3://<bucket> --delete` + invalidación.
3. Con `enable_bucket_versioning=true`, restaurar versiones previas de objetos también es posible.

## Workflows relacionados

- `.github/workflows/frontend-ci.yml`: checks (Biome, tsc, build) en PRs hacia `develop`. Sin credenciales AWS.
- `.github/workflows/frontend-deploy.yml`: push a `develop` / manual → checks → build real → OIDC → sync S3 → invalidación CloudFront.
