// src/main/java/com/odontologia/gestion_citas/persistence/repositories/ConfiguracionWhatsappRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.ConfiguracionWhatsapp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionWhatsappRepository extends JpaRepository<ConfiguracionWhatsapp, Integer> {
}