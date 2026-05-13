package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.CitaRequestDTO;
import com.odontologia.gestion_citas.domain.dtos.CitaResponseDTO;
import com.odontologia.gestion_citas.persistence.entities.*;
import com.odontologia.gestion_citas.persistence.repositories.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitaService {
    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TratamientoRepository tratamientoRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    @Transactional
    public CitaResponseDTO agendarCita(CitaRequestDTO request) {
        // 1. Validar que el paciente existe (Usamos el ID que viene dentro del DTO)
        Usuario paciente = usuarioRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RuntimeException("El paciente seleccionado no existe en el sistema."));

        // 2. Obtener tratamiento
        Tratamiento tratamiento = tratamientoRepository.findById(request.tratamientoId())
                .orElseThrow(() -> new RuntimeException("El tratamiento seleccionado no es válido."));

        // 3. Obtener la disponibilidad
        Disponibilidad disponibilidad = disponibilidadRepository.findById(request.disponibilidadId())
                .orElseThrow(() -> new RuntimeException("La jornada de disponibilidad seleccionada no existe."));

        // 4. Calcular fecha fin
        LocalDateTime fechaFin = request.fechaHoraInicio().plusMinutes(tratamiento.getDuracionMin());

        // 5. Validar rango de disponibilidad
        if (request.fechaHoraInicio().isBefore(disponibilidad.getFechaInicio()) || 
            fechaFin.isAfter(disponibilidad.getFechaFin())) {
            throw new RuntimeException("El horario solicitado está fuera de la jornada laboral permitida.");
        }

        // 6. Validar traslapes con QueryDSL
        QCita qCita = QCita.cita;
        BooleanExpression traslapeExistente = qCita.fechaHoraInicio.before(fechaFin)
                .and(qCita.fechaHoraFin.after(request.fechaHoraInicio()))
                .and(qCita.estado.ne(Cita.EstadoCita.CANCELADA));

        if (citaRepository.exists(traslapeExistente)) {
            throw new RuntimeException("No es posible agendar: El horario ya está ocupado por otra cita clínica.");
        }

        // 7. Persistencia
        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setTratamiento(tratamiento);
        nuevaCita.setDisponibilidad(disponibilidad);
        nuevaCita.setFechaHoraInicio(request.fechaHoraInicio());
        nuevaCita.setFechaHoraFin(fechaFin);
        nuevaCita.setEstado(Cita.EstadoCita.PENDIENTE);

        Cita citaGuardada = citaRepository.save(nuevaCita);

        return mapearADTO(citaGuardada);
    }

    private CitaResponseDTO mapearADTO(Cita cita) {
        return new CitaResponseDTO(
                cita.getId(), // Asegúrate que en tu Entidad Cita el campo sea 'id'
                cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos(),
                cita.getTratamiento().getNombre(),
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFin(),
                cita.getEstado().name()
        );
    } 

    /**
     * Busca una cita específica por su ID y la devuelve mapeada a DTO.
     */
    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerPorId(UUID id) {
        return citaRepository.findById(id)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con el ID: " + id));
    }
}