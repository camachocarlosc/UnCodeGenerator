## UN Code Generator

It takes as input a PlantUML class diagram and outputs the JAVA classes that match it

Carlos Camacho & Jose Lopez

Pasos y comandos necesarios para ejecutar el código JavaScript que convierte un diagrama de clases en formato PlantUML a código Java:

Creamos los archivos necesarios:

index.html: Contendrá el HTML.
style.css: Contendrá el CSS.
script.js: Contendrá el código JavaScript.

Estructura de archivos:

Dentro de esta carpeta, estan los archivos index.html, style.css y script.js.

Los archivos necesarios:

mkdir uml-to-java
cd uml-to-java
touch index.html style.css script.js
Abre el archivo HTML en tu navegador:

open index.html

Abrir el archivo HTML:

Abrimos el archivo index.html en el navegador web. Esto cargará la página con el formulario para pegar el diagrama PlantUML y el botón para generar las clases Java.

Probar la funcionalidad:

Hay que pegar el diagrama de clases en formato PlantUML en el recuadro de texto.
Hacer clic en el botón "Generate Java Files".
Deberían verse las clases Java generadas en la sección "Generated Classes".

