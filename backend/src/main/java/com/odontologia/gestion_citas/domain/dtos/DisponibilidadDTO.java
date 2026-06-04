//src/main/java/com/odontologia/gestion_citas/domain/dtos/DisponibilidadDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record DisponibilidadDTO(
    Long id,

    @NotNull(message = "El ID del doctor es obligatorio")
    UUID idDoctor,
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La jornada no puede empezar en el pasado")
    LocalDateTime fechaInicio,
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser una fecha futura")
    LocalDateTime fechaFin,
    
    @NotBlank(message = "El motivo de la jornada (ej: Matutina) es obligatorio")
    @Size(min = 3, max = 100, message = "El motivo debe tener entre 3 y 100 caracteres")
    String motivo, 
    
    @NotNull(message = "Debe especificar si la jornada es de día completo")
    Boolean esDiaCompleto
) {}