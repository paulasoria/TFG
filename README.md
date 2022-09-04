# SeniorCare



Trabajo de Fin de Grado de Ingeniería Informática en la USAL

Aplicación de Android desarrollada en Kotlin para ayudar a las personas mayores a realizar tareas

[![Android](https://img.shields.io/badge/Android-brightgreen?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/intl/es_es)
[![Kotlin](https://img.shields.io/badge/Kotlin-blue?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)](https://nodejs.org/es)
[![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)](https://developer.mozilla.org/es/docs/Web/JavaScript)
[![Firebase](https://img.shields.io/badge/Firebase-orange?style=for-the-badge&logo=firebase&logoColor=white)](https://firebase.google.com)
[![Docker](https://img.shields.io/badge/Docker-darkblue?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com)
[![JitsiMeet](https://img.shields.io/badge/JitsiMeet-blueviolet?style=for-the-badge&logo=jitsi&logoColor=white)](https://meet.jit.si/)

## :rocket: Despliegue

### :bell: Cron de alertas

Para realizar el despliegue del cron de alertas, bastaría con abrir una terminal del ordenador. Navegar hasta la carpeta en la que se encuentren nuestras funciones utilizando el comando:

    cd ruta_del_directorio

Una vez ahí, hay que ejecutar la aplicación de Node.js y esto se hace con el comando:

    node index.js

### :cloud: Google Cloud Functions

Para realizar el despliegue de las funciones de Google Cloud, bastaría con abrir una terminal del ordenador. Navegar hasta la carpeta en la que se encuentren nuestras funciones utilizando el comando:

    cd ruta_del_directorio

Una vez ahí, hay que desplegar las funciones y esto se hace con el comando:

    firebase deploy --only functions

Y tras unos segundos o minutos, en función de las dimensiones del fichero, se habrán desplegado en Google Cloud Functions.

### :phone: Jitsi Meet

Para realizar el despliegue de Jitsi Meet, primero habría que instalar Docker mediante el comando:

    apt-get install docker.io docker-compose

Tras la instalación, hay que descargar el repositorio de Jitsi Meet Docker de GitHub mediante el comando:

    git clone https://github.com/jitsi/docker-jitsi-meet

Después, hay que entrar en el directorio, copiar el fichero de configuración de ejemplo y pegarlo en el mismo directorio con la extensión .env utilizando el comando:

    cp env.example .env

Ahora hay que editar el fichero, cambiando el valor de “HTTP_PORT” a 80 y “HTTPS_PORT” a 443, porque debe poder conectarse a Internet. También hay que modificar el valor de “PUBLIC_URL” a “https://jitsi.paulasoria.tk”, que es el DNS de mi dominio. Finalmente, para cargar la configuración, y descargar e iniciar los contenedores, se utilizará el comando:

    docker-compose up -d
