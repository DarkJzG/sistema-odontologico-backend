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

    @Query("SELECT c FROM Cita c WHERE c.estado = :estado " +
           "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio < :fin " +
           "AND c.recordatorioDiaEnviado = :enviadoDia")
    List<Cita> buscarCitasParaRecordatorio(
            @Param("estado") Cita.EstadoCita estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("enviadoDia") Boolean enviadoDia);
            

    @Query("SELECT c FROM Cita c WHERE c.estado = :estado " +
           "AND c.fechaHoraInicio >= :inicio AND c.fechaHoraInicio < :fin " +
           "AND (c.recordatorioHoraEnviado = :enviadoHora OR c.recordatorioHoraEnviado IS NULL)")
    List<Cita> buscarCitasParaRecordatorioHora(
            @Param("estado") Cita.EstadoCita estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("enviadoHora") Boolean enviadoHora);
}
