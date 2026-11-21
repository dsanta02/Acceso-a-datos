-- Trigger del historial de precios y protección de la trazabilidad.
-- InnoDB hace que el INSERT del trigger participe en la misma transacción:
-- un ROLLBACK del cambio de precio también revierte la fila histórica.
USE gasolinera;

DROP TRIGGER IF EXISTS trg_producto_historial_precio;
DROP TRIGGER IF EXISTS trg_historial_precio_no_update;
DROP TRIGGER IF EXISTS trg_historial_precio_no_delete;
DROP TRIGGER IF EXISTS trg_asignacion_funcion_bi;
DROP TRIGGER IF EXISTS trg_asignacion_funcion_bu;
DROP TRIGGER IF EXISTS trg_empleado_no_autosupervision_bi;
DROP TRIGGER IF EXISTS trg_empleado_no_autosupervision_bu;
DROP TRIGGER IF EXISTS trg_tarjeta_titular_fechas_bi;
DROP TRIGGER IF EXISTS trg_tarjeta_titular_fechas_bu;

DELIMITER //

-- MySQL no permite que una columna usada por una acción referencial de FK
-- participe en un CHECK. Estos triggers conservan la regla de no autosupervisión.
CREATE TRIGGER trg_empleado_no_autosupervision_bi
BEFORE INSERT ON EMPLEADO
FOR EACH ROW
BEGIN
    IF NEW.id_supervisor IS NOT NULL AND NEW.id_supervisor = NEW.id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un empleado no puede supervisarse a si mismo';
    END IF;
END//

CREATE TRIGGER trg_empleado_no_autosupervision_bu
BEFORE UPDATE ON EMPLEADO
FOR EACH ROW
BEGIN
    IF NEW.id_supervisor IS NOT NULL AND NEW.id_supervisor = NEW.id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Un empleado no puede supervisarse a si mismo';
    END IF;
END//

-- Valida la correspondencia entre titular y fechas sin CHECK sobre id_cliente,
-- que participa en una FK con acción referencial en MySQL 8.0.46.
CREATE TRIGGER trg_tarjeta_titular_fechas_bi
BEFORE INSERT ON TARJETA
FOR EACH ROW
BEGIN
    IF NEW.id_cliente IS NULL THEN
        IF NEW.fecha_asignacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Una tarjeta sin titular no puede tener fecha de asignacion';
        END IF;
    ELSEIF NEW.fecha_asignacion IS NULL
           OR NEW.fecha_asignacion < NEW.fecha_emision THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La fecha de asignacion debe ser igual o posterior a la emision';
    END IF;
END//

CREATE TRIGGER trg_tarjeta_titular_fechas_bu
BEFORE UPDATE ON TARJETA
FOR EACH ROW
BEGIN
    IF NEW.id_cliente IS NULL THEN
        IF NEW.fecha_asignacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Una tarjeta sin titular no puede tener fecha de asignacion';
        END IF;
    ELSEIF NEW.fecha_asignacion IS NULL
           OR NEW.fecha_asignacion < NEW.fecha_emision THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La fecha de asignacion debe ser igual o posterior a la emision';
    END IF;
END//

CREATE TRIGGER trg_producto_historial_precio
AFTER UPDATE ON PRODUCTO
FOR EACH ROW
BEGIN
    IF NOT (OLD.precio_venta <=> NEW.precio_venta) THEN
        INSERT INTO HISTORIAL_PRECIO
            (id_producto, precio_anterior, precio_nuevo, fecha_hora)
        VALUES
            (NEW.id, OLD.precio_venta, NEW.precio_venta, CURRENT_TIMESTAMP(3));
    END IF;
END//

-- El histórico es de solo inserción: no se editan ni eliminan filas históricas
-- mediante operaciones ordinarias de la aplicación.
CREATE TRIGGER trg_historial_precio_no_update
BEFORE UPDATE ON HISTORIAL_PRECIO
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El historial de precios no se puede modificar';
END//

CREATE TRIGGER trg_historial_precio_no_delete
BEFORE DELETE ON HISTORIAL_PRECIO
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El historial de precios no se puede borrar';
END//

-- Los CHECK de una tabla no pueden consultar FUNCION. Estos triggers aseguran
-- que los datos específicos coinciden con el código de función asociado.
CREATE TRIGGER trg_asignacion_funcion_bi
BEFORE INSERT ON ASIGNACION_FUNCION
FOR EACH ROW
BEGIN
    DECLARE v_codigo VARCHAR(30);
    SELECT codigo INTO v_codigo FROM FUNCION WHERE id = NEW.id_funcion;

    IF v_codigo = 'CAJERO' THEN
        IF NEW.limite_devoluciones IS NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para CAJERO';
        END IF;
    ELSEIF v_codigo = 'EXPENDEDOR' THEN
        IF NEW.numero_acreditacion IS NULL
           OR NEW.fecha_caducidad_acreditacion IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para EXPENDEDOR';
        END IF;
    ELSEIF v_codigo = 'RESPONSABLE_TIENDA' THEN
        IF NEW.presupuesto_compras IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para RESPONSABLE_TIENDA';
        END IF;
    ELSEIF v_codigo = 'JEFE_ESTACION' THEN
        IF NEW.nivel_autorizacion IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para JEFE_ESTACION';
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La función no tiene una regla de asignación definida';
    END IF;
END//

CREATE TRIGGER trg_asignacion_funcion_bu
BEFORE UPDATE ON ASIGNACION_FUNCION
FOR EACH ROW
BEGIN
    DECLARE v_codigo VARCHAR(30);
    SELECT codigo INTO v_codigo FROM FUNCION WHERE id = NEW.id_funcion;

    IF v_codigo = 'CAJERO' THEN
        IF NEW.limite_devoluciones IS NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para CAJERO';
        END IF;
    ELSEIF v_codigo = 'EXPENDEDOR' THEN
        IF NEW.numero_acreditacion IS NULL
           OR NEW.fecha_caducidad_acreditacion IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para EXPENDEDOR';
        END IF;
    ELSEIF v_codigo = 'RESPONSABLE_TIENDA' THEN
        IF NEW.presupuesto_compras IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.nivel_autorizacion IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para RESPONSABLE_TIENDA';
        END IF;
    ELSEIF v_codigo = 'JEFE_ESTACION' THEN
        IF NEW.nivel_autorizacion IS NULL
           OR NEW.limite_devoluciones IS NOT NULL
           OR NEW.numero_acreditacion IS NOT NULL
           OR NEW.fecha_caducidad_acreditacion IS NOT NULL
           OR NEW.presupuesto_compras IS NOT NULL THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Datos específicos no válidos para JEFE_ESTACION';
        END IF;
    ELSE
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La función no tiene una regla de asignación definida';
    END IF;
END//

DELIMITER ;
