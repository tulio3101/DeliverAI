## -----------------------------------------------------------------------
## DeliverAI static frontend hosting: private S3 + CloudFront (OAC) with
## the backend Spring Boot app on Azure fronted as a second CloudFront
## origin for API paths. GitHub Actions deploys via OIDC (no static keys).
## -----------------------------------------------------------------------

data "aws_caller_identity" "current" {}
data "aws_partition" "current" {}

locals {
  bucket_name = "${var.project_name}-${data.aws_caller_identity.current.account_id}"

  s3_origin_id      = "s3-frontend-origin"
  backend_origin_id = "backend-api-origin"

  # Every path pattern below is routed to the backend origin instead of
  # being served/handled by the SPA. Keep this list in sync with the
  # backend's actual controller base paths.
  api_path_patterns = [
    "/order*",
    "/order-items*",
    "/products*",
    "/user*",
    "/v3/*",
    "/swagger-ui*",
  ]

  github_oidc_provider_arn = var.create_github_oidc_provider ? aws_iam_openid_connect_provider.github_actions[0].arn : data.aws_iam_openid_connect_provider.github_actions[0].arn
}

## -----------------------------------------------------------------------
## S3 bucket: private origin storage for the built React app (ui-design/dist).
## No static website hosting is enabled - CloudFront (via OAC) is the only
## reader, and it always requests index.html as a document, so S3 website
## routing rules are unnecessary and would only widen the exposure surface.
## -----------------------------------------------------------------------

resource "aws_s3_bucket" "frontend" {
  bucket = local.bucket_name

  # Safety net against accidental `terraform destroy` on a bucket holding
  # the only copy of the built assets. Demo teardown goes through
  # scripts/destroy-demo.sh, which empties the bucket first.
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  # All four stay true: the OAC bucket policy below grants a service
  # principal (cloudfront.amazonaws.com) scoped by AWS:SourceArn, which S3
  # does not classify as "public", so it attaches fine with these blocks on.
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_ownership_controls" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    # No ACLs are used anywhere (OAC + bucket policy only), so the bucket
    # owner enforces object ownership unconditionally.
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_versioning" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  versioning_configuration {
    status = var.enable_bucket_versioning ? "Enabled" : "Suspended"
  }
}

## -----------------------------------------------------------------------
## CloudFront Origin Access Control: replaces the legacy OAI mechanism,
## signs every S3 request with SigV4 so the bucket can stay 100% private.
## -----------------------------------------------------------------------

