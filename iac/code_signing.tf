resource "aws_signer_signing_profile" "lambda_signing_profile" {
  name = "lambda-signing-profile"
  platform_id = "AWSLambda-SHA384-ECDSA"
}

resource "aws_lambda_code_signing_config" "lambda_csc" {
  allowed_publishers {
    signing_profile_version_arns = [aws_signer_signing_profile.lambda_signing_profile.arn]
  }

  policies {
    untrusted_artifact_on_deployment = "Enforce"
  }
}
