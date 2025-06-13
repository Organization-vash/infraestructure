resource "aws_kms_key" "lambda_env_vars" {
  description             = "KMS key for encrypting Lambda environment variables"
  deletion_window_in_days = 7
  enable_key_rotation     = true
}
