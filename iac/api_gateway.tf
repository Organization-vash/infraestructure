resource "aws_apigatewayv2_api" "main_api" {
  name          = "main-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = ["*"]
    allow_methods = ["GET","POST","PUT","DELETE","OPTIONS"]
    allow_headers = ["*"]
    expose_headers = ["*"]
    max_age       = 3600
  }
}

resource "aws_apigatewayv2_integration" "user_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.user_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "service_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.service_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "code_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.code_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "module_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.module_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_integration" "agency_integration" {
  api_id                 = aws_apigatewayv2_api.main_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.agency_lambda.invoke_arn
  integration_method     = "POST"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "user_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /users"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "user_by_id_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /users/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "user_route_post" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "POST /users"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "user_by_id_route_put" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "PUT /users/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "user_by_id_route_delete" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "DELETE /users/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "user_route_options" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "OPTIONS /users"
  target    = "integrations/${aws_apigatewayv2_integration.user_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /services"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_by_id_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /services/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_route_post" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "POST /services"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_by_id_route_put" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "PUT /services/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_by_id_route_delete" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "DELETE /services/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "service_route_options" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "OPTIONS /services"
  target    = "integrations/${aws_apigatewayv2_integration.service_integration.id}"
  authorization_type = "NONE"
}

# Rutas para Code
resource "aws_apigatewayv2_route" "code_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /codes"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "code_by_id_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /codes/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "code_route_post" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "POST /codes"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "code_by_id_route_put" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "PUT /codes/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "code_by_id_route_delete" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "DELETE /codes/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "code_route_options" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "OPTIONS /codes"
  target    = "integrations/${aws_apigatewayv2_integration.code_integration.id}"
  authorization_type = "NONE"
}

# Rutas para Modulo
resource "aws_apigatewayv2_route" "module_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /modules"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "module_by_id_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /modules/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "module_route_post" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "POST /modules"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "module_by_id_route_put" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "PUT /modules/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "module_by_id_route_delete" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "DELETE /modules/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "module_route_options" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "OPTIONS /modules"
  target    = "integrations/${aws_apigatewayv2_integration.module_integration.id}"
  authorization_type = "NONE"
}

# Rutas para Agencia
resource "aws_apigatewayv2_route" "agency_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /agencies"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "agency_by_id_route_get" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "GET /agencies/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "agency_route_post" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "POST /agencies"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "agency_by_id_route_put" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "PUT /agencies/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "agency_by_id_route_delete" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "DELETE /agencies/{id}"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

resource "aws_apigatewayv2_route" "agency_route_options" {
  api_id    = aws_apigatewayv2_api.main_api.id
  route_key = "OPTIONS /agencies"
  target    = "integrations/${aws_apigatewayv2_integration.agency_integration.id}"
  authorization_type = "NONE"
}

# Permisos de ejecución
resource "aws_lambda_permission" "allow_apigw_invoke_user" {
  statement_id  = "AllowExecutionFromAPIGatewayUser"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.user_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "allow_apigw_invoke_service" {
  statement_id  = "AllowExecutionFromAPIGatewayService"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.service_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "allow_apigw_invoke_code" {
  statement_id  = "AllowExecutionFromAPIGatewayCode"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.code_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "allow_apigw_invoke_module" {
  statement_id  = "AllowExecutionFromAPIGatewayModule"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.module_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}

resource "aws_lambda_permission" "allow_apigw_invoke_agency" {
  statement_id  = "AllowExecutionFromAPIGatewayAgency"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.agency_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.main_api.execution_arn}/*/*"
}
resource "aws_cloudwatch_log_group" "apigw_logs" {
  # checkov:skip=CKV_AWS_158 reason: no se requiere cifrado KMS en entorno de desarrollo
  name              = "/aws/apigateway/v2"
  retention_in_days = 365
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.main_api.id
  name        = "$default"
  auto_deploy = true

    access_log_settings {
    destination_arn = aws_cloudwatch_log_group.apigw_logs.arn
    format = jsonencode({
      requestId               = "$context.requestId",
      ip                      = "$context.identity.sourceIp",
      requestTime             = "$context.requestTime",
      httpMethod              = "$context.httpMethod",
      routeKey                = "$context.routeKey",
      status                  = "$context.status",
      protocol                = "$context.protocol",
      responseLength          = "$context.responseLength"
    })
  }

}
