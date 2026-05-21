package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record OdontogramaEstadoDTO(

    UUID id,

    UUID pacienteId,

    @NotNull(message = "El ID de la pieza es obligatorio, no puede ser nulo")
    Integer piezaId,

    @NotBlank(message = "La posición es obligatoria, no puede estar vacía")
    String posicion,

    @NotBlank(message = "El estado es obligatorio, no puede estar vacío")
    String estado,

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    String notas,

    LocalDateTime fechaRegistro
) {
    
}
