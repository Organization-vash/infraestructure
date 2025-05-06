resource "aws_lambda_function" "user_lambda" {
  function_name = "user-lambda"
  handler       = "com.vash.lambda.UserLambdaHandler::handleRequest" 
  runtime       = "java17" 
  role          = aws_iam_role.lambda_role.arn

  s3_bucket = "entel-s3-bucket-lambda"
  s3_key    = "lambdas/user-lambda.jar"

  depends_on = [null_resource.upload_lambda_user]

  memory_size = 512
  timeout     = 20

  environment {
    variables = {
      ENV = "dev"
    }
  }

  tags = {
    Name = "user-lambda-function"
  }
}

resource "null_resource" "upload_lambda_user" {
  provisioner "local-exec" {
    command = "aws s3 cp ./lambda_users/target/lambda_users-1.0-SNAPSHOT.jar s3://entel-s3-bucket-lambda/lambdas/user-lambda.jar"
  }

  depends_on = [aws_s3_bucket.lambda_bucket]
}


