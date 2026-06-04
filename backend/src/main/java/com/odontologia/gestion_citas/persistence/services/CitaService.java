//src/main/java/com/odontologia/gestion_citas/persistence/services/CitaService.java
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CitaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TratamientoRepository tratamientoRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final HorarioDoctorRepository horarioDoctorRepository;

    @Transactional
    public CitaResponseDTO agendarCita(CitaRequestDTO request) {
        
        // MAGIA: Soporta tanto ID de Postgres (paciente walk-in) como ID de Keycloak (paciente desde su app)
        Usuario paciente = usuarioRepository.findById(request.idPaciente())
                .orElseGet(() -> usuarioRepository.findByKeycloakId(request.idPaciente())
                .orElseThrow(() -> new RuntimeException("El paciente no existe en el sistema.")));

        // MAGIA: Soporta tanto ID de Postgres (elegido de lista) como ID de Keycloak (doctor logueado)
        Usuario doctor = usuarioRepository.findById(request.idDoctor())
                .orElseGet(() -> usuarioRepository.findByKeycloakId(request.idDoctor())
                .orElseThrow(() -> new RuntimeException("El doctor no existe en el sistema.")));

        Tratamiento tratamiento = tratamientoRepository.findById(request.idTratamiento())
                .orElseThrow(() -> new RuntimeException("El tratamiento no es válido."));

        LocalDateTime fechaInicio = request.fechaHoraInicio();
        LocalDateTime fechaFin = fechaInicio.plusMinutes(tratamiento.getDuracionMin());
        DayOfWeek diaSemana = fechaInicio.getDayOfWeek();
        LocalTime horaInicio = fechaInicio.toLocalTime();
        LocalTime horaFin = fechaFin.toLocalTime();

        // ¡IMPORTANTE! Usamos doctor.getId() para garantizar la llave foránea
        List<HorarioDoctor> horariosDelDia = horarioDoctorRepository
                .findByDoctor_IdAndDiaSemana(doctor.getId(), diaSemana);

        if (horariosDelDia.isEmpty()) {
            throw new RuntimeException("El doctor no labora en el día seleccionado.");
        }

        boolean dentroDelHorario = horariosDelDia.stream().anyMatch(horario -> 
            !horaInicio.isBefore(horario.getHoraInicio()) && !horaFin.isAfter(horario.getHoraFin())
        );

        if (!dentroDelHorario) {
            throw new RuntimeException("La hora seleccionada está fuera del horario de atención del doctor para este día.");
        }

        QDisponibilidad qDisponibilidad = QDisponibilidad.disponibilidad;
        BooleanExpression doctorAusente = qDisponibilidad.doctor.id.eq(doctor.getId())
                .and(qDisponibilidad.fechaInicio.before(fechaFin))
                .and(qDisponibilidad.fechaFin.after(fechaInicio));

        if (disponibilidadRepository.exists(doctorAusente)) {
            throw new RuntimeException("El horario seleccionado no está disponible por una ausencia programada del doctor.");
        }

        QCita qCita = QCita.cita;
        BooleanExpression traslapeCita = qCita.doctor.id.eq(doctor.getId())
                .and(qCita.fechaHoraInicio.before(fechaFin))
                .and(qCita.fechaHoraFin.after(fechaInicio))
                .and(qCita.estado.ne(Cita.EstadoCita.CANCELADA));

        if (citaRepository.exists(traslapeCita)) {
            throw new RuntimeException("El doctor ya tiene una cita clínica programada en ese horario.");
        }

        Cita nuevaCita = new Cita();
        nuevaCita.setPaciente(paciente);
        nuevaCita.setDoctor(doctor);
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
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseGet(() -> usuarioRepository.findByKeycloakId(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con el ID: " + pacienteId)));

        // Buscamos las citas usando la clave primaria real de la entidad encontrada
        return citaRepository.findByPaciente_Id(paciente.getId()).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
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
                cita.getPaciente().getId(),
                cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidos(),
                cita.getTratamiento().getId(), 
                cita.getTratamiento().getNombre() + " con Dr. " + cita.getDoctor().getApellidos(),
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFin(),
                cita.getEstado().name()
        );
    }

    @Transactional(readOnly = true)
    public List<String> obtenerHorasDisponibles(UUID doctorId, LocalDate fecha, Long tratamientoId) {
        List<String> horasDisponibles = new ArrayList<>();

        // MAGIA: Buscamos al doctor sin importar qué tipo de ID nos envíe Angular
        Usuario doctor = usuarioRepository.findById(doctorId)
                .orElseGet(() -> usuarioRepository.findByKeycloakId(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado.")));

        Tratamiento tratamiento = tratamientoRepository.findById(tratamientoId)
                .orElseThrow(() -> new RuntimeException("Tratamiento no encontrado"));
        int duracion = tratamiento.getDuracionMin();

        // ¡IMPORTANTE! Usamos doctor.getId()
        List<HorarioDoctor> horariosDelDia = horarioDoctorRepository
                .findByDoctor_IdAndDiaSemana(doctor.getId(), fecha.getDayOfWeek());

        if (horariosDelDia.isEmpty()) {
            return horasDisponibles; 
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        
        QCita qCita = QCita.cita;
        Iterable<Cita> citasDelDia = citaRepository.findAll(
                qCita.doctor.id.eq(doctor.getId())
                .and(qCita.fechaHoraInicio.between(inicioDia, finDia))
                .and(qCita.estado.ne(Cita.EstadoCita.CANCELADA))
        );

        QDisponibilidad qDisp = QDisponibilidad.disponibilidad;
        Iterable<Disponibilidad> bloqueos = disponibilidadRepository.findAll(
                qDisp.doctor.id.eq(doctor.getId())
                .and(qDisp.fechaInicio.before(finDia))
                .and(qDisp.fechaFin.after(inicioDia))
        );

        for (HorarioDoctor horario : horariosDelDia) {
            LocalTime horaActual = horario.getHoraInicio();

            while (!horaActual.plusMinutes(duracion).isAfter(horario.getHoraFin())) {
                LocalDateTime posibleInicio = fecha.atTime(horaActual);
                LocalDateTime posibleFin = posibleInicio.plusMinutes(duracion);

                boolean hayTraslape = false;

                for (Cita cita : citasDelDia) {
                    if (posibleInicio.isBefore(cita.getFechaHoraFin()) && posibleFin.isAfter(cita.getFechaHoraInicio())) {
                        hayTraslape = true;
                        break;
                    }
                }

                if (!hayTraslape) {
                    for (Disponibilidad bloqueo : bloqueos) {
                        if (posibleInicio.isBefore(bloqueo.getFechaFin()) && posibleFin.isAfter(bloqueo.getFechaInicio())) {
                            hayTraslape = true;
                            break;
                        }
                    }
                }
                
                if (fecha.isEqual(LocalDate.now()) && horaActual.isBefore(LocalTime.now())) {
                     hayTraslape = true;
                }

                if (!hayTraslape) {
                    horasDisponibles.add(horaActual.toString().substring(0, 5)); 
                }

                horaActual = horaActual.plusMinutes(15);
            }
        }

        return horasDisponibles;
    }

    //Obtiene las citas programadas para el día actual de un doctor específico
    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasDeHoyPorDoctor(UUID doctorId) {
        Usuario doctor = usuarioRepository.findByKeycloakId(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        
        LocalDateTime inicioDia = LocalDateTime.now().with(LocalTime.MIN); 
        LocalDateTime finDia = LocalDateTime.now().with(LocalTime.MAX);   
        
        return citaRepository.findByDoctor_IdAndFechaHoraInicioBetween(doctor.getId(), inicioDia, finDia)
                .stream()
                .sorted((c1, c2) -> c1.getFechaHoraInicio().compareTo(c2.getFechaHoraInicio()))
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerCitasPorDoctor(UUID doctorKeycloakId) {
        Usuario doctor = usuarioRepository.findByKeycloakId(doctorKeycloakId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));
        return citaRepository.findByDoctor_Id(doctor.getId())
                .stream()
                .sorted((c1, c2) -> c2.getFechaHoraInicio().compareTo(c1.getFechaHoraInicio()))
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CitaResponseDTO cambiarEstadoCita(UUID id, String nuevoEstado) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(Cita.EstadoCita.valueOf(nuevoEstado));
        return mapearADTO(citaRepository.save(cita));
    }
}
