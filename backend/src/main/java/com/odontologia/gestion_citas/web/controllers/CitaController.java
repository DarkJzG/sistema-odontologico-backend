package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.CitaRequestDTO;
import com.odontologia.gestion_citas.domain.dtos.CitaResponseDTO;
import com.odontologia.gestion_citas.persistence.services.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    /**
     * Endpoint para agendamiento automatizado.
     * Al usar @Valid, si el DTO no cumple las reglas, el GlobalExceptionHandler responde automáticamente.
     */
    @PostMapping("/agendar")
    public ResponseEntity<CitaResponseDTO> agendar(@Valid @RequestBody CitaRequestDTO request) {
        // Ya no enviamos el pacienteId por separado, el Service lo toma del request
        CitaResponseDTO respuesta = citaService.agendarCita(request);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint para consultar los detalles de una cita específica.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        // Asegúrate de implementar 'obtenerPorId' en tu CitaService para que esto funcione
        return ResponseEntity.ok(citaService.obtenerPorId(id)); 
    }

    /**
     * Endpoint para consultar el historial de citas de un paciente.
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<java.util.List<CitaResponseDTO>> obtenerCitasPorPaciente(@PathVariable UUID pacienteId) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(pacienteId));
    }

    /**
     * Endpoint para que el paciente pueda cancelar su cita.
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelarCita(@PathVariable UUID id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }
}