-- Datos completamente ficticios para pruebas académicas.
-- Ejecutar después de 01_esquema.sql y 02_triggers.sql en una base vacía.
-- Los IDs explícitos facilitan leer las relaciones de este conjunto de ejemplo.
USE gasolinera;

INSERT INTO ESTACION
    (id, codigo, nombre, direccion, telefono, fecha_apertura)
VALUES
    (1, 'EST-001', 'Estación Norte', 'Calle del Norte 10', '910000001', '2018-03-12'),
    (2, 'EST-002', 'Estación Sur', 'Avenida Sur 25', '910000002', '2019-06-05'),
    (3, 'EST-003', 'Estación Este', 'Carretera Este km 4', '910000003', '2020-01-20'),
    (4, 'EST-004', 'Estación Oeste', 'Plaza Oeste 8', '910000004', '2021-09-01'),
    (5, 'EST-005', 'Estación Centro', 'Calle Central 42', '910000005', '2022-05-17');

INSERT INTO CONFIGURACION_FACTURACION
    (id, id_estacion, codigo, serie_ticket, siguiente_numero, pie_justificante)
VALUES
    (1, 1, 'CFG-001', 'N', 101, 'Gracias por su visita'),
    (2, 2, 'CFG-002', 'S', 201, 'Conserve su justificante'),
    (3, 3, 'CFG-003', 'E', 301, 'Atención al cliente: 900 000 003'),
    (4, 4, 'CFG-004', 'O', 401, 'Vuelva pronto'),
    (5, 5, 'CFG-005', 'C', 501, 'Gracias por confiar en nosotros');

INSERT INTO SURTIDOR
    (id, id_estacion, codigo_equipo, numero_visible, combustible, estado)
VALUES
    (1, 1, 'EQ-001', 1, 'GASOLINA', 'OPERATIVO'),
    (2, 1, 'EQ-002', 2, 'DIESEL', 'OPERATIVO'),
    (3, 2, 'EQ-003', 1, 'GASOLINA', 'MANTENIMIENTO'),
    (4, 3, 'EQ-004', 1, 'DIESEL', 'OPERATIVO'),
    (5, 4, 'EQ-005', 1, 'GASOLINA', 'AVERIADO'),
    (6, 5, 'EQ-006', 1, 'DIESEL', 'OPERATIVO');

-- Primero se insertan empleados sin supervisor y después se asignan supervisores.
INSERT INTO EMPLEADO
    (id, numero_empleado, documento, nombre, telefono, email,
     fecha_incorporacion, id_estacion, id_supervisor)
VALUES
    (1, 'EMP-001', 'DOC-1001', 'Ana Ruiz', '600100001', 'ana@example.test', '2018-04-01', 1, NULL),
    (2, 'EMP-002', 'DOC-1002', 'Luis Sol', '600100002', 'luis@example.test', '2019-07-10', 1, NULL),
    (3, 'EMP-003', 'DOC-1003', 'Marta Gil', '600100003', 'marta@example.test', '2020-02-14', 2, NULL),
    (4, 'EMP-004', 'DOC-1004', 'Pablo Mar', '600100004', 'pablo@example.test', '2021-10-03', 2, NULL),
    (5, 'EMP-005', 'DOC-1005', 'Nuria Rey', '600100005', 'nuria@example.test', '2022-06-01', 3, NULL),
    (6, 'EMP-006', 'DOC-1006', 'Ivan Soler', NULL, 'ivan@example.test', '2024-01-15', 3, NULL);

UPDATE EMPLEADO SET id_supervisor = 1 WHERE id IN (2, 3);
UPDATE EMPLEADO SET id_supervisor = 2 WHERE id = 4;
UPDATE EMPLEADO SET id_supervisor = 5 WHERE id = 6;

INSERT INTO FUNCION (id, codigo, nombre, descripcion)
VALUES
    (1, 'CAJERO', 'Cajero', 'Atiende cobros y devoluciones'),
    (2, 'EXPENDEDOR', 'Expendedor', 'Atiende equipos de repostaje'),
    (3, 'RESPONSABLE_TIENDA', 'Responsable de tienda', 'Gestiona la tienda'),
    (4, 'JEFE_ESTACION', 'Jefe de estación', 'Coordina una estación');

