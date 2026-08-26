USE proyecto_2;

CREATE TABLE IF NOT EXISTS usuario (
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
    CONSTRAINT ck_usuario_tipo_recurso CHECK (
        (tipo = 'Recurso' AND recurso_id IS NOT NULL)
        OR (tipo = 'Gestor' AND recurso_id IS NULL)
    )
) ENGINE=InnoDB;

INSERT INTO recurso (nombre, correo, rol, tarifa_base, tipo, estado)
VALUES ('Desarrollador de prueba', 'desarrollador@proyecto.com', 'Desarrollador', 25.00, 'Junior', 'Activo')
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

SET @recurso_id = LAST_INSERT_ID();

INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado)
VALUES ('admin', 'admin123', 'Administrador', 'Gestor', NULL, 'Activo')
ON DUPLICATE KEY UPDATE
    contrasena = VALUES(contrasena),
    rol = VALUES(rol),
    tipo = VALUES(tipo),
    recurso_id = VALUES(recurso_id),
    estado = VALUES(estado);

INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado)
VALUES ('desarrollador', 'dev123', 'Desarrollador', 'Recurso', @recurso_id, 'Activo')
ON DUPLICATE KEY UPDATE
    contrasena = VALUES(contrasena),
    rol = VALUES(rol),
    tipo = VALUES(tipo),
    recurso_id = VALUES(recurso_id),
    estado = VALUES(estado);
