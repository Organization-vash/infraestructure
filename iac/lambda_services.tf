resource "aws_lambda_function" "service_lambda" {
  function_name = "service-lambda"
  handler       = "com.vash.lambda.ServiceLambdaHandler::handleRequest"
  runtime       = "java17"
  role          = aws_iam_role.lambda_role.arn

  s3_bucket = "entel-s3-bucket-lambda"
  s3_key    = "lambdas/service-lambda.jar"

  depends_on = [null_resource.upload_lambda_service]

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
    Name = "service-lambda-function"
  }
}

resource "null_resource" "upload_lambda_service" {
  provisioner "local-exec" {
    command = "aws s3 cp ../lambda_services/target/lambda_services-1.0-SNAPSHOT.jar s3://entel-s3-bucket-lambda/lambdas/service-lambda.jar"
  }

  depends_on = [aws_s3_bucket.lambda_bucket]
}
