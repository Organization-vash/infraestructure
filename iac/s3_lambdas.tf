resource "aws_s3_bucket" "lambda_bucket" {
  bucket        = "entel-s3-bucket-lambda"
  force_destroy = true

  tags = {
    Name        = "LambdaCodeBucket"
    Environment = "dev"
  }
}
