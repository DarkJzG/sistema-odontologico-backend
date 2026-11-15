package com.odontologia.gestion_citas.modules.pagos.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CrearCuentaRequest(
        @NotNull(message = "El paciente es obligatorio")
        UUID pacienteId,

        Long tratamientoId,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción admite hasta 255 caracteres")
        String descripcion,

        @NotNull(message = "El monto total es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto total debe ser mayor que cero")
        @Digits(integer = 8, fraction = 2, message = "Monto con máximo 8 enteros y 2 decimales")
        BigDecimal montoTotal,

        @DecimalMin(value = "0.00", message = "La entrada no puede ser negativa")
        @Digits(integer = 8, fraction = 2, message = "Monto con máximo 8 enteros y 2 decimales")
        BigDecimal entrada,

        @DecimalMin(value = "0.00", message = "La cuota sugerida no puede ser negativa")
        @Digits(integer = 8, fraction = 2, message = "Monto con máximo 8 enteros y 2 decimales")
        BigDecimal cuotaSugerida
) {
}
