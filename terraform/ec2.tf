resource "aws_instance" "ranky-ec2" {
  ami           = "ami-0175d4f2509d1d9e8"
  instance_type = "t2.micro"
  subnet_id = aws_subnet.ranky-subnet.id
  vpc_security_group_ids = [aws_security_group.ranky-sg-ec2.id]
  key_name      = aws_key_pair.ranky-ec2-keypair.key_name

  user_data = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install docker -y
              sudo systemctl start docker
              sudo systemctl enable docker

              # Autenticarse con ECR
              aws ecr get-login-password --region eu-west-2 | sudo docker login --username AWS --password-stdin ${var.AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo


              # Tirar la imagen desde ECR
              sudo docker pull ${var.AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo:latest

              # Ejecutar la imagen
              sudo docker run -d -p 8080:8080 --name ranky-app ${var.AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo:latest
              EOF

  iam_instance_profile = aws_iam_instance_profile.ranky-ec2-profile.name

  tags = {
    Name = "ranky-ec2-app"
  }
  depends_on = [ 
    null_resource.ranky-docker-build-and-push,
    aws_security_group.ranky-sg-ec2
  ]
}

resource "null_resource" "ranky-ec2-ip-to-file" {
  depends_on = [aws_instance.ranky-ec2]

  provisioner "local-exec" {
    command = "echo ${aws_instance.ranky-ec2.public_ip} > ranky-ec2-ip.txt"
  }
}

resource "aws_iam_instance_profile" "ranky-ec2-profile" {
  name = "ranky-ec2-ecr-instance-profile"
  role = aws_iam_role.ranky-ec2-role.name
}
