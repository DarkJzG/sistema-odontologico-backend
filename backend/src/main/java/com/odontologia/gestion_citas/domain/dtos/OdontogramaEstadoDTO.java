package com.odontologia.gestion_citas.domain.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record OdontogramaEstadoDTO(
    UUID id,
    UUID pacienteId,
    Integer piezaId,
    String posicion,
    String estado,
    String notas,
    LocalDateTime fechaRegistro
) {
    
}
