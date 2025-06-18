resource "aws_lambda_function" "module_lambda" {
  # checkov:skip=CKV_AWS_272: Code signing no es requerido en este entorno de desarrollo (no es producción)
  # checkov:skip=CKV_AWS_173: No se requiere cifrado KMS de variables de entorno en desarrollo.
  # checkov:skip=CKV_AWS_116: DLQ no es necesario para entorno de desarrollo.
  # checkov:skip=CKV_AWS_50: X-Ray tracing no es necesario para entorno de desarrollo.
  # checkov:skip=CKV_AWS_115: No se requiere limitar concurrencia en entorno de desarrollo.
  function_name = "module-lambda"
  handler       = "com.vash.lambda.ModuleLambdaHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_role.arn

  s3_bucket = "entel-s3-bucket-lambda"
  s3_key    = "lambdas/module-lambda.jar"

  depends_on = [null_resource.upload_lambda_module]

  memory_size = 512
  timeout     = 30

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
