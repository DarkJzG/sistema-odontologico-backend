// src/main/java/com/odontologia/gestion_citas/persistence/entities/ConfiguracionWhatsapp.java
package com.odontologia.gestion_citas.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "configuracion_whatsapp")
public class ConfiguracionWhatsapp {

    @Id
    private Integer id = 1;

    @Column(name = "mensaje_24h", columnDefinition = "TEXT")
    private String mensaje24h = "Hola *[NOMBRE_PACIENTE]* 👋\n\nOdontoStyle te recuerda tu cita para mañana a las *[HORA_CITA]*.\n\n📍 _Por favor, llega 5 minutos antes._ ¡Te esperamos!";

    @Column(name = "mensaje_1h", columnDefinition = "TEXT")
    private String mensaje1h = "Hola *[NOMBRE_PACIENTE]* ⏳\n\nTu cita en OdontoStyle comienza en exactamente *1 hora*.\n\n¡Nos vemos pronto!";
}