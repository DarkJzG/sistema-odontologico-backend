// src/main/java/com/odontologia/gestion_citas/web/controllers/PlantillaDocumentoController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.PlantillaDocumentoDTO;
import com.odontologia.gestion_citas.persistence.services.PlantillaDocumentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plantillas")
@RequiredArgsConstructor
public class PlantillaDocumentoController {

    private final PlantillaDocumentoService plantillaService;

    @GetMapping
    public ResponseEntity<List<PlantillaDocumentoDTO>> listar() {
        return ResponseEntity.ok(plantillaService.listarActivas());
    }

    @PostMapping
    public ResponseEntity<PlantillaDocumentoDTO> crear(@Valid @RequestBody PlantillaDocumentoDTO dto) {
        return new ResponseEntity<>(plantillaService.guardar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantillaDocumentoDTO> actualizar(
            @PathVariable("id") UUID id, 
            @Valid @RequestBody PlantillaDocumentoDTO dto) {
        
        // Aseguramos que el ID del path coincida con el objeto antes de guardar
        PlantillaDocumentoDTO dtoActualizar = new PlantillaDocumentoDTO(
                id, dto.titulo(), dto.contenido(), dto.esActivo());
                
        return ResponseEntity.ok(plantillaService.guardar(dtoActualizar));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") UUID id) {
        plantillaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}