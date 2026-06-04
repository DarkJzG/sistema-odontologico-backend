//src/main/java/com/odontologia/gestion_citas/persistence/entities/PacientePerfil.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente_perfil")
public class PacientePerfil {

    @Id
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_paciente")
    private Usuario usuario;

    @Column(name = "grupo_sanguineo", length = 5)
    private String grupoSanguineo;

    @Column(columnDefinition = "TEXT")
    private String alergias;

    @Column(name = "medicamentos_habituales", columnDefinition = "TEXT")
    private String medicamentosHabituales;

    @Column(name = "antecedentes_familiares", columnDefinition = "TEXT")
    private String antecedentesFamiliares;

    @Column(name ="motivo_consulta_inicial", columnDefinition = "TEXT")
    private String motivoConsultaInicial;
}
