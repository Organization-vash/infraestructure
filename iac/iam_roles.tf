data "aws_caller_identity" "current" {}

resource "aws_iam_role" "lambda_role" {
  name = "lambda-execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda_logs_policy" {
  # checkov:skip=CKV_AWS_355 reason="Uso de '*' permitido en entorno de desarrollo para simplificar la configuración de logs"
  # checkov:skip=CKV_AWS_290 reason="Acciones de escritura necesarias sin restricciones adicionales en desarrollo; se aplicarán restricciones en producción"
  name = "lambda-logs-policy"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_rds_access" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}

resource "aws_iam_role_policy" "lambda_vpc_permissions" {
  # checkov:skip=CKV_AWS_355 reason="Se permite '*' para acciones de red en entorno de desarrollo por flexibilidad durante pruebas"
  # checkov:skip=CKV_AWS_290 reason="Permisos amplios de EC2 aceptados temporalmente en entorno no productivo"
  name = "lambda-vpc-permissions"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ec2:CreateNetworkInterface",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DeleteNetworkInterface"
        ],
        Resource = "*"
      }
    ]
  })
}
