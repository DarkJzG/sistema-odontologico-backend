package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.TratamientoDTO;
import com.odontologia.gestion_citas.persistence.entities.Tratamiento;
import com.odontologia.gestion_citas.persistence.repositories.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TratamientoService {

    private final TratamientoRepository tratamientoRepository;

    @Transactional(readOnly = true)
    public List<TratamientoDTO> listarTodos() {
        return tratamientoRepository.findAll().stream()
                .map(this::mapearADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TratamientoDTO obtenerPorId(Long id) {
        return tratamientoRepository.findById(id)
                .map(this::mapearADTO)
                .orElseThrow(() -> new RuntimeException("No existe un tratamiento con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public Tratamiento obtenerEntidadPorId(Long id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error interno: Entidad Tratamiento no encontrada."));
    }

    @Transactional
    public TratamientoDTO crearTratamiento(TratamientoDTO dto) {
        // VALIDACIÓN DE NEGOCIO: Evitar duplicados por nombre
        if (tratamientoRepository.existsByNombre(dto.nombre())) {
            throw new RuntimeException("Ya existe un tratamiento llamado '" + dto.nombre() + "'. Intenta con otro nombre.");
        }

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setNombre(dto.nombre());
        tratamiento.setDuracionMin(dto.duracionMin());
        tratamiento.setPrecioBase(dto.precioBase());
        tratamiento.setDescripcion(dto.descripcion());

        return mapearADTO(tratamientoRepository.save(tratamiento));
    }

    @Transactional
    public TratamientoDTO actualizarTratamiento(Long id, TratamientoDTO dto) {
        Tratamiento tratamiento = tratamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede actualizar: Tratamiento no encontrado."));

        // Si el nombre cambia, validamos que el nuevo no esté ocupado
        if (!tratamiento.getNombre().equals(dto.nombre()) && tratamientoRepository.existsByNombre(dto.nombre())) {
            throw new RuntimeException("El nuevo nombre '" + dto.nombre() + "' ya está en uso.");
        }

        tratamiento.setNombre(dto.nombre());
        tratamiento.setDuracionMin(dto.duracionMin());
        tratamiento.setPrecioBase(dto.precioBase());
        tratamiento.setDescripcion(dto.descripcion());

        return mapearADTO(tratamientoRepository.save(tratamiento));
    }

    @Transactional
    public void eliminarTratamiento(Long id) {
        if (!tratamientoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: El tratamiento no existe.");
        }
        // OJO: Aquí podrías validar si hay citas usando este tratamiento antes de borrar
        tratamientoRepository.deleteById(id);
    }

    private TratamientoDTO mapearADTO(Tratamiento entidad) {
        return new TratamientoDTO(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDuracionMin(),
                entidad.getPrecioBase(),
                entidad.getDescripcion()
        );
    }
}