package com.odontologia.gestion_citas.modules.pagos.application.dto;

import com.odontologia.gestion_citas.modules.pagos.domain.EstadoAbono;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Si fecha es nula se usa la fecha de hoy en America/Guayaquil. */
public record RegistrarAbonoRequest(
        @PastOrPresent(message = "La fecha del abono no puede ser futura")
        LocalDate fecha,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El abono debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2, message = "Monto con máximo 8 enteros y 2 decimales")
        BigDecimal monto,

        UUID citaId,

        @Size(max = 1000, message = "La observación admite hasta 1000 caracteres")
        String observacion,

        @NotNull(message = "Indique si el abono está PAGADO o PENDIENTE_DE_PAGO")
        EstadoAbono estado
) {
}
