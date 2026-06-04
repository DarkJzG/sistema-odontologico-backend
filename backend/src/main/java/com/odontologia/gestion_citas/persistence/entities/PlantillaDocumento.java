// src/main/java/com/odontologia/gestion_citas/persistence/entities/PlantillaDocumento.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "plantillas_documentos")
public class PlantillaDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String titulo; // Ej: "Consentimiento de Extracción"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido; // El texto con las variables [NOMBRE_PACIENTE], etc.

    @Column(name = "es_activo")
    private boolean esActivo = true;

    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}