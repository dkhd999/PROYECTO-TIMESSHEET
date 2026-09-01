-- ============================================================
-- STORED PROCEDURES - Gestion de Proyectos y Registro de Horas
-- Proyecto_2MVC - MySQL
-- Ejecutar en la base proyecto_2
-- ============================================================
USE proyecto_2;

-- ════════════════════════════════════════════════════════════
-- 1) sp_enviar_hoja
--    REGLA DE NEGOCIO: El DESARROLLADOR es el unico que puede
--    ENVIAR la hoja (Borrador -> Enviada). 
--    El GERENTE/Administrador NO puede aprobar ni rechazar.
--    Valida el maximo de horas (RF-03.7) dentro de una transaccion.
--
--    Uso: CALL sp_enviar_hoja(idHoja, @ok, @msg)
--    @ok  = 1 si exito, 0 si fallo
--    @msg = mensaje de resultado
-- ════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS sp_enviar_hoja;
DELIMITER $$
CREATE PROCEDURE sp_enviar_hoja(
    IN p_hoja_id       INT,
    IN p_max_horas     DECIMAL(6,2),   -- maximo de horas permitido (por defecto 60)
    OUT p_ok           INT,
    OUT p_msg          VARCHAR(255)
)
BEGIN
    DECLARE v_estado_actual VARCHAR(20);
    DECLARE v_horas_total   DECIMAL(6,2);

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_ok = 0;
        SET p_msg = 'Error en la base de datos. Operacion cancelada.';
    END;

    START TRANSACTION;

    -- 1. Validar que la hoja exista y no este inactiva
    SELECT estado INTO v_estado_actual
      FROM hoja_tiempo
     WHERE id = p_hoja_id AND estado <> 'Inactiva'
     LIMIT 1;

    IF v_estado_actual IS NULL THEN
        SET p_ok = 0; SET p_msg = 'La hoja de tiempo no existe o esta inactiva.';
        ROLLBACK;
    ELSEIF v_estado_actual = 'Enviada' THEN
        SET p_ok = 0; SET p_msg = 'La hoja ya fue enviada. No se puede reenviar.';
        ROLLBACK;
    ELSEIF v_estado_actual <> 'Borrador' THEN
        SET p_ok = 0; SET p_msg = 'Solo se puede enviar una hoja en estado Borrador.';
        ROLLBACK;
    ELSE
        -- Verificar maximo de horas (RF-03.7)
        SELECT COALESCE(SUM(horas),0) INTO v_horas_total
          FROM detalle_actividad
         WHERE hoja_tiempo_id = p_hoja_id;

        IF v_horas_total > p_max_horas THEN
            SET p_ok = 0;
            SET p_msg = CONCAT('El total de horas (', v_horas_total, ') supera el maximo de ', p_max_horas, '.');
            ROLLBACK;
        ELSE
            UPDATE hoja_tiempo SET estado = 'Enviada' WHERE id = p_hoja_id;
            SET p_ok = 1;
            SET p_msg = 'Hoja enviada correctamente.';
        END IF;
    END IF;

    COMMIT;
END$$
DELIMITER ;

-- ════════════════════════════════════════════════════════════
-- 2) sp_reporte_mensual_recurso
--    Obtiene datos consolidados de un recurso para un mes dado
--    (resumen de todas sus hojas + horas y costo totales).
--
--    Uso: CALL sp_reporte_mensual_recurso(idRecurso, '2026-08')
--    Retorna dos conjuntos de resultados:
--      - Hoja 1: resumen por hoja (id, periodo, estado, horas, costo)
--      - Hoja 2: totales (total_horas_mes, costo_total_mes, nombre_recurso, tipo, tarifa)
-- ════════════════════════════════════════════════════════════
DROP PROCEDURE IF EXISTS sp_reporte_mensual_recurso;
DELIMITER $$
CREATE PROCEDURE sp_reporte_mensual_recurso(
    IN p_recurso_id INT,
    IN p_mes        VARCHAR(7)      -- formato 'YYYY-MM'
)
BEGIN
    DECLARE v_tarifa DECIMAL(10,2);
    DECLARE v_tipo   VARCHAR(20);

    -- 1. Datos del recurso
    SELECT tarifa_base, tipo INTO v_tarifa, v_tipo
      FROM recurso
     WHERE id = p_recurso_id;

    IF v_tarifa IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se encontro el recurso especificado.';
    END IF;

    -- 2. Detalle por hoja del mes (con horas y costo calculado en SQL)
    SELECT h.id                AS hoja_id,
           CONCAT('#', h.id)   AS hoja,
           h.periodo,
           h.estado,
           COALESCE(SUM(d.horas), 0)                                   AS horas,
           ROUND(COALESCE(SUM(d.horas), 0) * v_tarifa, 2)              AS costo
      FROM hoja_tiempo h
      LEFT JOIN detalle_actividad d ON d.hoja_tiempo_id = h.id
     WHERE h.recurso_id = p_recurso_id
       AND LEFT(h.periodo, 7) = p_mes
       AND h.estado <> 'Inactiva'
     GROUP BY h.id, h.periodo, h.estado
     ORDER BY h.periodo;

    -- 3. Totales mensuales
    SELECT ROUND(COALESCE(SUM(d.horas),0), 2)  AS total_horas_mes,
           ROUND(COALESCE(SUM(d.horas),0) * v_tarifa, 2) AS costo_total_mes,
           v_tipo                               AS tipo_recurso,
           v_tarifa                             AS tarifa_hora,
           (SELECT nombre FROM recurso WHERE id = p_recurso_id) AS nombre_recurso
      FROM hoja_tiempo h
      LEFT JOIN detalle_actividad d ON d.hoja_tiempo_id = h.id
     WHERE h.recurso_id = p_recurso_id
       AND LEFT(h.periodo, 7) = p_mes
       AND h.estado <> 'Inactiva';
END$$
DELIMITER ;

-- ════════════════════════════════════════════════════════════
-- EJEMPLOS DE USO
-- ════════════════════════════════════════════════════════════
-- -- El desarrollador envia la hoja 4 (Carlos):
-- SET @ok = 0; SET @msg = '';
-- CALL sp_enviar_hoja(4, 60, @ok, @msg);
-- SELECT @ok, @msg;

-- Nota: el gerente/administrador NO puede aprobar ni rechazar.
-- El unico cambio de estado permitido es: Borrador -> Enviada (por el desarrollador).

-- -- Reporte mensual de Carlos (recurso id=1) para agosto 2026:
-- CALL sp_reporte_mensual_recurso(1, '2026-08');
