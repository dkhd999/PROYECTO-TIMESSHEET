USE proyecto_2;

-- Escribe aqui exactamente el usuario usado en la pantalla de login.
SET @usuario = 'KevinY';

-- 0. Si la siguiente consulta no devuelve filas, la cuenta no existe con ese nombre.
SELECT id, usuario, rol, tipo, recurso_id, estado
FROM usuario
ORDER BY usuario;

-- Busca la cuenta por el nombre del desarrollador, aunque el usuario sea distinto.
SELECT u.usuario, u.contrasena, u.rol, u.tipo, u.estado,
       r.id AS recurso_id, r.nombre AS desarrollador
FROM usuario u
JOIN recurso r ON r.id = u.recurso_id
WHERE LOWER(r.nombre) LIKE '%kevin%';

-- 1. Confirma que el usuario apunta al recurso correcto.
SELECT u.usuario, u.tipo, u.rol, u.estado,
       r.id AS recurso_id, r.nombre AS desarrollador
FROM usuario u
LEFT JOIN recurso r ON r.id = u.recurso_id
WHERE LOWER(TRIM(u.usuario)) = LOWER(TRIM(@usuario));

-- 2. Muestra los proyectos asignados al recurso, aunque aun no tenga hoja.
SELECT r.id AS recurso_id, r.nombre AS desarrollador,
       p.id AS proyecto_id, p.nombre AS proyecto,
       pr.proyecto_id IS NOT NULL AS asignado,
       h.id AS hoja_id, h.periodo, h.estado
FROM usuario u
JOIN recurso r ON r.id = u.recurso_id
LEFT JOIN proyecto_recurso pr ON pr.recurso_id = r.id
LEFT JOIN proyecto p ON p.id = pr.proyecto_id
LEFT JOIN hoja_tiempo h ON h.proyecto_id = p.id
                              AND h.recurso_id = r.id
                              AND h.estado <> 'Inactiva'
WHERE LOWER(TRIM(u.usuario)) = LOWER(TRIM(@usuario))
ORDER BY p.nombre, h.id;

-- 3. Vista general: todas las asignaciones existentes.
SELECT pr.proyecto_id, p.nombre AS proyecto,
       pr.recurso_id, r.nombre AS desarrollador
FROM proyecto_recurso pr
JOIN proyecto p ON p.id = pr.proyecto_id
JOIN recurso r ON r.id = pr.recurso_id
ORDER BY p.nombre, r.nombre;

-- Si el primer resultado tiene recurso_id NULL, corrige la cuenta:
-- UPDATE usuario SET recurso_id = ID_DEL_RECURSO, tipo = 'Recurso',
--     rol = 'Desarrollador', estado = 'Activo'
-- WHERE usuario = @usuario;

-- Si el segundo resultado tiene asignado = 0, crea la asignacion:
-- INSERT IGNORE INTO proyecto_recurso (proyecto_id, recurso_id)
-- VALUES (ID_DEL_PROYECTO, ID_DEL_RECURSO);
