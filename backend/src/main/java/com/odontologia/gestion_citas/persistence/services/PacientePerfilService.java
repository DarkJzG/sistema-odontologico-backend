//src/main/java/com/odontologia/gestion_citas/persistence/services/PacientePerfilService.java
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
public class PacientePerfilService {

    private final PacientePerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public PacientePerfilDTO crearActualizarPerfil(UUID id, PacientePerfilDTO dto) {
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Validar que tenga el rol correcto
        if (usuario.getRol() !=Usuario.Rol.PACIENTE) {
            throw new RuntimeException("El usuario no es un paciente");
        }
        
        // Revisar si tiene un perfil o se crea uno nuevo
        PacientePerfil perfil = perfilRepository.findById(id)
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
    public PacientePerfilDTO obtenerPerfil(UUID id) {
        PacientePerfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Perfil clinico no encontrado"));
        return mapearADTO(perfil);
    }
    
    private PacientePerfilDTO mapearADTO(PacientePerfil perfil) {
        return new PacientePerfilDTO(
            perfil.getId(),
            perfil.getGrupoSanguineo(),
            perfil.getAlergias(),
            perfil.getMedicamentosHabituales(),
            perfil.getAntecedentesFamiliares(),
            perfil.getMotivoConsultaInicial()
        );
    }
    

    
}
