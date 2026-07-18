CREATE TABLE IF NOT EXISTS public.reporte_pedido_auditoria (
    rpa_id BIGSERIAL PRIMARY KEY,
    rpa_generacion_id UUID NOT NULL,
    rpa_empresa_id BIGINT NOT NULL,
    rpa_usuario_id BIGINT NOT NULL,
    rpa_usuario VARCHAR(255) NOT NULL,
    rpa_fecha_hora TIMESTAMP WITH TIME ZONE NOT NULL,
    rpa_pedido_id BIGINT NOT NULL,
    rpa_formato VARCHAR(10) NOT NULL,
    CONSTRAINT chk_reporte_pedido_formato CHECK (rpa_formato IN ('PDF', 'EXCEL'))
);

CREATE INDEX IF NOT EXISTS idx_rpa_empresa_fecha
    ON public.reporte_pedido_auditoria (rpa_empresa_id, rpa_fecha_hora DESC);

CREATE INDEX IF NOT EXISTS idx_rpa_pedido
    ON public.reporte_pedido_auditoria (rpa_pedido_id);
