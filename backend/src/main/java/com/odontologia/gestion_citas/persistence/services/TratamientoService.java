package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.persistence.repositories.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TratamientoService {
    private final TratamientoRepository tratamientoRepository;
    // Anderson está escribiendo un comentario aquí... jajall
    // TODO: Implementar lógica de precios con BigDecimal
}