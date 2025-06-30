resource "aws_db_subnet_group" "rds_subnet_group" {
  
  name       = "rds-subnet-group"
  subnet_ids = [
    aws_subnet.private_1.id,
    aws_subnet.private_2.id
  ]

  tags = {
    Name = "RDS Subnet Group"
  }
}

resource "aws_db_parameter_group" "postgres_logging" {
  name        = "entel-postgres-logging"
  family      = "postgres16"
  description = "Enable query logging for Postgres RDS"

  parameter {
    name  = "log_statement"
    value = "all"
  }
  parameter {
    name  = "log_min_duration_statement"
    value = "0"
  }
}

resource "aws_db_instance" "postgres" {
  # checkov:skip=CKV_AWS_157 : Multi-AZ no es necesario para este entorno de desarrollo.
  # checkov:skip=CKV2_AWS_69 reason="Backups no son requeridos en este entorno de desarrollo"
  # checkov:skip=CKV_AWS_118 : Enhanced monitoring no es requerido en desarrollo para evitar costos.
  # checkov:skip=CKV_AWS_353: Performance Insights no es necesario en entorno de desarrollo
  # checkov:skip=CKV_AWS_293 reason="Se permite eliminar esta instancia en entornos de desarrollo"

  identifier              = "entel-postgres"
  engine                  = "postgres"
  engine_version          = "16.9"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  storage_type            = "gp2"
  db_name                 = "entelapp"
  username                = "entelupao"
  password                = "entelupao"
  db_subnet_group_name    = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  skip_final_snapshot     = true
  publicly_accessible     = false
  multi_az                = false

  auto_minor_version_upgrade = true
  iam_database_authentication_enabled = true
  storage_encrypted                      = true

  parameter_group_name         = aws_db_parameter_group.postgres_logging.name
  enabled_cloudwatch_logs_exports = ["postgresql"]

  copy_tags_to_snapshot             = true

  tags = {
    Name = "Entel Postgres RDS"
  }
}
