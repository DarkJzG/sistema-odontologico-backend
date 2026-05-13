package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.persistence.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    // Para el registro inicial de pacientes en el portal
    @PostMapping
    public ResponseEntity<UsuarioDTO> registrarUsuario(@jakarta.validation.Valid @RequestBody UsuarioDTO usuarioDTO) {
        // Al poner @Valid, Spring revisa el DTO antes de entrar al Service
        UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    // CRUCIAL: Para obtener los datos del paciente logueado o buscar un paciente en el Panel Administrativo
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // Útil para el administrador cuando busca un paciente por su número de identificación
    @GetMapping("/buscar")
    public ResponseEntity<UsuarioDTO> obtenerPorCedula(@RequestParam String cedula) {
        return ResponseEntity.ok(usuarioService.obtenerPorCedula(cedula));
    }

    // Endpoint para actualizar
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable UUID id, @jakarta.validation.Valid @RequestBody UsuarioDTO dto) {
        // También validamos al editar
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, dto));
    }

    // Endpoint para eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build(); // Retorna un 204 No Content
    }
}