-- ODO-38 · Modelo de datos del control de pagos (CLAUDE.md §7)
-- Ejecutar PRIMERO en una rama de Neon, nunca directo en producción.
-- Coincide con las entidades CuentaPorCobrarEntity y AbonoEntity. Con ddl-auto=update Hibernate
-- crearía estas tablas al desplegar; este script deja el cambio versionado y revisable (ODO-51).
-- Sin datos: no insertar pacientes reales en pruebas.

CREATE TABLE IF NOT EXISTS cuentas_por_cobrar (
    id               UUID PRIMARY KEY,
    paciente_id      UUID           NOT NULL REFERENCES usuarios (id_usuario),
    tratamiento_id   BIGINT         REFERENCES tratamientos (id),
    descripcion      VARCHAR(255)   NOT NULL,
    monto_total      NUMERIC(10, 2) NOT NULL CHECK (monto_total > 0),
    entrada          NUMERIC(10, 2) NOT NULL DEFAULT 0 CHECK (entrada >= 0 AND entrada <= monto_total),
    cuota_sugerida   NUMERIC(10, 2) CHECK (cuota_sugerida >= 0),
    estado           VARCHAR(20)    NOT NULL CHECK (estado IN ('ACTIVA', 'SALDADA', 'ANULADA')),
    creado_por       UUID           REFERENCES usuarios (id_usuario),
    motivo_anulacion TEXT,
    anulado_por      UUID           REFERENCES usuarios (id_usuario),
    anulado_en       TIMESTAMP,
    creado_en        TIMESTAMP,
    actualizado_en   TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_cuentas_paciente ON cuentas_por_cobrar (paciente_id);

CREATE TABLE IF NOT EXISTS abonos (
    id               UUID PRIMARY KEY,
    cuenta_id        UUID           NOT NULL REFERENCES cuentas_por_cobrar (id),
    fecha            DATE           NOT NULL,
    monto            NUMERIC(10, 2) NOT NULL CHECK (monto > 0),
    cita_id          UUID           REFERENCES citas (id),
    observacion      TEXT,
    estado           VARCHAR(20)    NOT NULL CHECK (estado IN ('PENDIENTE_DE_PAGO', 'PAGADO')),
    registrado_por   UUID           REFERENCES usuarios (id_usuario),
    aprobado_por     UUID           REFERENCES usuarios (id_usuario),
    aprobado_en      TIMESTAMP,
    anulado          BOOLEAN        NOT NULL DEFAULT FALSE,
    motivo_anulacion TEXT,
    anulado_por      UUID           REFERENCES usuarios (id_usuario),
    anulado_en       TIMESTAMP,
    creado_en        TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_abonos_cuenta ON abonos (cuenta_id);

-- Verificación rápida (debe devolver 2 filas):
-- SELECT table_name FROM information_schema.tables WHERE table_name IN ('cuentas_por_cobrar', 'abonos');
