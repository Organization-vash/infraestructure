
# Sistema de Gestión de Códigos de Atención para ENTEL

### 👥 Integrantes

- Angel Stefano Yepez Zapata  
- Hernán Mauricio Gastón Berrospi Reyes  
- Oscar Segundo Llaure Calipuy  
- Sergio Emanuel Velásquez Reyes  
- Vania Melissa Ramos Cotrina

---

## 📝 Descripción general

Este proyecto despliega una infraestructura automatizada sobre AWS para un sistema de gestión de colas de atención al cliente para ENTEL – Trujillo. El objetivo es mejorar la eficiencia, trazabilidad y organización del proceso de atención presencial mediante la generación, asignación y control de tickets.

El proyecto ha sido desarrollado aplicando **Infraestructura como Código (IaC)** con Terraform, y está diseñado bajo una arquitectura desacoplada en múltiples capas, aprovechando servicios serverless, bases de datos en la nube, automatización CI/CD y un enfoque seguro con redes privadas. A partir de la rama `feature/pipeline-integration-backfront`, el despliegue se realiza completamente a través de **pipelines de Jenkins orquestados con Docker Compose**.

---

## 🛠️ Tecnologías utilizadas y versiones recomendadas

| Tecnología                     | Descripción                                              | Versión recomendada |
|-------------------------------|----------------------------------------------------------|----------------------|
| **Git**                       | Control de versiones, clonación de repositorios          | -                    |
| **Docker / Docker Compose**   | Contenedores y orquestación del entorno Jenkins          | -                    |
| **Jenkins**                   | Automatización CI/CD, ejecución de pipelines             | 2.440+               |
| **Jenkins Job DSL**           | Declaración de pipelines y jobs en Jenkins as code       | -                    |
| **Jenkins Configuration as Code (JCasC)** | Configuración de Jenkins vía YAML                     | -                    |
| **Terraform**                 | Infraestructura como código en AWS                       | 1.6.0 – 1.8.2        |
| **AWS CLI**                   | Interacción con servicios de AWS desde línea de comandos | 2.13.0               |
| **Node.js**                   | Entorno para ejecución y build del frontend Angular      | 18                   |
| **Angular / Angular CLI**     | Framework SPA frontend                                   | Angular 18 / CLI ^18 |
| **Maven**                     | Compilación y empaquetado de Lambdas Java                | 3.8 – 3.9.6          |
| **Java**                      | Desarrollo de Lambdas backend (handler en Java 17)       | Java 17              |
| **PostgreSQL**                | Base de datos relacional utilizada en RDS                | 16.7                 |
| **Spring Boot (parcial)**     | Estructura de servicios backend Java (no usado completo) | -                    |
| **AWS Lambda**                | Backend sin servidor (funciones Java)                    | -                    |
| **AWS API Gateway**           | Enrutamiento HTTP de peticiones a funciones Lambda       | -                    |
| **Amazon S3**                 | Almacenamiento del frontend y artefactos Lambda          | -                    |
| **AWS CloudFront**            | Distribución del frontend a través de CDN                | -                    |
| **Amazon RDS**                | Base de datos PostgreSQL gestionada                      | -                    |
| **Amazon VPC + Security Groups** | Red privada, control de acceso y conectividad segura | -                    |

> 💡 Antes de comenzar, asegúrate de tener instalado **Git**, **Docker**, **Jenkins** y acceso válido a credenciales de AWS.

---

## 🔄 Flujo del sistema

1. **El entorno Jenkins** se inicia en contenedores con Docker Compose.
2. **Seed Job** configura automáticamente los pipelines de despliegue y el Docker Cloud para agentes.
3. **El pipeline `pipeline-entel`** se encarga del despliegue completo:
   - Infraestructura AWS (VPC, RDS, S3, API Gateway, Lambda)
   - Subida del frontend Angular a S3
   - Despliegue de funciones Lambda en Java
4. **El usuario final accede al frontend distribuido por CloudFront**, que envía peticiones al API Gateway.
5. **API Gateway redirige a funciones Lambda**, que ejecutan lógica de negocio y acceden a RDS.

---

## 🖼️ Diagrama del sistema

