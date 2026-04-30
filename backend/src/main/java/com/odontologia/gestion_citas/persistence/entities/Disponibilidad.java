package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "disponibilidad")
public class Disponibilidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    private String motivo;

    @Column(name = "es_dia_completo", nullable = false)
    private Boolean esDiaCompleto;
}
