resource "aws_vpc" "ranky-vpc" {
  cidr_block = "10.0.0.0/16" 
  tags = {
    Name = "ranky-vpc"
  }
}

resource "aws_subnet" "ranky-subnet" {
  vpc_id                  = aws_vpc.ranky-vpc.id 
  cidr_block              = "10.0.1.0/24" 
  availability_zone       = "eu-west-3a" 
  map_public_ip_on_launch = true

  depends_on = [ 
    aws_vpc.ranky-vpc 
  ]

  tags = {
    Name = "ranky-subnet"
  }
}

resource "aws_internet_gateway" "ranky-internet-gateway" {
  vpc_id = aws_vpc.ranky-vpc.id

  depends_on = [ aws_vpc.ranky-vpc ]

  tags = {
    Name = "ranky-internet-gateway"
  }
}

resource "aws_route_table" "ranky-public-rt" {
  vpc_id = aws_vpc.ranky-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.ranky-internet-gateway.id
  }

  depends_on =  [
    aws_vpc.ranky-vpc,
    aws_internet_gateway.ranky-internet-gateway
  
  ]

  tags = {
    Name = "ranky-public-rt"
  }
}

resource "aws_route_table_association" "ranky-public-ta" {
  subnet_id      = aws_subnet.ranky-subnet.id
  route_table_id = aws_route_table.ranky-public-rt.id

  depends_on =  [
    aws_subnet.ranky-subnet,
    aws_route_table.ranky-public-rt
  ]
}

resource "aws_security_group" "ranky-sg-ec2" {
  name        = "ranky-sg"
  description = "Security group for ranky ec2"

  vpc_id = aws_vpc.ranky-vpc.id 

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
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

  depends_on = [ 
    aws_vpc.ranky-vpc
  ]
}