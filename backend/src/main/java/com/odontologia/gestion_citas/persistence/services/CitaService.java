package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.CitaRequestDTO;
import com.odontologia.gestion_citas.domain.dtos.CitaResponseDTO;
import com.odontologia.gestion_citas.persistence.entities.*;
import com.odontologia.gestion_citas.persistence.repositories.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TratamientoRepository tratamientoRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    @Transactional
    public CitaResponseDTO agendarCita(CitaRequestDTO request) {
        // 1. Validar existencia del paciente y del tratamiento
        Usuario paciente = usuarioRepository.findById(request.pacienteId())
                .orElseThrow(() -> new RuntimeException("El paciente seleccionado no existe en el sistema."));

        Tratamiento tratamiento = tratamientoRepository.findById(request.tratamientoId())
                .orElseThrow(() -> new RuntimeException("El tratamiento seleccionado no es válido."));

        // 2. Definir intervalos de tiempo
        LocalDateTime fechaInicio = request.fechaHoraInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(tratamiento.getDuracionMin());

        // 3. REGLA DE NEGOCIO: Validar días laborables (Lunes a Viernes)
        DayOfWeek diaSemana = fechaInicio.getDayOfWeek();
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            throw new RuntimeException("No es posible agendar: La clínica no opera los fines de semana.");
        }

        // 4. REGLA DE NEGOCIO: Validar rangos horarios permitidos (08:00 - 13:00 y 14:00 - 18:00)
        LocalTime horaInicio = fechaInicio.toLocalTime();
        LocalTime horaFin = fechaFin.toLocalTime();

        boolean enTurnoManana = !horaInicio.isBefore(LocalTime.of(8, 0)) && !horaFin.isAfter(LocalTime.of(13, 0));
        boolean enTurnoTarde = !horaInicio.isBefore(LocalTime.of(14, 0)) && !horaFin.isAfter(LocalTime.of(18, 0));

        if (!enTurnoManana && !enTurnoTarde) {
            throw new RuntimeException("Horario no permitido. La jornada laboral es de 08:00 a 13:00 y de 14:00 a 18:00.");
        }

        // 5. EXCLUSIÓN: Validar bloqueos o ausencias del doctor en la tabla de disponibilidad
        QDisponibilidad qDisponibilidad = QDisponibilidad.disponibilidad;
        BooleanExpression doctorAusente = qDisponibilidad.fechaInicio.before(fechaFin)
                .and(qDisponibilidad.fechaFin.after(fechaInicio));

        if (disponibilidadRepository.exists(doctorAusente)) {
            throw new RuntimeException("El horario seleccionado no está disponible debido a un bloqueo de agenda médica.");
        }

        // 6. TRASLAPES: Validar que el espacio no esté reservado por otra cita activa
        QCita qCita = QCita.cita;
        BooleanExpression traslapeCita = qCita.fechaHoraInicio.before(fechaFin)
                .and(qCita.fechaHoraFin.after(fechaInicio))
                .and(qCita.estado.ne(Cita.EstadoCita.CANCELADA));

        if (citaRepository.exists(traslapeCita)) {
            throw new RuntimeException("No es posible agendar: El horario ya está ocupado por otra cita clínica.");
        }

        // 7. Mapear entidad y persistir en la base de datos
        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setTratamiento(tratamiento);
        nuevaCita.setFechaHoraInicio(fechaInicio);
        nuevaCita.setFechaHoraFin(fechaFin);
        nuevaCita.setEstado(Cita.EstadoCita.PENDIENTE);

        Cita citaGuardada = citaRepository.save(nuevaCita);

        return mapearADTO(citaGuardada);
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

    /**
     * Busca todas las citas pertenecientes a un paciente específico utilizando su ID de usuario.
     */
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorPaciente(UUID pacienteId) {
        return citaRepository.findByPaciente_IdUsuario(pacienteId).stream()
                .map(this::mapearADTO)
                .toList();
    }

    /**
     * Modifica el estado de una cita clínica a CANCELADA.
     */
    @Transactional
    public CitaResponseDTO cancelarCita(UUID id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con el ID: " + id));
        
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(cita);
        
        return mapearADTO(cita);
    }

    /**
     * Transforma una entidad Cita a su correspondiente DTO de respuesta.
     */
    private CitaResponseDTO mapearADTO(Cita cita) {
        return new CitaResponseDTO(
                cita.getId(),
                cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos(),
                cita.getTratamiento().getNombre(),
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFin(),
                cita.getEstado().name()
        );
    }
}