resource "aws_s3_bucket" "frontend" {
  # checkov:skip=CKV_AWS_144:No necesitamos un bucket destino en otra región.
  # checkov:skip=CKV_AWS_18:No es necesario habilitar access logging en el bucket frontend, solo requerimos logs de las funciones.
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

resource "aws_s3_bucket_versioning" "frontend_versioning" {
  bucket = aws_s3_bucket.frontend.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "frontend_lifecycle" {
  bucket = aws_s3_bucket.frontend.id

  rule {
    id     = "cleanup-incomplete-multipart"
    status = "Enabled"
    filter {}
    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}
