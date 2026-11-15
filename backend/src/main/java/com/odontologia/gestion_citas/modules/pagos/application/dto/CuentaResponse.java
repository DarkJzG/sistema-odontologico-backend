package com.odontologia.gestion_citas.modules.pagos.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CuentaResponse(
        UUID id,
        UUID pacienteId,
        Long tratamientoId,
        String tratamientoNombre,
        String descripcion,
        BigDecimal montoTotal,
        BigDecimal entrada,
        BigDecimal cuotaSugerida,
        BigDecimal totalPagado,
        BigDecimal totalPendiente,
        BigDecimal saldo,
        String estado,
        String creadoPor,
        LocalDateTime creadoEn,
        String motivoAnulacion,
        List<AbonoResponse> abonos
) {
}
