package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.EvolucionDTO;
import com.odontologia.gestion_citas.persistence.services.EvolucionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/evoluciones")
@RequiredArgsConstructor
public class EvolucionController {

    private final EvolucionService evolucionService;

    @PostMapping
    public ResponseEntity<EvolucionDTO> crear(@Valid @RequestBody EvolucionDTO dto) {
        return new ResponseEntity<>(evolucionService.crearEvolucion(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvolucionDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(evolucionService.obtenerPorId(id));
    }

    @GetMapping("/cita/{citaId}")
    public ResponseEntity<EvolucionDTO> obtenerPorCita(@PathVariable UUID citaId) {
        return ResponseEntity.ok(evolucionService.obtenerPorCita(citaId));
    }
}