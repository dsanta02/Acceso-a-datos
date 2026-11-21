# Gestión de Gasolinera

Aplicación de consola en Java 21 para registrar clientes y pagos de repostaje.

## Estructura Java

- `gasolinera.model`: entidades heredadas `Clientes` y `Pagos` para mantener los formatos
  CSV/JSON existentes.
- `gasolinera.domain`: modelo ampliado, organizado por áreas funcionales (`station`, `staff`,
  `customer`, `loyalty`, `catalog`, `sales`, `refueling`, `campaign`, `incident`, `pricing`
  y elementos compartidos en `shared`).
- `Producto` es una clase abstracta con `ProductoEnvasado` y `ProductoGranel`; valida cantidades
  y calcula importes usando `BigDecimal` y redondeo `HALF_UP`.
- `Venta` contiene sus líneas y pagos, controla los estados de preparación, confirmación y pago.
  Las devoluciones sucesivas se registran desde `LineaVenta` usando el precio original.
- `gasolinera.repository`: contratos para clientes y pagos.
- `gasolinera.service`: gestión de clientes y pagos.
- `gasolinera.persistence.csv` y `gasolinera.persistence.json`: implementaciones de almacenamiento.
- `gasolinera.migration`: migración de CSV a JSON.
- `gasolinera.console`: interfaz de consola.
- `gasolinera.Main`: punto de entrada y selección de almacenamiento.

## Funcionalidades

- Dar de alta, listar y buscar clientes.
- Registrar y consultar pagos.
- Elegir CSV o JSON al iniciar.
- Migrar los datos CSV a JSON.

## Almacenamiento

Al ejecutar desde la raíz del proyecto, los datos se guardan en:

- `src/src/clientes.csv` y `src/src/pagos.csv`
- `src/src/clientesJson.json` y `src/src/PagosJson.json`

La aplicación pregunta antes de sustituir un JSON que no pueda leer. Si se confirma la recuperación desde CSV, conserva los JSON anteriores en una carpeta `backup-json-*` junto a los archivos.

Las clases de gestión usan `ClienteRepository` y `PagoRepository`; `Ficheros` y `Jsons` son implementaciones intercambiables. Esto permite añadir posteriormente una implementación JDBC sin acoplar la gestión a los archivos.

## Ejecución

Desde IntelliJ IDEA, ejecutar `Main` con el directorio de trabajo configurado en la raíz del proyecto. También puede compilarse y probarse con Maven:

```text
mvn test
```

Para ver el comportamiento de productos envasados y a granel sin persistencia, ejecutar
`gasolinera.demo.DominioDemo`.
