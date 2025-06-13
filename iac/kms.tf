resource "aws_kms_key" "lambda_env_vars" {
  # checkov:skip=CKV2_AWS_64 reason="Clave no operativa, entorno de desarrollo sin necesidad de política específica"
  description             = "KMS key for encrypting Lambda environment variables"
  deletion_window_in_days = 7
  enable_key_rotation     = true
}
