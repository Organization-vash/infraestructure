resource "aws_cloudfront_origin_access_control" "frontend_oac" {
  name                              = "frontend-oac"
  description                       = "Acceso de CloudFront a S3"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_response_headers_policy" "security_headers" {
  name = "entel-security-headers"

  security_headers_config {
    content_security_policy {
      override                = true
      content_security_policy = "default-src 'self';"
    }
    frame_options {
      frame_option = "DENY"
      override     = true
    }
    referrer_policy {
      referrer_policy = "no-referrer"
      override        = true
    }
    xss_protection {
      protection           = true
      mode_block           = true
      override             = true
    }
    strict_transport_security {
      override                    = true
      include_subdomains           = true
      preload                      = true
      access_control_max_age_sec   = 63072000
    }
  }
}

resource "aws_cloudfront_distribution" "frontend_distribution" {
  # checkov:skip=CKV2_AWS_42:No tenemos dominio.
  # checkov:skip=CKV2_AWS_47:ya se incluyó la regla AWSManagedRulesLog4jRuleSet en la WAF asociada
  enabled             = true
  default_root_object = "index.html"

  origin {
    domain_name = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id   = "s3-frontend"

    origin_access_control_id = aws_cloudfront_origin_access_control.frontend_oac.id
  }

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = "s3-frontend"

    viewer_protocol_policy = "redirect-to-https"

    response_headers_policy_id   = aws_cloudfront_response_headers_policy.security_headers.id

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }
  }

  restrictions {
    geo_restriction {
      restriction_type = "blacklist"
      locations        = ["CN", "RU", "KP", "IR"]
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
    minimum_protocol_version = "TLSv1.2_2021"
  }

  tags = {
    Name = "FrontendCloudFront"
  }
  web_acl_id = aws_wafv2_web_acl.frontend_waf.arn

}
resource "aws_wafv2_web_acl" "frontend_waf" {
  # checkov:skip=CKV2_AWS_31:no es necesario aumentar los log de waf para el funcionamiento de nuestra infraestructura
  name        = "frontend-waf"
  description = "WAF básica para CloudFront con protección Log4j"
  scope       = "CLOUDFRONT"

  default_action {
    allow {}
  }

  visibility_config {
    cloudwatch_metrics_enabled = true
    metric_name                = "frontend-waf"
    sampled_requests_enabled   = true
  }

  rule {
    name     = "AWS-AWSManagedRulesLog4jRuleSet"
    priority = 1

    override_action {
      none {}
    }

    statement {
      managed_rule_group_statement {
        name        = "AWSManagedRulesLog4jRuleSet"
        vendor_name = "AWS"
      }
    }

    visibility_config {
      cloudwatch_metrics_enabled = true
      metric_name                = "log4j-rule"
      sampled_requests_enabled   = true
    }
  }
}


resource "aws_s3_bucket_policy" "only_cloudfront" {
  bucket = aws_s3_bucket.frontend.id

  depends_on = [aws_cloudfront_distribution.frontend_distribution]

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "AllowCloudFrontServicePrincipal",
        Effect    = "Allow",
        Principal = {
          Service = "cloudfront.amazonaws.com"
        },
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.frontend.arn}/*",
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.frontend_distribution.arn
          }
        }
      }
    ]
  })
}
