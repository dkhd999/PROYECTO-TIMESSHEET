-- ============================================================
-- STORED PROCEDURE: sp_reporte_estadistico_horas
-- Reporte estadistico de horas trabajadas por desarrollo, en un
-- proyecto y rango de fechas, para toma de decisiones (gerente).
--
-- Valida (consistente con sp_guardar_reporte_estadistico):
--   * el proyecto existe y esta Activo o Finalizado
--   * fecha_inicio <= fecha_fin
--   * el rango cae dentro del periodo vigente del proyecto
--
-- Uso: CALL sp_reporte_estadistico_horas(idProyecto, '2026-08-01', '2026-08-31')
-- Devuelve: nombre del desarrollo, tipo (Junior/Senior) y total de
--           horas trabajadas en el rango (agrupadas por desarrollo).
-- ============================================================
USE proyecto_2;

DROP PROCEDURE IF EXISTS sp_reporte_estadistico_horas;
DELIMITER $$
CREATE PROCEDURE sp_reporte_estadistico_horas(
    IN p_proyecto_id INT,
    IN p_fecha_inicio DATE,
    IN p_fecha_fin    DATE
)
BEGIN
    DECLARE v_estado         VARCHAR(20);

    SELECT estado
      INTO v_estado
      FROM proyecto
     WHERE id = p_proyecto_id
     LIMIT 1;

    IF v_estado IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El proyecto no existe.';
    ELSEIF v_estado = 'Inactivo' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El reporte solo aplica a proyectos Activos o Finalizados.';
    ELSEIF p_fecha_inicio > p_fecha_fin THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de inicio debe ser anterior o igual a la fecha de fin.';
    ELSE
        SELECT r.nombre            AS desarrollador,
               r.tipo              AS tipo,
               ROUND(COALESCE(SUM(d.horas),0), 2) AS total_horas
          FROM detalle_actividad d
          JOIN hoja_tiempo h ON h.id = d.hoja_tiempo_id
          JOIN recurso r     ON r.id = h.recurso_id
         WHERE h.proyecto_id = p_proyecto_id
           AND h.estado <> 'Inactiva'
           AND d.fecha BETWEEN p_fecha_inicio AND p_fecha_fin
         GROUP BY r.id, r.nombre, r.tipo
         ORDER BY total_horas DESC;
    END IF;
END$$
DELIMITER ;