// src/main/java/com/odontologia/gestion_citas/persistence/repositories/RadiografiaRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Radiografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RadiografiaRepository extends JpaRepository<Radiografia, UUID> {
    // Busca las radiografías de un paciente y las ordena colocando la más reciente primero
    List<Radiografia> findByPaciente_IdOrderByCreadoEnDesc(UUID idPaciente);
}