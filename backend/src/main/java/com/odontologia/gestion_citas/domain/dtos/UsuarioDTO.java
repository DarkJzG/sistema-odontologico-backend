package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record UsuarioDTO (
    UUID idUsuario,

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 10, max = 13, message = "La cédula debe tener entre 10 y 13 caracteres")
    String cedula,

    @NotBlank(message = "El nombre es obligatorio")
    String nombres,

    @NotBlank(message = "El apellido es obligatorio")
    String apellidos,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email,

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
    String telefono,

    @NotBlank(message = "El rol es obligatorio")
    String rol
){}