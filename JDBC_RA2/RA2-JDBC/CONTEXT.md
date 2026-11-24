# Contexto — RA2 JDBC, Jardín Botánico

Datos observados en el repositorio. Las reglas de trabajo están en el `AGENTS.md` de la raíz.

## 1. Qué es esta práctica

- Proyecto Gradle llamado `ra2-jdbc-alumnado` (`settings.gradle.kts`).
- Según `README.md`, es una **entrega para alumnado de los bloques 01 y 02** de RA2; se abre esta carpeta como proyecto en IntelliJ IDEA.
- Requiere **JDK 21** también para Gradle; se usa el wrapper incluido.
- `build.gradle.kts`: codificación UTF-8, JUnit Jupiter 6.1.3 con `useJUnitPlatform()`, dependencias `runtimeOnly` de SQLite JDBC 3.53.4.0 y MySQL Connector/J 26.7.0.
- Dominio de ejemplo: Jardín Botánico. Los ejemplos SQLite usan `data/jardin-botanico.db` por defecto; según `README.md`, ese fichero y las carpetas generadas localmente no forman parte de la entrega.

## 2. Mapa de carpetas

| Ruta | Contenido observado |
| --- | --- |
| `sql/sqlite/` | `01-esquema.sql`, `02-datos.sql` del Jardín Botánico para SQLite. |
| `sql/mysql/` | `01-esquema.sql`, `02-datos.sql` del Jardín Botánico para MySQL. |
| `sql/` | `botanico.sql`, `prepararMySQL.sql` y `ra2_botanico.mwb` (modelo de Workbench). |
| `src/main/java/es/iesluisbraille/accesodatos/jardin/bloque02/` | `CrearBaseDatos`, `PrimeraConexionSqlite`, `PrimeraConexionMysql`, `MetadatosSqlite`, `ErrorDeConexion`. |
| `src/main/java/.../bloque03/` | `CrearTabla`, `VariasSentencias`, `ErrorSql`. |
| `src/test/java/.../bloque02/` | `ConexionSqliteTest.java`, prueba de conexión SQLite. |
| `tools/` | `generar_datos.py`, `preparar_sqlite.py`, `validar_sqlite.py`: apoyo para datos y validación SQLite. |
| `compose.yaml` | Servicio MySQL 8.4 local para la práctica. Las credenciales y el puerto están en el propio archivo: no copiarlos aquí ni al repositorio. |
| `gradle/`, `gradlew`, `gradlew.bat` | Wrapper y configuración de Gradle. |

## 3. Tareas de ejecución definidas

Según `build.gradle.kts`, todas en el grupo `application` y sobre el paquete `es.iesluisbraille.accesodatos.jardin.bloque02`:

| Tarea | Clase ejecutada |
| --- | --- |
| `runSqlite` | `PrimeraConexionSqlite` |
| `runSqliteMetadata` | `MetadatosSqlite` |
| `runMysql` | `PrimeraConexionMysql` |
| `runErrorConexion` | `ErrorDeConexion` |

Las pruebas se ejecutan con las tareas estándar de Gradle (`test`).

## 4. Estado y pendientes

- No hay en este contexto evidencia de que se hayan ejecutado las tareas, las pruebas ni los scripts SQL: cualquier resultado debe comprobarse antes de afirmarlo.
- No se ha localizado un enunciado propio de esta práctica dentro de la carpeta; el resumen anterior procede solo de `README.md`, `build.gradle.kts`, `settings.gradle.kts`, `compose.yaml`, `tools/` y la estructura de `src/` y `sql/`.
- Si necesitas requisitos concretos de los bloques 01, 02 o 03, pregúntalos en lugar de deducirlos.
