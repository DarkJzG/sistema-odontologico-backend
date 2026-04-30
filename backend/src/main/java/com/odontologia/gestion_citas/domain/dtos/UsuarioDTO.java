package com.odontologia.gestion_citas.domain.dtos;

import java.util.UUID;

public record UsuarioDTO (
    UUID idUsuario,
    String cedula,
    String nombres,
    String apellidos,
    String email,
    String telefono,
    String rol
){}
