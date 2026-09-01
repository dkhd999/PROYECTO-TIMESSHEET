-- ============================================================
-- DATOS ARTIFICIALES - Gestion de Proyectos y Registro de Horas
-- Agrega registros adicionales sin duplicar claves unicas.
-- Ejecutar despues de proyecto2.sql / datos_prueba.sql
-- ============================================================
USE proyecto_2;

-- ── NUEVOS PROYECTOS (3) ──
INSERT INTO proyecto (codigo, nombre, cliente, fecha_inicio, fecha_fin_estimada, estado) VALUES
('PRY-003', 'Plataforma E-commerce',  'Tienda Online SA',  '2026-09-01', '2027-03-31', 'Activo'),
('PRY-004', 'Sistema de Nomina',      'Grupo Empresarial', '2026-09-15', '2027-04-30', 'Activo'),
('PRY-005', 'App de Delivery',        'FastFood Express',  '2026-10-01', '2027-05-31', 'Activo');

-- ── NUEVOS RECURSOS (4) ──
INSERT INTO recurso (nombre, cedula, correo, rol, tarifa_base, tipo, estado) VALUES
('Sofia Torres',    '0912345678', 'sofia.torres@empresa.com',     'Desarrollador', 60.00, 'Senior', 'Activo'),
('Diego Herrera',   '1723456789', 'diego.herrera@empresa.com',    'Desarrollador', 25.00, 'Junior', 'Activo'),
('Valentina Rios',  '1876543219', 'valentina.rios@empresa.com',   'Desarrollador', 60.00, 'Senior', 'Activo'),
('Mateo Castillo',  '0543210987', 'mateo.castillo@empresa.com',   'Desarrollador', 25.00, 'Junior', 'Activo');

-- ── NUEVOS USUARIOS (4) ──
INSERT INTO usuario (usuario, contrasena, rol, tipo, recurso_id, estado) VALUES
('sofia',   'dev123', 'Desarrollador', 'Recurso',  7, 'Activo'),
('diego',   'dev123', 'Desarrollador', 'Recurso',  8, 'Activo'),
('valentina','dev123','Desarrollador', 'Recurso',  9, 'Activo'),
('mateo',   'dev123', 'Desarrollador', 'Recurso', 10, 'Activo');

-- ── ASIGNACIONES RECURSO-PROYECTO (nuevos recursos a proyectos activos) ──
INSERT INTO proyecto_recurso (proyecto_id, recurso_id) VALUES
(3, 4),   -- Kevin Yapu   -> E-commerce
(3, 7),   -- Sofia        -> E-commerce
(4, 5),   -- Kevin Leiton -> Nomina
(4, 8),   -- Diego        -> Nomina
(5, 9),   -- Valentina    -> Delivery
(5, 10),  -- Mateo        -> Delivery
(3, 1),   -- Ana Lopez    -> E-commerce
(3, 6);   -- Gabriela     -> E-commerce

-- ── HOJAS DE TIEMPO (12) ──
INSERT INTO hoja_tiempo (proyecto_id, recurso_id, periodo, estado) VALUES
-- Sofia - E-commerce (Sep 2026)
(3, 7, '2026-09-07 / 2026-09-13', 'Aprobada'),
(3, 7, '2026-09-14 / 2026-09-20', 'Enviada'),
-- Diego - Nomina (Sep 2026)
(4, 8, '2026-09-07 / 2026-09-13', 'Aprobada'),
(4, 8, '2026-09-14 / 2026-09-20', 'Borrador'),
-- Valentina - Delivery (Oct 2026)
(5, 9, '2026-10-05 / 2026-10-11', 'Enviada'),
-- Mateo - Delivery (Oct 2026)
(5, 10, '2026-10-05 / 2026-10-11', 'Aprobada'),
-- Ana - E-commerce (Sep 2026)
(3, 1, '2026-09-07 / 2026-09-13', 'Aprobada'),
-- Gabriela - E-commerce (Sep 2026)
(3, 6, '2026-09-07 / 2026-09-13', 'Enviada'),
-- Kevin Yapu - E-commerce (Sep 2026)
(3, 4, '2026-09-07 / 2026-09-13', 'Borrador'),
-- Kevin Leiton - Nomina (Sep 2026)
(4, 5, '2026-09-07 / 2026-09-13', 'Aprobada'),
-- Reutilizamos proyectos 1 y 2 con nuevos recursos
(1, 7, '2026-09-01 / 2026-09-07', 'Enviada'),
(2, 8, '2026-09-01 / 2026-09-07', 'Borrador');

-- ── DETALLE DE ACTIVIDADES (20 registros) ──
-- Sofia - Hoja 23 (E-commerce)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-09-07', 'Arquitectura de catalogo de productos', 8.0, 'Backend',   23),
('2026-09-08', 'Desarrollo de API de carrito',          7.5, 'Backend',   23),
('2026-09-09', 'Diseno de tabla de pedidos',            6.0, 'Database',  23),
('2026-09-10', 'Implementacion de pasarela de pago',    8.0, 'Backend',   23),
('2026-09-11', 'Testing de checkout completo',          5.5, 'Testing',   23),
('2026-09-12', 'Configuracion de ambiente de produccion',4.0, 'DevOps',   23),
('2026-09-13', 'Documentacion de endpoints',            3.5, 'Backend',   23);
-- Diego - Hoja 25 (Nomina)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-09-07', 'Diseno de modulo de empleados',         7.0, 'Backend',   25),
('2026-09-08', 'Desarrollo de calculo de sueldos',      8.0, 'Backend',   25),
('2026-09-09', 'Implementacion de deducciones',         6.5, 'Backend',   25),
('2026-09-10', 'Tabla de feriados y ausencias',         5.0, 'Database',  25),
('2026-09-11', 'Testing de calculo de aportes',         6.0, 'Testing',   25),
('2026-09-12', 'Generacion de rol de pagos',            4.5, 'Reporting', 25),
('2026-09-13', 'Revision de reglas de negocio',         3.0, 'Backend',   25);
-- Valentina - Hoja 27 (Delivery)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-10-05', 'Arquitectura de app de delivery',       8.0, 'Backend',   27),
('2026-10-06', 'Desarrollo de seguimiento en tiempo real',7.5, 'Backend',  27),
('2026-10-07', 'Diseno de interfaz de repartidor',      6.0, 'Frontend',  27),
('2026-10-08', 'Integracion con mapas',                 7.0, 'Backend',   27),
('2026-10-09', 'Testing de notificaciones push',        5.0, 'Testing',   27),
('2026-10-10', 'Optimizacion de rutas',                 8.0, 'Backend',   27);
-- Mateo - Hoja 28 (Delivery)
INSERT INTO detalle_actividad (fecha, descripcion, horas, modulo, hoja_tiempo_id) VALUES
('2026-10-05', 'Maquetacion de pantalla principal',     6.0, 'Frontend',  28),
('2026-10-06', 'Desarrollo de modulo de categorias',    7.0, 'Frontend',  28),
('2026-10-07', 'Integracion con API de restaurantes',   8.0, 'Backend',   28),
('2026-10-08', 'Implementacion de busqueda',            5.5, 'Backend',   28),
('2026-10-09', 'Pruebas en multiples dispositivos',     6.0, 'Testing',   28);