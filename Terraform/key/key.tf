resource "tls_private_key" "workshop" {
  algorithm = "RSA"
}

resource "aws_key_pair" "workshop" {
  key_name   = "workshop"
  public_key = tls_private_key.workshop.public_key_openssh
}

resource "local_file" "private_key" {
  filename = "./workshop.pem"
  content  = tls_private_key.workshop.private_key_pem
}