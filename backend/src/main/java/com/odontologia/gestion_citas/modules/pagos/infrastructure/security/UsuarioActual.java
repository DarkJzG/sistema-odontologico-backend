package com.odontologia.gestion_citas.modules.pagos.infrastructure.security;

import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Resuelve el usuario del sistema a partir del token de Keycloak (claim sub = keycloak_id). */
@Component
@RequiredArgsConstructor
public class UsuarioActual {

    private final UsuarioRepository usuarioRepository;

    public Usuario desde(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new AccessDeniedException("Token sin identificador de usuario.");
        }
        UUID keycloakId;
        try {
            keycloakId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            throw new AccessDeniedException("Identificador de usuario inválido.");
        }
        return usuarioRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new AccessDeniedException("El usuario autenticado no está registrado en el sistema."));
    }
}
