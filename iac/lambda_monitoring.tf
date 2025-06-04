#########################################
# MONITOREO DE LAMBDAS CON CLOUDWATCH
# Incluye:
# - Filtros de log para "ERROR"
# - Alarmas si hay muchos errores
# - Filtro para contar métodos HTTP
# - Filtro para nivel WARN
#########################################

# Lista de funciones Lambda
locals {
  lambda_names = [
    "user-lambda",
    "service-lambda",
    "agency-lambda",
    "module-lambda",
    "code-lambda"
  ]
}

resource "aws_cloudwatch_log_metric_filter" "error_filters" {
  for_each       = toset(local.lambda_names)
  name           = "${each.key}-error-filter"
  log_group_name = "/aws/lambda/${each.key}"
  pattern        = "{ $.level = \"ERROR\" }"

  metric_transformation {
    name      = "${each.key}-error-count"
    namespace = "LambdaMonitoring"
    value     = "1"
  }
}

resource "aws_cloudwatch_metric_alarm" "error_alarms" {
  for_each            = aws_cloudwatch_log_metric_filter.error_filters
  alarm_name          = "${each.key}-error-alarm"
  metric_name         = each.value.metric_transformation[0].name
  namespace           = each.value.metric_transformation[0].namespace
  statistic           = "Sum"
  period              = 60
  evaluation_periods  = 1
  threshold           = 1
  comparison_operator = "GreaterThanOrEqualToThreshold"
  alarm_description   = "Más de 2 errores en 1 minuto en ${each.key}"
}

resource "aws_cloudwatch_log_metric_filter" "warn_filters" {
  for_each       = toset(local.lambda_names)
  name           = "${each.key}-warn-filter"
  log_group_name = "/aws/lambda/${each.key}"
  pattern        = "{ $.level = \"WARN\" }"

  metric_transformation {
    name      = "${each.key}-warn-count"
    namespace = "LambdaMonitoring"
    value     = "1"
  }
}

resource "aws_cloudwatch_log_metric_filter" "http_method_filters" {
  for_each       = toset(local.lambda_names)
  name           = "${each.key}-http-method-filter"
  log_group_name = "/aws/lambda/${each.key}"
  pattern        = "{ $.httpMethod = * }"

  metric_transformation {
    name      = "${each.key}-http-method-usage"
    namespace = "LambdaMonitoring"
    value     = "1"
  }
}

resource "aws_cloudwatch_dashboard" "lambda_dashboard" {
  dashboard_name = "LambdaInsights"

  dashboard_body = jsonencode({
    widgets = [
      {
        "type" : "text",
        "x" : 0,
        "y" : 0,
        "width" : 24,
        "height" : 1,
        "properties" : {
          "markdown" : "# 🔍 Panel de Métricas de Lambdas"
        }
      },
      {
        "type" : "metric",
        "x" : 0,
        "y" : 1,
        "width" : 12,
        "height" : 6,
        "properties" : {
          "metrics" : [
            ["LambdaMonitoring", "user-lambda-error-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "service-lambda-error-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "agency-lambda-error-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "module-lambda-error-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "code-lambda-error-count", { "stat" : "Sum" }]
          ],
          "title" : "❌ Errores por Lambda",
          "view" : "timeSeries",
          "stacked" : false
        }
      },
      {
        "type" : "metric",
        "x" : 12,
        "y" : 1,
        "width" : 12,
        "height" : 6,
        "properties" : {
          "metrics" : [
            ["LambdaMonitoring", "user-lambda-warn-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "service-lambda-warn-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "agency-lambda-warn-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "module-lambda-warn-count", { "stat" : "Sum" }],
            ["LambdaMonitoring", "code-lambda-warn-count", { "stat" : "Sum" }]
          ],
          "title" : "⚠️ WARN por Lambda",
          "view" : "timeSeries",
          "stacked" : false
        }
      },
      {
        "type" : "metric",
        "x" : 0,
        "y" : 7,
        "width" : 24,
        "height" : 6,
        "properties" : {
          "metrics" : [
            ["LambdaMonitoring", "HttpMethodUsage", { "stat" : "Sum" }]
          ],
          "title" : "📊 Uso de métodos HTTP (user-lambda)",
          "view" : "timeSeries",
          "stacked" : false
        }
      }
    ]
  })
}
