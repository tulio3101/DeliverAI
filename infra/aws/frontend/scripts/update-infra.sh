#!/usr/bin/env bash
# update-infra.sh - plan + apply Terraform changes against existing
# infra/aws/frontend infrastructure (bucket/distribution/IAM role already
# exist; this just reconciles drift or applies edits to the .tf files).
#
# Respects AWS_PROFILE / AWS_REGION already set in your shell.
#
# Usage: ./update-infra.sh [terraform apply args...]
#   e.g. ./update-infra.sh -auto-approve
#        ./update-infra.sh -var-file=terraform.tfvars

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
MODULE_DIR="$(cd -- "${SCRIPT_DIR}/.." >/dev/null 2>&1 && pwd)"

log() { printf '\033[1;34m[update-infra]\033[0m %s\n' "$*"; }
die() { printf '\033[1;31m[update-infra] ERROR:\033[0m %s\n' "$*" >&2; exit 1; }

command -v terraform >/dev/null 2>&1 || die "terraform is not installed / not on PATH."
command -v aws       >/dev/null 2>&1 || die "aws CLI is not installed / not on PATH."

[[ -n "${AWS_PROFILE:-}" ]] && log "Using AWS_PROFILE=${AWS_PROFILE}"

log "Validating AWS credentials (aws sts get-caller-identity)..."
aws sts get-caller-identity --output text >/dev/null \
  || die "Could not resolve AWS credentials. Configure AWS_PROFILE, run 'aws configure' or 'aws sso login', then retry."

pushd "${MODULE_DIR}" >/dev/null

if [[ ! -d ".terraform" ]]; then
  log ".terraform not initialized yet, running terraform init..."
  terraform init -input=false
fi

log "Running terraform plan..."
terraform plan -input=false -out=tfplan.update "$@"

read -r -p "Apply the plan above? [y/N] " APPLY_ANSWER
case "${APPLY_ANSWER}" in
  [yY]|[yY][eE][sS])
    log "Running terraform apply..."
    terraform apply -input=false tfplan.update
    ;;
  *)
    log "Not applying. Plan left at ${MODULE_DIR}/tfplan.update for inspection (terraform show tfplan.update)."
    popd >/dev/null
    exit 0
    ;;
esac

rm -f tfplan.update
popd >/dev/null

log "Done."
