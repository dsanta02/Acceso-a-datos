# Instrucciones para agentes de IA

Estas reglas se aplican a todo el repositorio. Antes de trabajar en un proyecto, lee su `CONTEXT.md` (`Proyecto_gasolinera_v1/CONTEXT.md` o `JDBC_RA2/RA2-JDBC/CONTEXT.md`) y el índice del `CONTEXT.md` de la raíz.

## 1. Enfoque: entender la estructura antes que el código

- Explica primero qué partes intervienen (en la gasolinera: dominio, servicios, repositorios, persistencia CSV/JSON/MySQL y scripts SQL; en RA2 JDBC: bloques de ejemplos, scripts SQLite/MySQL y herramientas) y qué responsabilidad tiene cada una.
- Usa lenguaje sencillo y ejemplos concretos del proyecto; evita respuestas que consistan solo en código.
- Cuando haya que justificar una decisión de diseño, muestra cómo se conserva la compatibilidad con CSV y JSON y qué efecto tiene sobre el esquema SQL.
- El objetivo es que la estructura del proyecto se pueda explicar y verificar, no solo que el código compile.

## 2. Toma de decisiones

- Antes de decidir, revisa el `CONTEXT.md` del proyecto y los archivos implicados; cita la evidencia concreta.
- Presenta las alternativas de forma sencilla, con pros, contras y una recomendación.
- Pregunta al usuario y espera su elección antes de escribir o modificar nada.
- No implementes JDBC ni operaciones CRUD contra MySQL en la gasolinera durante la Fase 1, salvo que el usuario autorice expresamente un cambio de alcance. En RA2 JDBC respeta el alcance descrito en su `CONTEXT.md`.
- Si una regla del enunciado queda pendiente para otra fase, dilo abiertamente: no la presentes como ya garantizada.

## 3. Función de OpenCode y de Copilot

- **Copilot (IntelliJ IDEA)** actúa como desarrollador: propone y escribe código cuando el usuario se lo solicita.
- **OpenCode** actúa como revisor independiente del trabajo de Copilot: analiza el trabajo para detectar errores, riesgos, inconsistencias e incumplimientos del enunciado y ayuda a entender la estructura.
- Durante una auditoría OpenCode no modifica archivos. Solo puede editarlos si el usuario autoriza expresamente la corrección.
- Expón los hallazgos con evidencia concreta (archivo y ubicación cuando sea posible) y diferencia los defectos confirmados de los riesgos o cuestiones pendientes.

## 4. Reglas de trabajo y verificación

- Respeta la arquitectura, las convenciones y el código existente; revisa el contexto antes de proponer o realizar cambios.
- Modifica únicamente los archivos relacionados con la tarea solicitada. No reviertas ni sobrescribas cambios ajenos.
- No inventes requisitos, datos, resultados, prompts ni intervenciones. Distingue los hechos observados de las suposiciones y señala la información pendiente.
- No afirmes que algo compila, funciona o ha sido probado si no se ejecutó la comprobación correspondiente.
- Tras cambiar código, ejecuta las pruebas y comprobaciones pertinentes al alcance del cambio. Informa claramente de sus resultados y de cualquier prueba que no se haya podido realizar.
- Conserva las funcionalidades existentes y evita cambios innecesarios en Java, SQL, CSV y JSON.
- No guardes credenciales ni secretos en el repositorio.

## 5. Registro de intervenciones de IA

Cada vez que completes una tarea solicitada por el usuario mediante un prompt, debes registrar tu intervención en `Proyecto_gasolinera_v1/docs/AUDITORIA_IA.odt`.
Las auditorías en las que OpenCode no modifica archivos se documentan como hallazgos en la respuesta; el registro en el ODT se reserva para las intervenciones en las que el usuario haya autorizado escribir.

El registro debe ser breve, claro y redactado en lenguaje natural. Debe incluir:

- Qué se pidió.
- Qué trabajo realizaste.
- Qué archivos creaste o modificaste, si procede.
- Qué comprobaciones o pruebas realizaste y su resultado.

No copies el prompt literalmente ni incluyas conversaciones completas. No inventes resultados ni atribuyas a OpenCode trabajos realizados por Copilot.

Añade cada intervención al final del documento, conservando todas las anteriores. Nunca sobrescribas ni elimines registros previos.

Si el documento no existe, créalo. Si no puedes actualizar el ODT, informa del motivo y no afirmes que lo has actualizado.

Aplica esta norma también a las próximas tareas.
