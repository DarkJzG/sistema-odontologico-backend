// src/main/java/com/odontologia/gestion_citas/persistence/repositories/PlantillaRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.PlantillaDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlantillaDocumentoRepository extends JpaRepository<PlantillaDocumento, UUID> {
    // Para listar solo las plantillas que el doctor no haya "eliminado"
    List<PlantillaDocumento> findByEsActivoTrueOrderByTituloAsc();
}