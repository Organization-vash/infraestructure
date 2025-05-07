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
