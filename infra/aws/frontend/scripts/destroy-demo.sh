#!/usr/bin/env bash
# destroy-demo.sh - IRREVERSIBLY tear down infra/aws/frontend: empties the
# S3 bucket (all object versions, if versioning was enabled) and runs
# `terraform destroy`. This deletes the CloudFront distribution, the S3
# bucket and every object in it, the GitHub Actions IAM role, and (if it
# was created by this module) the GitHub OIDC provider.
#
# Requires typed confirmation - either the exact S3 bucket name or the
# literal word "destroy" - before doing anything destructive.
#
# Respects AWS_PROFILE / AWS_REGION already set in your shell.
#
# Usage: ./destroy-demo.sh

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
MODULE_DIR="$(cd -- "${SCRIPT_DIR}/.." >/dev/null 2>&1 && pwd)"

log()  { printf '\033[1;34m[destroy-demo]\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[destroy-demo] WARNING:\033[0m %s\n' "$*"; }
die()  { printf '\033[1;31m[destroy-demo] ERROR:\033[0m %s\n' "$*" >&2; exit 1; }

command -v terraform >/dev/null 2>&1 || die "terraform is not installed / not on PATH."
command -v aws       >/dev/null 2>&1 || die "aws CLI is not installed / not on PATH."

[[ -n "${AWS_PROFILE:-}" ]] && log "Using AWS_PROFILE=${AWS_PROFILE}"

log "Validating AWS credentials (aws sts get-caller-identity)..."
aws sts get-caller-identity --output text >/dev/null \
  || die "Could not resolve AWS credentials. Configure AWS_PROFILE, run 'aws configure' or 'aws sso login', then retry."

pushd "${MODULE_DIR}" >/dev/null
[[ -d ".terraform" ]] || die "Terraform not initialized in ${MODULE_DIR}. Nothing to destroy (or run terraform init first)."

S3_BUCKET="$(terraform output -raw s3_bucket_name 2>/dev/null || true)"
CLOUDFRONT_DISTRIBUTION_ID="$(terraform output -raw cloudfront_distribution_id 2>/dev/null || true)"

[[ -n "${S3_BUCKET}" ]] || die "Could not read s3_bucket_name output. Is there any state to destroy?"

warn "This will PERMANENTLY and IRREVERSIBLY delete:"
warn "  - S3 bucket: ${S3_BUCKET} (and every object/version in it)"
warn "  - CloudFront distribution: ${CLOUDFRONT_DISTRIBUTION_ID:-<unknown>}"
warn "  - The GitHub Actions deploy IAM role and its inline policy"
warn "  - The GitHub OIDC provider, IF it was created by this module"
warn "There is NO UNDO. Deployed frontend assets will be gone unless you have a copy elsewhere."
echo

read -r -p "Type the S3 bucket name (${S3_BUCKET}) or the word destroy to confirm: " CONFIRMATION

if [[ "${CONFIRMATION}" != "${S3_BUCKET}" && "${CONFIRMATION}" != "destroy" ]]; then
  popd >/dev/null
  die "Confirmation did not match. Aborted, nothing was deleted."
fi

log "Confirmed. Emptying S3 bucket ${S3_BUCKET} (all objects and versions)..."

# Plain delete for current objects.
aws s3 rm "s3://${S3_BUCKET}" --recursive || true

# Bucket may have versioning enabled (enable_bucket_versioning=true), in
# which case `s3 rm` alone leaves delete markers/prior versions behind and
# `terraform destroy` would fail on a non-empty bucket even with
# force_destroy set in some edge cases. Purge every version + delete
# marker explicitly via the API.
log "Purging any remaining object versions / delete markers..."
VERSIONS_JSON="$(aws s3api list-object-versions --bucket "${S3_BUCKET}" \
  --query '{Objects: Versions[].{Key:Key,VersionId:VersionId}}' --output json 2>/dev/null || echo '{}')"
if [[ -n "${VERSIONS_JSON}" && "${VERSIONS_JSON}" != '{}' && "${VERSIONS_JSON}" != '{"Objects": null}' ]]; then
  echo "${VERSIONS_JSON}" | aws s3api delete-objects --bucket "${S3_BUCKET}" --delete file:///dev/stdin >/dev/null 2>&1 || true
fi

MARKERS_JSON="$(aws s3api list-object-versions --bucket "${S3_BUCKET}" \
  --query '{Objects: DeleteMarkers[].{Key:Key,VersionId:VersionId}}' --output json 2>/dev/null || echo '{}')"
if [[ -n "${MARKERS_JSON}" && "${MARKERS_JSON}" != '{}' && "${MARKERS_JSON}" != '{"Objects": null}' ]]; then
  echo "${MARKERS_JSON}" | aws s3api delete-objects --bucket "${S3_BUCKET}" --delete file:///dev/stdin >/dev/null 2>&1 || true
fi

log "Bucket emptied. Running terraform destroy..."
terraform destroy -input=false

popd >/dev/null

log "Destroy complete. Remember to remove the now-stale GitHub Variables (AWS_ROLE_ARN, S3_BUCKET, CLOUDFRONT_DISTRIBUTION_ID) if this environment is gone for good."
