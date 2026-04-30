package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Builder
@Entity
@Table(name = "usuarios")


public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @Column(name = "keycloak_id", unique = true)
    private UUID keycloakId;

    @Column(unique = true, length = 15)
    private String cedula;

    private String nombres;
    private String apellidos;

    @Column(unique = true, length = 150)
    private String email;

    @Column(length = 15)
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        PACIENTE, ASISTENTE, DOCTOR
    }
    
    
}
