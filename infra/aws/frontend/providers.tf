terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }

    # Only used to read the live TLS certificate chain for GitHub's OIDC
    # issuer (see aws_iam_openid_connect_provider.github_actions in
    # main.tf), so thumbprint_list never goes stale after a CA rotation.
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }

  # Local state on purpose for this project (no remote backend configured).
  # State files WILL contain sensitive ARNs/identifiers - see the repo-root
  # .gitignore note in this module's README/scripts. Do not commit
  # terraform.tfstate, terraform.tfstate.backup, or the .terraform/ directory.
}

# Credentials are resolved via the default AWS credential provider chain
# (environment variables, shared config/credentials file, AWS_PROFILE,
# SSO, or an assumed role) - never hardcoded here. Running `aws sts
# get-caller-identity` (see scripts/init-setup.sh) confirms which identity
# Terraform will use before anything is created.
provider "aws" {
  region = var.aws_region

  default_tags {
    tags = var.tags
  }
}
