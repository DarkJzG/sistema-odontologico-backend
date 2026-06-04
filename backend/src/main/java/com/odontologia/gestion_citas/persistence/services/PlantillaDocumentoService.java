// src/main/java/com/odontologia/gestion_citas/persistence/services/PlantillaService.java
package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.PlantillaDocumentoDTO;
import com.odontologia.gestion_citas.persistence.entities.PlantillaDocumento;
import com.odontologia.gestion_citas.persistence.repositories.PlantillaDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlantillaDocumentoService {

    private final PlantillaDocumentoRepository plantillaRepository;

    @Transactional(readOnly = true)
    public List<PlantillaDocumentoDTO> listarActivas() {
        return plantillaRepository.findByEsActivoTrueOrderByTituloAsc().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlantillaDocumentoDTO guardar(PlantillaDocumentoDTO dto) {
        PlantillaDocumento plantilla;
        
        if (dto.id() != null) {
            plantilla = plantillaRepository.findById(dto.id())
                    .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
        } else {
            plantilla = new PlantillaDocumento();
        }

        plantilla.setTitulo(dto.titulo());
        plantilla.setContenido(dto.contenido());
        // Si viene nulo asumimos true para creaciones
        plantilla.setEsActivo(true); 

        PlantillaDocumento guardada = plantillaRepository.save(plantilla);
        return mapearADTO(guardada);
    }

    @Transactional
    public void desactivar(UUID id) {
        PlantillaDocumento plantilla = plantillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada"));
        plantilla.setEsActivo(false); // Borrado lógico para no afectar historiales
        plantillaRepository.save(plantilla);
    }

    private PlantillaDocumentoDTO mapearADTO(PlantillaDocumento p) {
        return new PlantillaDocumentoDTO(p.getId(), p.getTitulo(), p.getContenido(), p.isEsActivo());
    }
}