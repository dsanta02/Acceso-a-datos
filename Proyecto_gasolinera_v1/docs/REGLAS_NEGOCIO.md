# Reglas de negocio: garantías, validaciones y pendientes

Este documento clasifica las reglas del caso práctico según lo que realmente implementan `sql/01_esquema.sql`, `sql/02_triggers.sql` y las clases Java actuales. Una regla que se cumple en los datos de ejemplo no queda por ello garantizada. Las comprobaciones de Java tampoco protegen escrituras directas a MySQL, y las restricciones SQL no sustituyen las validaciones del dominio.

## 1. Garantías mediante restricciones SQL

Las siguientes reglas están declaradas en el esquema. Su funcionamiento efectivo en una instancia MySQL debe confirmarse ejecutando allí el esquema y las pruebas; la presencia de una restricción en el archivo no equivale a una ejecución comprobada.

### Identidad, unicidad y referencias

- Las 21 tablas tienen una clave primaria entera autoincremental.
- Las restricciones `UNIQUE` evitan repetir los códigos y referencias comerciales definidos para estaciones, configuraciones, surtidores, empleados, funciones, clientes, lotes, tarjetas, productos, proveedores, campañas, tickets e incidencias.
- `UNIQUE` en `CLIENTE.matricula` hace únicas las matrículas no nulas; varios clientes pueden mantener el valor `NULL`.
- `UNIQUE` en el identificador antiguo de cliente y en el identificador antiguo de pago de `PAGO_REPOSTAJE` evita repetir identificadores no nulos, conservándolos separados de los IDs técnicos y de los códigos de negocio. `PAGO_VENTA` no tiene identificador antiguo.
- Las claves únicas compuestas evitan repetir el número visible de surtidor dentro de una estación, el par proveedor-producto, el par cliente-campaña y el número de línea dentro de una venta. `TARJETA.id_cliente` es único cuando tiene valor y admite tarjetas sin titular.
- Las claves foráneas impiden referencias a filas inexistentes en las relaciones declaradas. Las asociaciones requeridas son `NOT NULL`; las asociaciones opcionales, como el cliente de una venta o el responsable de una incidencia, admiten `NULL`.
- Las políticas de borrado son normalmente `RESTRICT`; las excepciones documentadas son `SET NULL` al borrar un supervisor o un empleado responsable de una incidencia. Las actualizaciones de IDs referenciados usan `CASCADE`. Las FKs no garantizan la existencia de hijos mínimos.

### Valores, rangos y coherencia de una fila

Los `NOT NULL` y `CHECK` del esquema imponen, entre otras, estas reglas:

- Números visibles de surtidor positivos; combustible y estado del surtidor limitados a los valores declarados.
- El siguiente número de configuración de facturación debe ser positivo.
- Empleados y clientes con al menos un medio de contacto (teléfono o email).
- Fechas de una asignación de función no invertidas; límites, presupuestos y niveles de autorización positivos cuando se proporcionan; acreditación con número y caducidad ambos presentes o ambos ausentes; campos específicos mutuamente excluyentes a nivel de grupo.
- Cantidad de tarjetas por lote positiva y estado dentro de los valores permitidos.
- Coherencia de modalidad de producto: un producto envasado tiene formato de envase y no precisión de pesada; uno a granel tiene precisión configurada entre 0 y 3 y no formato de envase. El precio es positivo y el estado válido.
- Fechas de campaña ordenadas.
- Estado e importe no negativo de venta dentro de los valores declarados. La base permite el estado `PAGADA` aunque aún no se haya comprobado la suma de pagos; el `CHECK` no agrega pagos.
- En una línea de venta, número y cantidad positivos, precio unitario positivo e importe igual al redondeo a dos decimales de cantidad por precio unitario.
- En pagos de venta, importe positivo y medio de pago permitido.
- En devoluciones, cantidad positiva e importe reintegrado no negativo.
- En acuerdos de suministro, precio de compra positivo y plazo de entrega no negativo.
- Estados de incidencia permitidos.
- En el historial de precios, precios positivos y distintos.
- En `PAGO_REPOSTAJE`, ID antiguo nulo o positivo, importe y litros positivos y combustible permitido. Los litros se guardan como `DECIMAL(30,15)`.

### Acciones referenciales

