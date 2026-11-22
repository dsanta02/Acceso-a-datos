# Ampliación de la aplicación de gasolinera con MySQL — Fase 1

> Proyecto académico de Acceso a Datos, 2.º DAM. Esta fase prepara el diseño relacional y el modelo Java; no implementa consultas ni operaciones CRUD mediante JDBC.

## Estado actual

El proyecto conserva la aplicación de consola anterior y sus alternativas CSV y JSON. Se han añadido un modelo de dominio Java organizado por áreas funcionales, cuatro scripts SQL para la base de datos de 21 tablas, una conexión JDBC configurable por variables de entorno y documentación del modelo relacional y del dominio.

| Área | Estado según los archivos disponibles |
|---|---|
| Aplicación Java y persistencia CSV/JSON | Implementadas; las entidades heredadas `Clientes` y `Pagos` siguen siendo las usadas por esa persistencia. |
| Modelo de dominio ampliado | Implementado; incluye estaciones, personal, clientes/tarjetas, catálogo, ventas, repostajes, campañas e incidencias. |
| Demostración del dominio | `gasolinera.demo.DominioDemo` muestra productos envasados y a granel, importes y una venta con más de un pago. |
| Esquema, triggers y datos de ejemplo | Scripts disponibles en `sql/`; deben ejecutarse y verificarse en MySQL antes de considerar comprobada la base de datos. |
| Conexión JDBC desde Java | Preparada mediante `ConexionMySQL` y MySQL Connector/J; requiere configurar variables de entorno y tener disponible MySQL. No implementa operaciones CRUD ni repositorios MySQL. |
| Reglas entre varias filas y operaciones concurrentes | Algunas requieren transacciones/control de aplicación; se resumen más abajo y se detallan en `docs/MODELO_RELACIONAL.md`. |

## Requisitos

- JDK 21.
- Maven 3.9 o compatible para compilar y ejecutar las pruebas.
- MySQL 8.0.46 recomendado para ejecutar el diseño y los triggers. El esquema indica MySQL 8.0.16 o posterior; se sustituyeron algunos `CHECK` por triggers tras errores de compatibilidad 3823 reportados con 8.0.46.
- Un cliente MySQL, por ejemplo MySQL Workbench, y una cuenta con permisos para crear la base de datos y los triggers.

No guardes contraseñas, usuarios personales ni otros secretos en este repositorio. Los scripts SQL se ejecutan desde un cliente MySQL; la aplicación Java obtiene la URL y las credenciales JDBC exclusivamente de las variables de entorno descritas más abajo. La base creada por los scripts se llama `gasolinera`.

> **Alcance JDBC:** se incluye únicamente la dependencia del driver y dos clases: `ConexionMySQL`, que abre la conexión, y `PruebaConexionMySQL`, un `main` manual para comprobarla. No se han implementado operaciones CRUD, consultas ni repositorios MySQL.

### Variables JDBC en IntelliJ IDEA

En IntelliJ IDEA, abre **Run > Edit Configurations...**, selecciona la configuración de ejecución de la aplicación y edita el campo **Environment variables**. Añade estas tres variables usando la cuenta local de MySQL; no guardes sus valores en archivos del repositorio:

- `GASOLINERA_DB_URL=jdbc:mysql://localhost:3306/gasolinera`
- `GASOLINERA_DB_USER=<usuario local de MySQL>`
- `GASOLINERA_DB_PASSWORD=<contraseña local de MySQL>`

Guarda la configuración y ejecuta el proyecto desde esa configuración. `ConexionMySQL.obtenerConexion()` lee las variables mediante `System.getenv()` y devuelve un `java.sql.Connection`; si alguna variable falta o está vacía, lanza un mensaje que identifica su nombre sin mostrar valores ni contraseñas. La clase no abre una conexión automáticamente al arrancar la aplicación: debe invocarse expresamente desde código. Para que la conexión se establezca, el servidor debe estar disponible en `localhost:3306` y la base `gasolinera` debe existir.

## Preparación de la base de datos

Abre y ejecuta los scripts en este orden, en una instancia accesible de MySQL:

