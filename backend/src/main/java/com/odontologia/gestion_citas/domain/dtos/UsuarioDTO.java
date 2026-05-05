package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record UsuarioDTO (

    @NotNull(message = "El ID del usuario es obligatorio, no puede ser nulo")
    UUID idUsuario,

    @NotBlank(message = "La cédula es obligatoria, no puede estar vacía")
    @Pattern(regexp = "^[0-9]{10}$", message = "La cédula debe tener exactamente 10 dígitos numéricos")
    String cedula,

    @NotBlank(message = "Los nombres son obligatorios, no pueden estar vacíos")
    @Size(min = 1, max = 50, message = "Los nombres deben tener entre 1 y 50 caracteres")
    String nombres,

    @NotBlank(message = "Los apellidos son obligatorios, no pueden estar vacíos")
    @Size(min = 1, max = 50, message = "Los apellidos deben tener entre 1 y 50 caracteres")
    String apellidos,

    @NotBlank(message = "El email es obligatorio, no puede estar vacío")
    @Email(message = "El email debe ser válido")
    String email,

    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
    String telefono,

    @NotBlank(message = "El rol es obligatorio, no puede estar vacío")
    String rol
)
    {}
