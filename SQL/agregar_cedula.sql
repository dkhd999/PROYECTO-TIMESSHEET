-- Script para agregar la columna cedula a la tabla recurso
-- Ejecutar en la base de datos proyecto_2

USE proyecto_2;

-- Agregar columna cedula despues de nombre
ALTER TABLE recurso
    ADD COLUMN cedula VARCHAR(10) NOT NULL DEFAULT '' AFTER nombre;

-- Agregar restriccion UNIQUE para cedula
ALTER TABLE recurso
    ADD UNIQUE KEY uk_recurso_cedula (cedula);

-- Quitar el DEFAULT para forzar que siempre se ingrese
ALTER TABLE recurso
    ALTER COLUMN cedula DROP DEFAULT;

-- Actualizar el registro de prueba con una cedula valida
UPDATE recurso SET cedula = '1712345678' WHERE id = 1;
