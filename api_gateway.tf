resource "aws_apigatewayv2_api" "main_api" {
  name          = "main-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_route" "user_route" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "ANY /users"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
}

resource "aws_apigatewayv2_integration" "user_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.user_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "2.0"
}


resource "aws_lambda_permission" "allow_apigw_invoke_user" {
  statement_id  = "AllowExecutionFromAPIGatewayUser"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.user_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.main_api.id
  name        = "$default"
  auto_deploy = true
}
