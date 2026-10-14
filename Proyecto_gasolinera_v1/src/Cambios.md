\## Cambios en la estructura del programa



\- He añadido la clase `Jsons`, que se encarga de leer y guardar los datos en JSON.

\- He añadido la clase `MigraCSVToJson`, que se encarga de pasar los datos de los archivos CSV a JSON.

\- He modificado la clase `Main` para utilizar el nuevo sistema de almacenamiento JSON y gestionar el inicio de la aplicación.

\- He cambiado el inicio del programa para comprobar si existen los archivos JSON y preguntar si se quieren migrar los datos antes de mostrar el menú.

\- He separado la parte de la migración del resto de operaciones de la gasolinera.

\- He mantenido las clases `Clientes`, `Pagos`, `GestionClientes` y `GestionPagos` para seguir trabajando con los clientes y los pagos.

\- He mantenido la interfaz `ProcesosLeerEscribir` para definir las operaciones de lectura y escritura de datos.

\- He quitado la migración del menú principal, ya que ahora se comprueba al iniciar el programa y solo se realiza cuando todavía no existen los archivos JSON.



