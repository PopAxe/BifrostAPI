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
      aws_secretsmanager_secret.bifrost_api_user_key_secret.arn
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

data "archive_file" "bifrost_api_zip" {
  type        = "zip"
  source_file = "../bifrost-api"
  output_path = "bifrost-api.zip"
}

resource "aws_lambda_function" "bifrost_api_lambda" {
  filename      = data.archive_file.bifrost_api_zip.output_path
  function_name = "BifrostApi"
  role          = aws_iam_role.bifrost_api_lambda_role.arn
  handler       = "bifrost-api"
  memory_size   = 256

  source_code_hash = data.archive_file.bifrost_api_zip.output_base64sha256

  runtime = "go1.x"

  environment {
    variables = {
      STAGE           = "prod",
      COGNITO_POOL_ID = var.cognito_user_pool_id,
      COGNITO_REGION  = var.cognito_region
    }
  }
}