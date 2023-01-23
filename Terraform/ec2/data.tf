# Using existing key from the previous creation
data "aws_key_pair" "existing" {
  key_name = "workshop"
}