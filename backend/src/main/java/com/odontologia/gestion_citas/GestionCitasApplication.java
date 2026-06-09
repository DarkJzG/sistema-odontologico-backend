
//src/main/java/com/odontologia/gestion_citas/GestionCitasApplication.java
package com.odontologia.gestion_citas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@OpenAPIDefinition(
    info = @Info(
        title = "Sistema Odontológico API - ODONTOSTYLE",
        version = "1.0",
        description = "Documentación de los endpoints para el sistema odontológico (Módulo de Gestión Clínica y Citas)."
    )
)
@SpringBootApplication
@EnableScheduling
public class GestionCitasApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCitasApplication.class, args);
	}

}
