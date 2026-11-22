# Registro de cambios

Este archivo recoge la evolución del proyecto y distingue el trabajo disponible en el código de las tareas que todavía necesitan ejecutarse o verificarse. Las fechas se basan en el historial Git o en las comprobaciones registradas; cuando no existe evidencia, se indica como pendiente.

## 2026-10-14 — Persistencia JSON y migración desde CSV

Se conserva el registro histórico asociado al commit `56da0227fd9c8a93a4fd6ba21beaa1b7c39972dd`:

- Se añadió `Jsons` para leer y guardar clientes y pagos en JSON.
- Se añadió `MigraCSVToJson` para migrar los datos existentes de CSV a JSON.
- Se adaptó `Main` para seleccionar/preparar el almacenamiento JSON y ofrecer la migración durante el inicio cuando correspondía.
- Se separó la migración de las operaciones ordinarias de la aplicación.
- Se mantuvieron los modelos y gestores existentes de clientes y pagos y la interfaz `ProcesosLeerEscribir` que se utilizaba entonces.
- El registro original también indicaba que la migración dejó de estar en el menú principal y se ofrece durante la preparación inicial del almacenamiento JSON.
- La lógica de inicio actual también contempla JSON existente que no se puede leer: solicita confirmación antes de reemplazarlo desde CSV y guarda una copia de seguridad.

