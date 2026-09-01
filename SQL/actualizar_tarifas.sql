USE proyecto_2;

UPDATE recurso SET tarifa_base = 25.00 WHERE id >= 1 AND tipo = 'Junior';
UPDATE recurso SET tarifa_base = 60.00 WHERE id >= 1 AND tipo = 'Senior';