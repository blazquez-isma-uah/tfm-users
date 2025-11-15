-- Migración V3: Añadir columnas de version (@Version) y constraint UNIQUE para username
-- Detectado en las entidades:
--  - UserProfileEntity: campo `version` (optimistic locking) y `username` marcado como unique
--  - InstrumentEntity: campo `version` (optimistic locking)

-- 1) Añadir columna `version` a la tabla `user_profile` (INT, NOT NULL, valor por defecto 0)
ALTER TABLE user_profile
  ADD COLUMN version INT NOT NULL DEFAULT 0;

-- 2) Añadir columna `version` a la tabla `instrument` (INT, NOT NULL, valor por defecto 0)
ALTER TABLE instrument
  ADD COLUMN version INT NOT NULL DEFAULT 0;

-- 3) Añadir constraint UNIQUE para username en user_profile (la columna existe desde V1 pero no tenía constraint)
ALTER TABLE user_profile
  ADD CONSTRAINT uc_user_profile_username UNIQUE (username);

