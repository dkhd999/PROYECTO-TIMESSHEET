USE proyecto_2;

-- La tabla usuario debe vincular cada cuenta de desarrollador con su recurso.
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
    CONSTRAINT fk_usuario_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id)
) ENGINE=InnoDB;

-- Las hojas ya relacionan proyecto y recurso mediante estas claves.
-- No se eliminan datos existentes ni se crean cuentas duplicadas.
ALTER TABLE hoja_tiempo
    MODIFY COLUMN recurso_id INT NOT NULL;