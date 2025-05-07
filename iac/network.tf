resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  tags = {
    Name = "entel-vpc"
  }
}

resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "entel-gw"
  }
}

resource "aws_subnet" "private_1" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.11.0/24"
  availability_zone = "us-east-1a"

  tags = {
    Name = "entel-private-1"
  }
}

resource "aws_subnet" "private_2" {
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.12.0/24"
  availability_zone = "us-east-1b"

  tags = {
    Name = "entel-private-2"
  }
}

resource "aws_subnet" "nat_subnet" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = "10.0.100.0/24"
  availability_zone       = "us-east-1a"
  map_public_ip_on_launch = true

  tags = {
    Name = "nat-subnet"
  }
}

resource "aws_route_table" "nat_subnet_rt" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "nat-subnet-rt"
  }
}

resource "aws_route_table_association" "nat_subnet_assoc" {
  subnet_id      = aws_subnet.nat_subnet.id
  route_table_id = aws_route_table.nat_subnet_rt.id
}

resource "aws_route_table" "private_no_nat_rt" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "entel-private-no-nat"
  }
}

resource "aws_route_table_association" "private_1_no_nat_assoc" {
  subnet_id      = aws_subnet.private_1.id
  route_table_id = aws_route_table.private_no_nat_rt.id
}

resource "aws_route_table_association" "private_2_no_nat_assoc" {
  subnet_id      = aws_subnet.private_2.id
  route_table_id = aws_route_table.private_no_nat_rt.id
}
