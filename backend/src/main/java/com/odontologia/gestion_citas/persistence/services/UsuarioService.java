package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("null")
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .telefono(dto.telefono())
                .rol(Usuario.Rol.valueOf(dto.rol()))
                .build();

        @NonNull Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapearADTO(usuarioGuardado);
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorCedula(String cedula) {
        return usuarioRepository.findByCedula(cedula)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con cédula: " + cedula));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con email: " + email));
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioDTO actualizarUsuario(UUID id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar"));

        if (!usuario.getEmail().equals(dto.email()) && 
            usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El nuevo correo ya está registrado por otro usuario");
        }

        usuario.setCedula(dto.cedula());
        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setEmail(dto.email());
        usuario.setTelefono(dto.telefono());
        usuario.setRol(Usuario.Rol.valueOf(dto.rol()));

        @NonNull Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return mapearADTO(usuarioActualizado);
    }

    @Transactional
    public void eliminarUsuario(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO mapearADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getCedula(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol() != null ? usuario.getRol().name() : "NO_ASIGNADO"
        );
    }
}