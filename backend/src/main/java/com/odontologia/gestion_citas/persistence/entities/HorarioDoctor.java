//src/main/java/com/odontologia/gestion_citas/persistence/entities/HorarioDoctor.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "horarios_doctor")
public class HorarioDoctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación directa: Este horario le pertenece a un doctor específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Usuario doctor;

    // Día de la semana (Lunes, Martes, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
}