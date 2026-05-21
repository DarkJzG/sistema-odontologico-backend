package com.odontologia.gestion_citas.web.controllers;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.domain.dtos.PacientePerfilDTO;
import com.odontologia.gestion_citas.domain.dtos.DetallePacienteDTO;
import com.odontologia.gestion_citas.persistence.services.UsuarioService;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
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

    //listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    //buscar por cedula
    @GetMapping("/cedula/{cedula}")
    public ResponseEntity<UsuarioDTO> buscarPorCedula(@PathVariable("cedula") String cedula) {
        return ResponseEntity.ok(usuarioService.buscarPorCedula(cedula));
    }
    
    //mostrar informacion basica de un usuario
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuario(id));
    }
    
    //Mostrar detalle completo (tabla usuario y perfilpaciente)
    @GetMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> obtenerDetallePaciente(@PathVariable("id") UUID id) {

        //busco los datos del usuario por su id
        UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
        //busco su perfil medico en base a su id
        PacientePerfilDTO perfil = null;
        try {
            perfil = pacientePerfilService.obtenerPerfil(id);
        } catch (Exception e) {
            //si no tiene perfil medico, se queda null
        }
        //empaqueto y envio la información
        DetallePacienteDTO detalleCompleto = new DetallePacienteDTO(usuario, perfil);
        return ResponseEntity.ok(detalleCompleto);
    }

    //Actualizar y guardar cambios en las tabla Usuario y PacientePerfil
    @PutMapping("/{id}/detalle")
    public ResponseEntity<DetallePacienteDTO> actualizarDetallePaciente(
        @PathVariable("id") UUID id,
        @Valid @RequestBody DetallePacienteDTO dto) {

        UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, dto.paciente());
        PacientePerfilDTO perfilActualizado = pacientePerfilService.crearActualizarPerfil(id, dto.perfilMedico());
        DetallePacienteDTO detalleActualizado = new DetallePacienteDTO(usuarioActualizado, perfilActualizado);
        return ResponseEntity.ok(detalleActualizado);
    }

}

