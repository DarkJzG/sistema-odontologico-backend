// src/main/java/com/odontologia/gestion_citas/persistence/services/RadiografiaService.java
package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.RadiografiaDTO;
import com.odontologia.gestion_citas.persistence.entities.Radiografia;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.RadiografiaRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadiografiaService {

    private final RadiografiaRepository radiografiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService; // Inyecta la estrategia (LocalFileStorageService de momento)

    @Transactional
    public RadiografiaDTO subirRadiografia(UUID idPaciente, String tipo, MultipartFile archivo) {
        // Validación relacional de soporte dual de identidades
        Usuario paciente = usuarioRepository.findById(idPaciente)
                .orElseGet(() -> usuarioRepository.findByKeycloakId(idPaciente)
                .orElseThrow(() -> new RuntimeException("No se puede registrar el estudio: El paciente no existe.")));

        // Guardamos el archivo binario físicamente mediante el servicio abstracto
        String urlResultado = storageService.guardarArchivo(archivo, "radiografias");

        // Creamos y guardamos la metadata en PostgreSQL
        Radiografia rx = new Radiografia();
        rx.setPaciente(paciente);
        rx.setTipo(tipo.toUpperCase());
        rx.setUrlArchivo(urlResultado);

        Radiografia guardada = radiografiaRepository.save(rx);
        return mapearADTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<RadiografiaDTO> obtenerPorPaciente(UUID idPaciente) {
        Usuario paciente = usuarioRepository.findById(idPaciente)
                .orElseGet(() -> usuarioRepository.findByKeycloakId(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado.")));

        return radiografiaRepository.findByPaciente_IdOrderByCreadoEnDesc(paciente.getId()).stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    private RadiografiaDTO mapearADTO(Radiografia rx) {
        return new RadiografiaDTO(
                rx.getId(),
                rx.getPaciente().getId(),
                rx.getTipo(),
                rx.getUrlArchivo(),
                rx.getCreadoEn()
        );
    }
}