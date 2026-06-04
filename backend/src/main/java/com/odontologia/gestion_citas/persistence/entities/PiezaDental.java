
//src/main/java/com/odontologia/gestion_citas/persistence/entities/PiezaDental.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "piezas_dentales")
public class PiezaDental {
   
    @Id
    @Column(name = "id_pieza")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nombre;
    
    
}
