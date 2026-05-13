package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "cita") // Evitamos bucles infinitos y problemas de Lazy Loading
@Entity
@Table(name = "evoluciones")
public class Evoluciones { // Se recomienda el nombre en singular para la clase

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relación con la cita (una evolución pertenece a una cita específica)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private Cita cita;

    // Usamos columnDefinition = "TEXT" para que en la BD sea tipo 'text' y no 'varchar(255)'
    @Column(name = "descripcion_procedimiento", columnDefinition = "TEXT", nullable = false)
    private String descripcionProcedimiento;

    @Column(name = "prescripcion_medica", columnDefinition = "TEXT")
    private String prescripcionMedica;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "proxima_cita_sugerida")
    private LocalDateTime proximaCitaSugerida;

    // Tiempo de registro de la evolución
    @CreationTimestamp
    @Column(name = "creado_en", updatable = false)
    private LocalDateTime creadoEn;

    @org.hibernate.annotations.UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}