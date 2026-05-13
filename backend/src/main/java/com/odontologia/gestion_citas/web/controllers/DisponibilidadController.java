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

public class DisponibilidadController {
    private final DisponibilidadService disponibilidadService;

    // Listar jornadas para que el paciente elija en tiempo real
    @GetMapping
    public ResponseEntity<List<DisponibilidadDTO>> listar() {
        return ResponseEntity.ok(disponibilidadService.listarDisponibilidades());
    }

    @PostMapping
    public ResponseEntity<DisponibilidadDTO> crear(@Valid @RequestBody DisponibilidadDTO dto) {
        return new ResponseEntity<>(disponibilidadService.crearDisponibilidad(dto), HttpStatus.CREATED);
    }
}
