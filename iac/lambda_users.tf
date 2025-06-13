resource "aws_lambda_function" "user_lambda" {
  # checkov:skip=CKV_AWS_272: Code signing no es requerido en este entorno de desarrollo (no es producción)
  function_name = "user-lambda"
  handler       = "com.vash.lambda.UserLambdaHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_role.arn

  s3_bucket = "entel-s3-bucket-lambda"
  s3_key    = "lambdas/user-lambda.jar"

  depends_on = [null_resource.upload_lambda_user]

  memory_size = 512
  timeout     = 30
  reserved_concurrent_executions = 10
  kms_key_arn = aws_kms_key.lambda_env_vars.arn

  environment {
    variables = {
      ENV = "dev"
      DB_URL      = "jdbc:postgresql://${aws_db_instance.postgres.address}:5432/entelapp"
      DB_USER     = "entelupao"
      DB_PASSWORD = "entelupao"
    }
  }

  dead_letter_config {
    target_arn = aws_sqs_queue.lambda_dlq.arn
  }

  vpc_config {
    subnet_ids         = [aws_subnet.private_1.id, aws_subnet.private_2.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  tracing_config {
    mode = "Active"
  }

  tags = {
    Name = "user-lambda-function"
  }
}

resource "null_resource" "upload_lambda_user" {
  provisioner "local-exec" {
    command = "aws s3 cp ../lambda_users/target/lambda_users-1.0-SNAPSHOT.jar s3://entel-s3-bucket-lambda/lambdas/user-lambda.jar"
  }

  depends_on = [aws_s3_bucket.lambda_bucket]
}
