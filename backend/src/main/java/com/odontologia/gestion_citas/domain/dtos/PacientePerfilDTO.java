package com.odontologia.gestion_citas.domain.dtos;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record PacientePerfilDTO(

    UUID idPaciente,

    @NotBlank(message = "El grupo sanguíneo es obligatorio, no puede estar vacío")
    @Size(max = 10, message = "El grupo sanguíneo no puede exceder 10 caracteres")
    String grupoSanguineo,

    @Size(max = 500, message = "Las alergias no pueden exceder 500 caracteres")
    String alergias,

    @Size(max = 500, message = "Los medicamentos habituales no pueden exceder 500 caracteres")
    String medicamentosHabituales,

    @Size(max = 500, message = "Los antecedentes familiares no pueden exceder 500 caracteres")
    String antecedentesFamiliares,

    @NotBlank(message = "El motivo de consulta inicial es obligatorio, no puede estar vacío")
    @Size(max = 500, message = "El motivo de consulta inicial no puede exceder 500 caracteres")
    String motivoConsultaInicial
) {}
    