resource "aws_cloudfront_origin_access_control" "frontend" {
  name                              = "${var.project_name}-oac"
  description                       = "OAC for ${local.bucket_name} (private React SPA origin)"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

## -----------------------------------------------------------------------
## Managed CloudFront policies (AWS-owned, referenced by id - never
## hardcode these ids, they differ per partition/region rollout).
## -----------------------------------------------------------------------

data "aws_cloudfront_cache_policy" "caching_optimized" {
  name = "Managed-CachingOptimized"
}

data "aws_cloudfront_cache_policy" "caching_disabled" {
  name = "Managed-CachingDisabled"
}

data "aws_cloudfront_origin_request_policy" "all_viewer_except_host" {
  # Forwards all viewer headers/cookies/query strings to the origin except
  # Host - required so the external backend origin does not receive the
  # CloudFront domain as its Host header, which would break Spring Boot
  # routing/virtual-host handling on Azure.
  name = "Managed-AllViewerExceptHostHeader"
}

## -----------------------------------------------------------------------
## CloudFront distribution
## -----------------------------------------------------------------------

resource "aws_cloudfront_distribution" "frontend" {
  comment             = "${var.project_name} (React SPA + API passthrough to Azure backend)"
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"

  # Price class rationale: primary users are in Colombia/Bogota (South
  # America). PriceClass_100 covers only North America + Europe edge
  # locations and would route LATAM viewers to a distant PoP, adding
  # meaningful latency. PriceClass_All includes South American edge
  # locations, trading a higher CloudFront data-transfer/request cost for
  # materially better latency for the actual user base. Override via
  # var.cloudfront_price_class (e.g. PriceClass_100) for a cost-sensitive
  # demo/staging environment where LATAM latency doesn't matter.
  price_class = var.cloudfront_price_class

  origin {
    domain_name              = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id                = local.s3_origin_id
    origin_access_control_id = aws_cloudfront_origin_access_control.frontend.id
  }

  origin {
    domain_name = var.backend_origin_domain
    origin_id   = local.backend_origin_id

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  # Default behavior: serve the React SPA static assets from S3.
  default_cache_behavior {
    target_origin_id = local.s3_origin_id

    allowed_methods = ["GET", "HEAD"]
    cached_methods  = ["GET", "HEAD"]

    viewer_protocol_policy = "redirect-to-https"
    compress               = true

    cache_policy_id = data.aws_cloudfront_cache_policy.caching_optimized.id
  }

  # API behaviors: everything under these path patterns bypasses the SPA
  # entirely and goes straight to the external Spring Boot backend, with
  # caching disabled and every HTTP method/query string forwarded.
  dynamic "ordered_cache_behavior" {
    for_each = local.api_path_patterns

    content {
      path_pattern     = ordered_cache_behavior.value
      target_origin_id = local.backend_origin_id

      allowed_methods = ["GET", "HEAD", "OPTIONS", "PUT", "POST", "PATCH", "DELETE"]
      cached_methods  = ["GET", "HEAD"]

      viewer_protocol_policy = "redirect-to-https"
      compress               = true

      cache_policy_id          = data.aws_cloudfront_cache_policy.caching_disabled.id
      origin_request_policy_id = data.aws_cloudfront_origin_request_policy.all_viewer_except_host.id
    }
  }

  # SPA client-side routing fallback. This only applies to the default
  # behavior's origin (S3): a 403/404 from S3 (unknown static path, e.g.
  # /orders/42 typed directly in the browser) is rewritten to index.html
  # so the React Router can take over. API path patterns above are matched
  # first by CloudFront's ordered_cache_behavior precedence and never fall
  # through to this rule, so a real 404 from the backend still returns
  # whatever the backend returned to the API caller... except CloudFront
  # applies custom_error_response distribution-wide by HTTP status code,
  # not per-origin. Backend 403/404 API responses ARE rewritten to
  # index.html:200 the same as S3 ones. If the backend needs to surface
  # real 403/404 JSON bodies to API clients, add error-code exceptions per
  # path or serve those statuses via a code other than 403/404. Documented
  # here rather than silently "fixed" because the requested behavior is a
  # distribution-wide custom_error_response.
  custom_error_response {
    error_code         = 403
    response_code      = 200
    response_page_path = "/index.html"
  }

  custom_error_response {
    error_code         = 404
    response_code      = 200
    response_page_path = "/index.html"
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    # No custom domain/ACM certificate wired up yet - served on the
    # *.cloudfront.net domain. Swap for an ACM cert (us-east-1) + your own
    # domain via aliases/acm_certificate_arn when a custom domain is ready.
    cloudfront_default_certificate = true
    minimum_protocol_version       = "TLSv1.2_2021"
  }

  tags = var.tags
}

## -----------------------------------------------------------------------
## S3 bucket policy: only the CloudFront distribution above (via OAC,
## scoped by AWS:SourceArn) may read objects. No other principal, and no
## public access whatsoever.
## -----------------------------------------------------------------------

data "aws_iam_policy_document" "frontend_bucket_policy" {
  statement {
    sid    = "AllowCloudFrontServicePrincipalReadOnly"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }

    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.frontend.arn}/*"]

    condition {
      test     = "StringEquals"
      variable = "AWS:SourceArn"
      values   = [aws_cloudfront_distribution.frontend.arn]
    }
  }
}

resource "aws_s3_bucket_policy" "frontend" {
  bucket = aws_s3_bucket.frontend.id
  policy = data.aws_iam_policy_document.frontend_bucket_policy.json
}

## -----------------------------------------------------------------------
## GitHub Actions OIDC provider. AWS accounts may only register the
## token.actions.githubusercontent.com provider once - if another stack /
## module in this account already created it, set
## create_github_oidc_provider = false and it will be looked up instead.
## -----------------------------------------------------------------------

# GitHub's OIDC issuer TLS chain has rotated CAs before (and will again -
# AWS no longer actually validates thumbprint_list content against
# well-known providers like GitHub, but the API still requires a
# syntactically valid 40-char SHA1 hex value). Fetching it live keeps
# thumbprint_list correct across CA rotations instead of baking in a
# fingerprint that silently goes stale.
data "tls_certificate" "github_actions" {
  url = "https://token.actions.githubusercontent.com/.well-known/openid-configuration"
}

resource "aws_iam_openid_connect_provider" "github_actions" {
  count = var.create_github_oidc_provider ? 1 : 0

  url            = "https://token.actions.githubusercontent.com"
  client_id_list = ["sts.amazonaws.com"]

  thumbprint_list = [for cert in data.tls_certificate.github_actions.certificates : cert.sha1_fingerprint]
}

data "aws_iam_openid_connect_provider" "github_actions" {
  count = var.create_github_oidc_provider ? 0 : 1

  url = "https://token.actions.githubusercontent.com"
}

## -----------------------------------------------------------------------
## IAM role assumed by the GitHub Actions deploy workflow (OIDC only - no
## long-lived AWS access keys). Trust is scoped to the exact repo + branch.
## -----------------------------------------------------------------------

data "aws_iam_policy_document" "github_actions_trust" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [local.github_oidc_provider_arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:${var.github_repo}:ref:refs/heads/${var.github_branch}"]
    }
  }
}

resource "aws_iam_role" "github_actions_deploy" {
  name               = "${var.project_name}-gha-deploy"
  description        = "Assumed by GitHub Actions (OIDC) on ${var.github_repo}@${var.github_branch} to deploy the frontend to S3/CloudFront."
  assume_role_policy = data.aws_iam_policy_document.github_actions_trust.json

  tags = var.tags
}

data "aws_iam_policy_document" "github_actions_deploy" {
  statement {
    sid       = "ListBucket"
    effect    = "Allow"
    actions   = ["s3:ListBucket"]
    resources = [aws_s3_bucket.frontend.arn]
  }

  statement {
    sid       = "ReadWriteObjects"
    effect    = "Allow"
    actions   = ["s3:PutObject", "s3:DeleteObject"]
    resources = ["${aws_s3_bucket.frontend.arn}/*"]
  }

  statement {
    sid       = "InvalidateDistribution"
    effect    = "Allow"
    actions   = ["cloudfront:CreateInvalidation"]
    resources = ["arn:${data.aws_partition.current.partition}:cloudfront::${data.aws_caller_identity.current.account_id}:distribution/${aws_cloudfront_distribution.frontend.id}"]
  }
}

resource "aws_iam_role_policy" "github_actions_deploy" {
  name   = "${var.project_name}-gha-deploy"
  role   = aws_iam_role.github_actions_deploy.id
  policy = data.aws_iam_policy_document.github_actions_deploy.json
}