Referencia conservada del registro anterior: [repositorio](https://github.com/dsanta02/Acceso-a-datos.git), commit `56da0227fd9c8a93a4fd6ba21beaa1b7c39972dd`.

## 2026-11-21 — Preparación de la arquitectura Java

El commit `56e32dc` registra la reorganización de la aplicación existente:

- Se incorporaron los contratos `ClienteRepository` y `PagoRepository` para desacoplar la gestión de las implementaciones concretas de almacenamiento.
- Se reorganizaron las capas de persistencia CSV y JSON, servicios, migración y aplicación de consola.
- Se actualizaron `Main`, `AplicacionConsola`, `GestionClientes` y `GestionPagos` para trabajar con las alternativas de almacenamiento existentes.
- Se añadió manejo explícito de errores de persistencia y se mantuvo la compatibilidad con los modelos heredados `Clientes` y `Pagos`.
- Se añadieron pruebas de integración para los flujos CSV/JSON y la migración.

La existencia de pruebas está confirmada en el proyecto; sus resultados concretos se indican únicamente en las comprobaciones ejecutadas que se registran más adelante.

## 2026-11-21 — Modelo de dominio Java para la ampliación

El commit `3aee398` registra la incorporación del modelo ampliado y sus pruebas:

- Se crearon entidades y tipos de dominio bajo `gasolinera.domain`, organizados por áreas funcionales: estaciones, personal, clientes, fidelización, catálogo, ventas, repostajes, campañas, incidencias, precios y tipos compartidos.
- Se implementaron, entre otras, las clases `Estacion`, `Empleado`, `Funcion`, `AsignacionFuncion`, `Cliente`, `Tarjeta`, `Producto`, `ProductoEnvasado`, `ProductoGranel`, `Venta`, `LineaVenta`, `PagoVenta`, `Devolucion` y `PagoRepostaje`.
- Se utilizaron referencias tipadas entre objetos y `BigDecimal` para valores monetarios y cantidades del nuevo dominio. Las modalidades de producto validan cantidades y calculan importes; la venta controla estados y pagos en memoria.
- Se añadieron `DominioDemo` y pruebas de dominio.
- Se mantuvieron `gasolinera.model.Clientes` y `gasolinera.model.Pagos` para que las persistencias CSV/JSON existentes no fueran sustituidas por las nuevas entidades.
- No se añadió JDBC ni persistencia MySQL desde Java.

## 2026-11-21 — Esquema relacional de 21 tablas y scripts SQL

Se añadieron los siguientes archivos:

- `sql/01_esquema.sql`: creación de la base `gasolinera` y las 21 tablas con claves, restricciones, tipos y acciones referenciales.
- `sql/02_triggers.sql`: trigger de historial de precios y triggers para reglas que no pudieron mantenerse como `CHECK` compatibles con MySQL 8.0.46.
- `sql/03_datos.sql`: datos ficticios relacionados para demostrar las entidades y asociaciones del caso.
- `sql/04_comprobaciones.sql`: consultas de revisión, prueba transaccional del historial y casos negativos comentados para ejecutarse por separado.

El esquema mantiene `PAGO_REPOSTAJE` separado de `PAGO_VENTA`. El pago de repostaje conserva un ID antiguo opcional, separado del ID técnico, y una precisión ampliada para litros. Los scripts incluyen escenarios de estaciones, personal y funciones, clientes y tarjetas, productos y proveedores, campañas, ventas, pagos, devoluciones, incidencias e historial de precios.

El trigger de producto registra solamente cambios reales de precio; al ser transaccional con InnoDB, su fila histórica debe revertirse junto con el cambio si se ejecuta `ROLLBACK`. Otros triggers protegen el historial frente a modificaciones y borrados ordinarios y conservan validaciones de negocio trasladadas desde `CHECK`.

### Correcciones de compatibilidad MySQL 8.0.46

- Tras el error 3823 reportado para `ck_empleado_no_autosupervision` y `fk_empleado_supervisor`, se eliminó el `CHECK` incompatible y se trasladó la regla de no autosupervisión directa a triggers `BEFORE INSERT` y `BEFORE UPDATE`.
- Tras el error 3823 reportado para `ck_tarjeta_titular_fechas` y `fk_tarjeta_cliente`, se eliminó ese `CHECK` incompatible y se trasladaron a triggers las reglas: sin titular, la fecha de asignación debe ser nula; con titular, debe existir y no ser anterior a la fecha de emisión.
- Se revisaron otros `CHECK` del esquema para localizar incompatibilidades del mismo tipo. Las reglas de negocio se conservaron.

Los errores se detectaron al ejecutar el usuario el esquema en MySQL 8.0.46. No se registra aquí como verificada una ejecución posterior de todos los scripts: esa comprobación completa sigue pendiente.

## 2026-11-21 — Documentación del modelo

- Se creó `docs/MODELO_RELACIONAL.md`, que describe el esquema implementado, las 21 tablas, restricciones, relaciones y reglas que requieren lógica de aplicación o transacciones.
- Se creó `docs/JUSTIFICACION_MODELO_JAVA.md`, que explica las decisiones del dominio Java y distingue lo implementado de lo que todavía no está conectado a CSV/JSON o MySQL.

## 2026-11-22 — README de entrega

- Se creó `README.md` en la raíz con requisitos, orden de preparación de SQL, instrucciones para ejecutar la aplicación y las pruebas, estado de las reglas, operaciones JDBC futuras y pendientes de verificación.
- El README reemplaza la ubicación anterior `src/readme.md`; se conservó allí el contenido relevante sobre ejecución y persistencias al ampliarlo.
- Se actualizaron `AGENTS.md` e `.github/copilot-instructions.md` con instrucciones permanentes para agentes y el registro de IA. `docs/AUDITORIA_IA.odt` recoge intervenciones y señala como pendientes los datos de OpenCode que no tienen evidencia conservada.
- Se indicó expresamente que no se ha verificado la ejecución completa de los scripts contra un servidor MySQL.

## 2026-11-22 — Corrección del pago parcial de la venta 3

- En `sql/03_datos.sql` se añadió a `PAGO_VENTA` el registro con ID 6, venta 3, fecha `2026-10-11 12:05:00`, importe `4.00` y medio `EFECTIVO`.
- En `sql/04_comprobaciones.sql` se actualizaron los comentarios y se añadió una consulta específica para contrastar la venta 3 (estado `CONFIRMADA`, total `10.00`, cobrado `4.00`) y la venta 1 (dos pagos que suman `23.00`).
- Una verificación estática de los datos y la consulta confirmó esos valores. No se ejecutó la consulta contra MySQL en esa comprobación porque el cliente MySQL no estaba disponible.

## Comprobaciones registradas

- En una ejecución de Maven del 2026-11-22 se obtuvo `BUILD SUCCESS`: 9 pruebas, 0 fallos y 0 errores. Esta comprobación cubre las pruebas Java, CSV y JSON; no valida los scripts SQL en un servidor MySQL.
- Para la corrección del pago parcial se verificaron estáticamente el bloque de inserción y las consultas esperadas. La ejecución real en MySQL queda pendiente.
- No se afirma que `01_esquema.sql`, `02_triggers.sql`, `03_datos.sql` y `04_comprobaciones.sql` se hayan ejecutado en conjunto con éxito en una instancia limpia de MySQL.

## Pendiente para completar la Fase 1

- Ejecutar los tres primeros scripts, en orden, sobre una base de datos vacía MySQL 8.0.46 y guardar la evidencia de resultados.
- Ejecutar las consultas de `04_comprobaciones.sql`, incluido el `ROLLBACK` del cambio de precio, y probar individualmente los casos negativos comentados.
- Contrastar en la base cargada todos los casos mínimos de datos requeridos por el enunciado, incluidas las cantidades mínimas por tabla principal y las diez líneas de venta.
- Completar la evidencia de las tres intervenciones de IA exigidas por el enunciado. `docs/AUDITORIA_IA.odt` contiene un registro de intervenciones, pero cualquier detalle de OpenCode que no esté respaldado por su informe o registro original debe seguir marcado como pendiente; no se le atribuyen cambios sin evidencia.
- Las consultas y operaciones CRUD JDBC y los controles transaccionales pendientes corresponden a una fase posterior y no están implementados en esta Fase 1; solo se ha preparado la conexión básica en `persistence/mysql`.
