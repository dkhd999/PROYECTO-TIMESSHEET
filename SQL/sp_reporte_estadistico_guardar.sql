-- ============================================================
-- HISTORIAL DE REPORTES ESTADISTICOS
-- Guarda cada reporte estadistico generado (para toma de decisiones).
-- Proyecto_2MVC - MySQL
-- Ejecutar en la base proyecto_2
-- ============================================================
USE proyecto_2;

-- ════════════════════════════════════════════════════════════
-- TABLA: reporte_estadistico
-- Archiva un registro por cada (proyecto + desarrollo) en el rango
-- de fechas consultado, con el total de horas calculado.
-- ════════════════════════════════════════════════════════════
DROP TABLE IF EXISTS reporte_estadistico;
CREATE TABLE reporte_estadistico (
    id INT NOT NULL AUTO_INCREMENT,
    proyecto_id INT NOT NULL,
    desarrollador VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    total_horas DECIMAL(10,2) NOT NULL,
    fecha_generacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_reporte_est_proyecto FOREIGN KEY (proyecto_id) REFERENCES proyecto(id),
    CONSTRAINT ck_reporte_dias CHECK (fecha_inicio <= fecha_fin)
) ENGINE=InnoDB;

-- ════════════════════════════════════════════════════════════
-- SP: sp_guardar_reporte_estadistico
-- Calcula las horas por desarrollo de un proyecto y archiva un
-- registro por desarrollo en reporte_estadistico.
--
-- IMPORTANTE: el rango de fechas se IGNORA (no filtra las horas).
-- Se archivan las horas TOTALES del proyecto, de forma consistente
-- con el reporte (sp_reporte_estadistico_horas), que tambien ignora
-- la fecha.
--
-- Valida: proyecto existe y esta Activo/Finalizado.
--
-- Uso: CALL sp_guardar_reporte_estadistico(idProyecto, '2026-01-01', '2026-12-31')
-- Devuelve (out): @filas guardadas, @total_horas general y @msg.
-- ════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS sp_guardar_reporte_estadistico;
DELIMITER $$
CREATE PROCEDURE sp_guardar_reporte_estadistico(
    IN p_proyecto_id   INT,
    IN p_fecha_inicio  DATE,
    IN p_fecha_fin     DATE,
    OUT p_filas        INT,
    OUT p_total_horas  DECIMAL(10,2),
    OUT p_msg          VARCHAR(255)
)
BEGIN
    DECLARE v_estado         VARCHAR(20);
    DECLARE v_total           DECIMAL(10,2) DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_filas = 0; SET p_total_horas = 0;
        SET p_msg = 'Error en la base de datos. Operacion cancelada.';
    END;

    START TRANSACTION;

    -- 1. Validar proyecto y su estado
    SELECT estado
      INTO v_estado
      FROM proyecto
     WHERE id = p_proyecto_id
     LIMIT 1;

    IF v_estado IS NULL THEN
        SET p_msg = 'El proyecto no existe.';
        ROLLBACK;
    ELSEIF v_estado = 'Inactivo' THEN
        SET p_msg = 'El reporte solo aplica a proyectos Activos o Finalizados.';
        ROLLBACK;
    ELSE
        -- 2. Archivar un registro por desarrollo con las horas TOTALES del
        --    proyecto (sin filtro por fecha, como exige el reporte)
        INSERT INTO reporte_estadistico (proyecto_id, desarrollador, tipo, fecha_inicio, fecha_fin, total_horas)
        SELECT h.proyecto_id,
               r.nombre,
               r.tipo,
               p_fecha_inicio,
               p_fecha_fin,
               ROUND(COALESCE(SUM(d.horas),0), 2)
          FROM detalle_actividad d
          JOIN hoja_tiempo h ON h.id = d.hoja_tiempo_id
          JOIN recurso r     ON r.id = h.recurso_id
         WHERE h.proyecto_id = p_proyecto_id
           AND h.estado <> 'Inactiva'
         GROUP BY h.proyecto_id, r.id, r.nombre, r.tipo
         ORDER BY r.nombre;

        SET p_filas = ROW_COUNT();

        -- 3. Total general del reporte (para mostrar en el PDF)
        SELECT ROUND(COALESCE(SUM(total_horas),0),2) INTO v_total
          FROM reporte_estadistico
         WHERE proyecto_id = p_proyecto_id AND fecha_inicio = p_fecha_inicio AND fecha_fin = p_fecha_fin;
        SET p_total_horas = v_total;

        SET p_msg = CONCAT('Reporte archivado: ', p_filas, ' registros.');

        IF p_filas = 0 THEN
            SET p_msg = 'No hay horas registradas para el proyecto seleccionado.';
        END IF;
    END IF;

    COMMIT;
END$$
DELIMITER ;

-- ════════════════════════════════════════════════════════════
-- SP: sp_historial_reporte_estadistico
-- Consulta el historial de reportes archivados (opcional).
-- ════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS sp_historial_reporte_estadistico;
DELIMITER $$
CREATE PROCEDURE sp_historial_reporte_estadistico()
BEGIN
    SELECT id, proyecto_id, desarrollador, tipo, fecha_inicio, fecha_fin,
           total_horas, fecha_generacion
      FROM reporte_estadistico
     ORDER BY fecha_generacion DESC, id DESC;
END$$
DELIMITER ;