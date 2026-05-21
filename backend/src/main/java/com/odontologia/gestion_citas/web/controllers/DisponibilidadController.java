package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.DisponibilidadDTO;
import com.odontologia.gestion_citas.persistence.services.DisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilidad")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    // Listar bloqueos de agenda activos en el sistema (Ausencias médicas, festivos)
    @GetMapping
    public ResponseEntity<List<DisponibilidadDTO>> listar() {
        return ResponseEntity.ok(disponibilidadService.listarDisponibilidades());
    }

    // Crear un bloqueo de agenda (Ej: Doctor asistirá a congreso de 8am a 12pm)
    @PostMapping
    public ResponseEntity<DisponibilidadDTO> crear(@Valid @RequestBody DisponibilidadDTO dto) {
        return new ResponseEntity<>(disponibilidadService.crearDisponibilidad(dto), HttpStatus.CREATED);
    }
}