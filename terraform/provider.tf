#####  Set Terraform  #####

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

#####  Configuring the AWS Provider and access keys  #####

provider "aws" {
  region = "${local.region}"
}

resource "aws_launch_configuration" "ecs_launch_configuration" {
  name                 = "ranky-ecs-launch-config"
  image_id             = "ami-0288d9523e7053c23"  # AMI de Amazon Linux 2 u otra AMI que desees
  instance_type        = "t2.micro"  # Tipo de instancia
  security_groups      = [aws_security_group.ranky-sg.id] # Grupo de seguridad que permite tráfico de contenedores
  key_name             = aws_key_pair.ranky-ec2-keypair.key_name  # Nombre del par de claves
  associate_public_ip_address = true
  user_data = <<-EOF
    #!/bin/bash
    echo ECS_CLUSTER=${aws_ecs_cluster.ranky-cluster.name} >> /etc/ecs/ecs.config
    start ecs
  EOF
}

resource "aws_autoscaling_group" "ecs_autoscaling_group" {
  name                 = "ecs-autoscaling-group"
  launch_configuration = aws_launch_configuration.ecs_launch_configuration.id
  min_size             = 1  # Número mínimo de instancias EC2
  max_size             = 1  # Número máximo de instancias EC2
  desired_capacity     = 1  # Capacidad deseada de instancias EC2
  vpc_zone_identifier  = [aws_subnet.ranky-subnet.id]  # Subred donde se lanzarán las instancias
}

resource "null_resource" "docker_build_and_push" {
  provisioner "local-exec" {
    command = <<-EOT
      docker build -t ranky-image .
      
      docker tag ranky-image:latest ${aws_ecr_repository.ranky-repo.repository_url}:latest
      
      docker push ${aws_ecr_repository.ranky-repo.repository_url}:latest
    EOT
  }
    depends_on = [aws_ecr_repository.ranky-repo]
}

resource "aws_ecs_cluster" "ranky-cluster" {
  name = "ranky-cluster"
}

resource "aws_ecs_task_definition" "ranky-task-definition" {
  family                   = "ranky-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  container_definitions    = jsonencode([
    
      {
        "name": "ranky-container",
        "image": "${aws_ecr_repository.ranky-repo.repository_url}:latest",
        "cpu": 256,
        "memory": 512,
        "portMappings": [
          {
            "containerPort": 80,
            "hostPort": 80,
            "protocol": "tcp"
          }
        ],
      }
    ])
      execution_role_arn = aws_iam_role.ecsTaskExecutionRole.arn
}

resource "aws_ecs_service" "ranky_service" {
  name            = "ranky-service"
  cluster         = aws_ecs_cluster.ranky-cluster.id
  task_definition = aws_ecs_task_definition.ranky-task-definition.arn
  desired_count = 3
  launch_type = "FARGATE"
  
  network_configuration {
    subnets = [aws_subnet.ranky-subnet.id]  # Especifica la subred donde se ejecutará el servicio
    security_groups = [aws_security_group.ranky-sg.id]
    assign_public_ip = true
  }
}