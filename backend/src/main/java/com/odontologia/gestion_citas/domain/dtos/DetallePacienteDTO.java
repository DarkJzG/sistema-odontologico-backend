package com.odontologia.gestion_citas.domain.dtos;

public record DetallePacienteDTO(
    UsuarioDTO paciente,
    PacientePerfilDTO perfilMedico
) {}