1. `sql/01_esquema.sql`: crea `gasolinera` y las 21 tablas, claves y restricciones.
2. `sql/02_triggers.sql`: crea los triggers de historial de precios y las validaciones trasladadas desde `CHECK` incompatibles.
3. `sql/03_datos.sql`: carga datos ficticios relacionados para la demostración. Ejecuta este script sobre una base vacía; no está diseñado para insertar los mismos datos varias veces.
4. `sql/04_comprobaciones.sql`: ejecuta consultas de revisión y la prueba transaccional del trigger. Los casos negativos están comentados: descoméntalos y ejecútalos individualmente para comprobar el error esperado, evitando que interrumpan las demás consultas.

No desactives las claves foráneas. El script de comprobaciones incluye una modificación de precio seguida de `ROLLBACK`; el resultado esperado es que se reviertan tanto el precio como la fila de historial generada por el trigger.

**Evidencia pendiente:** en la revisión de este README no se ejecutaron los scripts contra un servidor MySQL. Por tanto, su ejecución completa, el resultado de las pruebas negativas y la prueba del `ROLLBACK` deben verificarse en el entorno de entrega y registrarse con sus resultados reales.

## Estructura del proyecto

- `src/main/java/gasolinera/model`: modelos heredados `Clientes` y `Pagos`, conservados para CSV/JSON.
- `src/main/java/gasolinera/domain`: modelo ampliado en paquetes `station`, `staff`, `customer`, `loyalty`, `catalog`, `sales`, `refueling`, `campaign`, `incident`, `pricing` y `shared`.
- `src/main/java/gasolinera/repository`: contratos `ClienteRepository` y `PagoRepository`.
- `src/main/java/gasolinera/service`: gestión de clientes y pagos heredados.
- `src/main/java/gasolinera/persistence/csv` y `persistence/json`: almacenamiento en archivos.
- `src/main/java/gasolinera/persistence/mysql/ConexionMySQL.java`: apertura de conexión JDBC configurada por entorno, sin operaciones de datos.
- `src/main/java/gasolinera/migration`: migración de CSV a JSON.
- `src/main/java/gasolinera/demo/DominioDemo.java`: demostración del modelo nuevo sin conexión a base de datos.
- `sql/`: scripts de esquema, triggers, datos de prueba y comprobaciones.
- `docs/MODELO_RELACIONAL.md`: diccionario y descripción del esquema SQL.
- `docs/JUSTIFICACION_MODELO_JAVA.md`: decisiones y límites del modelo Java.
- `AGENTS.md`: contexto e instrucciones para agentes de IA.
- `docs/AUDITORIA_IA.odt`: registro de intervenciones de IA; véase la sección de pendientes de entrega.

## Ejecutar la aplicación y las pruebas

Desde la raíz del proyecto:

```text
mvn test
```

Para iniciar la aplicación heredada, ejecuta `gasolinera.Main` desde IntelliJ IDEA y elige CSV o JSON. Los archivos usados por defecto son `src/src/clientes.csv`, `src/src/pagos.csv`, `src/src/clientesJson.json` y `src/src/PagosJson.json`. La aplicación conserva el flujo de recuperación/migración de JSON descrito en el código.

Para ejecutar la demostración completa del dominio desde IntelliJ IDEA:

1. Abre `src/main/java/gasolinera/demo/DominioDemo.java`.
2. Pulsa el icono verde de ejecución junto al método `main`.
3. Selecciona **Run 'DominioDemo.main()'**.

La salida muestra la creación de una estación y un empleado, el cálculo de un producto envasado y otro a granel, el rechazo de una cantidad de 1,5 unidades en el producto envasado y de 0,1256 kg en un producto con precisión 3, una venta con dos líneas y dos pagos que cubren exactamente el total, el cambio a `PAGADA` y dos devoluciones parciales sucesivas sobre la línea a granel. La demostración se ejecuta en memoria y no necesita MySQL.

**Última comprobación documentada para esta entrega:** el 22/11/2026 se ejecutó `mvn test`: 9 pruebas, 0 fallos y 0 errores. Esta comprobación cubre las pruebas Java/CSV/JSON; no demuestra que los scripts SQL hayan sido ejecutados.

## Reglas garantizadas y reglas pendientes

El SQL define claves primarias y foráneas, restricciones `UNIQUE`, `NOT NULL` y `CHECK` y triggers. Entre otras cosas, se protege la unicidad de códigos y referencias, la existencia de registros referenciados, la coherencia de fechas de asignación de tarjetas, la autosupervisión directa, los datos específicos de las funciones conocidas y la trazabilidad del precio. El trigger de precio solo debe registrar cambios reales; otro precio igual o un cambio de descripción no deben añadir historial. Los triggers también impiden modificaciones y borrados ordinarios del historial.

