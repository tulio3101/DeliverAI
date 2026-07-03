#!/usr/bin/env bash
# init-setup.sh - first-run bootstrap for infra/aws/frontend.
#
# Validates AWS credentials, runs terraform init/plan/apply, and prints the
# resulting values mapped to the exact GitHub Variable names this repo's
# Actions workflow expects (AWS_REGION, AWS_ROLE_ARN, S3_BUCKET,
# CLOUDFRONT_DISTRIBUTION_ID). VITE_API_BASE_URL is not produced here since
# it's the frontend's own build-time variable, not a Terraform output - set
# it manually to https://<cloudfront_domain_name> or your API path, per
# how ui-design consumes it.
#
# Respects AWS_PROFILE / AWS_REGION / any other AWS SDK env vars already set
# in your shell - it does not set or override credentials itself.
#
# Usage: ./init-setup.sh [terraform apply args...]
#   e.g. ./init-setup.sh -auto-approve
#        ./init-setup.sh -var-file=terraform.tfvars

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
MODULE_DIR="$(cd -- "${SCRIPT_DIR}/.." >/dev/null 2>&1 && pwd)"

log()  { printf '\033[1;34m[init-setup]\033[0m %s\n' "$*"; }
die()  { printf '\033[1;31m[init-setup] ERROR:\033[0m %s\n' "$*" >&2; exit 1; }

command -v terraform >/dev/null 2>&1 || die "terraform is not installed / not on PATH."
command -v aws       >/dev/null 2>&1 || die "aws CLI is not installed / not on PATH."

log "Module directory: ${MODULE_DIR}"

if [[ -n "${AWS_PROFILE:-}" ]]; then
  log "Using AWS_PROFILE=${AWS_PROFILE}"
fi

log "Validating AWS credentials (aws sts get-caller-identity)..."
CALLER_IDENTITY_JSON="$(aws sts get-caller-identity --output json)" \
  || die "Could not resolve AWS credentials. Configure AWS_PROFILE, run 'aws configure' or 'aws sso login', then retry."

CALLER_ACCOUNT="$(printf '%s' "${CALLER_IDENTITY_JSON}" | grep -o '"Account":[[:space:]]*"[^"]*"' | sed -E 's/.*"([0-9]+)"/\1/')"
CALLER_ARN="$(printf '%s' "${CALLER_IDENTITY_JSON}" | grep -o '"Arn":[[:space:]]*"[^"]*"' | sed -E 's/.*"(arn:[^"]+)"/\1/')"
log "Authenticated as: ${CALLER_ARN:-unknown} (account ${CALLER_ACCOUNT:-unknown})"

if [[ ! -f "${MODULE_DIR}/terraform.tfvars" ]]; then
  log "No terraform.tfvars found next to the module."
  log "Copy terraform.tfvars.example to terraform.tfvars and fill in real values (at minimum backend_origin_domain) before continuing."
  read -r -p "Continue anyway using -var flags / defaults only? [y/N] " CONTINUE_ANSWER
  case "${CONTINUE_ANSWER}" in
    [yY]|[yY][eE][sS]) : ;;
    *) die "Aborted. Create terraform.tfvars and re-run." ;;
  esac
fi

pushd "${MODULE_DIR}" >/dev/null

log "Running terraform init..."
terraform init -input=false

log "Running terraform plan..."
terraform plan -input=false -out=tfplan.init "$@"

log "Running terraform apply..."
terraform apply -input=false tfplan.init
rm -f tfplan.init

log "Apply complete. Fetching outputs..."

TF_S3_BUCKET="$(terraform output -raw s3_bucket_name)"
TF_DISTRIBUTION_ID="$(terraform output -raw cloudfront_distribution_id)"
TF_CLOUDFRONT_DOMAIN="$(terraform output -raw cloudfront_domain_name)"
TF_ROLE_ARN="$(terraform output -raw github_actions_deploy_role_arn)"
TF_REGION="$(terraform output -raw aws_region)"

popd >/dev/null

cat <<SUMMARY

============================================================
 Set these as GitHub repository Variables
 (Settings > Secrets and variables > Actions > Variables)
 on tulio3101/DeliverAI, branch develop:
============================================================
AWS_REGION                  = ${TF_REGION}
AWS_ROLE_ARN                = ${TF_ROLE_ARN}
S3_BUCKET                   = ${TF_S3_BUCKET}
CLOUDFRONT_DISTRIBUTION_ID  = ${TF_DISTRIBUTION_ID}
VITE_API_BASE_URL           = <set to your backend/API base URL - not a Terraform output>

CloudFront URL: https://${TF_CLOUDFRONT_DOMAIN}
============================================================

Example gh CLI commands:
  gh variable set AWS_REGION --repo tulio3101/DeliverAI --body "${TF_REGION}"
  gh variable set AWS_ROLE_ARN --repo tulio3101/DeliverAI --body "${TF_ROLE_ARN}"
  gh variable set S3_BUCKET --repo tulio3101/DeliverAI --body "${TF_S3_BUCKET}"
  gh variable set CLOUDFRONT_DISTRIBUTION_ID --repo tulio3101/DeliverAI --body "${TF_DISTRIBUTION_ID}"

SUMMARY

log "Done."
