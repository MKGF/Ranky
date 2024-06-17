resource "tls_private_key" "ranky-ec2-keypair" {
  algorithm = "RSA"
  rsa_bits  = 2048
}

resource "aws_key_pair" "ranky-ec2-keypair" {
  key_name   = "ranky-ec2-keypair"
  public_key = tls_private_key.ranky-ec2-keypair.public_key_openssh
}

resource "local_file" "private_key_file" {
  filename = "ranky-ec2-ssh.pem"
  content = tls_private_key.ranky-ec2-keypair.private_key_pem
}

resource "aws_iam_role" "ranky-ec2-role" {
  name               = "ranky-ec2-ecr-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect    = "Allow",
      Principal = {
        Service = "ec2.amazonaws.com"
      },
      Action    = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_policy_attachment" "ranky-ec2-ecr-policy-attachment" {
  name       = "ec2-ecr-policy-attachment"
  roles      = [aws_iam_role.ranky-ec2-role.name]
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}