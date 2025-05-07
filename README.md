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

## 🛠️ Tecnologías utilizadas

- **Terraform** – Automatización de infraestructura
- **AWS S3** – Almacenamiento de frontend y artefactos Lambda
- **AWS CloudFront** – Distribución global del frontend
- **AWS API Gateway** – Enrutamiento de peticiones HTTP hacia funciones Lambda
- **AWS Lambda (Java 17)** – Backend serverless con lógica de negocio
- **Amazon RDS (PostgreSQL)** – Persistencia relacional de datos
- **Spring Boot** – Backend modular en Java para lógica empresarial
- **Angular 18** – Frontend web responsivo
- **Amazon VPC + Security Groups** – Aislamiento de red y control de acceso

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



## 📁 Estructura del Proyecto
```markdown

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
