//src/main/java/com/odontologia/gestion_citas/web/controllers/UsuarioController.java
package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.domain.dtos.PacientePerfilDTO;
import com.odontologia.gestion_citas.domain.dtos.DetallePacienteDTO;
import com.odontologia.gestion_citas.persistence.services.UsuarioService;
import com.odontologia.gestion_citas.persistence.services.PacientePerfilService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final PacientePerfilService pacientePerfilService;
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> obtenerPorCedula(@RequestParam("cedula") String cedula) {
        return ResponseEntity.ok(usuarioService.obtenerPorCedula(cedula));
    }

    @GetMapping("/buscar-correo")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable("id") UUID id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints del Módulo Clínico (Johan) ---

    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> obtenerDetallePaciente(@PathVariable("id") UUID id) {
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        PacientePerfilDTO perfil = null;
        try {
            perfil = pacientePerfilService.obtenerPerfil(id);
        } catch (Exception e) {
            // si no tiene perfil medico, se queda null
        }
        DetallePacienteDTO detalleCompleto = new DetallePacienteDTO(usuario, perfil);
        return ResponseEntity.ok(detalleCompleto);
    }

    @PutMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> actualizarDetallePaciente(
        @PathVariable("id") UUID id,
        @Valid @RequestBody DetallePacienteDTO dto) {

        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, dto.paciente());
        PacientePerfilDTO perfilActualizado = pacientePerfilService.crearActualizarPerfil(id, dto.perfilMedico());
        DetallePacienteDTO detalleActualizado = new DetallePacienteDTO(usuarioActualizado, perfilActualizado);
        return ResponseEntity.ok(detalleActualizado);
    }

    @GetMapping("/lista/doctores")
    public ResponseEntity<List<UsuarioDTO>> obtenerListaDoctores() {
        return ResponseEntity.ok(usuarioService.listarDoctores());
    }
}