-- Ana desempeña dos funciones simultáneamente. Pablo terminó una asignación
-- de tienda y la recuperó después. Ivan todavía no tiene funciones asignadas.
INSERT INTO ASIGNACION_FUNCION
    (id, id_empleado, id_funcion, fecha_inicio, fecha_fin,
     limite_devoluciones, numero_acreditacion, fecha_caducidad_acreditacion,
     presupuesto_compras, nivel_autorizacion)
VALUES
    (1, 1, 1, '2022-01-01', NULL, 100.00, NULL, NULL, NULL, NULL),
    (2, 1, 2, '2023-02-01', NULL, NULL, 'ACR-ANA-01', '2027-02-01', NULL, NULL),
    (3, 2, 4, '2020-01-01', NULL, NULL, NULL, NULL, NULL, 3),
    (4, 3, 1, '2021-04-01', NULL, 50.00, NULL, NULL, NULL, NULL),
    (5, 4, 3, '2022-01-01', '2023-12-31', NULL, NULL, NULL, 5000.00, NULL),
    (6, 4, 3, '2024-02-01', NULL, NULL, NULL, NULL, 6500.00, NULL),
    (7, 5, 2, '2022-07-01', NULL, NULL, 'ACR-NUR-05', '2027-07-01', NULL, NULL);

INSERT INTO CLIENTE
    (id, id_cliente_antiguo, codigo_cliente, nombre, telefono, email, matricula)
VALUES
    (1, 1, 'CLI-0001', 'Clara Monte', '611000001', 'clara@example.test', '1234ABC'),
    (2, 2, 'CLI-0002', 'Diego Lago', '611000002', NULL, '2345BCD'),
    (3, NULL, 'CLI-0003', 'Elena Prado', NULL, 'elena@example.test', NULL),
    (4, NULL, 'CLI-0004', 'Fabio Cruz', '611000004', NULL, '3456CDE'),
    (5, NULL, 'CLI-0005', 'Ines Valle', NULL, 'ines@example.test', NULL),
    (6, NULL, 'CLI-0006', 'Jorge Alba', '611000006', 'jorge@example.test', NULL);

INSERT INTO LOTE_TARJETA
    (id, codigo_lote, fecha_recepcion, cantidad, estado)
VALUES
    (1, 'LOTE-2026-A', '2026-01-10', 3, 'CERRADO'),
    (2, 'LOTE-2026-B', '2026-02-05', 1, 'CERRADO'),
    (3, 'LOTE-2026-C', '2026-03-01', 1, 'CERRADO'),
    (4, 'LOTE-2026-D', '2026-04-12', 1, 'CERRADO'),
    (5, 'LOTE-2026-E', '2026-05-20', 1, 'CERRADO');

INSERT INTO TARJETA
    (id, numero_tarjeta, id_lote, id_cliente, fecha_emision, fecha_asignacion)
VALUES
    (1, 'CARD-000001', 1, 1, '2026-01-15', '2026-01-16'),
    (2, 'CARD-000002', 1, 2, '2026-01-15', '2026-02-01'),
    (3, 'CARD-000003', 1, 3, '2026-01-15', '2026-02-10'),
    (4, 'CARD-000004', 2, NULL, '2026-02-10', NULL),
    (5, 'CARD-000005', 3, 4, '2026-03-02', '2026-03-05'),
    (6, 'CARD-000006', 4, NULL, '2026-04-15', NULL),
    (7, 'CARD-000007', 5, 5, '2026-05-22', '2026-06-01');

INSERT INTO PRODUCTO
    (id, referencia, descripcion, precio_venta, estado, modalidad,
     formato_envase, precision_pesada)
VALUES
    (1, 'ENV-001', 'Agua 1 litro', 1.50, 'ACTIVO', 'ENVASADO', 'Botella 1 L', NULL),
    (2, 'ENV-002', 'Barrita de cereal', 2.00, 'ACTIVO', 'ENVASADO', 'Unidad 40 g', NULL),
    (3, 'ENV-003', 'Café en lata', 3.25, 'ACTIVO', 'ENVASADO', 'Lata 250 ml', NULL),
    (4, 'GRA-001', 'Frutos secos surtidos', 12.00, 'ACTIVO', 'GRANEL', NULL, 3),
    (5, 'GRA-002', 'Caramelos a granel', 8.00, 'ACTIVO', 'GRANEL', NULL, 2),
    (6, 'GRA-003', 'Café en grano', 15.00, 'ACTIVO', 'GRANEL', NULL, 3);

INSERT INTO PROVEEDOR
    (id, codigo, id_fiscal, razon_social, contacto)
