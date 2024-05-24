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