-- Consultas de comprobación y pruebas manuales de restricciones.
-- Ejecutar después de 01_esquema.sql, 02_triggers.sql y 03_datos.sql.
USE gasolinera;

-- Deben aparecer 21 filas: 21 tablas del diseño.
SELECT COUNT(*) AS tablas_del_diseno
FROM information_schema.tables
WHERE table_schema = DATABASE()
  AND table_type = 'BASE TABLE';

-- Relaciones básicas y recuentos de ejemplo.
SELECT e.codigo, e.nombre, c.codigo AS codigo_configuracion, COUNT(s.id) AS surtidores
FROM ESTACION e
JOIN CONFIGURACION_FACTURACION c ON c.id_estacion = e.id
JOIN SURTIDOR s ON s.id_estacion = e.id
GROUP BY e.id, e.codigo, e.nombre, c.codigo
ORDER BY e.id;

SELECT COUNT(*) AS clientes, COUNT(matricula) AS clientes_con_matricula
FROM CLIENTE;

SELECT p.modalidad, COUNT(*) AS productos
FROM PRODUCTO p
GROUP BY p.modalidad;

-- Debe mostrar los pagos múltiples de la venta 1 y el pago parcial de la venta 3.
SELECT v.referencia_ticket, v.estado, v.importe_total,
       COALESCE(SUM(pv.importe), 0.00) AS importe_cobrado
FROM VENTA v
LEFT JOIN PAGO_VENTA pv ON pv.id_venta = v.id
GROUP BY v.id, v.referencia_ticket, v.estado, v.importe_total
ORDER BY v.id;

-- Comprobar que líneas y cabeceras cuadran en los ejemplos.
SELECT v.referencia_ticket, v.importe_total,
       COALESCE(SUM(lv.importe), 0.00) AS suma_lineas
FROM VENTA v
LEFT JOIN LINEA_VENTA lv ON lv.id_venta = v.id
GROUP BY v.id, v.referencia_ticket, v.importe_total
ORDER BY v.id;

-- Las cantidades e importes devueltos acumulados no exceden los originales.
SELECT lv.id AS linea, lv.cantidad AS cantidad_comprada,
       COALESCE(SUM(d.cantidad), 0.000) AS cantidad_devuelta,
       lv.importe AS importe_original,
       COALESCE(SUM(d.importe_reintegrado), 0.00) AS importe_reintegrado
FROM LINEA_VENTA lv
LEFT JOIN DEVOLUCION d ON d.id_linea = lv.id
GROUP BY lv.id, lv.cantidad, lv.importe
HAVING cantidad_devuelta > cantidad_comprada
    OR importe_reintegrado > importe_original;
-- Resultado esperado: cero filas.

-- Comprobar datos ficticios de repostajes y trazabilidad de IDs antiguos.
SELECT pr.id, pr.id_pago_antiguo, c.id AS id_cliente_tecnico,
       c.id_cliente_antiguo, c.codigo_cliente, pr.fecha, pr.importe,
       pr.litros, pr.combustible
FROM PAGO_REPOSTAJE pr
JOIN CLIENTE c ON c.id = pr.id_cliente
ORDER BY pr.id;

-- El producto 6 debe tener exactamente una entrada por el cambio inicial de
-- precio del script 03; repetir el precio y cambiar descripción no generan otra.
SELECT hp.id, p.referencia, hp.precio_anterior, hp.precio_nuevo, hp.fecha_hora
FROM HISTORIAL_PRECIO hp
JOIN PRODUCTO p ON p.id = hp.id_producto
ORDER BY hp.id;

-- Prueba transaccional: el cambio temporal crea historial; ROLLBACK debe
-- revertir tanto el precio como la fila creada por el trigger.
START TRANSACTION;
UPDATE PRODUCTO SET precio_venta = 16.00 WHERE id = 6;
SELECT precio_venta FROM PRODUCTO WHERE id = 6;
SELECT COUNT(*) AS entradas_durante_transaccion
FROM HISTORIAL_PRECIO WHERE id_producto = 6;
ROLLBACK;
SELECT precio_venta AS precio_tras_rollback FROM PRODUCTO WHERE id = 6;
SELECT COUNT(*) AS entradas_tras_rollback
FROM HISTORIAL_PRECIO WHERE id_producto = 6;
-- Debe quedar precio 15.50 y el mismo número de entradas que antes de la prueba.

-- PRUEBAS NEGATIVAS: no se ejecutan automáticamente para que un error esperado
-- no interrumpa las comprobaciones anteriores. Descomentar y ejecutar cada
-- sentencia por separado; MySQL debe rechazarla.

-- Duplicado de referencia de negocio:
-- INSERT INTO ESTACION (codigo,nombre,direccion,telefono,fecha_apertura)
-- VALUES ('EST-001','Duplicada','Direccion','900000000','2026-01-01');

-- Referencia a una estación inexistente:
-- INSERT INTO SURTIDOR (id_estacion,codigo_equipo,numero_visible,combustible,estado)
-- VALUES (99999,'EQ-ERROR',99,'GASOLINA','OPERATIVO');

-- Inscripción duplicada de cliente y campaña:
-- INSERT INTO INSCRIPCION_CAMPANA (id_cliente,id_campana,fecha_inscripcion)
-- VALUES (1,1,'2026-03-03');

-- Tarjeta con fecha de asignación anterior a su emisión:
-- INSERT INTO TARJETA (numero_tarjeta,id_lote,id_cliente,fecha_emision,fecha_asignacion)
-- VALUES ('CARD-ERROR',2,6,'2026-05-02','2026-05-01');

-- El CHECK de línea rechaza un importe distinto a cantidad * precio:
-- INSERT INTO LINEA_VENTA (id_venta,id_producto,numero_linea,cantidad,precio_unitario,importe)
-- VALUES (4,1,2,1.000,1.50,99.00);

-- El trigger protege el historial frente a cambios y borrados ordinarios:
-- UPDATE HISTORIAL_PRECIO SET precio_nuevo = 99.00 WHERE id = 1;
-- DELETE FROM HISTORIAL_PRECIO WHERE id = 1;

-- CASOS QUE REQUIEREN LÓGICA JAVA/TRANSACCIONAL (no son pruebas SQL automáticas):
-- ciclos indirectos de supervisión, periodos de función solapados, existencia
-- de surtidores/acuerdos obligatorios, precisión de pesada consultando PRODUCTO,
-- existencia de líneas al confirmar, suma de cobros para marcar PAGADA y límites
-- acumulados de devoluciones frente a operaciones simultáneas.
