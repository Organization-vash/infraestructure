resource "aws_s3_bucket" "lambda_bucket" {
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

resource "aws_s3_bucket_notification" "lambda_eventbridge" {
  bucket      = aws_s3_bucket.lambda_bucket.id
  eventbridge = true

  depends_on = [aws_s3_bucket.lambda_bucket]
}
