package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvolucionDTO {
    
    private UUID id;

    @NotNull(message = "El ID de la cita es obligatorio para registrar la evolución")
    private UUID citaId;

    @NotBlank(message = "La descripción del procedimiento no puede estar vacía")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    private String descripcionProcedimiento;

    // Estos pueden ser opcionales, pero podrías limitar su tamaño si gustas
    @Size(max = 1000, message = "La prescripción es demasiado larga")
    private String prescripcionMedica;

    @Size(max = 1000, message = "Las observaciones son demasiado largas")
    private String observaciones;

    @Future(message = "La fecha sugerida para la próxima cita debe ser en el futuro")
    private LocalDateTime proximaCitaSugerida;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}