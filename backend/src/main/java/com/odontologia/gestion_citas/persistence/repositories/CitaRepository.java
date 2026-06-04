//src/main/java/com/odontologia/gestion_citas/persistence/repositories/CitaRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CitaRepository extends JpaRepository<Cita, UUID>, QuerydslPredicateExecutor<Cita> {
    
    List<Cita> findByPaciente_Id(UUID id);

    List<Cita> findByDoctor_IdAndFechaHoraInicioBetween(UUID doctorId, LocalDateTime start, LocalDateTime end);

    List<Cita> findByDoctor_Id(UUID doctorId);
    
    @Modifying
    @Query("DELETE FROM Cita c WHERE c.paciente.id = :pacienteId")
    void borrarCitasPorPaciente(@Param("pacienteId") UUID pacienteId);
}
