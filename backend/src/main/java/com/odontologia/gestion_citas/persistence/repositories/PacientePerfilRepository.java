//src/main/java/com/odontologia/gestion_citas/persistence/repositories/PacientePerfilRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.PacientePerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PacientePerfilRepository extends JpaRepository<PacientePerfil, UUID> {
    
}
