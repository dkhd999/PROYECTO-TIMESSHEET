USE proyecto_2;

-- Muestra las hojas y cuantas actividades tiene cada una.
SELECT h.id AS hoja_id,
       h.proyecto_id,
       p.nombre AS proyecto,
       h.recurso_id,
       r.nombre AS desarrollador,
       h.periodo,
       h.estado,
       COUNT(d.id) AS cantidad_detalles,
       COALESCE(SUM(d.horas), 0) AS total_horas
FROM hoja_tiempo h
JOIN proyecto p ON p.id = h.proyecto_id
JOIN recurso r ON r.id = h.recurso_id
LEFT JOIN detalle_actividad d ON d.hoja_tiempo_id = h.id
WHERE h.estado <> 'Inactiva'
GROUP BY h.id, h.proyecto_id, p.nombre, h.recurso_id, r.nombre, h.periodo, h.estado
ORDER BY h.id DESC;

-- Para revisar solo las hojas de KevinY (recurso_id = 4):
-- SELECT * FROM detalle_actividad WHERE hoja_tiempo_id IN
-- (SELECT id FROM hoja_tiempo WHERE recurso_id = 4);