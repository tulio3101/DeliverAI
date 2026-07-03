output "s3_bucket_name" {
  description = "Name of the private S3 bucket holding the built React app. Maps to GitHub Variable S3_BUCKET."
  value       = aws_s3_bucket.frontend.id
}

output "s3_bucket_arn" {
  description = "ARN of the private S3 bucket."
  value       = aws_s3_bucket.frontend.arn
}

output "cloudfront_distribution_id" {
  description = "CloudFront distribution id. Maps to GitHub Variable CLOUDFRONT_DISTRIBUTION_ID."
  value       = aws_cloudfront_distribution.frontend.id
}

output "cloudfront_distribution_arn" {
  description = "CloudFront distribution ARN."
  value       = aws_cloudfront_distribution.frontend.arn
}

output "cloudfront_domain_name" {
  description = "CloudFront distribution domain name (*.cloudfront.net) - the public URL for the app until a custom domain is attached."
  value       = aws_cloudfront_distribution.frontend.domain_name
}

output "cloudfront_url" {
  description = "Convenience HTTPS URL for the CloudFront distribution."
  value       = "https://${aws_cloudfront_distribution.frontend.domain_name}"
}

output "github_actions_deploy_role_arn" {
  description = "IAM role ARN GitHub Actions assumes via OIDC to deploy. Maps to GitHub Variable AWS_ROLE_ARN."
  value       = aws_iam_role.github_actions_deploy.arn
}

output "aws_region" {
  description = "AWS region resources were created in. Maps to GitHub Variable AWS_REGION."
  value       = var.aws_region
}

output "github_oidc_provider_arn" {
  description = "ARN of the GitHub Actions OIDC provider in use (created or looked up)."
  value       = local.github_oidc_provider_arn
}
