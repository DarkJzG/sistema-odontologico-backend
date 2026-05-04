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
@RequestMapping("/api/odontograma")

public class OdontogramaEstadoController {

    private final OdontogramaEstadoService odontogramaEstadoService;

    @PostMapping("/paciente/{idPaciente}")
    public ResponseEntity<OdontogramaEstadoDTO> registrarEstado(
        @PathVariable UUID idPaciente,
        @RequestBody OdontogramaEstadoDTO dto
    ) {
        return new ResponseEntity<>(odontogramaEstadoService.registrarEstado(idPaciente, dto), HttpStatus.CREATED);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<OdontogramaEstadoDTO>> obtenerOdontograma(@PathVariable UUID idPaciente) {
        return ResponseEntity.ok(odontogramaEstadoService.obtenerOdontogramaPaciente(idPaciente));
    }
    
}
