//src/main/java/com/odontologia/gestion_citas/persistence/repositories/HorarioDoctorRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.HorarioDoctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;


public interface HorarioDoctorRepository extends JpaRepository<HorarioDoctor, Long> {
    List<HorarioDoctor> findByDoctor_IdAndDiaSemana(UUID id, DayOfWeek diaSemana);

    List<HorarioDoctor> findByDoctor_Id(UUID id);
}
