variable "project_name" {
  description = "Prefix used for naming every resource created by this module (bucket, distribution, IAM role, etc.)."
  type        = string
  default     = "deliverai-frontend"

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]{1,40}$", var.project_name))
    error_message = "project_name must be lowercase alphanumeric/hyphen, start with a letter, and be 2-41 characters long (kept short so it fits inside generated S3 bucket names, which have a 63 character limit)."
  }
}

variable "aws_region" {
  description = "AWS region for the S3 bucket and all regional resources. CloudFront itself is global."
  type        = string
  default     = "us-east-1"
}

variable "backend_origin_domain" {
  description = "External backend FQDN (Azure Web App), no scheme, e.g. \"deliverai-backend.azurewebsites.net\". CloudFront forwards API paths to this origin over HTTPS."
  type        = string

  validation {
    condition     = !can(regex("^https?://", var.backend_origin_domain))
    error_message = "backend_origin_domain must be a bare FQDN with no scheme (no http:// or https:// prefix)."
  }
}

variable "github_repo" {
  description = "GitHub repository (owner/name) allowed to assume the deploy role via OIDC."
  type        = string
  default     = "tulio3101/DeliverAI"
}

variable "github_branch" {
  description = "Branch allowed to assume the deploy role via OIDC (GitHub Actions trust is scoped to this ref only)."
  type        = string
  default     = "develop"
}

variable "create_github_oidc_provider" {
  description = "Whether to create the GitHub Actions OIDC provider (token.actions.githubusercontent.com) in this AWS account. Set to false if the provider already exists in the account (an account may only have one, IAM will error on a duplicate) and instead reference it via a data source."
  type        = bool
  default     = true
}

variable "enable_bucket_versioning" {
  description = "Enable S3 bucket versioning for the frontend assets bucket. Optional safety net for rollbacks; adds storage cost for old object versions."
  type        = bool
  default     = false
}

variable "cloudfront_price_class" {
  description = <<-EOT
    CloudFront price class. Defaults to PriceClass_All so that South America
    edge locations (including Bogota / Colombia-adjacent PoPs) are used,
    which materially improves latency for the primary user base. The
    tradeoff is higher CloudFront cost than PriceClass_100 (North
    America/Europe only) or PriceClass_200 (adds Asia, still excludes South
    America). Override to PriceClass_100 if cost matters more than LATAM
    latency for a given environment (e.g. a throwaway demo stack).
  EOT
  type        = string
  default     = "PriceClass_All"

  validation {
    condition     = contains(["PriceClass_100", "PriceClass_200", "PriceClass_All"], var.cloudfront_price_class)
    error_message = "cloudfront_price_class must be one of PriceClass_100, PriceClass_200, PriceClass_All."
  }
}

variable "tags" {
  description = "Common tags applied to every resource via provider default_tags."
  type        = map(string)
  default = {
    Project   = "DeliverAI"
    ManagedBy = "terraform"
    Component = "frontend"
  }
}