Las siguientes reglas **no quedan completamente garantizadas por el SQL actual** y deberán implementarse con lógica de aplicación y, cuando corresponda, transacciones en la siguiente fase:

- Crear una estación junto con exactamente una configuración de facturación y al menos un surtidor; impedir eliminar el último surtidor.
- Detectar ciclos indirectos en la jerarquía de supervisores.
- Evitar que se solapen periodos de la misma función para el mismo empleado.
- Garantizar que cada proveedor y producto conserva al menos un acuerdo de suministro.
- Confirmar ventas solo con líneas y con un total igual a la suma de estas; marcar una venta como pagada solo si la suma de pagos coincide exactamente con el total.
- Autorizar devoluciones solo para ventas pagadas y controlar, también ante operaciones simultáneas, la cantidad devuelta y el importe acumulado reintegrado.
- Validar en persistencia la cantidad y precisión de los productos a granel en líneas y devoluciones.
- Impedir que el número de tarjetas emitidas supere la cantidad recibida en el lote.

La descripción completa está en `docs/MODELO_RELACIONAL.md`. No se deben presentar estas reglas pendientes como si ya fueran rechazadas automáticamente por MySQL.

## Operaciones JDBC previstas para una fase posterior

Estas son propuestas de operaciones futuras derivadas del enunciado; **no están implementadas**:

1. **Alta transaccional de estación:** recibir los datos de estación, configuración de facturación y al menos un surtidor. Comprobar códigos únicos y referencias válidas; insertar el conjunto en una transacción y dejarlo todo confirmado o no insertar ninguna parte.
2. **Confirmación y cobro de venta:** recibir estación, empleado, cliente opcional, líneas (producto y cantidad) y uno o varios pagos. Validar modalidad y cantidades, conservar el precio aplicado, recalcular el total y confirmar la venta; marcarla como `PAGADA` únicamente si los cobros suman exactamente el total.
3. **Devolución parcial:** recibir línea de venta, empleado, cantidad, motivo y aptitud para reventa. Verificar que la venta esté pagada; dentro de una transacción proteger la línea frente a concurrencia, controlar cantidades e importes acumulados y no reintegrar más de lo cobrado originalmente.

## Pendientes para completar los objetivos de la entrega

- **Ejecutar y guardar evidencia de los cuatro scripts** sobre una base MySQL vacía (idealmente MySQL 8.0.46), incluyendo resultados de consultas, restricciones, trigger y `ROLLBACK`. No afirmar que se verificaron hasta ejecutarlos.
- **Cerrar la trazabilidad del registro de IA.** `docs/AUDITORIA_IA.odt` ya contiene tres o más intervenciones, ejemplos de buen y mal funcionamiento de la IA y comprobaciones concretas; aun así, las secciones 2.7 y 2.8 conservan avisos de pendiente por falta del informe original de OpenCode. Completarlos solo con datos reales; no inventarlos.
- **Revisar los datos de ejemplo en MySQL** con la rúbrica completa del enunciado. `sql/03_datos.sql` tiene ejemplos para los casos principales y `04_comprobaciones.sql` incluye consultas y pruebas comentadas, pero la ejecución efectiva en servidor aún debe confirmarse.
- **Probar la conexión JDBC en un entorno configurado** con MySQL accesible, la base `gasolinera` creada y las tres variables de entorno locales. La compilación no verifica la disponibilidad del servidor ni las credenciales.
- **Mantener fuera de esta fase la implementación de repositorios JDBC y CRUD**. La persistencia MySQL desde Java se realizará en la fase siguiente.

## Documentación

- [Modelo relacional y reglas SQL](docs/MODELO_RELACIONAL.md)
- [Reglas de negocio: garantías, validaciones y pendientes](docs/REGLAS_NEGOCIO.md)
- [Justificación del modelo Java](docs/JUSTIFICACION_MODELO_JAVA.md)
- [Registro de intervenciones de IA](docs/AUDITORIA_IA.odt)
- [Enunciado de la práctica](src/Caso_practico_Gasolinera_MySQL_Fase_1.md)
