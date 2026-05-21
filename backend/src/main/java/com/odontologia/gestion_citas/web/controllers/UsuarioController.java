package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.domain.dtos.PacientePerfilDTO;
import com.odontologia.gestion_citas.domain.dtos.DetallePacienteDTO;
import com.odontologia.gestion_citas.persistence.services.UsuarioService;
import com.odontologia.gestion_citas.persistence.services.PacientePerfilService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final PacientePerfilService pacientePerfilService; // Inyectamos el servicio de tu compañero
    
    // 1. Registro inicial
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // 2. Listar todos los usuarios (Aporte de tu compañero)
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // 3. Obtener por ID (Tu aporte)
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // 4. Buscar por Cédula (Ruta RESTful de tu compañero conectada a tu lógica)
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<UsuarioDTO> buscarPorCedula(@PathVariable("cedula") String cedula) {
        return ResponseEntity.ok(usuarioService.obtenerPorCedula(cedula));
    }

    // 5. Buscar por Correo (Tu aporte)
    @GetMapping("/buscar-correo")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    // 6. Actualizar información básica (Tu aporte)
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable UUID id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    // 7. Eliminar usuario (Tu aporte)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build(); 
    }

    // =========================================================================
    // MÓDULO CLÍNICO (Aportes de tu compañero para el perfil médico)
    // =========================================================================

    // Mostrar detalle completo (tabla usuario y perfilpaciente)
    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> obtenerDetallePaciente(@PathVariable("id") UUID id) {

        // Usamos TU método del servicio que dejamos estandarizado
        UsuarioDTO usuario = usuarioService.obtenerPorId(id);
        
        // Buscamos el perfil médico
        PacientePerfilDTO perfil = null;
        try {
            perfil = pacientePerfilService.obtenerPerfil(id);
        } catch (Exception e) {
            // Si no tiene perfil médico, se queda null y no rompe la app
        }
        
        DetallePacienteDTO detalleCompleto = new DetallePacienteDTO(usuario, perfil);
        return ResponseEntity.ok(detalleCompleto);
    }

    // Actualizar y guardar cambios en las tablas Usuario y PacientePerfil
    @PutMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> actualizarDetallePaciente(
        @PathVariable("id") UUID id,
        @Valid @RequestBody DetallePacienteDTO dto) {

        // Usamos TU método del servicio para actualizar al usuario
        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, dto.paciente());
        
        // Actualizamos el perfil médico usando el servicio de tu compañero
        PacientePerfilDTO perfilActualizado = pacientePerfilService.crearActualizarPerfil(id, dto.perfilMedico());
        
        DetallePacienteDTO detalleActualizado = new DetallePacienteDTO(usuarioActualizado, perfilActualizado);
        return ResponseEntity.ok(detalleActualizado);
    }
}