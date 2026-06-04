//src/main/java/com/odontologia/gestion_citas/persistence/repositories/PiezaDentalRepository.java
package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.PiezaDental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PiezaDentalRepository extends JpaRepository<PiezaDental, Integer> {

}
