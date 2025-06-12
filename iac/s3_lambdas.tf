resource "aws_s3_bucket" "lambda_bucket" {
  bucket        = "entel-s3-bucket-lambda"
  force_destroy = true

  tags = {
    Name        = "LambdaCodeBucket"
    Environment = "dev"
  }
}

resource "aws_s3_bucket_notification" "lambda_eventbridge" {
  bucket      = aws_s3_bucket.lambda_bucket.id
  eventbridge = true

  depends_on = [aws_s3_bucket.lambda_bucket]
}
