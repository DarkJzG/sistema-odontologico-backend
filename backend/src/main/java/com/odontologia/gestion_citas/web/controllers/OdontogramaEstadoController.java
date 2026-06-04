
//src/main/java/com/odontologia/gestion_citas/web/controllers/OdontogramaEstadoController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.OdontogramaEstadoDTO;
import com.odontologia.gestion_citas.persistence.services.OdontogramaEstadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/odontogramas")

public class OdontogramaEstadoController {

    private final OdontogramaEstadoService odontogramaEstadoService;

    @PostMapping("/pacientes/{idPaciente}")
    public ResponseEntity<OdontogramaEstadoDTO> registrarEstado(
        @PathVariable("idPaciente") UUID idPaciente,
        @RequestBody OdontogramaEstadoDTO dto
    ) {
        return new ResponseEntity<>(odontogramaEstadoService.registrarEstado(idPaciente, dto), HttpStatus.CREATED);
    }

    @GetMapping("/pacientes/{idPaciente}")
    public ResponseEntity<List<OdontogramaEstadoDTO>> obtenerOdontograma(
            @PathVariable("idPaciente") UUID idPaciente) {
        return ResponseEntity.ok(odontogramaEstadoService.obtenerOdontogramaPaciente(idPaciente));
    }
    
    @GetMapping("/pacientes/{idPaciente}/actual")
    public ResponseEntity<List<OdontogramaEstadoDTO>> obtenerEstadoActual(
            @PathVariable("idPaciente") UUID idPaciente) {
        return ResponseEntity.ok(odontogramaEstadoService.obtenerEstadoActualOdontograma(idPaciente));
    }
    
}