Las claves foráneas preservan la existencia de las referencias durante operaciones ordinarias: no se puede borrar una fila padre que tenga hijos protegidos por `RESTRICT`; al borrar un empleado se deja sin supervisor a sus subordinados y sin responsable sus incidencias mediante `SET NULL`. El esquema no impide que una estación se quede sin surtidores ni que un producto/proveedor se quede sin acuerdos.

## 2. Garantías implementadas mediante triggers

Las reglas siguientes están programadas en `sql/02_triggers.sql`:

- **Historial de precios:** después de actualizar `PRODUCTO`, inserta una fila en `HISTORIAL_PRECIO` solo si `precio_venta` cambió realmente. La inserción inicial no activa este trigger de actualización. Cambiar solo la descripción o volver a escribir el mismo precio no debe generar otra fila. Al ser InnoDB, el registro forma parte de la transacción y se revierte con ella.
- **Protección del historial:** triggers `BEFORE UPDATE` y `BEFORE DELETE` rechazan la modificación o el borrado ordinario de una fila histórica.
- **Autosupervisión directa:** los triggers `BEFORE INSERT` y `BEFORE UPDATE` de `EMPLEADO` rechazan que `id_supervisor` sea el propio ID del empleado. No detectan ciclos indirectos.
- **Coherencia de titular y fechas de tarjeta:** `BEFORE INSERT` y `BEFORE UPDATE` exigen fecha de asignación nula si `id_cliente` es nulo; si hay titular, exigen fecha y que no sea anterior a la emisión.
- **Campos de asignación de función:** en `ASIGNACION_FUNCION`, los triggers `BEFORE INSERT` y `BEFORE UPDATE` consultan el código de función y exigen los campos específicos correspondientes a `CAJERO`, `EXPENDEDOR`, `RESPONSABLE_TIENDA` o `JEFE_ESTACION`; rechazan códigos sin regla implementada en esos triggers. La comprobación ocurre al escribir la asignación, no evita que una modificación posterior del catálogo de funciones deje datos históricos incoherentes.

Estas reglas se consideran implementadas en el archivo de triggers, no verificadas en un servidor durante esta revisión.

## 3. Validaciones actuales del modelo Java

Las comprobaciones de esta sección se ejecutan al construir o utilizar objetos Java. No son restricciones de MySQL, no protegen escrituras SQL externas y varias actúan solo sobre el grafo de objetos que existe en memoria.

- **Validación común:** `ValidacionDominio` exige textos obligatorios no vacíos, recorta espacios, limita longitudes; admite textos opcionales; valida IDs técnicos no negativos, enteros positivos, valores monetarios con hasta dos decimales y decimales positivos con precisión y escala indicadas.
- **Cliente y empleado:** los constructores exigen sus identificadores de negocio y datos requeridos, y teléfono o email. `Empleado` requiere estación y fecha de incorporación. Comprueba en la cadena de objetos supervisor que no haya autosupervisión ni dependencia indirecta, siempre que esa cadena esté disponible en el objeto.
- **Tarjetas:** `Tarjeta` comprueba la correspondencia entre titular y fecha de asignación y que la fecha no preceda a la emisión. No controla que las tarjetas asignadas no superen la cantidad del lote ni unicidad frente a otros objetos.
- **Funciones:** `AsignacionFuncion` valida fechas, valores positivos y los campos específicos de los cuatro códigos de función implementados. No compara los periodos con otras asignaciones y no admite nuevos códigos con campos específicos sin ampliar su lógica.
- **Productos:** `Producto` exige atributos y precio válidos. `ProductoEnvasado` admite cantidades enteras positivas; `ProductoGranel` acepta escala no superior a la precisión configurada, entre cero y tres. `calcularImporte` multiplica por el precio y redondea a dos decimales con `HALF_UP`.
- **Líneas y ventas:** `LineaVenta` verifica cantidad, modalidad, número de línea positivo e importe coherente con cantidad por precio aplicado. `Venta` solo permite líneas durante preparación, evita números de línea repetidos en su propia lista y requiere líneas para confirmar; calcula el total desde esas líneas. Los pagos solo se agregan en estado confirmado, deben corresponder al mismo objeto venta y no superar el total. La venta solo pasa a pagada si la suma de pagos coincide exactamente con el total.
- **Devoluciones:** `LineaVenta.registrarDevolucion` solo opera en una venta pagada, valida modalidad y cantidad acumulada en esa línea, calcula el reintegro incremental a partir del importe original y evita superar la cantidad comprada. `Devolucion` valida motivo, datos obligatorios e importe no negativo que no exceda por sí solo el importe original de la línea.
- **Separación de persistencia:** estas entidades no abren conexiones JDBC ni ejecutan SQL. `Clientes` y `Pagos`, usados por los repositorios de archivo, siguen siendo modelos heredados independientes del dominio nuevo.

