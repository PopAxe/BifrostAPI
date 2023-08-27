data "aws_iam_policy_document" "bifrost_api_lambda_assume_role_policy_document" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

/*
This will grant access to the IAM secret information from the lambda
*/
data "aws_iam_policy_document" "bifrost_api_secretsmanager_policy_document" {
  policy_id = "bifrost-api-lambda-secretsmanager"
  version   = "2012-10-17"
  statement {
    effect  = "Allow"
    actions = ["secretsmanager:GetSecretValue"]

    resources = [
      "*"
    ]
  }
}

resource "aws_iam_policy" "bifrost_api_secretsmanager" {
  name   = "bifrost-api-lambda-secretsmanager"
  policy = data.aws_iam_policy_document.bifrost_api_secretsmanager_policy_document.json
}

resource "aws_iam_role_policy_attachment" "bifrost_api_secretsmanager_policy_attachment" {
  depends_on = [aws_iam_role.bifrost_api_lambda_role, aws_iam_policy.bifrost_api_secretsmanager]
  role       = aws_iam_role.bifrost_api_lambda_role.name
  policy_arn = aws_iam_policy.bifrost_api_secretsmanager.arn
}

// Logs Policy
resource "aws_iam_role_policy_attachment" "bifrost_api_lambda_default_policy_attachment" {
  depends_on = [aws_iam_role.bifrost_api_lambda_role]
  role       = aws_iam_role.bifrost_api_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role" "bifrost_api_lambda_role" {
  name               = "BifrostApiLambda"
  assume_role_policy = data.aws_iam_policy_document.bifrost_api_lambda_assume_role_policy_document.json
}

resource "aws_s3_bucket" "bifrost_api" {
  bucket = "bifrost-api"
}

resource "aws_s3_bucket_server_side_encryption_configuration" "bifrost_api_bucket_sse" {
  bucket = aws_s3_bucket.bifrost_api.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "aws:kms"
    }
  }
}

resource "aws_s3_object" "bifrost_code" {
  bucket = aws_s3_bucket.bifrost_api.bucket
  key    = "bifrost-api.zip"
  source = "../build/distributions/bifrost-api-1.0.0.zip"
  etag   = filesha256("../build/distributions/bifrost-api-1.0.0.zip")
}

resource "aws_lambda_function" "bifrost_api_lambda" {
  s3_bucket     = aws_s3_bucket.bifrost_api.arn
  s3_key        = aws_s3_object.bifrost_code.key
  function_name = "BifrostApi"
  role          = aws_iam_role.bifrost_api_lambda_role.arn
  handler       = "dev.gtech.bifrost.bifrostapi.BifrostApiLambdaApplication"
  memory_size   = 256

  source_code_hash = aws_s3_object.bifrost_code.source_hash

  runtime = "java17"

  environment {
    variables = {
      ISSUER_URI     = var.issuer_uri,
      CLIENT_ID      = var.client_id,
      CLIENT_SECRET  = var.client_secret,
      KEYPAIR_ID     = var.keypair_id,
      MONGO_USERNAME = var.mongo_username,
      MONGO_PASSWORD = var.mongo_password,
      MONGO_HOST     = var.mongo_host,
      MONGO_DB       = var.mongo_db
    }
  }
}

resource "aws_lambda_alias" "lambda_alias" {
  depends_on       = [aws_lambda_function.bifrost_api_lambda]
  name             = "BifrostLambdaAlias"
  function_name    = aws_lambda_function.bifrost_api_lambda.arn
  function_version = "1"
}

resource "aws_lambda_provisioned_concurrency_config" "lambda_provisioned_concurrency_config" {
  function_name                     = aws_lambda_function.bifrost_api_lambda.arn
  provisioned_concurrent_executions = 2
  qualifier                         = aws_lambda_alias.lambda_alias.name
}