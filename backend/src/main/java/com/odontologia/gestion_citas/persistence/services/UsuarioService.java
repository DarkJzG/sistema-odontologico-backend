package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica de negocio para la gestión de usuarios.
 */
@RequiredArgsConstructor
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Crea un nuevo usuario validando que el correo electrónico sea único.
     */
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        // Validar duplicados por correo electrónico
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Mapeo: convertir DTO a entidad usando el patrón Builder de Lombok
        Usuario usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .telefono(dto.telefono())
                .rol(Usuario.Rol.valueOf(dto.rol()))
                .build();

        // Persistencia: Guardar en la BD asegurando que el objeto no sea nulo
        @NonNull Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return mapearADTO(usuarioGuardado);
    }

    /**
     * Busca un usuario por su ID (UUID).
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Busca un usuario por su número de cédula.
     */
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorCedula(String cedula) {
        return usuarioRepository.findByCedula(cedula)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con cédula: " + cedula));
    }

    /**
     * Lista todos los usuarios registrados.
     */
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza la información de un usuario existente.
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(UUID id, UsuarioDTO dto) {
        // 1. Buscar el usuario existente
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));

        // 2. Validar si el correo cambió y si el nuevo ya está en uso
        if (!usuario.getEmail().equals(dto.email()) && 
            usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El nuevo correo ya está registrado por otro usuario");
        }

        // 3. Actualizar campos mediante setters
        usuario.setCedula(dto.cedula());
        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setEmail(dto.email());
        usuario.setTelefono(dto.telefono());
        usuario.setRol(Usuario.Rol.valueOf(dto.rol()));

        // 4. Guardar cambios
        @NonNull Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return mapearADTO(usuarioActualizado);
    }

    /**
     * Elimina físicamente a un usuario de la base de datos.
     */
    @Transactional
    public void eliminarUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Método privado (Helper) para evitar repetir código de mapeo.
     */
    private UsuarioDTO mapearADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getCedula(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol().name()
        );
    }
}