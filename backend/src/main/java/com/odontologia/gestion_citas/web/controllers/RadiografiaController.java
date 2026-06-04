// src/main/java/com/odontologia/gestion_citas/web/controllers/RadiografiaController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.RadiografiaDTO;
import com.odontologia.gestion_citas.persistence.services.RadiografiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/radiografias")
@RequiredArgsConstructor
public class RadiografiaController {

    private final RadiografiaService radiografiaService;

    @PostMapping("/subir")
    public ResponseEntity<RadiografiaDTO> subir(
            @RequestParam("idPaciente") UUID idPaciente,
            @RequestParam("tipo") String tipo,
            @RequestParam("archivo") MultipartFile archivo) {
        
        RadiografiaDTO dto = radiografiaService.subirRadiografia(idPaciente, tipo, archivo);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<RadiografiaDTO>> obtenerPorPaciente(
            @PathVariable("idPaciente") UUID idPaciente) {
        return ResponseEntity.ok(radiografiaService.obtenerPorPaciente(idPaciente));
    }
}