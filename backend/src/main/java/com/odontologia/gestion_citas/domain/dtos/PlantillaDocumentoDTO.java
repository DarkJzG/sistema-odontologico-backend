// src/main/java/com/odontologia/gestion_citas/domain/dtos/PlantillaDocumentoDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record PlantillaDocumentoDTO(
    UUID id,
    
    @NotBlank(message = "El título de la plantilla es obligatorio")
    String titulo,
    
    @NotBlank(message = "El contenido no puede estar vacío")
    String contenido,
    
    boolean esActivo
) {}