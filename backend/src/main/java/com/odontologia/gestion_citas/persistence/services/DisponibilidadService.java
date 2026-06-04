// src/main/java/com/odontologia/gestion_citas/persistence/services/DisponibilidadService.java
package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.DisponibilidadDTO;
import com.odontologia.gestion_citas.persistence.entities.Disponibilidad;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.DisponibilidadRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class DisponibilidadService {
    
    private final DisponibilidadRepository disponibilidadRepository;
    private final UsuarioRepository usuarioRepository; 

    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> listarDisponibilidades() {
        return disponibilidadRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisponibilidadDTO crearDisponibilidad(DisponibilidadDTO dto) {
        validarFechas(dto);

        // CORRECCIÓN: Buscamos al doctor por su Keycloak ID
        Usuario doctor = usuarioRepository.findByKeycloakId(dto.idDoctor())
                .orElseThrow(() -> new RuntimeException("No se puede crear disponibilidad: Doctor no encontrado."));

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setDoctor(doctor); 
        disponibilidad.setFechaInicio(dto.fechaInicio());
        disponibilidad.setFechaFin(dto.fechaFin());
        disponibilidad.setMotivo(dto.motivo());
        disponibilidad.setEsDiaCompleto(dto.esDiaCompleto());

        Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);
        return mapearADTO(guardada);
    }

    @Transactional
    public DisponibilidadDTO actualizarDisponibilidad(Long id, DisponibilidadDTO dto) {
        validarFechas(dto);

        Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: Disponibilidad no encontrada."));
        
        // CORRECCIÓN: Buscamos al doctor por su Keycloak ID
        Usuario doctor = usuarioRepository.findByKeycloakId(dto.idDoctor())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado."));

        disponibilidad.setDoctor(doctor); 
        disponibilidad.setFechaInicio(dto.fechaInicio());
        disponibilidad.setFechaFin(dto.fechaFin());
        disponibilidad.setMotivo(dto.motivo());
        disponibilidad.setEsDiaCompleto(dto.esDiaCompleto());

        Disponibilidad actualizada = disponibilidadRepository.save(disponibilidad);
        return mapearADTO(actualizada);
    }

    @Transactional
    public void eliminarDisponibilidad(Long id) {
        if (!disponibilidadRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: La disponibilidad no existe.");
        }
        disponibilidadRepository.deleteById(id);
    }

    private void validarFechas(DisponibilidadDTO dto) {
        if (dto.fechaFin().isBefore(dto.fechaInicio()) || dto.fechaFin().isEqual(dto.fechaInicio())) {
            throw new RuntimeException("La fecha/hora de fin debe ser estrictamente posterior a la de inicio.");
        }
    }

    private DisponibilidadDTO mapearADTO(Disponibilidad entidad) {
        return new DisponibilidadDTO(
                entidad.getId(),
                entidad.getDoctor().getKeycloakId(), 
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getMotivo(),
                entidad.getEsDiaCompleto()
        );
    }
}