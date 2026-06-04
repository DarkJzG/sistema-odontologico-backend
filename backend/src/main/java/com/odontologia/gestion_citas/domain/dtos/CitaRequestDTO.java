//src/main/java/com/odontologia/gestion_citas/domain/dtos/CitaRequestDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record CitaRequestDTO (
    @NotNull(message = "El ID del paciente es obligatorio")
    UUID idPaciente,
    
    @NotNull(message = "El ID del tratamiento es obligatorio")
    Long idTratamiento,

    @NotNull(message = "El ID del doctor es obligatorio")
    UUID idDoctor,

    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime fechaHoraInicio
) {}