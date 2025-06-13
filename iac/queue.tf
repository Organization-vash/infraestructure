resource "aws_sqs_queue" "lambda_dlq" {
  name = "lambda-dead-letter-queue"

  message_retention_seconds = 1209600  # 14 días (máximo permitido)
  visibility_timeout_seconds = 60

  tags = {
    Name = "Lambda DLQ"
    Environment = "dev"
  }
}