# Instrucciones para GitHub Copilot

## 1. Función

Actúa como agente desarrollador del proyecto: implementa las tareas solicitadas y corrige los errores cuando el usuario autorice su corrección. No realices cambios fuera del alcance autorizado.

## 2. Contexto

- Proyecto académico de gasolinera de 2.º DAM.
- Fase 1: diseño y preparación del modelo MySQL.
- El diseño aprobado de la base de datos consta de 21 tablas.
- La aplicación Java debe conservar la compatibilidad con sus persistencias CSV y JSON.
- Las consultas y la persistencia CRUD con MySQL no forman parte de esta fase; solo se ha preparado la conexión básica en `persistence/mysql` (`ConexionMySQL` y `PruebaConexionMySQL`).

## 3. Reglas de trabajo

- Respeta la arquitectura, los patrones y las convenciones ya existentes; examina el código relevante antes de editar.
- No implementes JDBC ni operaciones CRUD contra MySQL en esta fase, salvo que el usuario autorice expresamente un cambio de alcance.
- Modifica únicamente los archivos relacionados con la tarea solicitada. No alteres ni reviertas cambios ajenos.
- Mantén las soluciones claras y adecuadas al nivel de un estudiante de 2.º DAM.
- No inventes requisitos, comportamientos, datos ni resultados. Si falta información, indícalo.
- Después de modificar archivos, ejecuta las pruebas y comprobaciones pertinentes al cambio. No afirmes que algo se ha probado si no se ejecutó.

## 4. Registro de intervenciones de IA

Cada vez que completes una tarea solicitada por el usuario mediante un prompt, registra tu intervención en `docs/AUDITORIA_IA.odt`.

El registro debe ser breve, claro y redactado en lenguaje natural. Debe incluir:

- Qué se pidió.
- Qué trabajo realizaste.
- Qué archivos creaste o modificaste, si procede.
- Qué comprobaciones o pruebas realizaste y su resultado.

No copies el prompt literalmente ni incluyas conversaciones completas. No inventes resultados ni atribuyas a Copilot trabajos realizados por OpenCode.

Añade cada intervención al final del documento, conservando todas las anteriores. Nunca sobrescribas ni elimines registros previos.

Si el documento no existe, créalo. Si no puedes actualizar el ODT, informa del motivo y no afirmes que lo has actualizado.

Aplica esta norma también a las próximas tareas.

## 5. Verificación y entrega

Antes de terminar una tarea, informa de los archivos modificados y del resultado de las pruebas o comprobaciones. Menciona las comprobaciones no ejecutadas o bloqueadas cuando corresponda.
