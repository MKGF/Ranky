resource "aws_vpc" "ranky-vpc" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_subnet" "ranky-subnet" {
  vpc_id                  = aws_vpc.ranky-vpc.id 
  cidr_block              = "10.0.1.0/24" 
  availability_zone       = "eu-west-3a" 
}

resource "aws_internet_gateway" "example" {
  vpc_id = aws_vpc.ranky-vpc.id
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.ranky-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.example.id
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.ranky-subnet.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "ranky-sg" {
  name        = "ranky-sg"
  description = "Security group for container traffic"

  vpc_id = aws_vpc.ranky-vpc.id  # ID de tu VPC

  
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }


  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}