resource "aws_ecr_repository" "ranky-repo" {
  name = "ranky-repo" 
  image_tag_mutability = "MUTABLE"
  force_delete = true
  image_scanning_configuration {
    scan_on_push = true
  }
}