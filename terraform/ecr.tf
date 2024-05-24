resource "aws_ecr_repository" "ranky-repo" {
  name = "ranky-repo" 
  force_delete = true
}