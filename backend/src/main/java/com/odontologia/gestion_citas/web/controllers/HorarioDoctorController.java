//src/main/java/com/odontologia/gestion_citas/web/controllers/HorarioDoctorController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.HorarioDoctorDTO;
import com.odontologia.gestion_citas.persistence.services.HorarioDoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/horarios-doctor")
@RequiredArgsConstructor
public class HorarioDoctorController {

    private final HorarioDoctorService horarioService;

    @PostMapping
    public ResponseEntity<HorarioDoctorDTO> guardar(@Valid @RequestBody HorarioDoctorDTO dto) {
        return ResponseEntity.ok(horarioService.guardarHorario(dto));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<HorarioDoctorDTO>> obtenerPorDoctor(@PathVariable("doctorId") UUID doctorId) {
        return ResponseEntity.ok(horarioService.obtenerHorariosPorDoctor(doctorId));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.noContent().build();
    }
}
