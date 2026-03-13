-- Migración V2: Agregar columna role_names a la tabla user_profile

ALTER TABLE user_profile
ADD COLUMN role_names VARCHAR(255) NULL;
