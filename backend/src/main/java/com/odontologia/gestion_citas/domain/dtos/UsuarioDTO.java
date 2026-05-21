package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record UsuarioDTO (

    @NotNull(message = "El ID del usuario es obligatorio, no puede ser nulo")
    UUID idUsuario,

    @NotBlank(message = "La cédula es obligatoria, no puede estar vacía")
    @Size(min = 10, max = 13, message = "La cédula debe tener entre 10 y 13 caracteres")
    @Pattern(regexp = "^[0-9]+$", message = "La cédula debe contener solo números")
    String cedula,

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 1, max = 50, message = "Los nombres deben tener entre 1 y 50 caracteres")
    String nombres,

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 1, max = 50, message = "Los apellidos deben tener entre 1 y 50 caracteres")
    String apellidos,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email,

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
    String telefono,

    @NotBlank(message = "El rol es obligatorio")
    String rol
) {}