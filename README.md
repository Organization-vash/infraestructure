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

![Flujo del sistema](/iac/diagrama/DIAGRAMA.png)


## 📁 Estructura del Proyecto
```markdown

📁 raíz del proyecto  
├── 📄 main.tf — orquestador principal  
├── 📄 network.tf — definición de red VPC y subredes  
├── 📄 security_group.tf — configuración de SGs para Lambda y RDS  
├── 📄 api_gateway.tf — definición de endpoints HTTP  
├── 📄 cloudfront.tf — distribución del frontend  
├── 📄 s3.tf — bucket para frontend estático  
├── 📄 s3_lambdas.tf — bucket para despliegue de JARs  
├── 📄 iam_roles.tf — roles de ejecución para Lambda  
├── 📄 lambda_users.tf — función Lambda de usuarios  
├── 📄 rds.tf — configuración de base de datos PostgreSQL  
├── 📄 deploy_frontend.tf — despliegue automático del frontend  
├── 📄 outputs.tf — exportación de valores clave  
├── 📄 variables.tf — parámetros reutilizables  
├── 📄 terraform.tfstate  
├── 📄 terraform.tfstate.backup  
├── 📄 README.md  
├── 📄 estructura.txt — vista generada del árbol de carpetas  

📁 lambda_users  
├── 📄 pom.xml — configuración Maven del proyecto  
└── 📁 src  
    └── 📁 main  
        └── 🧠 Lógica backend en Java (Spring Boot para Lambda)  

📁 front (Frontend Angular 18)  
├── 📄 angular.json — configuración del proyecto  
├── 📄 package.json / lock — dependencias  
├── 📄 tsconfig*.json — configuración de compilación  
├── 📄 proxy.conf.json — proxy para desarrollo  
├── 📁 dist  
│   └── 📁 gestion-servicio-frontend — build de producción  
├── 📁 public  
│   ├── 🖼️ Entel_logo_pe.png  
│   ├── 🖼️ chat-box.jpg  
│   ├── 🖼️ entellogo.png  
│   ├── 🖼️ favicon.ico  
│   └── 🖼️ logo_entel.jpg  
└── 📁 src  
    ├── 📁 app — módulos y componentes Angular  
    ├── 📄 index.html  
    ├── 📄 main.ts  
    └── 📄 styles.css  