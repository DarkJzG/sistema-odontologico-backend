//src/main/java/com/odontologia/gestion_citas/web/controllers/TratamientoController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.TratamientoDTO;
import com.odontologia.gestion_citas.persistence.services.TratamientoService;
import jakarta.validation.Valid; // ¡Importante!
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tratamientos")
@RequiredArgsConstructor
public class TratamientoController {

    private final TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<TratamientoDTO>> listar() {
        return ResponseEntity.ok(tratamientoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TratamientoDTO> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tratamientoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TratamientoDTO> crear(@Valid @RequestBody TratamientoDTO dto) {
        // Agregamos @Valid para que las reglas del DTO funcionen
        return new ResponseEntity<>(tratamientoService.crearTratamiento(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TratamientoDTO> actualizar(@PathVariable("id") Long id, @Valid @RequestBody TratamientoDTO dto) {
        return ResponseEntity.ok(tratamientoService.actualizarTratamiento(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        // Permitimos eliminar (Devuelve 204 No Content)
        tratamientoService.eliminarTratamiento(id);
        return ResponseEntity.noContent().build();
    }
}