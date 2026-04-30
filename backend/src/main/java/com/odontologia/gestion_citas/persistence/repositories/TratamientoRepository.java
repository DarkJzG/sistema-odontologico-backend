package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, UUID>, QuerydslPredicateExecutor<Tratamiento> {
    
}
