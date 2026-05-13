package com.odontologia.gestion_citas.web.exceptions;

import com.odontologia.gestion_citas.domain.dtos.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    /**
     * Captura las excepciones de lógica de negocio (RuntimeException)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> manejarRuntimeException(RuntimeException ex, WebRequest request) {
        
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Error de Negocio")
                .mensaje(ex.getMessage()) 
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Este método atrapa los errores de las anotaciones (@NotBlank, @Email, etc.)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> manejarValidaciones(org.springframework.web.bind.MethodArgumentNotValidException ex, org.springframework.web.context.request.WebRequest request) {
        
        // Creamos un mapa para guardar: "campo": "error"
        java.util.Map<String, String> errores = new java.util.HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        ErrorResponseDTO response = ErrorResponseDTO.builder()
                .timestamp(java.time.LocalDateTime.now())
                .status(org.springframework.http.HttpStatus.BAD_REQUEST.value())
                .error("Error de Validación")
                .mensaje("Varios campos no cumplen con los requisitos")
                .path(request.getDescription(false))
                .validaciones(errores) // Aquí metemos el mapa de errores
                .build();

        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.BAD_REQUEST);
    }
}