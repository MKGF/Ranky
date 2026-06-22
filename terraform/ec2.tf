resource "aws_instance" "ranky-ec2" {
  ami           = "ami-0175d4f2509d1d9e8"
  instance_type = "t2.micro"
  subnet_id = aws_subnet.ranky-subnet.id
  vpc_security_group_ids = [aws_security_group.ranky-sg-ec2.id]
  key_name      = aws_key_pair.ranky-ec2-keypair.key_name

  user_data = <<-EOF
#!/bin/bash
set -e

yum update -y

# Docker
yum install -y docker
systemctl enable docker
systemctl start docker

sleep 5

# AWS CLI
yum install -y awscli

# NGINX
yum install -y nginx
systemctl enable nginx
systemctl start nginx

# Certbot
sleep 20
yum install -y certbot python3-certbot-nginx

# ECR login
aws ecr get-login-password --region eu-west-2 \
| docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo

# Pull image
docker pull ${AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo:latest

# Run container SOLO LOCAL
docker run -d \
  --name ranky-app \
  -p 127.0.0.1:8080:8080 \
  --restart always \
  ${AWS_ACCOUNT_ID}.dkr.ecr.eu-west-2.amazonaws.com/ranky-repo:latest

# NGINX reverse proxy
cat > /etc/nginx/conf.d/ranky.conf <<'EOF'
server {
    listen 80;
    server_name api.ranky.top;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
EOF

nginx -t && systemctl restart nginx

# Certbot (HTTPS)
certbot --nginx -d api.ranky.top --non-interactive --agree-tos -m mikel.garin@ranky.top
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
