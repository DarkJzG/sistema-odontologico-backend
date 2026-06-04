//src/main/java/com/odontologia/gestion_citas/persistence/entities/OdontogramaEstado.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "odontograma_estado")

public class OdontogramaEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    //Relación con Paciente (usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Usuario paciente;

    //Relacion con la pieza dental
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pieza_id", nullable = false)
    private PiezaDental piezaDental;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Posicion posicion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @CreationTimestamp
    @Column(name="fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    //Operaciones
    public enum Posicion {
        VESTIBULAR, OCLUSAL, PALATINO, DISTAL, MESIAL, GENERAL
    }
    
    public enum Estado {
        SANO, CARIES, OBTURADO, AUSENTE, CORONA, ENDODONCIA, PERDIDA_PARCIAL, IMPLANTE, OTRO
    }

}
