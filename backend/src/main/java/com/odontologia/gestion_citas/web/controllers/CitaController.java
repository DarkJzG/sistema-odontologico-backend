
//src/main/java/com/odontologia/gestion_citas/web/controllers/CitaController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.CitaRequestDTO;
import com.odontologia.gestion_citas.domain.dtos.CitaResponseDTO;
import com.odontologia.gestion_citas.persistence.services.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

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
    
    // Consultar los detalles de una cita específica.
    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id)); 
    }

    /**
     * Endpoint para consultar el historial de citas de un paciente.
     */
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPorPaciente(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(citaService.obtenerCitasPorPaciente(id));
    }

    // Paciente puede cancelar su cita.
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelarCita(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<String>> obtenerHorasDisponibles(
            @RequestParam ("doctorId") UUID doctorId,
            @RequestParam ("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, 
            @RequestParam ("tratamientoId") Long tratamientoId) {
        
        return ResponseEntity.ok(citaService.obtenerHorasDisponibles(doctorId, fecha, tratamientoId));
    }
    /**
     * Endpoint para el Dashboard del Doctor: Citas de hoy
     */
    @GetMapping("/doctor/{id}/hoy")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasDoctorHoy(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(citaService.obtenerCitasDeHoyPorDoctor(id));
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasDoctor(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(citaService.obtenerCitasPorDoctor(id));
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDTO> cambiarEstadoCita(@PathVariable("id") UUID id, @RequestParam("estado") String estado) {
        return ResponseEntity.ok(citaService.cambiarEstadoCita(id, estado));
    }
}