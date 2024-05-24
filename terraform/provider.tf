#####  Set Terraform  #####
locals {
  region = "eu-west-3"
}
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
  access_key = "AKIAZI2LGPFKF4B2OSVW"
  secret_key = "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"
}

resource "aws_vpc" "ranky-vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "ranky-test-vpc"
  }  
}
