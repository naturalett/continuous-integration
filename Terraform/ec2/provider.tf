provider "aws" {
  region                  = "us-east-1"
  profile                 = "default"
  shared_credentials_file = "~/.aws/credentials"
}

terraform {
  backend "local" {
    path = "../workshop/ec2/terraform.tfstate"
  }
}
