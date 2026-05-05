package com.odontologia.gestion_citas.web.exceptions;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
    String error,
    String detalle,
    LocalDateTime timestamp
) {
}
