# desafio

# API de Noticias - Desafío Finalizado

Este proyecto es una API RESTful para consultar noticias del sitio ABC y devolver los resultados en múltiples formatos (JSON, XML, HTML, Texto Plano). La API soporta paginación, codificación de imágenes en Base64, y es segura gracias al uso de una API Key y firmas HMAC-SHA256.

---

## **Requisitos Previos**

Asegúrate de tener las siguientes herramientas instaladas en tu máquina:

1. **Java JDK** (versión 11 o superior): [Descargar](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
2. **Apache Maven** (versión 3.6 o superior): [Descargar](https://maven.apache.org/download.cgi)
3. **PostgreSQL v.16**:
   - Base de datos configurada con el nombre y credenciales correspondientes (ver sección de configuración).
4. **Git** (opcional, para clonar el repositorio): [Descargar](https://git-scm.com/)
5. **Postman** (opcional, para probar los endpoints): [Descargar](https://www.postman.com/downloads/)

---

## **Configuración del Proyecto**

1. **Clonar el repositorio**:

   ```bash
   git clone https://github.com/Gaussdrigo/desafio.git

   ```

2. **Definir una base de datos de nombre "apificar"**:
3. **Actualizar las dependencias con:**:
   mvn clean install

4. **Ejecutar el Proyecto con**:
   mvn spring-boot:run

5. **Utilizar la Documentacion Swagger, Postman u otra herramienta de preferencia para la prueba de los endpoints**:
   GET Ejemplo: http://localhost:8080/consulta?q=arrecife&page=1&size=2&f=true
   Donde q de tipo cadena
   page int positvo
   size int positivo
   f true o fasle (Dependiendo si se quiere o no la imagen codificada en base64)
   Definir en Authorization
   user:admin
   pass:admin
   headers
   key: x-api-key value: mi-api-key-valida (cargada por defecto en la BD como apikey valida)
   key: Accept value: application/json o application/xml o text/html o application/text
6. **Prueba con la interfaz de Swagger**:
   Una vez levantado el proyecto acceder a la siguiente url:
   http://localhost:8080/swagger-ui/index.html en el navegador de su preferencia y colocar los parametros correspondientes especificados en el punto anterior
   OBS: si se especifica f como true pero en la respuesta se obtiene null el encode indica que la
   noticia original en la pagina no posee imagen definida en nigun formato tradicional (jpg,png etc)
