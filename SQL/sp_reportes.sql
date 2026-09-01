-- ============================================================
-- STORED PROCEDURE: sp_reporte_horas_por_proyecto
-- Reporte de horas trabajadas por un desarrollador, agrupadas por
-- PROYECTO, dentro de un rango de fechas (toma de decisiones).
--
-- Devuelve: nombre del proyecto y total de horas que el desarrollador
--           trabajo en ese proyecto dentro del rango [inicio, fin].
--
-- Uso: CALL sp_reporte_horas_por_proyecto(idRecurso, '2026-01-01', '2026-08-31')
-- ============================================================
USE proyecto_2;

DROP PROCEDURE IF EXISTS sp_reporte_horas_por_proyecto;
DELIMITER $$
CREATE PROCEDURE sp_reporte_horas_por_proyecto(
    IN p_recurso_id   INT,
    IN p_fecha_inicio DATE,
    IN p_fecha_fin    DATE
)
BEGIN
    IF p_recurso_id <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Debe seleccionar un desarrollador.';
    ELSEIF p_fecha_inicio > p_fecha_fin THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de inicio debe ser anterior o igual a la fecha de fin.';
    ELSE
        SELECT p.id             AS proyecto_id,
               p.nombre         AS proyecto,
               ROUND(COALESCE(SUM(d.horas),0), 2) AS total_horas
          FROM detalle_actividad d
          JOIN hoja_tiempo h ON h.id = d.hoja_tiempo_id
          JOIN proyecto p    ON p.id = h.proyecto_id
         WHERE h.recurso_id = p_recurso_id
           AND h.estado <> 'Inactiva'
           AND d.fecha BETWEEN p_fecha_inicio AND p_fecha_fin
         GROUP BY p.id, p.nombre
         ORDER BY total_horas DESC;
    END IF;
END$$
DELIMITER ;