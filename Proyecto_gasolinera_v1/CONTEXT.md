# Contexto del proyecto — Gasolinera, Fase 1

Información factual del caso. Las reglas de trabajo están en el `AGENTS.md` de la raíz del repositorio.

## 1. Enunciado y fase

- Proyecto académico de **Acceso a Datos, 2.º de DAM**.
- Trabajo: **Ampliación de la aplicación de gasolinera con MySQL — Fase 1: diseño de datos y preparación del modelo Java**.
- Enunciado disponible en `src/Caso_practico_Gasolinera_MySQL_Fase_1.md`.
- La aplicación anterior trabaja con las clases `Gasolinera` y `Pagos` y conserva información en **CSV y JSON**. La empresa quiere añadir una persistencia MySQL mediante JDBC sin perder esas alternativas ni sus funcionalidades.
- Se mantiene la separación entre modelo, lógica de aplicación y almacenamiento. Cualquier adaptación debe justificarse y comprobarse contra CSV y JSON.
- Esta fase comprende: diseño relacional, base de datos y datos de ejemplo, clases Java del dominio y registro automático de cambios de precio **en MySQL mediante trigger**. Las consultas y operaciones de persistencia desde Java se dejan para la fase siguiente.
- El enunciado no proporciona catálogo de tablas ni jerarquía de clases resuelta, y **no pide diagrama entidad-relación**: las decisiones hay que deducirlas del relato.

## 2. Estado observado en el repositorio

Según `README.md`, `docs/` y los archivos presentes. No implica que los scripts se hayan ejecutado sobre MySQL.

- Aplicación de consola heredada y persistencias CSV/JSON operativas; las entidades `Clientes` y `Pagos` de `gasolinera.model` siguen siendo las usadas por esa persistencia.
- Modelo de dominio ampliado organizado por áreas: `station`, `staff`, `customer`, `loyalty`, `catalog`, `sales`, `refueling`, `campaign`, `incident`, `pricing` y `shared`.
- `gasolinera.demo.DominioDemo` muestra en memoria productos envasados y a granel, cálculo de importes, rechazo de cantidades no válidas, venta con dos pagos y dos devoluciones parciales. No necesita MySQL.
- Esquema de **21 tablas** con scripts en `sql/`: `01_esquema.sql`, `02_triggers.sql`, `03_datos.sql` y `04_comprobaciones.sql`.
- Documentación: `docs/MODELO_RELACIONAL.md` (diccionario y reglas SQL), `docs/JUSTIFICACION_MODELO_JAVA.md` (decisiones del dominio), `docs/REGLAS_NEGOCIO.md` (garantías y pendientes) y `docs/AUDITORIA_IA.odt`.
- Conexión JDBC preparada únicamente en `persistence/mysql`: `ConexionMySQL` y `PruebaConexionMySQL`. Configuración por variables de entorno `GASOLINERA_DB_URL`, `GASOLINERA_DB_USER` y `GASOLINERA_DB_PASSWORD`. **No hay repositorios ni operaciones CRUD MySQL.**

### Mapa de carpetas

| Ruta | Función |
| --- | --- |
| `src/main/java/gasolinera/model` | Entidades heredadas `Clientes` y `Pagos`, conservadas para CSV/JSON. |
| `src/main/java/gasolinera/domain` | Modelo ampliado, dividido por áreas funcionales. |
| `src/main/java/gasolinera/repository` | Contratos `ClienteRepository` y `PagoRepository`. |
| `src/main/java/gasolinera/service` | Gestión de clientes y pagos heredados. |
| `src/main/java/gasolinera/persistence/csv` y `json` | Almacenamiento en archivos. |
| `src/main/java/gasolinera/persistence/mysql` | Solo apertura de conexión y prueba manual; sin acceso a datos. |
| `src/main/java/gasolinera/migration` | Migración de CSV a JSON. |
| `src/main/java/gasolinera/demo` | Demostración del modelo nuevo sin base de datos. |
| `sql/` | Esquema, triggers, datos de ejemplo y comprobaciones. |
| `docs/` | Modelo relacional, reglas de negocio, justificación Java y auditoría de IA. |

## 3. Decisiones de diseño ya tomadas

