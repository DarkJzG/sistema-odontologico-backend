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
@Service

public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {

        //validar duplicados
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        //mapeo: convertir el dto en entidad usando lombok
        Usuario usuario = Usuario.builder()
                .idUsuario(UUID.randomUUID())
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .telefono(dto.telefono())
                .rol(Usuario.Rol.valueOf(dto.rol()))
                .build();

        //persistencia_ guardar en la BD
        @NonNull Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return new UsuarioDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getCedula(),
                usuarioGuardado.getNombres(),
                usuarioGuardado.getApellidos(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getTelefono(),
                usuarioGuardado.getRol().name()
        );

    }

    //listar todos los usuarios
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioDTO(
                        usuario.getIdUsuario(),
                        usuario.getCedula(),
                        usuario.getNombres(),
                        usuario.getApellidos(),
                        usuario.getEmail(),
                        usuario.getTelefono(),
                        usuario.getRol() != null ? usuario.getRol().name() : "NO_ASIGNADO"
                ))
                .collect(Collectors.toList());
    }

    //mostrar informacion de un usuario
    public UsuarioDTO obtenerUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
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

    //Actualziar los cambios de DETALLEPACIENTE
    public UsuarioDTO actualizarUsuario(UUID id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setCedula(dto.cedula());
        usuario.setNombres(dto.nombres());
        usuario.setApellidos(dto.apellidos());
        usuario.setTelefono(dto.telefono());
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return new UsuarioDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getCedula(),
                usuarioGuardado.getNombres(),
                usuarioGuardado.getApellidos(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getTelefono(),
                usuarioGuardado.getRol() != null ? usuarioGuardado.getRol().name() : "NO_ASIGNADO"
        );
    }
    
    //buscar por cedula
    public UsuarioDTO buscarPorCedula(String cedula) {
        Usuario usuario = usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
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
