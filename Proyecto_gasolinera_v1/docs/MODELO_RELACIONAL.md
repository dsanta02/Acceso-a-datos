# Modelo relacional de la gasolinera

Este documento describe el esquema implementado en `sql/01_esquema.sql` y los triggers definidos en `sql/02_triggers.sql`. La referencia normativa para tipos, restricciones y relaciones es el SQL; este documento no añade ni altera reglas. No incluye diagrama entidad-relación.

## Configuración común

- Base de datos: `gasolinera`, creada con `utf8mb4` y cotejamiento `utf8mb4_0900_ai_ci`.
- Las 21 tablas usan `InnoDB`, `utf8mb4` y `utf8mb4_0900_ai_ci`.
- Todas tienen una PK técnica `id INT NOT NULL AUTO_INCREMENT`.
- Salvo que se indique lo contrario, los campos no nulos no tienen valor predeterminado.
- Las claves foráneas usan `ON UPDATE CASCADE`. Para `ON DELETE`, rige la acción indicada en cada relación; no hay cascadas de borrado.
- MySQL aplica los `CHECK` en versiones que los soportan (el esquema indica MySQL 8.0.16 o posterior).

## Tablas

### 1. `ESTACION`

**Finalidad:** datos identificativos y de apertura de una estación.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_estacion` |
| `codigo` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_estacion_codigo` |
| `nombre` | `VARCHAR(100)` | NOT NULL | — |
| `direccion` | `VARCHAR(200)` | NOT NULL | — |
| `telefono` | `VARCHAR(20)` | NOT NULL | — |
| `fecha_apertura` | `DATE` | NOT NULL | — |

**Reglas garantizadas:** identificador técnico único y código de estación único. Que cada estación se cree conjuntamente con configuración de facturación y al menos un surtidor se debe garantizar desde la aplicación mediante una operación transaccional; no lo garantiza esta tabla por sí sola.

### 2. `CONFIGURACION_FACTURACION`

**Finalidad:** configuración de emisión de tickets de una estación.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_configuracion_facturacion` |
| `id_estacion` | `INT` | NOT NULL | UNIQUE `uq_configuracion_estacion`; FK `fk_configuracion_estacion` |
| `codigo` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_configuracion_codigo` |
| `serie_ticket` | `VARCHAR(20)` | NOT NULL | — |
| `siguiente_numero` | `INT` | NOT NULL, DEFAULT `1` | CHECK `ck_configuracion_siguiente_numero`: `siguiente_numero > 0` |
| `pie_justificante` | `VARCHAR(255)` | NOT NULL | — |

**FK:** `id_estacion` → `ESTACION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** una configuración no puede repetirse por estación y su código es único; la FK impide configuraciones para estaciones inexistentes. La unicidad de `id_estacion` limita a una configuración por estación. La existencia obligatoria de configuración para toda estación debe imponerse en el alta transaccional desde la aplicación.

### 3. `SURTIDOR`

**Finalidad:** surtidores pertenecientes a estaciones.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_surtidor` |
| `id_estacion` | `INT` | NOT NULL | FK `fk_surtidor_estacion` |
| `codigo_equipo` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_surtidor_equipo` |
| `numero_visible` | `INT` | NOT NULL | CHECK `ck_surtidor_numero`: `numero_visible > 0`; UNIQUE compuesto `uq_surtidor_numero_estacion` (`id_estacion`, `numero_visible`) |
| `combustible` | `VARCHAR(20)` | NOT NULL | CHECK `ck_surtidor_combustible`: `GASOLINA` o `DIESEL` |
| `estado` | `VARCHAR(20)` | NOT NULL | CHECK `ck_surtidor_estado`: `OPERATIVO`, `AVERIADO` o `MANTENIMIENTO` |

**FK:** `id_estacion` → `ESTACION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** equipo único globalmente, numeración visible única dentro de cada estación, número positivo y valores permitidos para combustible y estado. Que una estación tenga al menos un surtidor y no retirar el último requiere control de aplicación/transacción.

### 4. `EMPLEADO`

