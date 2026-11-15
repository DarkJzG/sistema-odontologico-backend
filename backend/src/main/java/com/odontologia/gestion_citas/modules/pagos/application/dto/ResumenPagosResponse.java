package com.odontologia.gestion_citas.modules.pagos.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Totales del paciente (excluye cuentas ANULADA) y detalle de todas sus cuentas. */
public record ResumenPagosResponse(
        UUID pacienteId,
        BigDecimal totalCuentas,
        BigDecimal totalPagado,
        BigDecimal totalPendiente,
        BigDecimal saldo,
        List<CuentaResponse> cuentas
) {
}
