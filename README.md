#  Sistema de Gestión de Códigos de Atención para ENTEL

### 👥 Integrantes

- Angel Stefano Yepez Zapata  
- Hernán Mauricio Gastón Berrospi Reyes  
- Oscar Segundo Llaure Calipuy  
- Sergio Emanuel Velásquez Reyes  
- Vania Melissa Ramos Cotrina

##  Descripción general

Este proyecto despliega una infraestructura automatizada sobre AWS para un sistema de gestión de colas de atención al cliente para ENTEL – Trujillo. El objetivo es mejorar la eficiencia, trazabilidad y organización del proceso de atención presencial mediante la generación, asignación y control de tickets.

El proyecto ha sido desarrollado aplicando **Infraestructura como Código (IaC)** con Terraform, y está diseñado bajo una arquitectura desacoplada en múltiples capas, aprovechando servicios serverless, bases de datos en la nube, automatización CI/CD y un enfoque seguro con redes privadas.

---

## 🛠️ Tecnologías utilizadas y versiones recomendadas

| Tecnología                     | Descripción                                              | Versión   |
|-------------------------------|----------------------------------------------------------|--------------------------|
| Terraform                     | Automatización de infraestructura                       |  1.6.0                 |
| AWS CLI                       | Gestión de recursos AWS desde línea de comandos         |  2.13.0                |
| AWS Provider para Terraform   | Proveedor oficial para Terraform                        |  5.0                   |
| AWS S3                        | Almacenamiento de frontend y artefactos Lambda          | -                        |
| AWS CloudFront                | Distribución global del frontend                        | -                        |
| AWS API Gateway               | Enrutamiento HTTP hacia funciones Lambda                | -                        |
| AWS Lambda                    | Backend serverless (Java 17)                            | Java 17                  |
| Amazon RDS (PostgreSQL)       | Base de datos relacional gestionada                    | PostgreSQL 16.7          |
| Spring Boot                   | Framework backend Java                                  | -                        |
| Maven                         | Sistema de construcción para Java                      |  3.8                   |
| Angular / Angular CLI         | Frontend web responsivo                                 | Angular 18 / CLI ^18.0.0 |
| Node.js                       | Entorno de ejecución para Angular                      |  18                    |
| Amazon VPC + Security Groups  | Aislamiento de red y control de acceso                  | -                        |

> ⚠️ Puedes verificar las versiones con los comandos: `terraform -version`, `aws --version`, `java -version`, `mvn -v`, `node -v`, `ng version`

---

## 🔄  Flujo del sistema

1. **Terraform** levanta toda la infraestructura:
   - Red privada (VPC) y subredes
   - Buckets S3 (frontend y backend)
   - Distribución en CloudFront
   - API Gateway para exponer rutas HTTP
   - Funciones Lambda (por ejemplo: `user-lambda`)
   - Base de datos Amazon RDS (PostgreSQL)
   - Roles IAM, Security Groups, integración de red

2. **El frontend Angular** es cargado en S3 y distribuido globalmente por CloudFront.

3. **El usuario interactúa con el frontend**, que envía solicitudes HTTP a API Gateway.

4. **API Gateway enruta las solicitudes** hacia funciones Lambda que procesan la lógica (registro, atención, consultas, etc.).

5. **Las Lambdas se conectan a Amazon RDS**, realizando operaciones CRUD sobre los datos persistentes.

###  Diagrama del sistema

![DIAGRAMA](https://github.com/user-attachments/assets/7ba87570-ddb5-44b7-9574-20e88d76cd36)

## 🧰 Comandos para Despliegue

A continuación, los principales comandos utilizados para desplegar y destruir la infraestructura:
```bash

# Iniciar Terraform y descargar proveedores
terraform init

# Previsualizar los cambios a aplicar
terraform plan

# Aplicar cambios y desplegar infraestructura
terraform apply

# Destruir todos los recursos provisionados
terraform destroy
```

## 📁 Estructura del Proyecto
```bash
📦 organization-vash-infraestructure/
├── 📄 README.md
├── 📁 iac/
│   ├── 📄 api_gateway.tf
│   ├── 📄 cloudfront.tf
│   ├── 📄 deploy_frontend.tf
│   ├── 📄 iam_roles.tf
│   ├── 📄 lambda_users.tf
│   ├── 📄 main.tf
│   ├── 📄 network.tf
│   ├── 📄 outputs.tf
│   ├── 📄 rds.tf
│   ├── 📄 s3.tf
│   ├── 📄 s3_lambdas.tf
│   ├── 📄 security_group.tf
│   ├── 📄 variables.tf
│   ├── 📄 .gitignore
│   ├── 📁 diagrama/
│   └── 📁 front/
├── 📁 lambda_users/
│   ├── 📄 pom.xml
│   ├── 📄 .gitignore
│   ├── 📁 src/
│   │   └── 📁 main/
│   │       └── 📁 java/
│   │           └── 📁 com/
│   │               └── 📁 vash/
│   │                   ├── 📁 db/
│   │                   │   └── 📄 DatabaseInitializer.java
│   │                   └── 📁 lambda/
│   │                       ├── 📄 UserLambdaHandler.java
│   │                       ├── 📁 model/
│   │                       │   └── 📄 UserDTO.java
│   │                       └── 📁 service/
│   │                           ├── 📄 DatabaseConnection.java
│   │                           └── 📄 UserServiceLambda.java
│   └── 📁 .vscode/
│       └── 📄 settings.json
