package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.EvolucionDTO;
import com.odontologia.gestion_citas.persistence.entities.Cita;
import com.odontologia.gestion_citas.persistence.entities.Evoluciones;
import com.odontologia.gestion_citas.persistence.repositories.CitaRepository;
import com.odontologia.gestion_citas.persistence.repositories.EvolucionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvolucionService {

    private final EvolucionRepository evolucionRepository;
    private final CitaRepository citaRepository;

    @Transactional
    public EvolucionDTO crearEvolucion(EvolucionDTO dto) {
        // 1. Buscar la cita
        Cita cita = citaRepository.findById(dto.getCitaId())
                .orElseThrow(() -> new RuntimeException("No se puede registrar evolución: Cita no encontrada"));

        // 2. Crear la entidad
        Evoluciones evolucion = new Evoluciones();
        evolucion.setCita(cita);
        evolucion.setDescripcionProcedimiento(dto.getDescripcionProcedimiento());
        evolucion.setPrescripcionMedica(dto.getPrescripcionMedica());
        evolucion.setObservaciones(dto.getObservaciones());
        evolucion.setProximaCitaSugerida(dto.getProximaCitaSugerida());

        // 3. CAMBIO DE ESTADO AUTOMÁTICO
        // Marcamos la cita como completada ya que se registró la atención clínica
        cita.setEstado(Cita.EstadoCita.COMPLETADA);
        citaRepository.save(cita);

        Evoluciones guardada = evolucionRepository.save(evolucion);
        return mapearADTO(guardada);
    }

    @Transactional(readOnly = true)
    public EvolucionDTO obtenerPorId(UUID id) {
        return evolucionRepository.findById(id)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Evolución clínica no encontrada"));
    }

    @Transactional(readOnly = true)
    public EvolucionDTO obtenerPorCita(UUID citaId) {
        // Recuerda declarar: Optional<Evoluciones> findByCitaId(UUID citaId) en el Repository
        return evolucionRepository.findByCitaId(citaId)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("No se encontró una evolución para la cita solicitada"));
    }

    private EvolucionDTO mapearADTO(Evoluciones e) {
        return new EvolucionDTO(
                e.getId(),
                e.getCita().getId(),
                e.getDescripcionProcedimiento(),
                e.getPrescripcionMedica(),
                e.getObservaciones(),
                e.getProximaCitaSugerida(),
                e.getCreadoEn(),
                e.getActualizadoEn()
        );
    }
}