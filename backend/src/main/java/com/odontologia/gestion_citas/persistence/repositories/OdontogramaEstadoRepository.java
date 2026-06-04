//src/main/java/com/odontologia/gestion_citas/persistence/repositories/OdontogramaEstadoRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.OdontogramaEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface OdontogramaEstadoRepository extends JpaRepository<OdontogramaEstado, UUID> {
    List<OdontogramaEstado> findByPaciente_Id(UUID id);
}