**Finalidad:** identidad, contacto, adscripción actual y supervisor actual de cada empleado.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_empleado` |
| `numero_empleado` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_empleado_numero` |
| `documento` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_empleado_documento` |
| `nombre` | `VARCHAR(100)` | NOT NULL | — |
| `telefono` | `VARCHAR(20)` | NULL | — |
| `email` | `VARCHAR(254)` | NULL | — |
| `fecha_incorporacion` | `DATE` | NOT NULL | — |
| `id_estacion` | `INT` | NOT NULL | FK `fk_empleado_estacion` |
| `id_supervisor` | `INT` | NULL | FK autorreferente `fk_empleado_supervisor` |

**CHECK:** `ck_empleado_contacto`: al menos uno entre `telefono` y `email` debe ser no nulo. No exige que el valor no sea una cadena vacía.

**FK:**
- `id_estacion` → `ESTACION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_supervisor` → `EMPLEADO(id)`, `ON DELETE SET NULL ON UPDATE CASCADE`.

**Trigger:** `trg_empleado_no_autosupervision_bi` y `trg_empleado_no_autosupervision_bu` rechazan un valor de supervisor igual a `NEW.id` en inserción o actualización.

**Reglas garantizadas:** número de empleado y documento únicos; existencia de estación y supervisor referenciados; contacto telefónico o email no nulo; la condición directa de autosupervisión comprobada por los triggers. Los ciclos indirectos de supervisión y las reglas de asignación/historial de estación no están garantizados por el esquema actual.

### 5. `FUNCION`

**Finalidad:** catálogo de funciones laborales.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_funcion` |
| `codigo` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_funcion_codigo` |
| `nombre` | `VARCHAR(60)` | NOT NULL | — |
| `descripcion` | `VARCHAR(200)` | NULL | — |

**Reglas garantizadas:** identificador técnico y código de función únicos. Que el catálogo contenga un conjunto concreto de funciones no se restringe mediante `CHECK`.

### 6. `ASIGNACION_FUNCION`

**Finalidad:** periodos y condiciones específicas de cada función asignada a un empleado.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_asignacion_funcion` |
| `id_empleado` | `INT` | NOT NULL | FK `fk_asignacion_empleado` |
| `id_funcion` | `INT` | NOT NULL | FK `fk_asignacion_funcion` |
| `fecha_inicio` | `DATE` | NOT NULL | — |
| `fecha_fin` | `DATE` | NULL | CHECK `ck_asignacion_fechas`: NULL o no anterior a `fecha_inicio` |
| `limite_devoluciones` | `DECIMAL(10,2)` | NULL | CHECK `ck_asignacion_valores_positivos`: si no nulo, mayor que cero |
| `numero_acreditacion` | `VARCHAR(30)` | NULL | Pareja con `fecha_caducidad_acreditacion` comprobada por `ck_asignacion_acreditacion_completa` |
| `fecha_caducidad_acreditacion` | `DATE` | NULL | Pareja con `numero_acreditacion` comprobada por `ck_asignacion_acreditacion_completa` |
| `presupuesto_compras` | `DECIMAL(10,2)` | NULL | CHECK `ck_asignacion_valores_positivos`: si no nulo, mayor que cero |
| `nivel_autorizacion` | `INT` | NULL | CHECK `ck_asignacion_valores_positivos`: si no nulo, mayor que cero |

**CHECK adicional:** `ck_asignacion_un_grupo_especifico` exige que exactamente uno de estos grupos se informe: `limite_devoluciones`, `numero_acreditacion`, `presupuesto_compras` o `nivel_autorizacion`. El CHECK de acreditación exige que el número y su caducidad estén ambos informados o ambos nulos.

