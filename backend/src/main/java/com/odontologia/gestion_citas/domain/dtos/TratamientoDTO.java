package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TratamientoDTO(
    Long id,
    
    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String nombre,
    
    @NotNull(message = "La duración es obligatoria")
    @Min(value = 5, message = "La duración mínima debe ser de al menos 5 minutos")
    @Max(value = 480, message = "La duración no puede exceder las 8 horas (480 min)")
    Integer duracionMin,
    
    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    BigDecimal precioBase,
    
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    String descripcion
) {}