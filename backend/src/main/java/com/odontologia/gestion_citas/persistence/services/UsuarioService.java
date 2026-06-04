

//src/main/java/com/odontologia/gestion_citas/persistence/services/UsuarioService.java
package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.UsuarioDTO;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import jakarta.ws.rs.core.Response;
import java.util.Collections;

import com.odontologia.gestion_citas.persistence.repositories.PacientePerfilRepository;
import com.odontologia.gestion_citas.persistence.repositories.CitaRepository;
import com.odontologia.gestion_citas.persistence.entities.Cita;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("null")
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final Keycloak keycloak;

    private final PacientePerfilRepository pacientePerfilRepository;
    private final CitaRepository citaRepository;

    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // 1. Crear el usuario en Keycloak
        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.email());
        user.setEmail(dto.email());
        user.setFirstName(dto.nombres());
        user.setLastName(dto.apellidos());
        user.setEnabled(true);
        user.setEmailVerified(true);

        // Contraseña temporal (usamos la cédula como pediste)
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.cedula()); 
        credential.setTemporary(true); // ¡Obliga al cambio de clave al primer login!
        user.setCredentials(Collections.singletonList(credential));

        UsersResource usersResource = keycloak.realm("odontostyle-realm").users();
        Response response = usersResource.create(user);
        
        if (response.getStatus() != 201) {
            throw new RuntimeException("Error al crear usuario en Keycloak: " + response.getStatusInfo());
        }

        // Obtener el ID que Keycloak asignó al usuario
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        String roleName = dto.rol(); // "PACIENTE", "DOCTOR", etc.
        var roleToAdd = keycloak.realm("odontostyle-realm").roles().get(roleName).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleToAdd));

        // 2. Guardar en PostgreSQL incluyendo el nuevo keycloak_id
        Usuario usuario = Usuario.builder()
                .cedula(dto.cedula())
                .nombres(dto.nombres())
                .apellidos(dto.apellidos())
                .email(dto.email())
                .telefono(dto.telefono())
                .rol(Usuario.Rol.valueOf(dto.rol()))
                .keycloakId(UUID.fromString(userId)) // <-- Guardamos la conexión
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
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
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar: Usuario no encontrado"));

        List<Cita> citasDelPaciente = citaRepository.findByPaciente_Id(id);
        if (!citasDelPaciente.isEmpty()) {
            citaRepository.borrarCitasPorPaciente(id);
        }
        if (pacientePerfilRepository.existsById(id)) {
            pacientePerfilRepository.deleteById(id);
        }
        if (usuario.getKeycloakId() != null) {
            try {
                keycloak.realm("odontostyle-realm").users().get(usuario.getKeycloakId().toString()).remove();
            } catch (Exception e) {
                System.out.println("Nota: El usuario no existía en Keycloak o hubo un error de red.");
            }
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO mapearADTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getCedula(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol() != null ? usuario.getRol().name() : "NO_ASIGNADO"
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarDoctores() {
        return usuarioRepository.findByRol(Usuario.Rol.DOCTOR).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }
}