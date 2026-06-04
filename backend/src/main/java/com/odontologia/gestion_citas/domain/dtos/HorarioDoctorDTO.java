//src/main/java/com/odontologia/gestion_citas/domain/dtos/HorarioDoctorDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioDoctorDTO(
    Long id,
    
    @NotNull(message = "El ID del doctor es obligatorio")
    UUID idDoctor,
    
    @NotNull(message = "El día de la semana es obligatorio")
    String diaSemana, 
    
    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime horaInicio,
    
    @NotNull(message = "La hora de fin es obligatoria")
    LocalTime horaFin
) {}