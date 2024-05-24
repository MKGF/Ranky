resource "null_resource" "ranky-docker-build-and-push" {
  provisioner "local-exec" {
    command = <<-EOT
      docker build -t ranky-image .
      
      docker tag ranky-image:latest ${aws_ecr_repository.ranky-repo.repository_url}:latest
      
      docker push ${aws_ecr_repository.ranky-repo.repository_url}:latest
    EOT
  }

    depends_on = [
        aws_ecr_repository.ranky-repo
    ]
}