VALUES
    (1, 'PROV-001', 'FISC-0001', 'Distribuciones Norte SL', 'ventas@norte.example.test'),
    (2, 'PROV-002', 'FISC-0002', 'Alimentacion Sur SL', 'pedidos@sur.example.test'),
    (3, 'PROV-003', 'FISC-0003', 'Bebidas Centro SL', 'comercial@centro.example.test'),
    (4, 'PROV-004', 'FISC-0004', 'Granel Este SL', 'granel@este.example.test'),
    (5, 'PROV-005', 'FISC-0005', 'Suministros Oeste SL', 'info@oeste.example.test');

-- Cada proveedor y cada producto tiene al menos un acuerdo; el producto 4
-- tiene varios proveedores y el proveedor 1 suministra varios productos.
INSERT INTO ACUERDO_SUMINISTRO
    (id, id_proveedor, id_producto, referencia_proveedor, precio_compra, plazo_entrega)
VALUES
    (1, 1, 1, 'N-AGUA-01', 0.70, 2),
    (2, 1, 2, 'N-BARRA-02', 1.00, 2),
    (3, 2, 3, 'S-CAFE-03', 1.80, 3),
    (4, 2, 4, 'S-FRUTO-04', 7.00, 4),
    (5, 3, 4, 'C-FRUTO-04', 7.25, 3),
    (6, 3, 5, 'C-CARAMELO-05', 4.00, 2),
    (7, 4, 6, 'E-CAFE-06', 9.00, 5),
    (8, 5, 1, 'O-AGUA-01', 0.72, 1);

INSERT INTO CAMPANA
    (id, codigo, nombre, descripcion, fecha_inicio, fecha_fin)
VALUES
    (1, 'CAM-001', 'Primavera', 'Promocion de primavera', '2026-03-01', '2026-05-31'),
    (2, 'CAM-002', 'Verano', 'Promocion de verano', '2026-06-01', '2026-08-31'),
    (3, 'CAM-003', 'Vuelta', 'Campana de vuelta a la rutina', '2026-09-01', '2026-10-15'),
    (4, 'CAM-004', 'Invierno', 'Promocion de invierno', '2026-11-01', '2027-01-31'),
    (5, 'CAM-005', 'Sin inscritos', 'Campana de ejemplo aun vacia', '2026-12-01', '2026-12-31');

INSERT INTO INSCRIPCION_CAMPANA
    (id, id_cliente, id_campana, fecha_inscripcion)
VALUES
    (1, 1, 1, '2026-03-02'),
    (2, 1, 2, '2026-06-03'),
    (3, 2, 1, '2026-03-05'),
    (4, 3, 2, '2026-06-10'),
    (5, 4, 3, '2026-09-02'),
    (6, 5, 4, '2026-11-03');

INSERT INTO VENTA
    (id, referencia_ticket, id_estacion, id_empleado, id_cliente,
     fecha_hora, estado, importe_total)
VALUES
    (1, 'T-2026-0001', 1, 1, 1, '2026-10-10 10:15:00.000', 'PAGADA', 23.00),
    (2, 'T-2026-0002', 1, 2, NULL, '2026-10-10 11:20:00.000', 'PAGADA', 5.25),
    (3, 'T-2026-0003', 2, 3, 2, '2026-10-11 12:00:00.000', 'CONFIRMADA', 10.00),
    (4, 'T-2026-0004', 2, 4, NULL, '2026-10-11 13:30:00.000', 'PREPARACION', 3.00),
    (5, 'T-2026-0005', 3, 5, 3, '2026-10-12 09:00:00.000', 'PAGADA', 18.25),
    (6, 'T-2026-0006', 4, 2, 4, '2026-10-12 16:45:00.000', 'PAGADA', 6.50);

-- Se guardan precio aplicado e importe en cada línea. La venta 1 combina
-- productos envasados y a granel; la venta 2 es anónima y tiene varias líneas.
INSERT INTO LINEA_VENTA
    (id, id_venta, id_producto, numero_linea, cantidad, precio_unitario, importe)
VALUES
    (1, 1, 1, 1, 2.000, 1.50, 3.00),
    (2, 1, 4, 2, 1.500, 12.00, 18.00),
    (3, 1, 2, 3, 1.000, 2.00, 2.00),
    (4, 2, 3, 1, 1.000, 3.25, 3.25),
    (5, 2, 2, 2, 1.000, 2.00, 2.00),
    (6, 3, 5, 1, 1.25, 8.00, 10.00),
    (7, 4, 1, 1, 2.000, 1.50, 3.00),
    (8, 5, 6, 1, 1.000, 15.00, 15.00),
    (9, 5, 3, 2, 1.000, 3.25, 3.25),
    (10, 6, 3, 1, 2.000, 3.25, 6.50);