Las validaciones agregadas de venta y devolución descritas arriba dependen de las colecciones del objeto Java. No se convierten por sí solas en garantías al guardar varios objetos en MySQL.

## 4. Reglas pendientes y tratamiento recomendado para la fase JDBC

| Regla pendiente | Situación actual | Resolución recomendada y concurrencia |
|---|---|---|
| Alta de estación con exactamente una configuración y al menos un surtidor | La FK y `UNIQUE` de configuración solo impiden más de una configuración por estación; no obligan a que exista. La FK del surtidor permite estaciones sin surtidores. | Implementar un único servicio transaccional que inserte estación, configuración y surtidor(es), confirme todo junto o haga `ROLLBACK`. Para eliminar un surtidor, bloquear la estación y comprobar en la misma transacción que queda al menos uno. |
| Impedir borrar el último surtidor | No existe trigger ni restricción agregada que cuente los surtidores restantes. | Bloquear la fila de estación (`SELECT ... FOR UPDATE`), contar surtidores y borrar en la misma transacción. Todas las operaciones que añadan o retiren surtidores deben seguir el mismo protocolo para evitar carreras. |
| Evitar ciclos indirectos de supervisión | SQL y trigger solo impiden la autorreferencia directa; Java recorre la cadena de supervisor presente en memoria. | Antes de cambiar supervisor, recorrer la jerarquía actual y rechazar si se alcanza al empleado. Ejecutar la lectura y actualización en transacción con bloqueo de las filas relevantes; acordar un protocolo de bloqueo común para que dos cambios simultáneos no creen un ciclo combinado. |
| Evitar solapamientos de la misma función para una persona | La clase Java valida el orden de fechas de una asignación, pero no la compara con otras. Los CHECK SQL tampoco comparan filas. | Para el mismo empleado-función, buscar intervalos que se crucen y rechazar la nueva asignación. Serializar altas/cambios bloqueando la fila del empleado dentro de una transacción; permitir solapamiento de funciones distintas. |
| Mantener al menos un acuerdo para cada proveedor y producto | La tabla de acuerdos tiene FKs y unicidad de pareja, pero permite proveedores o productos aislados y no impide borrar su último acuerdo. | Crear proveedor/producto con acuerdo(s) en una transacción o impedir confirmar el alta sin ellos. Al borrar o cambiar acuerdos, bloquear los padres afectados, contar los acuerdos restantes y realizar la operación dentro de la misma transacción. |
| Confirmar venta solo con líneas y total correcto | Java valida líneas y calcula total en memoria. SQL permite insertar/cambiar estado y total sin comparar con las líneas. | En una transacción bloquear la cabecera de venta, verificar que tenga líneas, sumar sus importes y guardar el total/estado confirmado. Los cambios de líneas deben respetar estado y bloqueo de esa misma venta. |
| Marcar venta pagada solo con pagos por el total exacto | Java `Venta.marcarPagada` comprueba la suma del objeto. La base no calcula ni compara pagos acumulados con la cabecera. | Bloquear la venta, sumar pagos y cambiar a `PAGADA` solo si la suma coincide exactamente con `importe_total`; registrar cualquier pago y cambio de estado bajo el mismo protocolo transaccional. |
| Autorizar devoluciones y limitar acumulados ante simultaneidad | Java exige venta pagada y controla cantidades acumuladas en la lista de una línea. SQL valida cada fila, pero no la suma de devoluciones ni el estado de la venta. | En una transacción bloquear la venta y la línea, volver a comprobar estado pagado, sumar devoluciones previas y calcular el reintegro incremental. Insertar solo si cantidad e importe acumulados no exceden los originales. El bloqueo evita que dos devoluciones concurrentes pasen simultáneamente la misma comprobación. |
| Aplicar en persistencia modalidad y precisión de producto | `ProductoEnvasado` y `ProductoGranel` validan en Java; `LINEA_VENTA` y `DEVOLUCION` solo tienen restricciones de positividad y escala máxima de sus columnas, no consultan modalidad de producto. | Antes de guardar líneas o devoluciones, cargar el producto y validar cantidad/precisión. Coordinar con el bloqueo de la operación de venta/devolución y conservar validación de servicio; si también se permiten escrituras SQL externas, definir una protección adicional en base de datos. |
| No superar la cantidad recibida en un lote de tarjetas | `TARJETA.id_lote` referencia el lote, pero ni FK ni CHECK comparan el número de tarjetas con `LOTE_TARJETA.cantidad`. La clase `Tarjeta` no lleva un contador de lote. | Al emitir/asignar una tarjeta, bloquear la fila del lote, contar las tarjetas ya registradas y permitir la inserción solo si no excede `cantidad`, todo en una transacción. |
| Impedir cambios ordinarios a líneas de ventas cerradas | Las FKs y CHECK actuales no congelan líneas cuando la venta se confirma o paga. | En la capa de servicio rechazar cambios de líneas si la venta ya está confirmada; coordinar toda escritura con bloqueo de la cabecera. El enunciado lo menciona como ampliación opcional, no como requisito SQL implementado. |

