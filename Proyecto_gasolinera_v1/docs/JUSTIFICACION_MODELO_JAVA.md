# Justificación del modelo de dominio Java

## 1. Decisiones generales del modelo

El modelo se preparó a partir de las necesidades de la ampliación descritas en el enunciado y se organizó por áreas funcionales bajo `gasolinera.domain`: estaciones, personal, clientes, fidelización, catálogo, ventas, repostajes, campañas, incidencias, precios y tipos compartidos. Esta separación permite localizar las clases por responsabilidad y evita concentrar todo el dominio en un único paquete.

El diseño no replica mecánicamente las 21 tablas SQL como 21 clases. `PRODUCTO`, por ejemplo, se expresa con una clase abstracta y dos especializaciones Java que representan sus modalidades; sus datos persistidos siguen correspondiendo a la tabla `PRODUCTO`. Los estados y tipos cerrados se representan con enums, y `ValidacionDominio` es una clase auxiliar, no una entidad persistente. En cambio, `AsignacionFuncion`, `LineaVenta`, `PagoVenta`, `Devolucion` e `InscripcionCampana` son clases propias porque cada una representa una asociación con información o comportamiento relevante.

En el código actual no existe una clase `Gasolinera`; `Estacion` representa cada estación del dominio ampliado. Tampoco se ha añadido una entidad Java para cada elemento auxiliar de la base de datos si no necesita comportamiento propio.

Las entidades nuevas admiten el identificador técnico en el constructor. También ofrecen, en general, un constructor sin ese valor que asigna `0`, utilizado como convención para un objeto aún no persistido; la validación común admite IDs no negativos. No existe todavía una capa JDBC que asigne IDs generados ni un mecanismo que sincronice automáticamente las referencias con una base de datos.

## 2. Clases del dominio

Las clases siguientes son las que existen actualmente en `gasolinera.domain`; se indican sus responsabilidades y asociaciones implementadas.

### Estaciones (`gasolinera.domain.station`)

- **`Estacion`** contiene ID, código, nombre, dirección, teléfono y fecha de apertura.
- **`ConfiguracionFacturacion`** contiene una referencia a su `Estacion`, el código de configuración, la serie, el siguiente número y el pie del justificante. La clase requiere una estación al construirse; la creación conjunta y atómica de ambos registros no está implementada aquí.
- **`Surtidor`** referencia una `Estacion` y contiene código de equipo, número visible, combustible y estado.
- **`EstadoSurtidor`** define `OPERATIVO`, `AVERIADO` y `MANTENIMIENTO`.

### Personal (`gasolinera.domain.staff`)

- **`Empleado`** contiene sus identificadores de negocio, nombre, contactos, fecha de incorporación, estación y referencia opcional a otro `Empleado` como supervisor. Requiere teléfono o email y comprueba la cadena de supervisores accesible desde la referencia al construir el objeto.
- **`Funcion`** representa el código, nombre y descripción del catálogo de funciones. Su código se normaliza a mayúsculas.
- **`AsignacionFuncion`** vincula un empleado y una función durante un periodo, con campos opcionales específicos de cada función. Valida las fechas, los valores positivos y los campos que corresponden a los cuatro códigos reconocidos: `CAJERO`, `EXPENDEDOR`, `RESPONSABLE_TIENDA` y `JEFE_ESTACION`.

### Clientes y fidelización (`customer` y `loyalty`)

- **`Cliente`** representa el cliente del nuevo dominio: ID técnico, ID antiguo opcional, código de negocio, nombre, teléfono, email y matrícula opcional. Exige que se proporcione teléfono o email.
- **`LoteTarjeta`** representa el lote, fecha de recepción, cantidad positiva y estado.
- **`Tarjeta`** referencia su lote y opcionalmente un cliente. Valida que la fecha de asignación sea nula si no hay titular; si existe titular, exige fecha y que esta no sea anterior a la emisión.
- **`EstadoLoteTarjeta`** define `ABIERTO` y `CERRADO`.

### Catálogo y proveedores (`gasolinera.domain.catalog`)

- **`Producto`** es la clase abstracta común con ID, referencia, descripción, precio vigente y estado. Declara la validación de cantidad y calcula el importe redondeado a dos decimales con `HALF_UP`.
- **`ProductoEnvasado`** almacena el formato del envase y solo admite cantidades enteras positivas.
- **`ProductoGranel`** almacena una precisión de pesada entre 0 y 3 decimales y comprueba que la cantidad positiva no la supere.
- **`EstadoProducto`** define `ACTIVO` y `RETIRADO`.
- **`Proveedor`** contiene los datos de identificación y contacto del proveedor.
- **`AcuerdoSuministro`** referencia un proveedor y un producto y almacena la referencia del proveedor, el precio de compra positivo y el plazo no negativo.

### Ventas (`gasolinera.domain.sales`)

