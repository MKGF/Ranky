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
  
  depends_on = [ 
    aws_ecr_repository.ranky-repo,
    aws_iam_role.ecsTaskExecutionRole 
  ]
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

  depends_on = [ 
    aws_ecs_cluster.ranky-cluster,
    aws_ecs_task_definition.ranky-task-definition,
    aws_subnet.ranky-subnet,
    aws_security_group.ranky-sg 
  ]
}

resource "aws_launch_configuration" "ranky-ecs-launch-config" {
  name                 = "ranky-ecs-launch-config1"
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

  depends_on = [ 
    aws_security_group.ranky-sg,
    aws_key_pair.ranky-ec2-keypair,
    aws_ecs_cluster.ranky-cluster
  ]
}

resource "aws_autoscaling_group" "ranky-ecs-autoscaling-group" {
  name                 = "ranky-ecs-autoscaling-group"
  launch_configuration = aws_launch_configuration.ranky-ecs-launch-config.id
  min_size             = 1  # Número mínimo de instancias EC2
  max_size             = 1  # Número máximo de instancias EC2
  desired_capacity     = 1  # Capacidad deseada de instancias EC2
  vpc_zone_identifier  = [aws_subnet.ranky-subnet.id]  # Subred donde se lanzarán las instancias

  depends_on = [ 
    aws_launch_configuration.ranky-ecs-launch-config ,
    aws_subnet.ranky-subnet
  ]
}