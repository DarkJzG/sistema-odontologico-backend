package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record CitaRequestDTO (
    @NotNull(message = "El ID del paciente es obligatorio")
    UUID pacienteId,
    
    @NotNull(message = "El ID del tratamiento es obligatorio")
    Long tratamientoId,
    
    @NotNull(message = "La fecha y hora de inicio son obligatorias")
    @Future(message = "La cita debe ser programada en el futuro")
    LocalDateTime fechaHoraInicio
    
 ) {}
