resource "aws_s3_bucket" "frontend" {
  bucket        = "entel-s3-bucket-21"
  force_destroy = true

  tags = {
    Name        = "EntelFrontend"
    Environment = "dev"
  }
}

resource "aws_s3_bucket_website_configuration" "frontend" {
  bucket = aws_s3_bucket.frontend.id

  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "frontend_block" {
  bucket = aws_s3_bucket.frontend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "frontend_sse" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = "alias/aws/s3"
    }
  }

  depends_on = [aws_s3_bucket.frontend]
}

resource "aws_s3_bucket_notification" "frontend_eventbridge" {
  bucket      = aws_s3_bucket.frontend.id
  eventbridge = true

  depends_on = [aws_s3_bucket.frontend]
}
