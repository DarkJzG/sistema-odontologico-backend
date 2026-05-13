package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.DisponibilidadDTO;
import com.odontologia.gestion_citas.persistence.entities.Disponibilidad;
import com.odontologia.gestion_citas.persistence.repositories.DisponibilidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilidadService {
    private final DisponibilidadRepository disponibilidadRepository;

    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> listarDisponibilidades() {
        return disponibilidadRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisponibilidadDTO crearDisponibilidad(DisponibilidadDTO dto) {
        // VALIDACIÓN LÓGICA
        validarFechas(dto);

        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setFechaInicio(dto.fechaInicio());
        disponibilidad.setFechaFin(dto.fechaFin());
        disponibilidad.setMotivo(dto.motivo());
        disponibilidad.setEsDiaCompleto(dto.esDiaCompleto());

        Disponibilidad guardada = disponibilidadRepository.save(disponibilidad);
        return mapearADTO(guardada);
    }

    @Transactional
    public DisponibilidadDTO actualizarDisponibilidad(Long id, DisponibilidadDTO dto) {
        // VALIDACIÓN LÓGICA
        validarFechas(dto);

        Disponibilidad disponibilidad = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: Disponibilidad no encontrada con ID: " + id));
        
        disponibilidad.setFechaInicio(dto.fechaInicio());
        disponibilidad.setFechaFin(dto.fechaFin());
        disponibilidad.setMotivo(dto.motivo());
        disponibilidad.setEsDiaCompleto(dto.esDiaCompleto());

        Disponibilidad actualizada = disponibilidadRepository.save(disponibilidad);
        return mapearADTO(actualizada);
    }

    @Transactional
    public void eliminarDisponibilidad(Long id) {
        // Mejora: Verificar si existe antes de intentar eliminar
        if (!disponibilidadRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: La disponibilidad con ID " + id + " no existe.");
        }
        disponibilidadRepository.deleteById(id);
    }

    // MÉTODO DE APOYO PARA VALIDAR LA COHERENCIA DEL TIEMPO
    private void validarFechas(DisponibilidadDTO dto) {
        if (dto.fechaFin().isBefore(dto.fechaInicio()) || dto.fechaFin().isEqual(dto.fechaInicio())) {
            throw new RuntimeException("La fecha/hora de fin debe ser estrictamente posterior a la de inicio.");
        }
    }

    private DisponibilidadDTO mapearADTO(Disponibilidad entidad) {
        return new DisponibilidadDTO(
                entidad.getId(),
                entidad.getFechaInicio(),
                entidad.getFechaFin(),
                entidad.getMotivo(),
                entidad.getEsDiaCompleto()
        );
    }
}