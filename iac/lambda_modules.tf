resource "aws_lambda_function" "module_lambda" {
  function_name = "module-lambda"
  handler       = "com.vash.lambda.ModuleLambdaHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_role.arn

  s3_bucket = "entel-s3-bucket-lambda"
  s3_key    = "lambdas/module-lambda.jar"

  depends_on = [null_resource.upload_lambda_module]

  memory_size = 512
  timeout     = 30

  reserved_concurrent_executions = 10
  kms_key_arn = aws_kms_key.lambda_env_vars.arn
  code_signing_config_arn = aws_lambda_code_signing_config.lambda_csc.arn
  environment {
    variables = {
      ENV         = "dev"
      DB_URL      = "jdbc:postgresql://${aws_db_instance.postgres.address}:5432/entelapp"
      DB_USER     = "entelupao"
      DB_PASSWORD = "entelupao"
    }
  }

  vpc_config {
    subnet_ids         = [aws_subnet.private_1.id, aws_subnet.private_2.id]
    security_group_ids = [aws_security_group.lambda_sg.id]
  }

  tracing_config {
    mode = "Active"
  }

  dead_letter_config {
    target_arn = aws_sqs_queue.lambda_dlq.arn
  }
  tags = {
    Name = "module-lambda-function"
    Description = "Lambda para crear y eliminar modulos"
  }
}

resource "null_resource" "upload_lambda_module" {
  provisioner "local-exec" {
    command = "aws s3 cp ../lambda_modules/target/lambda_modules-1.0-SNAPSHOT.jar s3://entel-s3-bucket-lambda/lambdas/module-lambda.jar"
  }

  depends_on = [aws_s3_bucket.lambda_bucket]
}
