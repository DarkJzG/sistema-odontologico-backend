// src/main/java/com/odontologia/gestion_citas/web/controllers/ConfiguracionWhatsappController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.persistence.entities.ConfiguracionWhatsapp;
import com.odontologia.gestion_citas.persistence.repositories.ConfiguracionWhatsappRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion-whatsapp")
@RequiredArgsConstructor
public class ConfiguracionWhatsappController {

    private final ConfiguracionWhatsappRepository repository;

    @GetMapping
    public ResponseEntity<ConfiguracionWhatsapp> obtenerConfiguracion() {
        ConfiguracionWhatsapp config = repository.findById(1).orElse(new ConfiguracionWhatsapp());
        return ResponseEntity.ok(config);
    }

    @PutMapping
    public ResponseEntity<ConfiguracionWhatsapp> guardarConfiguracion(@RequestBody ConfiguracionWhatsapp configActualizada) {
        configActualizada.setId(1); // Forzamos el ID 1 por seguridad
        return ResponseEntity.ok(repository.save(configActualizada));
    }
}