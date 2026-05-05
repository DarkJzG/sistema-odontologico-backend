package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.PacientePerfilDTO;
import com.odontologia.gestion_citas.persistence.services.PacientePerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacientePerfilController {

    private final PacientePerfilService perfilService;

    @PostMapping("/{id}/perfil")
    public ResponseEntity<PacientePerfilDTO> guardarPerfil(
            @PathVariable UUID id,
            @RequestBody PacientePerfilDTO dto) {
        return ResponseEntity.ok(perfilService.crearActualizarPerfil(id, dto));
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<PacientePerfilDTO> obtenerPerfil(
            @PathVariable UUID id) {
        return ResponseEntity.ok(perfilService.obtenerPerfil(id));
    }
}