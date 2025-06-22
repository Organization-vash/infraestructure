resource "aws_security_group" "rds_sg" {
  # checkov:skip=CKV_AWS_382: Se permite egress global por simplicidad en entorno de desarrollo
  name        = "rds-sg"
  description = "Permite conexiones desde Lambda"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.lambda_sg.id]
    description     = "Permitir acceso a RDS desde Lambda"
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Permite salida total (considerar restringir)"
  }

  tags = {
    Name = "rds-sg"
  }
}

resource "aws_security_group" "lambda_sg" {
  # checkov:skip=CKV_AWS_382: Se permite egress global por simplicidad en entorno de desarrollo
  name        = "lambda-sg"
  description = "Permite a Lambda acceder a RDS"
  vpc_id      = aws_vpc.main.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Permite salida total desde Lambda"
  }

  tags = {
    Name = "lambda-sg"
  }
}
