-- Esquema relacional de la gasolinera (MySQL 8.0.16 o posterior).
-- Ejecutar este archivo sobre una instancia MySQL con permisos de creación.
CREATE DATABASE IF NOT EXISTS gasolinera
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE gasolinera;

-- Una estación se crea junto con su configuración y al menos un surtidor
-- mediante una operación transaccional de la aplicación.
CREATE TABLE IF NOT EXISTS ESTACION (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha_apertura DATE NOT NULL,
    CONSTRAINT pk_estacion PRIMARY KEY (id),
    CONSTRAINT uq_estacion_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS CONFIGURACION_FACTURACION (
    id INT NOT NULL AUTO_INCREMENT,
    id_estacion INT NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    serie_ticket VARCHAR(20) NOT NULL,
    siguiente_numero INT NOT NULL DEFAULT 1,
    pie_justificante VARCHAR(255) NOT NULL,
    CONSTRAINT pk_configuracion_facturacion PRIMARY KEY (id),
    CONSTRAINT uq_configuracion_estacion UNIQUE (id_estacion),
    CONSTRAINT uq_configuracion_codigo UNIQUE (codigo),
    CONSTRAINT ck_configuracion_siguiente_numero CHECK (siguiente_numero > 0),
    CONSTRAINT fk_configuracion_estacion FOREIGN KEY (id_estacion)
        REFERENCES ESTACION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS SURTIDOR (
    id INT NOT NULL AUTO_INCREMENT,
    id_estacion INT NOT NULL,
    codigo_equipo VARCHAR(30) NOT NULL,
    numero_visible INT NOT NULL,
    combustible VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT pk_surtidor PRIMARY KEY (id),
    CONSTRAINT uq_surtidor_equipo UNIQUE (codigo_equipo),
    CONSTRAINT uq_surtidor_numero_estacion UNIQUE (id_estacion, numero_visible),
    CONSTRAINT ck_surtidor_numero CHECK (numero_visible > 0),
    CONSTRAINT ck_surtidor_combustible
        CHECK (combustible IN ('GASOLINA', 'DIESEL')),
    CONSTRAINT ck_surtidor_estado
        CHECK (estado IN ('OPERATIVO', 'AVERIADO', 'MANTENIMIENTO')),
    CONSTRAINT fk_surtidor_estacion FOREIGN KEY (id_estacion)
        REFERENCES ESTACION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS EMPLEADO (
    id INT NOT NULL AUTO_INCREMENT,
    numero_empleado VARCHAR(20) NOT NULL,
    documento VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(254) NULL,
    fecha_incorporacion DATE NOT NULL,
    id_estacion INT NOT NULL,
    id_supervisor INT NULL,
    CONSTRAINT pk_empleado PRIMARY KEY (id),
    CONSTRAINT uq_empleado_numero UNIQUE (numero_empleado),
    CONSTRAINT uq_empleado_documento UNIQUE (documento),
    CONSTRAINT ck_empleado_contacto
        CHECK (telefono IS NOT NULL OR email IS NOT NULL),
    CONSTRAINT fk_empleado_estacion FOREIGN KEY (id_estacion)
        REFERENCES ESTACION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_empleado_supervisor FOREIGN KEY (id_supervisor)
        REFERENCES EMPLEADO (id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS FUNCION (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(30) NOT NULL,
    nombre VARCHAR(60) NOT NULL,
    descripcion VARCHAR(200) NULL,
    CONSTRAINT pk_funcion PRIMARY KEY (id),
    CONSTRAINT uq_funcion_codigo UNIQUE (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS ASIGNACION_FUNCION (
    id INT NOT NULL AUTO_INCREMENT,
    id_empleado INT NOT NULL,
    id_funcion INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    limite_devoluciones DECIMAL(10,2) NULL,
    numero_acreditacion VARCHAR(30) NULL,
    fecha_caducidad_acreditacion DATE NULL,
    presupuesto_compras DECIMAL(10,2) NULL,
    nivel_autorizacion INT NULL,
    CONSTRAINT pk_asignacion_funcion PRIMARY KEY (id),
    CONSTRAINT ck_asignacion_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT ck_asignacion_valores_positivos CHECK (
        (limite_devoluciones IS NULL OR limite_devoluciones > 0)
        AND (presupuesto_compras IS NULL OR presupuesto_compras > 0)
        AND (nivel_autorizacion IS NULL OR nivel_autorizacion > 0)
    ),
    CONSTRAINT ck_asignacion_acreditacion_completa CHECK (
        (numero_acreditacion IS NULL AND fecha_caducidad_acreditacion IS NULL)
        OR
        (numero_acreditacion IS NOT NULL AND fecha_caducidad_acreditacion IS NOT NULL)
    ),
    CONSTRAINT ck_asignacion_un_grupo_especifico CHECK (
        ((limite_devoluciones IS NOT NULL) + (numero_acreditacion IS NOT NULL)
         + (presupuesto_compras IS NOT NULL) + (nivel_autorizacion IS NOT NULL)) = 1
    ),
    CONSTRAINT fk_asignacion_empleado FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_asignacion_funcion FOREIGN KEY (id_funcion)
        REFERENCES FUNCION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS CLIENTE (
    id INT NOT NULL AUTO_INCREMENT,
    id_cliente_antiguo INT NULL,
    codigo_cliente VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NULL,
    email VARCHAR(254) NULL,
    matricula VARCHAR(15) NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT uq_cliente_id_antiguo UNIQUE (id_cliente_antiguo),
    CONSTRAINT uq_cliente_codigo UNIQUE (codigo_cliente),
    CONSTRAINT uq_cliente_matricula UNIQUE (matricula),
    CONSTRAINT ck_cliente_contacto
        CHECK (telefono IS NOT NULL OR email IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS LOTE_TARJETA (
    id INT NOT NULL AUTO_INCREMENT,
    codigo_lote VARCHAR(30) NOT NULL,
    fecha_recepcion DATE NOT NULL,
    cantidad INT NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'ABIERTO',
    CONSTRAINT pk_lote_tarjeta PRIMARY KEY (id),
    CONSTRAINT uq_lote_tarjeta_codigo UNIQUE (codigo_lote),
    CONSTRAINT ck_lote_tarjeta_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_lote_tarjeta_estado CHECK (estado IN ('ABIERTO', 'CERRADO'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS TARJETA (
    id INT NOT NULL AUTO_INCREMENT,
    numero_tarjeta VARCHAR(30) NOT NULL,
    id_lote INT NOT NULL,
    id_cliente INT NULL,
    fecha_emision DATE NOT NULL,
    fecha_asignacion DATE NULL,
    CONSTRAINT pk_tarjeta PRIMARY KEY (id),
    CONSTRAINT uq_tarjeta_numero UNIQUE (numero_tarjeta),
    CONSTRAINT uq_tarjeta_cliente UNIQUE (id_cliente),
    CONSTRAINT fk_tarjeta_lote FOREIGN KEY (id_lote)
        REFERENCES LOTE_TARJETA (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tarjeta_cliente FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS PRODUCTO (
    id INT NOT NULL AUTO_INCREMENT,
    referencia VARCHAR(30) NOT NULL,
    descripcion VARCHAR(150) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    modalidad VARCHAR(15) NOT NULL,
    formato_envase VARCHAR(50) NULL,
    precision_pesada TINYINT NULL,
    CONSTRAINT pk_producto PRIMARY KEY (id),
    CONSTRAINT uq_producto_referencia UNIQUE (referencia),
    CONSTRAINT ck_producto_precio CHECK (precio_venta > 0),
    CONSTRAINT ck_producto_estado CHECK (estado IN ('ACTIVO', 'RETIRADO')),
    CONSTRAINT ck_producto_modalidad CHECK (
        (modalidad = 'ENVASADO'
         AND formato_envase IS NOT NULL AND precision_pesada IS NULL)
        OR
        (modalidad = 'GRANEL'
         AND formato_envase IS NULL AND precision_pesada BETWEEN 0 AND 3)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS PROVEEDOR (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    id_fiscal VARCHAR(20) NOT NULL,
    razon_social VARCHAR(120) NOT NULL,
    contacto VARCHAR(100) NOT NULL,
    CONSTRAINT pk_proveedor PRIMARY KEY (id),
    CONSTRAINT uq_proveedor_codigo UNIQUE (codigo),
    CONSTRAINT uq_proveedor_id_fiscal UNIQUE (id_fiscal)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS ACUERDO_SUMINISTRO (
    id INT NOT NULL AUTO_INCREMENT,
    id_proveedor INT NOT NULL,
    id_producto INT NOT NULL,
    referencia_proveedor VARCHAR(40) NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    plazo_entrega INT NOT NULL,
    CONSTRAINT pk_acuerdo_suministro PRIMARY KEY (id),
    CONSTRAINT uq_acuerdo_proveedor_producto UNIQUE (id_proveedor, id_producto),
    CONSTRAINT ck_acuerdo_precio CHECK (precio_compra > 0),
    CONSTRAINT ck_acuerdo_plazo CHECK (plazo_entrega >= 0),
    CONSTRAINT fk_acuerdo_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES PROVEEDOR (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_acuerdo_producto FOREIGN KEY (id_producto)
        REFERENCES PRODUCTO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS CAMPANA (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    CONSTRAINT pk_campana PRIMARY KEY (id),
    CONSTRAINT uq_campana_codigo UNIQUE (codigo),
    CONSTRAINT ck_campana_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS INSCRIPCION_CAMPANA (
    id INT NOT NULL AUTO_INCREMENT,
    id_cliente INT NOT NULL,
    id_campana INT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    CONSTRAINT pk_inscripcion_campana PRIMARY KEY (id),
    CONSTRAINT uq_inscripcion_cliente_campana UNIQUE (id_cliente, id_campana),
    CONSTRAINT fk_inscripcion_cliente FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_inscripcion_campana FOREIGN KEY (id_campana)
        REFERENCES CAMPANA (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS VENTA (
    id INT NOT NULL AUTO_INCREMENT,
    referencia_ticket VARCHAR(40) NOT NULL,
    id_estacion INT NOT NULL,
    id_empleado INT NOT NULL,
    id_cliente INT NULL,
    fecha_hora DATETIME(3) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PREPARACION',
    importe_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT pk_venta PRIMARY KEY (id),
    CONSTRAINT uq_venta_ticket UNIQUE (referencia_ticket),
    CONSTRAINT ck_venta_estado
        CHECK (estado IN ('PREPARACION', 'CONFIRMADA', 'PAGADA')),
    CONSTRAINT ck_venta_importe CHECK (importe_total >= 0),
    CONSTRAINT fk_venta_estacion FOREIGN KEY (id_estacion)
        REFERENCES ESTACION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_empleado FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS LINEA_VENTA (
    id INT NOT NULL AUTO_INCREMENT,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    numero_linea INT NOT NULL,
    cantidad DECIMAL(10,3) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_linea_venta PRIMARY KEY (id),
    CONSTRAINT uq_linea_venta_numero UNIQUE (id_venta, numero_linea),
    CONSTRAINT ck_linea_numero CHECK (numero_linea > 0),
    CONSTRAINT ck_linea_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_linea_precio CHECK (precio_unitario > 0),
    CONSTRAINT ck_linea_importe CHECK (
        importe > 0 AND importe = ROUND(cantidad * precio_unitario, 2)
    ),
    CONSTRAINT fk_linea_venta FOREIGN KEY (id_venta)
        REFERENCES VENTA (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_linea_producto FOREIGN KEY (id_producto)
        REFERENCES PRODUCTO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS PAGO_VENTA (
    id INT NOT NULL AUTO_INCREMENT,
    id_venta INT NOT NULL,
    fecha_hora DATETIME(3) NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    medio_pago VARCHAR(20) NOT NULL,
    CONSTRAINT pk_pago_venta PRIMARY KEY (id),
    CONSTRAINT ck_pago_venta_importe CHECK (importe > 0),
    CONSTRAINT ck_pago_venta_medio
        CHECK (medio_pago IN ('EFECTIVO', 'TARJETA', 'OTRO')),
    CONSTRAINT fk_pago_venta_venta FOREIGN KEY (id_venta)
        REFERENCES VENTA (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS DEVOLUCION (
    id INT NOT NULL AUTO_INCREMENT,
    id_linea INT NOT NULL,
    id_empleado INT NOT NULL,
    cantidad DECIMAL(10,3) NOT NULL,
    motivo VARCHAR(200) NOT NULL,
    fecha_hora DATETIME(3) NOT NULL,
    importe_reintegrado DECIMAL(10,2) NOT NULL,
    apta_reventa BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_devolucion PRIMARY KEY (id),
    CONSTRAINT ck_devolucion_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_devolucion_importe CHECK (importe_reintegrado >= 0),
    CONSTRAINT fk_devolucion_linea FOREIGN KEY (id_linea)
        REFERENCES LINEA_VENTA (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_devolucion_empleado FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS INCIDENCIA (
    id INT NOT NULL AUTO_INCREMENT,
    referencia VARCHAR(30) NOT NULL,
    id_estacion INT NOT NULL,
    id_empleado INT NULL,
    descripcion VARCHAR(255) NOT NULL,
    fecha_hora DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT pk_incidencia PRIMARY KEY (id),
    CONSTRAINT uq_incidencia_referencia UNIQUE (referencia),
    CONSTRAINT ck_incidencia_estado
        CHECK (estado IN ('ABIERTA', 'EN_CURSO', 'CERRADA')),
    CONSTRAINT fk_incidencia_estacion FOREIGN KEY (id_estacion)
        REFERENCES ESTACION (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_incidencia_empleado FOREIGN KEY (id_empleado)
        REFERENCES EMPLEADO (id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS HISTORIAL_PRECIO (
    id INT NOT NULL AUTO_INCREMENT,
    id_producto INT NOT NULL,
    precio_anterior DECIMAL(10,2) NOT NULL,
    precio_nuevo DECIMAL(10,2) NOT NULL,
    fecha_hora DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT pk_historial_precio PRIMARY KEY (id),
    CONSTRAINT ck_historial_precios_positivos
        CHECK (precio_anterior > 0 AND precio_nuevo > 0),
    CONSTRAINT ck_historial_precios_distintos
        CHECK (precio_anterior <> precio_nuevo),
    CONSTRAINT fk_historial_precio_producto FOREIGN KEY (id_producto)
        REFERENCES PRODUCTO (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS PAGO_REPOSTAJE (
    id INT NOT NULL AUTO_INCREMENT,
    id_pago_antiguo INT NULL,
    id_cliente INT NOT NULL,
    fecha DATE NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    -- La escala ampliada conserva decimales que Gson puede escribir en JSON.
    -- Antes de migrar, comprobar que los valores reales caben en DECIMAL(30,15).
    litros DECIMAL(30,15) NOT NULL,
    combustible VARCHAR(20) NOT NULL,
    CONSTRAINT pk_pago_repostaje PRIMARY KEY (id),
    CONSTRAINT uq_pago_repostaje_id_antiguo UNIQUE (id_pago_antiguo),
    CONSTRAINT ck_pago_repostaje_id_antiguo
        CHECK (id_pago_antiguo IS NULL OR id_pago_antiguo > 0),
    CONSTRAINT ck_pago_repostaje_importe CHECK (importe > 0),
    CONSTRAINT ck_pago_repostaje_litros CHECK (litros > 0),
    CONSTRAINT ck_pago_repostaje_combustible
        CHECK (combustible IN ('GASOLINA', 'DIESEL')),
    CONSTRAINT fk_pago_repostaje_cliente FOREIGN KEY (id_cliente)
        REFERENCES CLIENTE (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
