output "api_gateway_url" {
  description = "URL pública del API Gateway principal"
  value       = aws_apigatewayv2_api.main_api.api_endpoint
}

output "user_lambda_endpoint" {
  description = "URL completa del endpoint de la Lambda User"
  value       = "${aws_apigatewayv2_api.main_api.api_endpoint}/users"
}
