//src/main/java/com/odontologia/gestion_citas/domain/dtos/ErrorResponseDTO.java
package com.odontologia.gestion_citas.domain.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;


@Data
@Builder
public class ErrorResponseDTO {
    // Cuándo ocurrió el error
    private LocalDateTime timestamp;
    
    // Código de estado (ej. 400, 404, 500)
    private int status;
    
    // El nombre del error (ej. "Bad Request")
    private String error;
    
    // El mensaje amigable para el usuario (ej. "El correo ya existe")
    private String mensaje;
    
    // La URL donde ocurrió el error
    private String path;
    
    // Detalle de errores de validación (ej. "cedula": "Debe tener 10 dígitos")
    private Map<String, String> validaciones; 
}