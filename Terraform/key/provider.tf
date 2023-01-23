provider "aws" {
  region                  = "us-east-1"
  profile                 = "devops"
  shared_credentials_file = "~/.aws/credentials"
}

terraform {
  backend "local" {
    path = "../workshop/key/terraform.tfstate"
  }
}