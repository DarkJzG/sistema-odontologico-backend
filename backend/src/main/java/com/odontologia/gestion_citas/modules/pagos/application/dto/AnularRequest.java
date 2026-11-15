package com.odontologia.gestion_citas.modules.pagos.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnularRequest(
        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 5, max = 500, message = "El motivo debe tener entre 5 y 500 caracteres")
        String motivo
) {
}
