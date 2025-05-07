output "api_gateway_url" {
  description = "URL pública del API Gateway principal"
  value       = aws_apigatewayv2_api.main_api.api_endpoint
}

output "cloudfront_url" {
  description = "URL pública de la distribución CloudFront"
  value       = "https://${aws_cloudfront_distribution.frontend_distribution.domain_name}"
}

output "user_lambda_endpoint" {
  description = "URL completa del endpoint de la Lambda User"
  value       = "${aws_apigatewayv2_api.main_api.api_endpoint}/users"
}

output "service_lambda_endpoint" {
  description = "URL completa del endpoint de la Lambda Service"
  value       = "${aws_apigatewayv2_api.main_api.api_endpoint}/services"
}