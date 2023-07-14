resource "aws_iam_access_key" "bifrost_api_user_key" {
  user = aws_iam_user.bifrost_api_user.name
}

resource "aws_iam_user" "bifrost_api_user" {
  name = "bifrost-api"
}

resource "aws_secretsmanager_secret" "bifrost_api_user_key_secret" {
  name = "BifrostApiIamUser"
}

resource "aws_secretsmanager_secret_version" "bifrost_api_user_key_secret_version" {
  secret_id     = aws_secretsmanager_secret.bifrost_api_user_key_secret.id
  secret_string = jsonencode({ "AccessKeyId" : aws_iam_access_key.bifrost_api_user_key.id, "SecretAccessKey" : aws_iam_access_key.bifrost_api_user_key.secret })
}

resource "aws_iam_policy" "bifrost_api_user_sts_policy" {
  name        = "AllowAssumeRole"
  description = "Allows the user to assume any role anywhere"
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : "sts:AssumeRole",
        "Resource" : "*"
      }
    ]
  })
}

resource "aws_iam_user_policy_attachment" "bifrost_api_user_sts_policy_attachment" {
  user       = aws_iam_user.bifrost_api_user.name
  policy_arn = aws_iam_policy.bifrost_api_user_sts_policy.arn
}