-- ============================================================
-- DATOS DE PRUEBA - Gestion de Proyectos y Registro de Horas
-- Ejecutar despues de proyecto2.sql
-- ============================================================
USE proyecto_2;

-- ── PROYECTOS ──
INSERT INTO proyecto (codigo, nombre, cliente, fecha_inicio, fecha_fin_estimada, estado) VALUES
('PRJ-001', 'Sistema de Inventario', 'Corporacion XYZ', '2026-07-01', '2026-12-31', 'Activo'),
('PRJ-002', 'App Mobile Banking', 'Banco Nacional', '2026-08-01', '2027-02-28', 'Activo'),
('PRJ-003', 'Portal Web E-commerce', 'Tiendas Online SA', '2026-06-15', '2026-11-30', 'Inactivo');

-- ── RECURSOS (desarrolladores) ──
INSERT INTO recurso (nombre, cedula, correo, rol, tarifa_base, tipo, estado) VALUES
('Carlos Perez',     '1712345678', 'carlos@empresa.com',   'Desarrollador', 25.00, 'Junior', 'Activo'),
('Maria Garcia',     '0923456789', 'maria@empresa.com',    'Desarrollador', 60.00, 'Senior', 'Activo'),
('Juan Rodriguez',   '0118765432', 'juan@empresa.com',     'Desarrollador', 25.00, 'Junior', 'Activo'),
('Ana Martinez',     '1809876543', 'ana@empresa.com',      'Desarrollador', 60.00, 'Senior', 'Activo'),
('Pedro Lopez',      '1204567890', 'pedro@empresa.com',    'Desarrollador', 25.00, 'Junior', 'Inactivo');

-- ── USUARIOS ──
INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado) VALUES
('admin',        'admin123',    'Administrador', 'Gestor',   NULL, 'Activo'),
('carlos',       'dev123',      'Desarrollador', 'Recurso',  1,    'Activo'),
('maria',        'dev123',      'Desarrollador', 'Recurso',  2,    'Activo'),
('juan',         'dev123',      'Desarrollador', 'Recurso',  3,    'Activo'),
('ana',          'dev123',      'Desarrollador', 'Recurso',  4,    'Activo'),
('pedro',        'dev123',      'Desarrollador', 'Recurso',  5,    'Inactivo');

-- ── ASIGNACIONES RECURSO-PROYECTO ──
INSERT INTO proyecto_recurso (proyecto_id, recurso_id) VALUES
(1, 1),  -- Carlos  -> Sistema de Inventario
(1, 2),  -- Maria   -> Sistema de Inventario
(2, 2),  -- Maria   -> App Mobile Banking
(2, 3),  -- Juan    -> App Mobile Banking
(2, 4),  -- Ana     -> App Mobile Banking
(3, 1),  -- Carlos  -> Portal Web (inactivo)
(3, 3);  -- Juan    -> Portal Web (inactivo)

-- ── HOJAS DE TIEMPO ──
INSERT INTO hoja_tiempo (proyecto_id, recurso_id, periodo, estado) VALUES
-- Carlos - Agosto 2026
(1, 1, '2026-08-01 / 2026-08-07', 'Aprobada'),
(1, 1, '2026-08-08 / 2026-08-14', 'Aprobada'),
(1, 1, '2026-08-15 / 2026-08-21', 'Enviada'),
(1, 1, '2026-08-22 / 2026-08-28', 'Borrador'),
-- Maria - Agosto 2026
(1, 2, '2026-08-01 / 2026-08-07', 'Aprobada'),
(1, 2, '2026-08-08 / 2026-08-14', 'Aprobada'),
(2, 2, '2026-08-15 / 2026-08-21', 'Enviada'),
-- Juan - Agosto 2026
(2, 3, '2026-08-01 / 2026-08-07', 'Aprobada'),
(2, 3, '2026-08-08 / 2026-08-14', 'Borrador'),
-- Ana - Agosto 2026
(2, 4, '2026-08-01 / 2026-08-07', 'Aprobada'),
(2, 4, '2026-08-08 / 2026-08-14', 'Enviada');

-- ── DETALLE ACTIVIDADES ──