- **Esquema relacional:** todas las tablas, incluidas asociaciones, asignaciones, líneas e históricos, tienen una clave primaria formada por un único entero autoincremental. Los códigos y números de negocio mantienen su unicidad mediante restricciones, sin ser la clave primaria.
- Las 21 tablas documentadas son: `ESTACION`, `CONFIGURACION_FACTURACION`, `SURTIDOR`, `EMPLEADO`, `FUNCION`, `ASIGNACION_FUNCION`, `CLIENTE`, `LOTE_TARJETA`, `TARJETA`, `PRODUCTO`, `PROVEEDOR`, `ACUERDO_SUMINISTRO`, `CAMPANA`, `INSCRIPCION_CAMPANA`, `VENTA`, `LINEA_VENTA`, `PAGO_VENTA`, `DEVOLUCION`, `INCIDENCIA`, `HISTORIAL_PRECIO` y `PAGO_REPOSTAJE`.
- **Funciones del personal:** `FUNCION` y `ASIGNACION_FUNCION` conservan fecha de inicio, fecha de fin y condiciones de cada periodo. Permiten varias funciones simultáneas de tipos distintos, periodos sin funciones y recuperar una función después; no solapan periodos de una misma función. La supervisión actual se guarda en `EMPLEADO`, sin historial.
- **Modalidades de producto:** jerarquía `Producto` → `ProductoEnvasado` / `ProductoGranel`. Java valida cantidades (enteras para envasados, precisión de pesada para granel) y calcula importes; la modalidad es estable. El precio cobrado se guarda en `LINEA_VENTA`, de modo que cambios posteriores del catálogo no alteran tickets antiguos.
- **Historial de precios:** se implementa con trigger en MySQL en `02_triggers.sql`. Solo registra cambios reales de precio: no registra el alta, ni guardar el mismo precio, ni modificar la descripción. Con InnoDB, un cambio dentro de una transacción que se deshace elimina también su entrada histórica.
- **Compatibilidad:** `Pagos` se conserva sin reinterpretar registros antiguos; los cobros de tienda se modelan por separado (`PagoVenta`). El trigger de precios no sustituye al historial de funciones ni al de ventas/devoluciones.
- El esquema usa InnoDB; algunos `CHECK` se trasladaron a triggers por incompatibilidad declarada con MySQL 8.0.46 (véase `README.md`).

## 4. Reglas pendientes para la siguiente fase

No deben presentarse como rechazos automáticos actuales. Según `README.md` y `docs/REGLAS_NEGOCIO.md`:

- Alta conjunta de estación con su configuración y al menos un surtidor; impedir borrar el último surtidor.
- Detección de ciclos indirectos de supervisión.
- Evitar periodos solapados de la misma función para un mismo empleado.
- Garantizar que todo proveedor y todo producto conserven al menos un acuerdo de suministro.
- Confirmar ventas solo con líneas y con total coincidente; marcar como pagada solo si la suma de pagos coincide exactamente.
- Autorizar devoluciones solo sobre ventas pagadas y controlar, también con operaciones simultáneas, cantidad e importe acumulado reintegrado.
- Validar en persistencia la cantidad y precisión de productos a granel en líneas y devoluciones.
- Impedir que las tarjetas emitidas superen las recibidas en el lote.

## 5. Comprobaciones y evidencia

- `README.md` registra `mvn test` ejecutado el 22/11/2026: 9 pruebas, 0 fallos, 0 errores. Cubre Java/CSV/JSON, no los scripts SQL.
- **Pendiente:** ejecutar los cuatro scripts SQL sobre una base MySQL vacía y guardar evidencia de restricciones, trigger y `ROLLBACK`; no desactivar claves foráneas.
- **Pendiente:** probar la conexión JDBC con MySQL accesible y las variables de entorno configuradas.
- **Pendiente:** completar la trazabilidad de las intervenciones de IA en `docs/AUDITORIA_IA.odt` solo con datos reales.

## 6. Operaciones JDBC previstas (no implementadas)

Propuestas para una fase posterior, según `README.md`:

1. **Alta transaccional de estación:** estación, configuración de facturación y al menos un surtidor, todo confirmado o nada.
2. **Confirmación y cobro de venta:** líneas, precios aplicados, cálculo del total y pagos que han de sumar exactamente ese total.
3. **Devolución parcial:** sobre línea de venta pagada, con control de cantidades e importes acumulados y protección frente a concurrencia.

## 7. Fuentes

- Enunciado: `src/Caso_practico_Gasolinera_MySQL_Fase_1.md`.
- `README.md`, `docs/MODELO_RELACIONAL.md`, `docs/JUSTIFICACION_MODELO_JAVA.md`, `docs/REGLAS_NEGOCIO.md` y scripts de `sql/`.

## 8. ## Restricciones de acceso a bases de datos

- El único mecanismo permitido para conectar e interactuar con bases de datos es **JDBC (Java Database Connectivity)**.
- Solo se permite utilizar los drivers JDBC oficiales correspondientes al sistema gestor de bases de datos utilizado.
- Queda prohibido utilizar cualquier alternativa a JDBC, incluyendo ORM (Hibernate, JPA), frameworks de persistencia, bibliotecas externas de acceso a datos o cualquier otra tecnología que abstraiga o sustituya JDBC.
- Las operaciones de conexión, consultas, inserciones, modificaciones y eliminaciones deben realizarse utilizando exclusivamente las clases e interfaces de JDBC, como `Connection`, `DriverManager`, `PreparedStatement`, `Statement` y `ResultSet`.
- No introducir dependencias, frameworks ni herramientas adicionales de persistencia sin autorización expresa.
- Mantener la implementación sencilla y adecuada al nivel de un proyecto académico de 2.º DAM.