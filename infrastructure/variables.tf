variable "cloudflare_api_token" {
  type = string
}

variable "cloudflare_zone_id" {
  type = string
}

variable "client_id" {
    type = string
}

variable "client_secret" {
    type = string
}

variable "keypair_id" {
    type = string
}

variable "mongo_username" {
    type = string
}

variable "mongo_password" {
    type = string
}

variable "mongo_host" {
    type = string
}

variable "mongo_db" {
    type = string
}

variable "audiences" {
  type = list(any)
}