- **`Venta`** representa una venta con referencia de ticket, estación, empleado, cliente opcional, fecha/hora, estado, importe total y listas internas de líneas y pagos. Permite añadir líneas solo durante la preparación, requiere líneas para confirmar, calcula el total a partir de ellas y solo marca la venta como pagada si la suma de pagos coincide exactamente con dicho total.
- **`LineaVenta`** referencia la venta y el producto, conserva número de línea, cantidad, precio unitario e importe. La creación a través de `Venta.agregarLinea` toma el precio vigente del producto y calcula el importe; el constructor también permite representar los valores ya determinados. Comprueba la modalidad mediante `Producto.validarCantidad` y que el importe corresponda a cantidad por precio unitario con redondeo `HALF_UP`.
- **`PagoVenta`** referencia una venta y contiene fecha/hora, importe positivo y medio de pago. `Venta.agregarPago` comprueba que el pago corresponde a esa venta y que la suma no supera su total.
- **`Devolucion`** representa una devolución de una línea, el empleado que la registra, cantidad, motivo, fecha/hora, importe reintegrado y si es apta para reventa. Comprueba que la venta esté pagada, que la cantidad cumpla la modalidad del producto y que el reintegro no supere el importe de la línea.
- **`EstadoVenta`** define `PREPARACION`, `CONFIRMADA` y `PAGADA`.
- **`MedioPago`** define `EFECTIVO`, `TARJETA` y `OTRO`.

### Otras áreas

- **`PagoRepostaje`** (`refueling`) representa el pago de repostaje del nuevo dominio. Conserva un `idPagoAntiguo` opcional separado del ID técnico, referencia a `Cliente` y usa `BigDecimal` para importe y litros. Es distinto de `PagoVenta` y no reemplaza al modelo legado `Pagos`.
- **`Campana`** y **`InscripcionCampana`** (`campaign`) representan una campaña con sus fechas y la asociación fechada entre una campaña y un cliente.
- **`Incidencia`** (`incident`) referencia una estación y admite un empleado responsable opcional; contiene referencia, descripción, fecha/hora y estado. **`EstadoIncidencia`** enumera `ABIERTA`, `EN_CURSO` y `CERRADA`.
- **`HistorialPrecio`** (`pricing`) contiene referencia a producto, precio anterior, precio nuevo y fecha/hora; exige precios positivos y distintos. La clase representa los datos históricos, pero no registra cambios por sí misma. El registro automático está en el trigger SQL.
- **`Combustible`** (`shared`) define `GASOLINA` y `DIESEL`, que pueden usarse en surtidores y pagos de repostaje.
- **`ValidacionDominio`** (`shared`) centraliza comprobaciones de ID no negativo, cadenas obligatorias/opcionales y límites de longitud, valores monetarios y decimales positivos, así como enteros positivos.

## 3. Funciones del personal

`Empleado`, `Funcion` y `AsignacionFuncion` tienen responsabilidades distintas. El empleado conserva su identidad, estación y supervisor; `Funcion` describe el tipo de trabajo; `AsignacionFuncion` representa que ese empleado desempeñó esa función durante unas fechas y con unas condiciones concretas.

Esta separación permite representar varias funciones para una persona mediante varias asignaciones y conservar periodos anteriores: una función finalizada se expresa con `fechaFin`, y una recuperación de esa función se representa con otra instancia de `AsignacionFuncion` y otro periodo. Las condiciones específicas quedan en cada asignación y no en el empleado. El objeto valida que las fechas no estén invertidas y que sus campos específicos concuerden con uno de los cuatro códigos de función implementados.

El código no mantiene una colección de asignaciones dentro de `Empleado` ni compara varias asignaciones entre sí. Por tanto, permitir múltiples registros está previsto por el diseño de clases, pero la detección de periodos solapados para una misma pareja empleado-función no está implementada en Java. Tampoco se pueden añadir nuevas funciones con campos específicos sin ampliar la validación de `AsignacionFuncion`; actualmente los códigos distintos de los cuatro indicados se rechazan.

## 4. Modalidades de producto

`Producto` agrupa los datos y el cálculo comunes, y declara `validarCantidad` como comportamiento que depende de la modalidad. `ProductoEnvasado` y `ProductoGranel` implementan esa regla de forma distinta. Al ser `Producto` abstracta y sellada, las implementaciones permitidas en el código actual son precisamente esas dos clases.

Ambas modalidades validan cantidades positivas compatibles con `DECIMAL(10,3)`. Envasado rechaza cualquier cantidad fraccionaria; granel comprueba que la escala efectiva no supere la precisión configurada, que debe estar entre 0 y 3. `calcularImporte` valida primero la cantidad y después multiplica por el precio, redondeando el resultado a dos decimales con `RoundingMode.HALF_UP`. `LineaVenta` reutiliza esa validación y conserva el precio unitario aplicado, de modo que el cálculo de una línea no depende de futuras modificaciones del precio del catálogo.

