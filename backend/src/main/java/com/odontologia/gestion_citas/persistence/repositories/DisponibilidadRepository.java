
//src/main/java/com/odontologia/gestion_citas/persistence/repositories/DisponibilidadRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long>, QuerydslPredicateExecutor<Disponibilidad> {
    
}