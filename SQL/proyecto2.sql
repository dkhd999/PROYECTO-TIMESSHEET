CREATE DATABASE IF NOT EXISTS proyecto_2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE proyecto_2;

DROP TABLE IF EXISTS detalle_actividad;
DROP TABLE IF EXISTS hoja_tiempo;
DROP TABLE IF EXISTS proyecto_recurso;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS recurso;
DROP TABLE IF EXISTS proyecto;

CREATE TABLE proyecto (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    cliente VARCHAR(150) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin_estimada DATE NOT NULL,
    estado ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',
    PRIMARY KEY (id),
    UNIQUE KEY uk_proyecto_codigo (codigo),
    CONSTRAINT ck_proyecto_fechas CHECK (fecha_inicio <= fecha_fin_estimada)
) ENGINE=InnoDB;

CREATE TABLE recurso (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    cedula VARCHAR(10) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    tarifa_base DECIMAL(10,2) NOT NULL,
    tipo ENUM('Junior', 'Senior') NOT NULL,
    estado ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',
    PRIMARY KEY (id),
    UNIQUE KEY uk_recurso_cedula (cedula),
    UNIQUE KEY uk_recurso_correo (correo),
    CONSTRAINT ck_recurso_tarifa CHECK (tarifa_base >= 0)
) ENGINE=InnoDB;

CREATE TABLE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(50) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol ENUM('Administrador', 'Desarrollador') NOT NULL,
    tipo ENUM('Gestor', 'Recurso') NOT NULL,
    recurso_id INT NULL,
    estado ENUM('Activo', 'Inactivo') NOT NULL DEFAULT 'Activo',
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_nombre (usuario),
    CONSTRAINT fk_usuario_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id),
    CONSTRAINT ck_usuario_tipo_recurso CHECK ((tipo='Recurso' AND recurso_id IS NOT NULL) OR (tipo='Gestor' AND recurso_id IS NULL))
) ENGINE=InnoDB;

CREATE TABLE proyecto_recurso (
    proyecto_id INT NOT NULL,
    recurso_id INT NOT NULL,
    PRIMARY KEY (proyecto_id, recurso_id),
    CONSTRAINT fk_pr_proyecto FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    CONSTRAINT fk_pr_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id)
) ENGINE=InnoDB;

CREATE TABLE hoja_tiempo (
    id INT NOT NULL AUTO_INCREMENT,
    proyecto_id INT NOT NULL,
    recurso_id INT NOT NULL,
    periodo VARCHAR(50) NOT NULL,
    estado ENUM('Borrador', 'Enviada', 'Aprobada', 'Rechazada', 'Inactiva') NOT NULL DEFAULT 'Borrador',
    PRIMARY KEY (id),
    CONSTRAINT fk_hoja_proyecto FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    CONSTRAINT fk_hoja_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id)
) ENGINE=InnoDB;

CREATE TABLE detalle_actividad (
    id INT NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    horas DECIMAL(5,2) NOT NULL,
    modulo VARCHAR(100) NOT NULL,
    hoja_tiempo_id INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_detalle_fecha_modulo (fecha, modulo, hoja_tiempo_id),
    CONSTRAINT fk_detalle_hoja FOREIGN KEY (hoja_tiempo_id) REFERENCES hoja_tiempo(id) ON DELETE CASCADE,
    CONSTRAINT ck_detalle_horas CHECK (horas > 0 AND horas <= 24)
) ENGINE=InnoDB;

INSERT INTO recurso (nombre, cedula, correo, rol, tarifa_base, tipo, estado)
VALUES ('Desarrollador de prueba', '1712345678', 'desarrollador@proyecto.com', 'Desarrollador', 25.00, 'Junior', 'Activo');

INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado) VALUES
('admin', 'admin123', 'Administrador', 'Gestor', NULL, 'Activo'),
('desarrollador', 'dev123', 'Desarrollador', 'Recurso', 1, 'Activo');
