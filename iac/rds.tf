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
  identifier              = "entel-postgres"
  engine                  = "postgres"
  engine_version          = "16.7"
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

  parameter_group_name         = aws_db_parameter_group.postgres_logging.name
  enabled_cloudwatch_logs_exports = ["postgresql"]

  copy_tags_to_snapshot             = true

  tags = {
    Name = "Entel Postgres RDS"
  }
}
