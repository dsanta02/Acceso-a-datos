

## Compilación y decisiones de diseño

Para ejecutar este proyecto se debe iniciar la clase `Main`. Los archivos `clientes.csv` y `pagos.csv` se crean automáticamente dentro de la carpeta `src` si no existen.

A la hora de plantear el diseño del proyecto, he decidido separar las funcionalidades en diferentes clases. `GestionClientes` se encarga de las operaciones relacionadas con los clientes, `GestionPagos` de las operaciones relacionadas con los pagos y `Ficheros` de las operaciones de creación, lectura y escritura de los archivos. Estas funcionalidades son utilizadas posteriormente desde `Main`.

También he creado la interfaz `ProcesosLeerEscribir`, que permite separar las operaciones de lectura y escritura de la implementación concreta utilizada para almacenar los datos. De esta forma, si en el futuro fuera necesario utilizar otro tipo de almacenamiento diferente a archivos CSV, se podrían implementar los métodos de la interfaz de una forma diferente sin tener que modificar la estructura principal del programa.

Para trabajar con los archivos he utilizado las clases y métodos vistos en clase, principalmente `Files.readAllLines()`, `Files.write()`, `Files.createDirectories()` y `Files.createFile()`.

VERSION JDK21