-- Carlos - Hoja 1 (01-07 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-01', 'Configuracion del entorno de desarrollo', 6.0,  'Backend',    1),
('2026-08-02', 'Diseno de base de datos inventario',     7.5,  'Database',   1),
('2026-08-03', 'Desarrollo de CRUD productos',           8.0,  'Backend',    1),
('2026-08-04', 'Implementacion de validaciones',          5.5,  'Backend',    1),
('2026-08-05', 'Testing unitario de modulos',             4.0,  'Testing',    1),
('2026-08-06', 'Documentacion de API REST',               6.0,  'Backend',    1),
('2026-08-07', 'Revision de codigo y commit',             3.0,  'Backend',    1);

-- Carlos - Hoja 2 (08-14 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-08', 'Desarrollo de reportes de inventario',   7.0,  'Reporting',  2),
('2026-08-09', 'Integracion con API de proveedores',     8.0,  'Backend',    2),
('2026-08-10', 'Correccion de bugs en busqueda',          5.0,  'Frontend',   2),
('2026-08-11', 'Optimizacion de consultas SQL',           6.5,  'Database',   2),
('2026-08-12', 'Desarrollo de graficos de stock',         7.0,  'Reporting',  2),
('2026-08-13', 'Deploy a servidor de pruebas',            4.0,  'DevOps',     2),
('2026-08-14', 'Revision y cierre de sprint',             2.5,  'Backend',    2);

-- Carlos - Hoja 3 (15-21 Ago) - Enviada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-15', 'Modulo de alertas de stock bajo',         8.0,  'Backend',    3),
('2026-08-16', 'Configuracion de notificaciones email',   6.0,  'Backend',    3),
('2026-08-17', 'Desarrollo de dashboard principal',       7.5,  'Frontend',   3),
('2026-08-18', 'Implementacion de filtros avanzados',     5.0,  'Frontend',   3),
('2026-08-19', 'Testing de integracion completo',         8.0,  'Testing',    3),
('2026-08-20', 'Correccion de issues del reviewer',       4.0,  'Backend',    3),
('2026-08-21', 'Actualizacion de documentacion',          3.5,  'Backend',    3);

-- Carlos - Hoja 4 (22-28 Ago) - Borrador (parcial)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-22', 'Modulo de exportacion a Excel',           7.0,  'Reporting',  4),
('2026-08-23', 'Pruebas de rendimiento',                  6.0,  'Testing',    4),
('2026-08-24', 'Optimizacion de frontend',                5.0,  'Frontend',   4),
('2026-08-25', 'Desarrollo de modulo de auditoria',       8.0,  'Backend',    4);

-- Maria - Hoja 5 (01-07 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-01', 'Arquitectura del sistema de inventario',  8.0,  'Backend',    5),
('2026-08-02', 'Diseno de esquema de base de datos',      7.0,  'Database',   5),
('2026-08-03', 'Setup de proyecto Maven y dependencias',  5.0,  'DevOps',     5),
('2026-08-04', 'Desarrollo de modulo de categorias',      6.5,  'Backend',    5),
('2026-08-05', 'Code review y refactorizacion',           4.0,  'Backend',    5),
('2026-08-06', 'Capacitacion al equipo de frontend',      3.5,  'Soporte',    5),
('2026-08-07', 'Documentacion tecnica del proyecto',      6.0,  'Backend',    5);

-- Maria - Hoja 6 (08-14 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-08', 'Desarrollo de modulo de usuarios',        7.5,  'Backend',    6),
('2026-08-09', 'Implementacion de roles y permisos',      8.0,  'Backend',    6),
('2026-08-10', 'Diseno de interfaz de administracion',    6.0,  'Frontend',   6),
('2026-08-11', 'Desarrollo de API de autenticacion',      7.0,  'Backend',    6),
('2026-08-12', 'Testing de seguridad y vulnerabilidades', 5.5,  'Testing',    6),
('2026-08-13', 'Optimizacion de queries de login',        4.0,  'Database',   6),
('2026-08-14', 'Revision de sprint con product owner',    3.0,  'Backend',    6);

-- Maria - Hoja 7 (15-21 Ago) - Enviada ( Banking App)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-15', 'Arquitectura microservicios Banking',     8.0,  'Backend',    7),
('2026-08-16', 'Diseno de flujo de transferencias',       7.0,  'Backend',    7),
('2026-08-17', 'Desarrollo de modulo de cuentas',         6.5,  'Backend',    7),
('2026-08-18', 'Implementacion de cifrado de datos',      8.0,  'Backend',    7),
('2026-08-19', 'Configuracion de博物馆de seguridad',         4.0,  'DevOps',     7),
('2026-08-20', 'Testing de flujo completo de login',      5.5,  'Testing',    7),
('2026-08-21', 'Documentacion de endpoints de seguridad', 3.0,  'Backend',    7);

-- Juan - Hoja 8 (01-07 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-01', 'Diseno de interfaz de usuario movil',     7.0,  'Frontend',   8),
('2026-08-02', 'Desarrollo de componentes React Native',  8.0,  'Frontend',   8),
('2026-08-03', 'Integracion con API REST del backend',    6.0,  'Backend',    8),
('2026-08-04', 'Implementacion de navegacion',            5.5,  'Frontend',   8),
('2026-08-05', 'Testing en dispositivos reales',          4.0,  'Testing',    8),
('2026-08-06', 'Correccion de bugs de UI',                6.0,  'Frontend',   8),
('2026-08-07', 'Deploy a tiendas de aplicaciones',        3.5,  'DevOps',     8);

-- Ana - Hoja 10 (01-07 Ago) - Aprobada
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-08-01', 'Diseno de arquitectura Banking App',      8.0,  'Backend',    10),
('2026-08-02', 'Configuracion de servidor de desarrollo', 5.0,  'DevOps',     10),
('2026-08-03', 'Desarrollo de modulo de pagos',           7.5,  'Backend',    10),
('2026-08-04', 'Implementacion de notificaciones push',   6.0,  'Backend',    10),
('2026-08-05', 'Testing de modulo de pagos',              8.0,  'Testing',    10),
('2026-08-06', 'Optimizacion de rendimiento',             5.5,  'Backend',    10),
('2026-08-07', 'Revision de codigo y merge',              3.0,  'Backend',    10);
