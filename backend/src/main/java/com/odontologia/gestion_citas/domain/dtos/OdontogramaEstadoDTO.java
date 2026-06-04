
//src/main/java/com/odontologia/gestion_citas/domain/dtos/OdontogramaEstadoDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record OdontogramaEstadoDTO(

    UUID id,

    UUID idPaciente,

    @NotNull(message = "El ID de la pieza es obligatorio, no puede ser nulo")
    Integer idPieza,

    @NotNull(message = "La posición es obligatoria, no puede estar vacía")
    String posicion,

    @NotNull(message = "El estado es obligatorio, no puede estar vacío")
    String estado,

    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    String notas,

    LocalDateTime fechaRegistro
) {
    
}
