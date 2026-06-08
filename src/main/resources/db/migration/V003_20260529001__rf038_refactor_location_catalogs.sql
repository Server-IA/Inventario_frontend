/*=============================================================================
 Nombre del archivo : V003_20260529001__rf038_refactor_location_catalogs.sql
 Descripcion        : Migracion para refactorizar catalogos globales de localizacion.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2026-05-29 | 1.0.0   | Juan Manuel          | Creacion del archivo.                                                                                                              |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
-- RF-038: Refactorizar pais, departamento y municipio como catalogos globales.
-- Debe ejecutarse despues de la limpieza/fusion de registros duplicados de localizacion.

-- 1. Quitar constraints antiguos que dependian de empresa.
ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS departamento_dep_nombre_dep_empresa_id_key;

ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS departamento_dep_nombre_dep_empresa_id_key1;

ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS departamento_dep_nombre_dep_empresa_id_key2;

ALTER TABLE IF EXISTS public.municipio
    DROP CONSTRAINT IF EXISTS municipio_mun_nombre_mun_empresa_id_key;

ALTER TABLE IF EXISTS public.municipio
    DROP CONSTRAINT IF EXISTS municipio_mun_empresa_id_fkey;

ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS departamento_dep_empresa_id_fkey;

ALTER TABLE IF EXISTS public.pais
    DROP CONSTRAINT IF EXISTS pais_pai_empresa_id_fkey;

-- 2. Quitar columnas de empresa para volver la localizacion global.
ALTER TABLE IF EXISTS public.municipio
    DROP COLUMN IF EXISTS mun_empresa_id;

ALTER TABLE IF EXISTS public.departamento
    DROP COLUMN IF EXISTS dep_empresa_id;

ALTER TABLE IF EXISTS public.pais
    DROP COLUMN IF EXISTS pai_empresa_id;

-- 3. Normalizar columnas sin cambiar TYPE, para no romper vistas dependientes.
ALTER TABLE IF EXISTS public.pais
    ALTER COLUMN pai_nombre SET NOT NULL,
    ALTER COLUMN pai_codigo SET NOT NULL,
    ALTER COLUMN pai_acronimo SET NOT NULL,
    ALTER COLUMN pai_estado_id SET DEFAULT 1,
    ALTER COLUMN pai_estado_id SET NOT NULL;

ALTER TABLE IF EXISTS public.departamento
    ALTER COLUMN dep_nombre SET NOT NULL,
    ALTER COLUMN dep_pais_id SET NOT NULL,
    ALTER COLUMN dep_codigo SET NOT NULL,
    ALTER COLUMN dep_acronimo SET NOT NULL,
    ALTER COLUMN dep_estado_id SET DEFAULT 1,
    ALTER COLUMN dep_estado_id SET NOT NULL;

ALTER TABLE IF EXISTS public.municipio
    ALTER COLUMN mun_nombre SET NOT NULL,
    ALTER COLUMN mun_departamento_id SET NOT NULL,
    ALTER COLUMN mun_estado_id SET DEFAULT 1,
    ALTER COLUMN mun_estado_id SET NOT NULL;

-- 4. Asegurar FKs principales si no existen.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'pais_pai_estado_id_fkey'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT pais_pai_estado_id_fkey
            FOREIGN KEY (pai_estado_id)
            REFERENCES public.estado (est_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'departamento_dep_pais_id_fkey'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT departamento_dep_pais_id_fkey
            FOREIGN KEY (dep_pais_id)
            REFERENCES public.pais (pai_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'departamento_dep_estado_id_fkey'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT departamento_dep_estado_id_fkey
            FOREIGN KEY (dep_estado_id)
            REFERENCES public.estado (est_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'municipio_mun_departamento_id_fkey'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT municipio_mun_departamento_id_fkey
            FOREIGN KEY (mun_departamento_id)
            REFERENCES public.departamento (dep_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'municipio_mun_estado_id_fkey'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT municipio_mun_estado_id_fkey
            FOREIGN KEY (mun_estado_id)
            REFERENCES public.estado (est_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;
END $$;

-- 5. Reemplazar CHECK constraints.
ALTER TABLE IF EXISTS public.pais
    DROP CONSTRAINT IF EXISTS chk_pais_acronimo_3_letras;

ALTER TABLE IF EXISTS public.pais
    DROP CONSTRAINT IF EXISTS chk_pais_codigo_positivo;

ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS chk_departamento_acronimo_3_letras;

ALTER TABLE IF EXISTS public.departamento
    DROP CONSTRAINT IF EXISTS chk_departamento_codigo_positivo;

ALTER TABLE IF EXISTS public.municipio
    DROP CONSTRAINT IF EXISTS chk_municipio_acronimo_3_letras;

ALTER TABLE IF EXISTS public.municipio
    DROP CONSTRAINT IF EXISTS chk_municipio_codigo_positivo;

ALTER TABLE IF EXISTS public.pais
    ADD CONSTRAINT chk_pais_acronimo_3_letras
    CHECK (pai_acronimo ~ '^[A-Za-z]{1,3}$');

ALTER TABLE IF EXISTS public.pais
    ADD CONSTRAINT chk_pais_codigo_positivo
    CHECK (pai_codigo > 0);

ALTER TABLE IF EXISTS public.departamento
    ADD CONSTRAINT chk_departamento_acronimo_3_letras
    CHECK (dep_acronimo ~ '^[A-Za-z]{1,3}$');

ALTER TABLE IF EXISTS public.departamento
    ADD CONSTRAINT chk_departamento_codigo_positivo
    CHECK (dep_codigo > 0);

ALTER TABLE IF EXISTS public.municipio
    ADD CONSTRAINT chk_municipio_acronimo_3_letras
    CHECK (mun_acronimo IS NULL OR mun_acronimo ~ '^[A-Za-z]{1,3}$');

ALTER TABLE IF EXISTS public.municipio
    ADD CONSTRAINT chk_municipio_codigo_positivo
    CHECK (mun_codigo IS NULL OR mun_codigo > 0);

-- 6. Crear UNIQUE constraints globales y jerarquicos.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_pais_nombre'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT uq_pais_nombre UNIQUE (pai_nombre);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_pais_codigo'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT uq_pais_codigo UNIQUE (pai_codigo);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_pais_acronimo'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT uq_pais_acronimo UNIQUE (pai_acronimo);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_departamento_pais_nombre'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT uq_departamento_pais_nombre UNIQUE (dep_pais_id, dep_nombre);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_departamento_pais_codigo'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT uq_departamento_pais_codigo UNIQUE (dep_pais_id, dep_codigo);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_departamento_pais_acronimo'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT uq_departamento_pais_acronimo UNIQUE (dep_pais_id, dep_acronimo);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_municipio_departamento_nombre'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT uq_municipio_departamento_nombre UNIQUE (mun_departamento_id, mun_nombre);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_municipio_departamento_codigo'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT uq_municipio_departamento_codigo UNIQUE (mun_departamento_id, mun_codigo);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_municipio_departamento_acronimo'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT uq_municipio_departamento_acronimo UNIQUE (mun_departamento_id, mun_acronimo);
    END IF;
END $$;

-- 7. Agregar columnas de auditoria requeridas por las entidades JPA.
ALTER TABLE IF EXISTS public.pais
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_by BIGINT,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT;

ALTER TABLE IF EXISTS public.departamento
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_by BIGINT,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT;

ALTER TABLE IF EXISTS public.municipio
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS created_by BIGINT,
    ADD COLUMN IF NOT EXISTS updated_by BIGINT;

UPDATE public.pais
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

UPDATE public.departamento
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

UPDATE public.municipio
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE IF EXISTS public.pais
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE IF EXISTS public.departamento
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE IF EXISTS public.municipio
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN created_at SET NOT NULL;

-- 8. Agregar FKs de auditoria hacia usuario.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_pais_created_by'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT fk_pais_created_by
            FOREIGN KEY (created_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_pais_updated_by'
          AND conrelid = 'public.pais'::regclass
    ) THEN
        ALTER TABLE public.pais
            ADD CONSTRAINT fk_pais_updated_by
            FOREIGN KEY (updated_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_departamento_created_by'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT fk_departamento_created_by
            FOREIGN KEY (created_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_departamento_updated_by'
          AND conrelid = 'public.departamento'::regclass
    ) THEN
        ALTER TABLE public.departamento
            ADD CONSTRAINT fk_departamento_updated_by
            FOREIGN KEY (updated_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_municipio_created_by'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT fk_municipio_created_by
            FOREIGN KEY (created_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_municipio_updated_by'
          AND conrelid = 'public.municipio'::regclass
    ) THEN
        ALTER TABLE public.municipio
            ADD CONSTRAINT fk_municipio_updated_by
            FOREIGN KEY (updated_by)
            REFERENCES public.usuario (usu_id)
            ON UPDATE NO ACTION
            ON DELETE NO ACTION;
    END IF;
END $$;









