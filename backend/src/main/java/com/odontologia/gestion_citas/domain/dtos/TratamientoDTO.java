package com.odontologia.gestion_citas.domain.dtos;

import java.math.BigDecimal;


public record TratamientoDTO(
    Long id,
    String nombre,
    Integer duracionMin,
    BigDecimal precioBase,
    String descripcion
) {}


// Agregar validaciones dto
// Agregar excepciones dto 
// Crear la rama para el modulo de tratamientos 