package com.odontologia.gestion_citas.modules.pagos.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AbonoResponse(
        UUID id,
        UUID cuentaId,
        LocalDate fecha,
        BigDecimal monto,
        UUID citaId,
        String observacion,
        String estado,
        String registradoPor,
        String aprobadoPor,
        LocalDateTime aprobadoEn,
        boolean anulado,
        String motivoAnulacion,
        String anuladoPor,
        LocalDateTime anuladoEn,
        LocalDateTime creadoEn
) {
}
