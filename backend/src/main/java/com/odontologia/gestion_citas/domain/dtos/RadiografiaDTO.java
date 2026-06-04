// src/main/java/com/odontologia/gestion_citas/domain/dtos/RadiografiaDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record RadiografiaDTO(
    UUID id,
    UUID idPaciente,
    String tipo,
    String urlArchivo,
    LocalDateTime creadoEn
) {}