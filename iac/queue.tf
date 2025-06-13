resource "aws_sqs_queue" "lambda_dlq" {
  # checkov:skip=CKV2_AWS_73 Desarrollando en entorno dev, sin datos sensibles
  name = "lambda-dead-letter-queue"

  message_retention_seconds  = 1209600  
  visibility_timeout_seconds = 60
  kms_master_key_id          = "alias/aws/sqs" 

  tags = {
    Name        = "Lambda DLQ"
    Environment = "dev"
  }
}