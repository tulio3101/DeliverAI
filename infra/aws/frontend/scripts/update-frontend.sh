#!/usr/bin/env bash
# update-frontend.sh - build and deploy the React SPA only. No Terraform
# runs here (infra is assumed to already exist - use init-setup.sh /
# update-infra.sh for that). Mirrors what the GitHub Actions deploy
# workflow does, for local/manual deploys.
#
# Steps:
#   1. pnpm build (ui-design/) with VITE_MOCK_DATA=false and VITE_API_BASE_URL
#   2. aws s3 sync ui-design/dist -> s3://<bucket> --delete
#   3. aws cloudfront create-invalidation --paths "/*"
#
# Bucket name and distribution id are read from `terraform output` in
# infra/aws/frontend, so the module must already be applied. Respects
# AWS_PROFILE / AWS_REGION already set in your shell.
#
# Usage:
#   ./update-frontend.sh
#   VITE_API_BASE_URL="https://d123abc.cloudfront.net" ./update-frontend.sh
#   SKIP_INSTALL=1 ./update-frontend.sh   # skip `pnpm install --frozen-lockfile`

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
MODULE_DIR="$(cd -- "${SCRIPT_DIR}/.." >/dev/null 2>&1 && pwd)"
REPO_ROOT="$(cd -- "${MODULE_DIR}/../../.." >/dev/null 2>&1 && pwd)"
UI_DIR="${REPO_ROOT}/ui-design"

log() { printf '\033[1;34m[update-frontend]\033[0m %s\n' "$*"; }
die() { printf '\033[1;31m[update-frontend] ERROR:\033[0m %s\n' "$*" >&2; exit 1; }

command -v terraform >/dev/null 2>&1 || die "terraform is not installed / not on PATH (needed to read bucket/distribution outputs)."
command -v pnpm      >/dev/null 2>&1 || die "pnpm is not installed / not on PATH."
command -v aws       >/dev/null 2>&1 || die "aws CLI is not installed / not on PATH."

[[ -d "${UI_DIR}" ]] || die "ui-design directory not found at ${UI_DIR}."

[[ -n "${AWS_PROFILE:-}" ]] && log "Using AWS_PROFILE=${AWS_PROFILE}"

log "Validating AWS credentials (aws sts get-caller-identity)..."
aws sts get-caller-identity --output text >/dev/null \
  || die "Could not resolve AWS credentials. Configure AWS_PROFILE, run 'aws configure' or 'aws sso login', then retry."

log "Reading bucket/distribution from terraform output (${MODULE_DIR})..."
pushd "${MODULE_DIR}" >/dev/null
[[ -d ".terraform" ]] || die "Terraform not initialized in ${MODULE_DIR}. Run init-setup.sh first."

S3_BUCKET="$(terraform output -raw s3_bucket_name 2>/dev/null)" \
  || die "Could not read s3_bucket_name output. Has the infra been applied?"
CLOUDFRONT_DISTRIBUTION_ID="$(terraform output -raw cloudfront_distribution_id 2>/dev/null)" \
  || die "Could not read cloudfront_distribution_id output. Has the infra been applied?"
CLOUDFRONT_DOMAIN="$(terraform output -raw cloudfront_domain_name 2>/dev/null || true)"
popd >/dev/null

log "S3 bucket: ${S3_BUCKET}"
log "CloudFront distribution: ${CLOUDFRONT_DISTRIBUTION_ID}"

# VITE_API_BASE_URL: honor an explicit override from the caller/CI
# (GitHub Variable of the same name). Otherwise default to the
# CloudFront domain itself, since /order*, /products*, /user*, /v3/*,
# /swagger-ui* etc. are all routed same-origin through CloudFront to the
# backend - this avoids hardcoding the Azure backend FQDN into the SPA.
if [[ -z "${VITE_API_BASE_URL:-}" ]]; then
  [[ -n "${CLOUDFRONT_DOMAIN}" ]] || die "VITE_API_BASE_URL is not set and cloudfront_domain_name output is unavailable. Export VITE_API_BASE_URL explicitly."
  VITE_API_BASE_URL="https://${CLOUDFRONT_DOMAIN}"
  log "VITE_API_BASE_URL not set, defaulting to CloudFront origin: ${VITE_API_BASE_URL}"
else
  log "VITE_API_BASE_URL=${VITE_API_BASE_URL}"
fi

pushd "${UI_DIR}" >/dev/null

if [[ "${SKIP_INSTALL:-0}" != "1" ]]; then
  log "Running pnpm install --frozen-lockfile..."
  pnpm install --frozen-lockfile
else
  log "SKIP_INSTALL=1, skipping pnpm install."
fi

log "Running pnpm build (VITE_MOCK_DATA=false)..."
VITE_MOCK_DATA=false \
VITE_API_BASE_URL="${VITE_API_BASE_URL}" \
  pnpm build

[[ -d "dist" ]] || die "Build did not produce ui-design/dist."

popd >/dev/null

log "Syncing ui-design/dist to s3://${S3_BUCKET} (--delete)..."
aws s3 sync "${UI_DIR}/dist" "s3://${S3_BUCKET}" --delete

log "Creating CloudFront invalidation for /* on ${CLOUDFRONT_DISTRIBUTION_ID}..."
INVALIDATION_ID="$(aws cloudfront create-invalidation \
  --distribution-id "${CLOUDFRONT_DISTRIBUTION_ID}" \
  --paths "/*" \
  --query 'Invalidation.Id' \
  --output text)"

log "Invalidation created: ${INVALIDATION_ID}"
[[ -n "${CLOUDFRONT_DOMAIN}" ]] && log "Deployed: https://${CLOUDFRONT_DOMAIN}"
log "Done."
