package com.odontologia.gestion_citas.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CitaResponseDTO(
    UUID idCita,
    String nombrePaciente,
    String nombreTratamiento,
    LocalDateTime fechaHoraInicio,
    LocalDateTime fechaHoraFin,
    String estado
) {
    
}
