USE proyecto_2;

-- Verifica qué proyectos tiene asignado cada desarrollador.
SELECT r.id AS recurso_id, r.nombre AS desarrollador,
       p.id AS proyecto_id, p.nombre AS proyecto
FROM recurso r
LEFT JOIN proyecto_recurso pr ON pr.recurso_id = r.id
LEFT JOIN proyecto p ON p.id = pr.proyecto_id
ORDER BY r.nombre, p.nombre;

-- Para asignar un desarrollador a un proyecto, reemplaza ambos valores:
-- INSERT IGNORE INTO proyecto_recurso (proyecto_id, recurso_id)
-- VALUES (ID_DEL_PROYECTO, ID_DEL_DESARROLLADOR);