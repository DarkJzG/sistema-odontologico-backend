//src/main/java/com/odontologia/gestion_citas/persistence/repositories/EvolucionRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Evoluciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository

public interface EvolucionRepository extends JpaRepository<Evoluciones, UUID> {
    Optional<Evoluciones> findByCita_Id(UUID id);
    boolean existsByCita_Id(UUID id); 
}