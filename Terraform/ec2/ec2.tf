resource "aws_security_group" "workshop" {
  name        = "workshop"
  description = "Example security group"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 81
    to_port     = 81
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "workshop" {
  ami                         = "ami-0c94855ba95c71c99"
  instance_type               = "t2.micro"
  associate_public_ip_address = true
  vpc_security_group_ids      = [aws_security_group.workshop.id]

  provisioner "remote-exec" {
    inline = [
      "sudo yum install -y docker",
      "sudo systemctl start docker"
    ]
    connection {
      user        = "ec2-user"
      host        = aws_instance.workshop.public_ip
      private_key = file("../key/workshop.pem")
    }
  }


  key_name = data.aws_key_pair.existing.key_name


  tags = {
    Name = "workshop"
  }
}