Para reglas cuya garantía debe sobrevivir a cualquier cliente SQL, la fase siguiente debe decidir si las validaciones de servicio son suficientes o si se requiere lógica adicional en la base. Una validación previa sin transacción o sin bloqueo no es segura frente a escrituras concurrentes.

## 5. Evidencias y estado de verificación

### Java, CSV y JSON

- Se ejecutó `mvn test` el 22/11/2026 durante la preparación del README: `BUILD SUCCESS`, 9 pruebas, 0 fallos y 0 errores. Incluye 3 pruebas de dominio y 6 de persistencia.
- Las pruebas de dominio cubren cantidades para productos envasados y a granel, cálculo de importes redondeados, confirmación y pago exacto de una venta y devoluciones parciales sucesivas sin exceder la cantidad.
- Las pruebas de persistencia cubren operaciones de consola CSV/JSON, migración con conservación de IDs y respaldo de JSON anterior, rechazo de JSON inválido y pagos a clientes inexistentes.
- Este resultado prueba los casos incluidos en esas pruebas, no todas las reglas de negocio ni la persistencia del nuevo dominio en MySQL.

### SQL y venta 3

- Los archivos `sql/01_esquema.sql` y `sql/02_triggers.sql` contienen las restricciones y triggers descritos. Se reportaron errores MySQL 8.0.46 código 3823 para dos CHECK asociados a columnas referenciadas; las reglas fueron trasladadas a triggers. No se afirma que la versión corregida de todos los scripts se haya ejecutado con éxito.
- `sql/04_comprobaciones.sql` contiene consultas de ventas, devoluciones, relaciones, historial, una prueba `ROLLBACK` y sentencias negativas comentadas para ejecutar individualmente. Son procedimientos preparados; no constituyen resultados de ejecución.
- Se añadió en `sql/03_datos.sql` el pago de `4.00` euros en efectivo para la venta 3, con fecha `2026-10-11 12:05:00`; los comentarios y la consulta de `04_comprobaciones.sql` esperan estado `CONFIRMADA`, total `10.00` y cobrado `4.00`. La venta 1 debe conservar dos pagos por `23.00`.
- La corrección del pago parcial se comprobó estáticamente leyendo los INSERT y comprobando importes, estado esperado, consulta y comentarios. Los scripts actualizados aún deben ejecutarse en MySQL para comprobar los datos realmente cargados, las ventas 1 y 3 y las demás pruebas SQL.
- En el entorno de esta revisión no estaba disponible el cliente `mysql`; por tanto no se ejecutó una conexión ni ninguna consulta contra una base real. La prueba del trigger, los casos negativos y `ROLLBACK` siguen pendientes de ejecución.

## 6. Resumen

Las restricciones SQL garantizan principalmente unicidad, existencia de referencias y reglas locales de una fila. Los triggers cubren trazabilidad de precios y algunas coherencias que no pudieron expresarse como `CHECK` compatibles. El modelo Java aporta validaciones útiles del dominio y del ciclo de venta en memoria. Las reglas que agregan varias filas, dependen del estado conjunto de una operación o deben resistir concurrencia necesitan transacciones y un protocolo explícito de bloqueo en la siguiente fase.