**FK:**
- `id_empleado` → `EMPLEADO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_funcion` → `FUNCION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Triggers:** `trg_asignacion_funcion_bi` y `trg_asignacion_funcion_bu` contrastan los campos específicos con el código de función referenciado: `CAJERO` requiere límite de devoluciones; `EXPENDEDOR`, acreditación y caducidad; `RESPONSABLE_TIENDA`, presupuesto; `JEFE_ESTACION`, nivel de autorización. Rechazan códigos no reconocidos por esas reglas.

**Reglas garantizadas:** integridad de referencias, fechas no invertidas, valores positivos, campos de acreditación emparejados, exactamente un grupo de datos específicos y correspondencia del grupo con las cuatro funciones reconocidas por el trigger. La ausencia de periodos solapados para una misma persona y función requiere lógica adicional transaccional.

### 7. `CLIENTE`

**Finalidad:** clientes registrados para operaciones identificadas; conserva un identificador del sistema anterior y la matrícula opcional.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_cliente` |
| `id_cliente_antiguo` | `INT` | NULL | UNIQUE `uq_cliente_id_antiguo` |
| `codigo_cliente` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_cliente_codigo` |
| `nombre` | `VARCHAR(100)` | NOT NULL | — |
| `telefono` | `VARCHAR(20)` | NULL | — |
| `email` | `VARCHAR(254)` | NULL | — |
| `matricula` | `VARCHAR(15)` | NULL | UNIQUE `uq_cliente_matricula` |

**CHECK:** `ck_cliente_contacto`: al menos uno entre `telefono` y `email` debe ser no nulo. El esquema permite varios NULL en las columnas únicas, conforme al comportamiento de MySQL.

**Reglas garantizadas:** código de negocio único; los identificadores antiguos y matrículas no nulos no se repiten; la matrícula, teléfono y email pueden faltar, pero al menos teléfono o email debe ser no nulo. El identificador técnico `id` no sustituye al código de cliente ni a `id_cliente_antiguo`.

### 8. `LOTE_TARJETA`

**Finalidad:** registro de lotes recibidos de tarjetas de fidelización.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_lote_tarjeta` |
| `codigo_lote` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_lote_tarjeta_codigo` |
| `fecha_recepcion` | `DATE` | NOT NULL | — |
| `cantidad` | `INT` | NOT NULL | CHECK `ck_lote_tarjeta_cantidad`: `cantidad > 0` |
| `estado` | `VARCHAR(10)` | NOT NULL, DEFAULT `ABIERTO` | CHECK `ck_lote_tarjeta_estado`: `ABIERTO` o `CERRADO` |

**Reglas garantizadas:** código de lote único, cantidad positiva y estado dentro de los valores permitidos. El esquema no comprueba que el número de tarjetas asociadas al lote sea igual o inferior a `cantidad`.

### 9. `TARJETA`

**Finalidad:** tarjetas individuales de fidelización, emitidas desde un lote y opcionalmente asignadas a un cliente.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_tarjeta` |
| `numero_tarjeta` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_tarjeta_numero` |
| `id_lote` | `INT` | NOT NULL | FK `fk_tarjeta_lote` |
| `id_cliente` | `INT` | NULL | UNIQUE `uq_tarjeta_cliente`; FK `fk_tarjeta_cliente` |
| `fecha_emision` | `DATE` | NOT NULL | — |
| `fecha_asignacion` | `DATE` | NULL | Validación de correlación en triggers |

**FK:**
- `id_lote` → `LOTE_TARJETA(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_cliente` → `CLIENTE(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Triggers:** `trg_tarjeta_titular_fechas_bi` y `trg_tarjeta_titular_fechas_bu` garantizan que, si `id_cliente` es NULL, `fecha_asignacion` también sea NULL; si hay titular, la fecha de asignación debe existir y no ser anterior a `fecha_emision`.

**Reglas garantizadas:** número de tarjeta único, procedencia de un lote existente, cliente existente cuando se asigna y como máximo una tarjeta por cliente (la columna nullable `id_cliente` tiene UNIQUE). La fecha de asignación debe concordar con la presencia del titular según los triggers.

### 10. `PRODUCTO`

**Finalidad:** catálogo de productos con precio vigente y una modalidad de venta estable.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_producto` |
| `referencia` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_producto_referencia` |
| `descripcion` | `VARCHAR(150)` | NOT NULL | — |
| `precio_venta` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_producto_precio`: mayor que cero |
| `estado` | `VARCHAR(20)` | NOT NULL | CHECK `ck_producto_estado`: `ACTIVO` o `RETIRADO` |
| `modalidad` | `VARCHAR(15)` | NOT NULL | Validada por `ck_producto_modalidad` |
| `formato_envase` | `VARCHAR(50)` | NULL | Según modalidad, validada por `ck_producto_modalidad` |
| `precision_pesada` | `TINYINT` | NULL | Según modalidad, validada por `ck_producto_modalidad` |

**CHECK `ck_producto_modalidad`:** para `ENVASADO`, `formato_envase` debe ser no nulo y `precision_pesada` NULL; para `GRANEL`, `formato_envase` debe ser NULL y `precision_pesada` estar entre 0 y 3.

**Reglas garantizadas:** referencia comercial única, precio positivo, estado permitido y coherencia de los campos persistidos con la modalidad. La validación de cantidades de venta según modalidad se realiza en el modelo Java; no existe una restricción de tabla que compare la cantidad de una línea con la modalidad del producto.

### 11. `PROVEEDOR`

**Finalidad:** datos identificativos y de contacto de proveedores.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_proveedor` |
| `codigo` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_proveedor_codigo` |
| `id_fiscal` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_proveedor_id_fiscal` |
| `razon_social` | `VARCHAR(120)` | NOT NULL | — |
| `contacto` | `VARCHAR(100)` | NOT NULL | — |

**Reglas garantizadas:** código interno e identificador fiscal únicos. El esquema no impide que exista un proveedor sin acuerdos; esa condición requiere control transaccional de altas y bajas.

### 12. `ACUERDO_SUMINISTRO`

**Finalidad:** relación de suministro entre un proveedor y un producto, con sus condiciones.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_acuerdo_suministro` |
| `id_proveedor` | `INT` | NOT NULL | FK `fk_acuerdo_proveedor` |
| `id_producto` | `INT` | NOT NULL | FK `fk_acuerdo_producto` |
| `referencia_proveedor` | `VARCHAR(40)` | NOT NULL | — |
| `precio_compra` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_acuerdo_precio`: mayor que cero |
| `plazo_entrega` | `INT` | NOT NULL | CHECK `ck_acuerdo_plazo`: mayor o igual a cero |

**UNIQUE:** `uq_acuerdo_proveedor_producto` (`id_proveedor`, `id_producto`).

**FK:**
- `id_proveedor` → `PROVEEDOR(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_producto` → `PRODUCTO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** no se duplica un acuerdo para el mismo par proveedor-producto; no se usan referencias inexistentes; precio y plazo respetan sus límites. Que cada proveedor y producto conserve al menos un acuerdo debe garantizarse mediante lógica transaccional.

### 13. `CAMPANA`

**Finalidad:** campañas promocionales y su periodo de vigencia.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_campana` |
| `codigo` | `VARCHAR(20)` | NOT NULL | UNIQUE `uq_campana_codigo` |
| `nombre` | `VARCHAR(100)` | NOT NULL | — |
| `descripcion` | `VARCHAR(255)` | NULL | — |
| `fecha_inicio` | `DATE` | NOT NULL | — |
| `fecha_fin` | `DATE` | NOT NULL | CHECK `ck_campana_fechas`: `fecha_fin >= fecha_inicio` |

**Reglas garantizadas:** código único y periodo válido. Una campaña puede existir sin inscripciones.

### 14. `INSCRIPCION_CAMPANA`

**Finalidad:** asociación de clientes con campañas, con fecha de inscripción.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_inscripcion_campana` |
| `id_cliente` | `INT` | NOT NULL | Parte de UNIQUE `uq_inscripcion_cliente_campana`; FK `fk_inscripcion_cliente` |
| `id_campana` | `INT` | NOT NULL | Parte de UNIQUE `uq_inscripcion_cliente_campana`; FK `fk_inscripcion_campana` |
| `fecha_inscripcion` | `DATE` | NOT NULL | — |

**FK:**
- `id_cliente` → `CLIENTE(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_campana` → `CAMPANA(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** evita inscripciones duplicadas del mismo cliente a la misma campaña y exige referencias existentes. No requiere que el cliente tenga tarjeta.

### 15. `VENTA`

**Finalidad:** cabecera de venta de tienda, estación, empleado, cliente opcional, estado y total.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_venta` |
| `referencia_ticket` | `VARCHAR(40)` | NOT NULL | UNIQUE `uq_venta_ticket` |
| `id_estacion` | `INT` | NOT NULL | FK `fk_venta_estacion` |
| `id_empleado` | `INT` | NOT NULL | FK `fk_venta_empleado` |
| `id_cliente` | `INT` | NULL | FK `fk_venta_cliente` |
| `fecha_hora` | `DATETIME(3)` | NOT NULL | — |
| `estado` | `VARCHAR(20)` | NOT NULL, DEFAULT `PREPARACION` | CHECK `ck_venta_estado`: `PREPARACION`, `CONFIRMADA` o `PAGADA` |
| `importe_total` | `DECIMAL(10,2)` | NOT NULL, DEFAULT `0.00` | CHECK `ck_venta_importe`: mayor o igual a cero |

**FK:**
- `id_estacion` → `ESTACION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_empleado` → `EMPLEADO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_cliente` → `CLIENTE(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** ticket único, referencias obligatorias válidas para estación/empleado y cliente opcional existente, estado permitido e importe no negativo. El esquema actual no incluye trigger que exija líneas al confirmar, que compare el total con las líneas ni que exija que los pagos sumen el total para pasar a `PAGADA`; esas condiciones requieren validación transaccional de la aplicación.

### 16. `LINEA_VENTA`

**Finalidad:** productos y precios aplicados en una venta; conserva el precio histórico cobrado.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_linea_venta` |
| `id_venta` | `INT` | NOT NULL | Parte de UNIQUE `uq_linea_venta_numero`; FK `fk_linea_venta` |
| `id_producto` | `INT` | NOT NULL | FK `fk_linea_producto` |
| `numero_linea` | `INT` | NOT NULL | CHECK `ck_linea_numero`: mayor que cero; parte de UNIQUE `uq_linea_venta_numero` |
| `cantidad` | `DECIMAL(10,3)` | NOT NULL | CHECK `ck_linea_cantidad`: mayor que cero |
| `precio_unitario` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_linea_precio`: mayor que cero |
| `importe` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_linea_importe`: mayor que cero y exactamente `ROUND(cantidad * precio_unitario, 2)` |

**FK:**
- `id_venta` → `VENTA(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_producto` → `PRODUCTO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** número de línea único dentro de la venta, referencias válidas, cantidades y precios positivos e importe coherente con la cantidad y el precio unitario guardado. La modalidad del producto y el límite de suma de devoluciones requieren validaciones adicionales; la modalidad se valida en el modelo Java para las operaciones que lo usan.

### 17. `PAGO_VENTA`

**Finalidad:** cobros de ventas de tienda, separados de los pagos antiguos de repostaje.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_pago_venta` |
| `id_venta` | `INT` | NOT NULL | FK `fk_pago_venta_venta` |
| `fecha_hora` | `DATETIME(3)` | NOT NULL | — |
| `importe` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_pago_venta_importe`: mayor que cero |
| `medio_pago` | `VARCHAR(20)` | NOT NULL | CHECK `ck_pago_venta_medio`: `EFECTIVO`, `TARJETA` o `OTRO` |

**FK:** `id_venta` → `VENTA(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** cada pago pertenece a una venta existente, importe positivo y medio permitido. La suma de pagos, su relación con el total y la transición de estado a pagada no se garantizan mediante las restricciones actuales; deben comprobarse transaccionalmente.

### 18. `DEVOLUCION`

**Finalidad:** devolución parcial o total de una línea, con empleado, motivo, fecha, reintegro y aptitud para reventa.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_devolucion` |
| `id_linea` | `INT` | NOT NULL | FK `fk_devolucion_linea` |
| `id_empleado` | `INT` | NOT NULL | FK `fk_devolucion_empleado` |
| `cantidad` | `DECIMAL(10,3)` | NOT NULL | CHECK `ck_devolucion_cantidad`: mayor que cero |
| `motivo` | `VARCHAR(200)` | NOT NULL | — |
| `fecha_hora` | `DATETIME(3)` | NOT NULL | — |
| `importe_reintegrado` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_devolucion_importe`: mayor o igual a cero |
| `apta_reventa` | `BOOLEAN` | NOT NULL, DEFAULT `FALSE` | — |

**FK:**
- `id_linea` → `LINEA_VENTA(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_empleado` → `EMPLEADO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** referencias existentes, cantidad positiva y reintegro no negativo. La tabla no verifica por sí sola que la venta esté pagada, que la cantidad e importe acumulados no excedan los de la línea ni la precisión/modalidad del producto. Son controles transaccionales pendientes; Java aplica reglas de dominio en el flujo de objetos, pero eso no sustituye un control concurrente en base de datos.

### 19. `INCIDENCIA`

**Finalidad:** incidencias de trabajo abiertas en una estación y su responsable actual opcional.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_incidencia` |
| `referencia` | `VARCHAR(30)` | NOT NULL | UNIQUE `uq_incidencia_referencia` |
| `id_estacion` | `INT` | NOT NULL | FK `fk_incidencia_estacion` |
| `id_empleado` | `INT` | NULL | FK `fk_incidencia_empleado` |
| `descripcion` | `VARCHAR(255)` | NOT NULL | — |
| `fecha_hora` | `DATETIME(3)` | NOT NULL, DEFAULT `CURRENT_TIMESTAMP(3)` | — |
| `estado` | `VARCHAR(20)` | NOT NULL | CHECK `ck_incidencia_estado`: `ABIERTA`, `EN_CURSO` o `CERRADA` |

**FK:**
- `id_estacion` → `ESTACION(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.
- `id_empleado` → `EMPLEADO(id)`, `ON DELETE SET NULL ON UPDATE CASCADE`.

**Reglas garantizadas:** referencia única, estación existente, empleado responsable opcional y estado dentro del conjunto permitido. No se restringe que el empleado pertenezca a la misma estación.

### 20. `HISTORIAL_PRECIO`

**Finalidad:** trazabilidad de cambios reales del precio de venta de un producto.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_historial_precio` |
| `id_producto` | `INT` | NOT NULL | FK `fk_historial_precio_producto` |
| `precio_anterior` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_historial_precios_positivos`: ambos precios positivos; `ck_historial_precios_distintos`: distinto del nuevo |
| `precio_nuevo` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_historial_precios_positivos` y `ck_historial_precios_distintos` |
| `fecha_hora` | `DATETIME(3)` | NOT NULL, DEFAULT `CURRENT_TIMESTAMP(3)` | — |

**FK:** `id_producto` → `PRODUCTO(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Triggers:** `trg_producto_historial_precio` inserta una fila después de actualizar `PRODUCTO` solo si cambia realmente `precio_venta`; por tanto, no registra el precio inicial ni actualizaciones de otros campos o al mismo precio. Al ser InnoDB, el registro participa en la transacción y se revierte con ella. `trg_historial_precio_no_update` y `trg_historial_precio_no_delete` rechazan modificaciones y borrados ordinarios de filas históricas.

**Reglas garantizadas:** referencia al producto existente, precios positivos y diferentes; registro automático de cambios reales de precio; protección por trigger frente a UPDATE/DELETE ordinarios.

### 21. `PAGO_REPOSTAJE`

**Finalidad:** pagos históricos de repostajes, separados de los cobros de tienda y compatibles con los datos del antiguo `Pagos.java`.

| Columna | Tipo MySQL | Null / default | Restricciones |
|---|---|---|---|
| `id` | `INT` | NOT NULL, AUTO_INCREMENT | PK `pk_pago_repostaje` |
| `id_pago_antiguo` | `INT` | NULL | UNIQUE `uq_pago_repostaje_id_antiguo`; CHECK `ck_pago_repostaje_id_antiguo`: NULL o mayor que cero |
| `id_cliente` | `INT` | NOT NULL | FK `fk_pago_repostaje_cliente` |
| `fecha` | `DATE` | NOT NULL | — |
| `importe` | `DECIMAL(10,2)` | NOT NULL | CHECK `ck_pago_repostaje_importe`: mayor que cero |
| `litros` | `DECIMAL(30,15)` | NOT NULL | CHECK `ck_pago_repostaje_litros`: mayor que cero |
| `combustible` | `VARCHAR(20)` | NOT NULL | CHECK `ck_pago_repostaje_combustible`: `GASOLINA` o `DIESEL` |

**FK:** `id_cliente` → `CLIENTE(id)`, `ON DELETE RESTRICT ON UPDATE CASCADE`.

**Reglas garantizadas:** conserva el identificador antiguo por separado del ID técnico; impide repetir valores antiguos no nulos; exige cliente existente, importe y litros positivos y combustible permitido. `litros` usa la precisión definida en el SQL, `DECIMAL(30,15)`.

## Relaciones y acciones referenciales

Cada relación de la tabla indica el `ON DELETE` y `ON UPDATE` implementado. Resumen:

| Tabla hija / FK | Tabla referenciada | Cardinalidad máxima desde padre | ON DELETE | ON UPDATE |
|---|---|---:|---|---|
| `CONFIGURACION_FACTURACION.id_estacion` | `ESTACION.id` | 1 configuración por estación (UNIQUE) | RESTRICT | CASCADE |
| `SURTIDOR.id_estacion` | `ESTACION.id` | N surtidores por estación | RESTRICT | CASCADE |
| `EMPLEADO.id_estacion` | `ESTACION.id` | N empleados por estación | RESTRICT | CASCADE |
| `EMPLEADO.id_supervisor` | `EMPLEADO.id` | N subordinados por supervisor | SET NULL | CASCADE |
| `ASIGNACION_FUNCION.id_empleado` | `EMPLEADO.id` | N asignaciones por empleado | RESTRICT | CASCADE |
| `ASIGNACION_FUNCION.id_funcion` | `FUNCION.id` | N asignaciones por función | RESTRICT | CASCADE |
| `TARJETA.id_lote` | `LOTE_TARJETA.id` | N tarjetas por lote | RESTRICT | CASCADE |
| `TARJETA.id_cliente` | `CLIENTE.id` | 0..1 tarjeta por cliente (UNIQUE nullable) | RESTRICT | CASCADE |
| `ACUERDO_SUMINISTRO.id_proveedor` | `PROVEEDOR.id` | N acuerdos por proveedor | RESTRICT | CASCADE |
| `ACUERDO_SUMINISTRO.id_producto` | `PRODUCTO.id` | N acuerdos por producto | RESTRICT | CASCADE |
| `INSCRIPCION_CAMPANA.id_cliente` | `CLIENTE.id` | N inscripciones por cliente | RESTRICT | CASCADE |
| `INSCRIPCION_CAMPANA.id_campana` | `CAMPANA.id` | N inscripciones por campaña | RESTRICT | CASCADE |
| `VENTA.id_estacion` | `ESTACION.id` | N ventas por estación | RESTRICT | CASCADE |
| `VENTA.id_empleado` | `EMPLEADO.id` | N ventas por empleado | RESTRICT | CASCADE |
| `VENTA.id_cliente` | `CLIENTE.id` | N ventas por cliente; cliente opcional en venta | RESTRICT | CASCADE |
| `LINEA_VENTA.id_venta` | `VENTA.id` | N líneas por venta | RESTRICT | CASCADE |
| `LINEA_VENTA.id_producto` | `PRODUCTO.id` | N líneas por producto | RESTRICT | CASCADE |
| `PAGO_VENTA.id_venta` | `VENTA.id` | N pagos por venta | RESTRICT | CASCADE |
| `DEVOLUCION.id_linea` | `LINEA_VENTA.id` | N devoluciones por línea | RESTRICT | CASCADE |
| `DEVOLUCION.id_empleado` | `EMPLEADO.id` | N devoluciones por empleado | RESTRICT | CASCADE |
| `INCIDENCIA.id_estacion` | `ESTACION.id` | N incidencias por estación | RESTRICT | CASCADE |
| `INCIDENCIA.id_empleado` | `EMPLEADO.id` | N incidencias por empleado; responsable opcional | SET NULL | CASCADE |
| `HISTORIAL_PRECIO.id_producto` | `PRODUCTO.id` | N cambios históricos por producto | RESTRICT | CASCADE |
| `PAGO_REPOSTAJE.id_cliente` | `CLIENTE.id` | N pagos antiguos por cliente | RESTRICT | CASCADE |

Las relaciones muchos-a-muchos se representan mediante `ACUERDO_SUMINISTRO` (proveedores-productos) e `INSCRIPCION_CAMPANA` (clientes-campañas), ambas con su propio ID técnico y restricción UNIQUE compuesta.

## Reglas que el esquema deja a la aplicación o a transacciones

Las siguientes condiciones no están completamente garantizadas por las PK, FK, UNIQUE, CHECK ni triggers incluidos en los scripts actuales:

- Alta de estación junto con exactamente una configuración y al menos un surtidor; impedir eliminar el último surtidor.
- Evitar ciclos indirectos en la jerarquía de supervisores; el trigger solo compara supervisor e ID del mismo registro.
- Evitar periodos solapados de asignación de la misma función al mismo empleado.
- Garantizar al menos un acuerdo para cada proveedor y cada producto, también al retirar/eliminar acuerdos.
- Confirmar solo ventas con líneas y verificar que el total coincida con la suma de importes de líneas.
- Cambiar a estado `PAGADA` solo cuando los pagos acumulados coincidan exactamente con `importe_total`.
- Asegurar que las devoluciones corresponden a una venta pagada y que sus cantidades e importes acumulados no exceden la línea original, incluyendo operaciones concurrentes.
- Aplicar en persistencia la modalidad y precisión de pesada a las cantidades de líneas y devoluciones. El modelo Java aporta validación de cantidades; el SQL no tiene trigger para esta regla.
- Comprobar que la cantidad de tarjetas emitidas desde un lote no supera `LOTE_TARJETA.cantidad`.

Estas comprobaciones deben diseñarse como operaciones transaccionales en la siguiente fase de aplicación/JDBC. El esquema preserva las relaciones y restricciones declaradas, pero no se deben presentar esas reglas agregadas como protegidas por SQL mientras no exista su implementación.