La modalidad concreta no se cambia en la instancia: se determina por el subtipo. El modelo no incluye un mecanismo para cambiar un producto envasado a granel; para el diseño actual son productos de clases distintas.

## 5. Relaciones entre objetos

Las asociaciones se representan mediante referencias tipadas, no mediante IDs sueltos en las relaciones del dominio. Por ejemplo, `Surtidor` referencia una `Estacion`; `Empleado`, una estación y opcionalmente un supervisor; `AsignacionFuncion`, un empleado y una función; `Tarjeta`, un lote y opcionalmente un cliente; y `AcuerdoSuministro`, un proveedor y un producto.

En ventas, `Venta` referencia estación, empleado y cliente opcional, y mantiene colecciones privadas de `LineaVenta` y `PagoVenta`. Cada línea vuelve a referenciar su venta y producto; cada pago referencia su venta. Las colecciones se exponen como copias no modificables mediante `List.copyOf`. `LineaVenta` mantiene las devoluciones registradas por su método `registrarDevolucion`. Las asociaciones de campañas e incidencias también se representan con referencias a sus objetos asociados.

Esta forma facilita construir y revisar un grafo de objetos relacionado sin conectarse a una base de datos. No significa que todas las reglas de integridad entre múltiples objetos o registros queden garantizadas: por ejemplo, la unicidad de negocio, los límites del lote de tarjetas, los acuerdos mínimos y las reglas concurrentes requieren persistencia y controles adicionales.

## 6. Tipos de datos y validaciones

Los importes y cantidades del nuevo dominio usan `BigDecimal`, evitando los errores de representación binaria propios de `double` en operaciones decimales. `ValidacionDominio.dinero` exige escala máxima de dos decimales sin redondear silenciosamente, aplica el signo permitido y limita el valor a la precisión de `DECIMAL(10,2)`. Para cantidades decimales, `decimalPositivo` recibe precisión y escala; `PagoRepostaje` usa los límites `30,15` del `DECIMAL(30,15)` de litros, mientras que cantidades de venta se validan con `10,3`.

Los constructores validan que los datos obligatorios existan, normalizan cadenas recortando espacios, limitan longitudes y rechazan valores fuera de rango. Las fechas y asociaciones obligatorias se comprueban con `Objects.requireNonNull`; los campos opcionales se conservan como `null` cuando faltan. Los enums limitan los estados, medios de pago y combustible a los valores definidos por el código.

El redondeo monetario explícito a céntimos se realiza con `HALF_UP` al calcular importes de producto y líneas. Para devoluciones, `LineaVenta.registrarDevolucion` calcula un importe acumulado proporcional a la cantidad ya devuelta, redondea ese acumulado a dos decimales y resta lo reintegrado previamente; cuando se devuelve toda la cantidad, usa el importe original exacto de la línea. En ese método se comprueba que la cantidad acumulada no supere la comprada. Este control opera sobre la lista en memoria del objeto y no es una garantía contra escrituras concurrentes en una futura persistencia JDBC.

## 7. Compatibilidad con el proyecto anterior

`gasolinera.model.Clientes` y `gasolinera.model.Pagos` se han conservado en sus paquetes y no se han sustituido por las nuevas clases `gasolinera.domain.customer.Cliente` y `gasolinera.domain.refueling.PagoRepostaje`. El modelo anterior conserva sus nombres, campos, constructores y comportamiento usado por los repositorios actuales: `Clientes` mantiene `id`, `nombre`, `tel` y `matricula`; `Pagos` conserva identificador, identificador de cliente, fecha, importe, litros y combustible. En particular, el modelo legado `Pagos` sigue usando `double` para importe y litros, tal como está implementado actualmente.

Las implementaciones existentes `Ficheros` y `Jsons` continúan trabajando con `Clientes` y `Pagos` mediante `ClienteRepository` y `PagoRepository`. No se han cambiado esas clases de persistencia en esta etapa. El nuevo modelo `Cliente` y `PagoRepostaje` aún no está conectado a CSV/JSON y no existe conversión automática entre el modelo heredado y el ampliado. Así se mantiene el flujo previo sin afirmar que las nuevas entidades ya se puedan guardar en los formatos existentes.

## 8. Separación de responsabilidades

Las clases de `gasolinera.domain` contienen datos, asociaciones y reglas propias del dominio, como validar cantidades, calcular importes y controlar el ciclo de una venta en memoria. No importan clases JDBC, no abren conexiones y no ejecutan SQL. La persistencia de archivos sigue aislada en `gasolinera.persistence.csv.Ficheros` y `gasolinera.persistence.json.Jsons`, y el flujo legado accede a ella mediante repositorios.

El trigger SQL de historial de precios sigue siendo responsabilidad de la base de datos y no de `HistorialPrecio` ni de `Producto`. La integración JDBC del nuevo dominio y la traducción entre objetos y filas son trabajo futuro; este documento no afirma que estén implementadas.