INSERT INTO PAGO_VENTA
    (id, id_venta, fecha_hora, importe, medio_pago)
VALUES
    (1, 1, '2026-10-10 10:16:00.000', 10.00, 'EFECTIVO'),
    (2, 1, '2026-10-10 10:17:00.000', 13.00, 'TARJETA'),
    (3, 2, '2026-10-10 11:21:00.000', 5.25, 'EFECTIVO'),
    (4, 5, '2026-10-12 09:01:00.000', 18.25, 'TARJETA'),
    (5, 6, '2026-10-12 16:46:00.000', 6.50, 'OTRO'),
    (6, 3, '2026-10-11 12:05:00', 4.00, 'EFECTIVO');
-- La venta 1 se cobra con dos pagos por un total de 23 euros. La venta 3
-- conserva un cobro parcial de 4 de sus 10 euros y sigue CONFIRMADA.

-- Dos devoluciones sucesivas de la misma línea; sus cantidades e importes
-- acumulados coinciden, pero no exceden, lo originalmente vendido/cobrado.
INSERT INTO DEVOLUCION
    (id, id_linea, id_empleado, cantidad, motivo, fecha_hora,
     importe_reintegrado, apta_reventa)
VALUES
    (1, 1, 1, 1.000, 'Envase sin abrir', '2026-10-10 10:30:00.000', 1.50, TRUE),
    (2, 1, 1, 1.000, 'Segunda unidad devuelta', '2026-10-10 10:40:00.000', 1.50, TRUE),
    (3, 2, 1, 0.500, 'Cantidad pesada de mas', '2026-10-10 10:45:00.000', 6.00, FALSE);

INSERT INTO INCIDENCIA
    (id, referencia, id_estacion, id_empleado, descripcion, fecha_hora, estado)
VALUES
    (1, 'INC-001', 1, 1, 'Revisar caja principal', '2026-10-01 08:00:00.000', 'ABIERTA'),
    (2, 'INC-002', 1, 1, 'Lector de tarjetas intermitente', '2026-10-02 09:30:00.000', 'EN_CURSO'),
    (3, 'INC-003', 2, 3, 'Iluminacion de tienda', '2026-10-03 14:15:00.000', 'CERRADA'),
    (4, 'INC-004', 3, NULL, 'Pendiente de asignacion', '2026-10-04 10:00:00.000', 'ABIERTA'),
    (5, 'INC-005', 4, 2, 'Impresora de tickets', '2026-10-05 11:20:00.000', 'EN_CURSO'),
    (6, 'INC-006', 5, 5, 'Puerta de almacen', '2026-10-06 12:00:00.000', 'ABIERTA');

-- Estos son datos ficticios de repostajes antiguos usados para comprobar
-- compatibilidad. id_pago_antiguo identifica el ID del antiguo Pagos.java;
-- id_cliente referencia el nuevo ID técnico y no el ID antiguo del cliente.
INSERT INTO PAGO_REPOSTAJE
    (id, id_pago_antiguo, id_cliente, fecha, importe, litros, combustible)
VALUES
    (1, 1, 1, '2026-10-01', 70.00, 40.000000000000000, 'GASOLINA'),
    (2, 2, 2, '2026-10-02', 55.50, 32.250000000000000, 'DIESEL'),
    (3, NULL, 3, '2026-10-03', 42.75, 25.123456789012345, 'GASOLINA'),
    (4, NULL, 4, '2026-10-04', 61.00, 35.500000000000000, 'DIESEL'),
    (5, NULL, 5, '2026-10-05', 48.20, 28.750000000000000, 'GASOLINA');

-- El alta inicial de PRODUCTO no genera historial. Estas operaciones ficticias
-- generan una entrada por cambio real; repetir precio y cambiar descripción
-- no deben añadir entradas adicionales.
UPDATE PRODUCTO SET precio_venta = 15.50 WHERE id = 6;
UPDATE PRODUCTO SET precio_venta = 15.50 WHERE id = 6;
UPDATE PRODUCTO SET descripcion = 'Cafe en grano seleccionado' WHERE id = 6;
