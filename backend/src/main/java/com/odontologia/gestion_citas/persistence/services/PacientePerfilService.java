package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.persistence.entities.PacientePerfil;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.PacientePerfilRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import com.odontologia.gestion_citas.domain.dtos.PacientePerfilDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PacientePerfilService {

    private final PacientePerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public PacientePerfilDTO crearActualizarPerfil(UUID idPaciente, PacientePerfilDTO dto) {
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Validar que tenga el rol correcto
        if (usuario.getRol() !=Usuario.Rol.PACIENTE) {
            throw new RuntimeException("El usuario no es un paciente");
        }
        
        // Revisar si tiene un perfil o se crea uno nuevo
        PacientePerfil perfil = perfilRepository.findById(idPaciente)
                .orElse(new PacientePerfil());
        
        perfil.setUsuario(usuario);
        perfil.setGrupoSanguineo(dto.grupoSanguineo());
        perfil.setAlergias(dto.alergias());
        perfil.setMedicamentosHabituales(dto.medicamentosHabituales());
        perfil.setAntecedentesFamiliares(dto.antecedentesFamiliares());
        perfil.setMotivoConsultaInicial(dto.motivoConsultaInicial());

        PacientePerfil perfilGuardado = perfilRepository.save(perfil);

        return mapearADTO(perfilGuardado);
    }

    @Transactional(readOnly = true)
    public PacientePerfilDTO obtenerPerfil(UUID idPaciente) {
        PacientePerfil perfil = perfilRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Perfil clinico no encontrado"));
        return mapearADTO(perfil);
    }
    
    private PacientePerfilDTO mapearADTO(PacientePerfil perfil) {
        return new PacientePerfilDTO(
            perfil.getIdPaciente(),
            perfil.getGrupoSanguineo(),
            perfil.getAlergias(),
            perfil.getMedicamentosHabituales(),
            perfil.getAntecedentesFamiliares(),
            perfil.getMotivoConsultaInicial()
        );
    }
    

    
}
