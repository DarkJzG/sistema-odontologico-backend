
//src/main/java/com/odontologia/gestion_citas/domain/dtos/CitaResponseDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CitaResponseDTO(

    UUID id,
    UUID idPaciente,
    String nombrePaciente,
    Long idTratamiento,
    String nombreTratamiento,
    LocalDateTime fechaHoraInicio,
    LocalDateTime fechaHoraFin,
    String estado
) {
    
}