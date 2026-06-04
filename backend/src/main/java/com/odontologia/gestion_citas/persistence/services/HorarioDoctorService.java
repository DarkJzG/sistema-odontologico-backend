package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.HorarioDoctorDTO;
import com.odontologia.gestion_citas.persistence.entities.HorarioDoctor;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.HorarioDoctorRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioDoctorService {

    private final HorarioDoctorRepository horarioRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public HorarioDoctorDTO guardarHorario(HorarioDoctorDTO dto) {
        Usuario doctor = usuarioRepository.findByKeycloakId(dto.idDoctor())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        HorarioDoctor horario = new HorarioDoctor();
        horario.setDoctor(doctor);
        horario.setDiaSemana(DayOfWeek.valueOf(dto.diaSemana().toUpperCase()));
        horario.setHoraInicio(dto.horaInicio());
        horario.setHoraFin(dto.horaFin());

        return mapearADTO(horarioRepository.save(horario));
    }

    @Transactional(readOnly = true)
    public List<HorarioDoctorDTO> obtenerHorariosPorDoctor(UUID doctorId) {
        Usuario doctor = usuarioRepository.findById(doctorId)
                .orElseGet(() -> usuarioRepository.findByKeycloakId(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado para consultar agenda.")));

        return horarioRepository.findByDoctor_Id(doctor.getId()).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    private HorarioDoctorDTO mapearADTO(HorarioDoctor h) {
        return new HorarioDoctorDTO(h.getId(), h.getDoctor().getId(), h.getDiaSemana().name(), h.getHoraInicio(), h.getHoraFin());
    }

    @Transactional
    public void eliminarHorario(Long id) {
        horarioRepository.deleteById(id);
    }
}