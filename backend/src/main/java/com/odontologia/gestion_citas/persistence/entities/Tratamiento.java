package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Entity
@Table(name = "tratamientos")

public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMin;

    @Column(name = "precio_base")
    private BigDecimal precioBase;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;

}
