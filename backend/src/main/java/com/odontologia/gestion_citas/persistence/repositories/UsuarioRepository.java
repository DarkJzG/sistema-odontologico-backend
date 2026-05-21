package com.odontologia.gestion_citas.persistence.repositories;

import com.odontologia.gestion_citas.persistence.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID>, QuerydslPredicateExecutor<Usuario> {
    
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCedula(String cedula);
    
    Optional<Usuario> findByNombres(String nombres);

    Optional<Usuario> findByApellidos(String apellidos);
    
}
