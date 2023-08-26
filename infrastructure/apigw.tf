resource "aws_apigatewayv2_api" "bifrost_api" {
  name          = "BifrostApi"
  protocol_type = "HTTP"
  target        = aws_lambda_function.bifrost_api_lambda.arn
}

resource "aws_apigatewayv2_domain_name" "bifrost_api_domain_name" {
  domain_name = "bifrost.gtech.dev"

  domain_name_configuration {
    certificate_arn = aws_acm_certificate_validation.api_cert_validation.certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
  depends_on = [aws_acm_certificate_validation.api_cert_validation]
}

resource "aws_apigatewayv2_api_mapping" "bifrost_api_domain_mapping" {
  api_id      = aws_apigatewayv2_api.bifrost_api.id
  domain_name = aws_apigatewayv2_domain_name.bifrost_api_domain_name.id
  stage       = "$default"
}

resource "aws_lambda_permission" "api" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.bifrost_api_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.bifrost_api.execution_arn}/*/*"
}