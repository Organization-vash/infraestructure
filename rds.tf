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

resource "aws_db_instance" "postgres" {
  identifier              = "entel-postgres"
  engine                  = "postgres"
  engine_version          = "16.7"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  storage_type            = "gp2"
  username                = "entelupao"
  password                = "entelupao"  # ⚠️ Usa Secrets Manager en producción
  db_subnet_group_name    = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids  = [aws_security_group.rds_sg.id]
  skip_final_snapshot     = true
  publicly_accessible     = false   # 👈 importante: solo accesible dentro de VPC privada
  multi_az                = false   # opcional para ambientes no productivos

  tags = {
    Name = "Entel Postgres RDS"
  }
}