-- ============================================================
-- Este archivo contenía el SP: sp_reporte_horas_por_proyecto
-- (reporte de horas por PROYECTO del desarrollador).
--
-- Fue ELIMINADO porque el "Reporte Estadístico" se revirtió al
-- modelo por DESARROLLADOR-dentro-de-PROYECTO (respeta el combo
-- de Proyecto), por lo que este procedimiento quedó sin uso.
-- El SP ya fue eliminado de la base de datos (proyecto_2).
--
-- El reporte estadístico actual usa:
--   sp_reporte_estadistico_horas   (ver sp_reporte_estadistico.sql)
-- ============================================================
USE proyecto_2;

DROP PROCEDURE IF EXISTS sp_reporte_horas_por_proyecto;