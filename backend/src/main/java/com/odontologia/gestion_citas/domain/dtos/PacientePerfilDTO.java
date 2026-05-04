package com.odontologia.gestion_citas.domain.dtos;

import java.util.UUID;

public record PacientePerfilDTO(
    UUID idPaciente,
    String grupoSanguineo,
    String alergias,
    String medicamentosHabituales,
    String antecedentesFamiliares,
    String motivoConsultaInicial
) {}
    

