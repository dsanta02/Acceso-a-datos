# Instrucciones para agentes de IA

## 1. Contexto del proyecto

- Este es un proyecto académico de gasolinera de 2.º DAM.
- La ampliación corresponde a la Fase 1: diseño y preparación del modelo MySQL.
- El diseño de la base de datos aprobado contiene 21 tablas.
- La aplicación Java existente debe conservar su compatibilidad con los formatos de persistencia CSV y JSON.
- La fase actual no incluye consultas ni operaciones CRUD contra MySQL; solo se ha preparado la conexión básica en `persistence/mysql` (`ConexionMySQL` y `PruebaConexionMySQL`).

## 2. Reglas generales

- Respeta la arquitectura, las convenciones y el código existente; revisa el contexto antes de proponer o realizar cambios.
- No implementes JDBC ni operaciones CRUD contra MySQL en esta fase, salvo que el usuario autorice expresamente un cambio de alcance.
- Modifica únicamente los archivos relacionados con la tarea solicitada. No reviertas ni sobrescribas cambios ajenos.
- No inventes requisitos, datos, resultados, prompts ni intervenciones. Distingue los hechos observados de las suposiciones y señala la información pendiente.
- No afirmes que algo compila, funciona o ha sido probado si no se ejecutó la comprobación correspondiente.
- Tras cambiar código, ejecuta las pruebas y comprobaciones pertinentes al alcance del cambio. Informa claramente de sus resultados y de cualquier prueba que no se haya podido realizar.
- Conserva las funcionalidades existentes y evita cambios innecesarios en Java, SQL, CSV y JSON.

## 3. Función de OpenCode

- OpenCode actúa como revisor independiente del trabajo realizado por Copilot.
- Analiza el trabajo para detectar errores, riesgos, inconsistencias e incumplimientos del enunciado.
- Durante una auditoría, no modifiques archivos. Solo puedes editarlos si el usuario autoriza expresamente la corrección.
- Expón los hallazgos con evidencia concreta (archivo y ubicación cuando sea posible), y diferencia los defectos confirmados de los riesgos o cuestiones pendientes.

## 4. Registro de intervenciones de IA

Cada vez que completes una tarea solicitada por el usuario mediante un prompt, debes registrar tu intervención en `docs/AUDITORIA_IA.odt`.

El registro debe ser breve, claro y redactado en lenguaje natural. Debe incluir:
- Qué se pidió.
- Qué trabajo realizaste.
- Qué archivos creaste o modificaste, si procede.
- Qué comprobaciones o pruebas realizaste y su resultado.

No copies el prompt literalmente ni incluyas conversaciones completas. No inventes resultados ni atribuyas a OpenCode trabajos realizados por Copilot.

Añade cada intervención al final del documento, conservando todas las anteriores. Nunca sobrescribas ni elimines registros previos.

Si el documento no existe, créalo. Si no puedes actualizar el ODT, informa del motivo y no afirmes que lo has actualizado.

Aplica esta norma también a las próximas tareas.
