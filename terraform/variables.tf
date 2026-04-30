variable "project_id" {
  type        = string
  description = "L'ID unique de ton projet GCP"
}

variable "region" {
  type    = string
  default = "europe-west1"
}

variable "environment" {
  type    = string
  default = "dev"
}