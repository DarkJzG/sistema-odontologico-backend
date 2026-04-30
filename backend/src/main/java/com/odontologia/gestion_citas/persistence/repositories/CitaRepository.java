package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

@Repository
public interface CitaRepository extends JpaRepository<Cita, UUID>, QuerydslPredicateExecutor<Cita> {
    
}
