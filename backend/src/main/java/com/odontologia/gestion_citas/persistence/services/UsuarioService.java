package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


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
}
