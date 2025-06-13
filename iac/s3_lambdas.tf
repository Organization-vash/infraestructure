resource "aws_s3_bucket" "lambda_bucket" {
  # checkov:skip=CKV_AWS_144:No necesitamos un bucket destino en otra región.
  # checkov:skip=CKV_AWS_18:No es necesario habilitar access logging en el bucket de lambdas, solo requerimos logs para las funciones, las peticiones llegan al api gateway.
  bucket        = "entel-s3-bucket-lambda"
  force_destroy = true

  tags = {
    Name        = "LambdaCodeBucket"
    Environment = "dev"
  }
}

resource "aws_s3_bucket_public_access_block" "lambda_block" {
  bucket = aws_s3_bucket.lambda_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "lambda_sse" {
  bucket = aws_s3_bucket.lambda_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = "alias/aws/s3"
    }
  }

  depends_on = [aws_s3_bucket.lambda_bucket]
}

resource "aws_s3_bucket_notification" "lambda_eventbridge" {
  bucket      = aws_s3_bucket.lambda_bucket.id
  eventbridge = true

  depends_on = [aws_s3_bucket.lambda_bucket]
}

resource "aws_s3_bucket_versioning" "lambda_versioning" {
  bucket = aws_s3_bucket.lambda_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "lambda_lifecycle" {
  bucket = aws_s3_bucket.lambda_bucket.id

  rule {
    id     = "cleanup-incomplete-multipart"
    status = "Enabled"

    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}