![DIAGRAMA](https://github.com/user-attachments/assets/7ba87570-ddb5-44b7-9574-20e88d76cd36)

---


## 🐳 Inicialización del Entorno Jenkins

1. **Clonar el repositorio y cambiar a la rama correspondiente:**
   ```bash
   git clone https://github.com/your-org/organization-vash-infrastructure.git
   git checkout feature/pipeline-back-front
   cd jenkins
   ```


2. **Crear un archivo `.env` con tus credenciales de AWS dentro de la carpeta `jenkins`:**
   ```bash
  
   nano .env
   ```

   Dentro de `.env`, agrega el siguiente contenido:
   ```env
   AWS_ACCESS_KEY_ID=TU_ACCESS_KEY
   AWS_SECRET_ACCESS_KEY=TU_SECRET_KEY
   AWS_SESSION_TOKEN=TU_SESSION_TOKEN  # si aplica
   ```

   > ⚠️ Este archivo se usa para que Jenkins y los pipelines tengan acceso a los recursos de AWS.


3. **Levantar Jenkins con Docker Compose:**
   ```bash
   docker compose up --build 
   ```

4. **Acceder a la interfaz de Jenkins:**
   ```
   http://localhost:8080
   ```
   CREDENCIALES PARA UI JENKINS:
	user: admin
	password:admin123

5. **Habilitar ejecución de scripts en proceso:**
   - Ir a: `Administrar Jenkins/ Manage Jenkins` → `In-process Script Approval`
   - Aceptar los scripts sugeridos para permitir configuraciones Job DSL

---



## 🌱 Ejecución del Seed Job

1. En el dashboard de Jenkins, ejecutar el **job `seed`**.
2. Este configurará automáticamente:
   - Conexiones al repositorio Git
   - El pipeline `pipeline-entel`
   - Docker Cloud y los agentes

---

## 🧑‍💻 Generación del Agente

1. **Esperar a que Docker descargue la imagen del agente** desde Docker Hub.
2. Jenkins iniciará automáticamente el **primer agente Docker**.
3. Si el agente aparece desconectado o con errores:
   - **Eliminarlo manualmente** desde Jenkins
   - Jenkins lo regenerará al ejecutar el pipeline

---

## ⚙️ Configuración del Docker Cloud en Jenkins

1. Ir a: `Administrar Jenkins` → `Docker Cloud` → `Configure Docker Agent`
2. En **Container Settings**, aplicar:
   - **DNS:** `8.8.8.8`
   - **Extra Host:** `--add-host jenkins-master:172.18.0.2`
3. Aplicar y Guardar.

---

## ▶️ Ejecución del Pipeline Principal

1. Ejecutar el pipeline `pipeline-entel` desde Dashboard de Jenkins.
2. Este pipeline realizará automáticamente:
   - Despliegue de infraestructura (Terraform)
   - Subida del frontend
   - Despliegue del backend con funciones Lambda
3. Verifica el despliegue en **Console Output** del pipeline.

---
````markdown
## 📁 Estructura del Proyecto

📁 organization-vash-infraestructure/
├── 📁 iac/
│   ├── 📄 api_gateway.tf
│   ├── 📄 cloudfront.tf
│   ├── 📄 deploy_frontend.tf
│   ├── 📄 iam_roles.tf
│   ├── 📄 import.sh
│   ├── 📄 lambda_services.tf
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
├── 📁 jenkins/
│   ├── 📄 docker-compose.yml
│   ├── 📄 Dockerfile.agent
│   ├── 📄 Dockerfile.jenkins
│   ├── 📄 install-plugins.sh
│   ├── 📄 jenkins.yaml
│   ├── 📄 Jenkinsfile
│   ├── 📄 plugins.txt
│   └── 📄 .gitignore
├── 📁 lambda_services/
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
│   │                       ├── 📄 ServiceLambdaHandler.java
│   │                       ├── 📁 model/
│   │                       │   └── 📄 ServiceDTO.java
│   │                       └── 📁 service/
│   │                           ├── 📄 DatabaseConnection.java
│   │                           └── 📄 ServiceServiceLambda.java
│   └── 📁 .vscode/
│       └── 📄 settings.json
└── 📁 lambda_users/
    ├── 📄 pom.xml
    ├── 📄 .gitignore
    ├── 📁 src/
    │   └── 📁 main/
    │       └── 📁 java/
    │           └── 📁 com/
    │               └── 📁 vash/
    │                   ├── 📁 db/
    │                   │   └── 📄 DatabaseInitializer.java
    │                   └── 📁 lambda/
    │                       ├── 📄 UserLambdaHandler.java
    │                       ├── 📁 model/
    │                       │   └── 📄 UserDTO.java
    │                       └── 📁 service/
    │                           ├── 📄 DatabaseConnection.java
    │                           └── 📄 UserServiceLambda.java
    └── 📁 .vscode/
        └── 📄 settings.json
