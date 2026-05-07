package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DisponibilidadDTO(
    Long id,
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    LocalDateTime fechaInicio,
    
    @NotNull(message = "La fecha de fin es obligatoria")
    LocalDateTime fechaFin,
    
    String motivo, // Ej: "Jornada Matutina" o "Bloqueo por Mantenimiento"
    
    @NotNull(message = "Debe especificar si es día completo")
    Boolean esDiaCompleto
) {}