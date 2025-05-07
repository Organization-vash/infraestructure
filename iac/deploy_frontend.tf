resource "null_resource" "deploy_frontend" {
  provisioner "local-exec" {
    command = <<EOT
      if [ -d "./front" ]; then
        rm -rf ./front
      fi

      git clone https://github.com/Organization-vash/front.git

      cd front
      npm install
      npm run build -- --configuration production --project gestion-servicio-frontend

      aws s3 sync ./dist/gestion-servicio-frontend/browser/ s3://${aws_s3_bucket.frontend.bucket} --delete
    EOT

    interpreter = ["bash", "-c"]
  }

  depends_on = [aws_s3_bucket_policy.only_cloudfront]
}
