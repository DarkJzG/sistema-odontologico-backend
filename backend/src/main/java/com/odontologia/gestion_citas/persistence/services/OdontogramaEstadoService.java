package com.odontologia.gestion_citas.persistence.services;

import com.odontologia.gestion_citas.domain.dtos.OdontogramaEstadoDTO;
import com.odontologia.gestion_citas.persistence.entities.OdontogramaEstado;
import com.odontologia.gestion_citas.persistence.entities.PiezaDental;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.OdontogramaEstadoRepository;
import com.odontologia.gestion_citas.persistence.repositories.PiezaDentalRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OdontogramaEstadoService {
    
    private final OdontogramaEstadoRepository odontogramaEstadoRepository;
    private final PiezaDentalRepository piezaDentalRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public OdontogramaEstadoDTO registrarEstado(UUID idPaciente, OdontogramaEstadoDTO dto) {
        
        //Existe el paciente?
        Usuario paciente = usuarioRepository.findById(idPaciente)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        //Existe la pieza dental??
        PiezaDental pieza = piezaDentalRepository.findById(dto.piezaId())
            .orElseThrow(() -> new RuntimeException("Pieza dental no encontrada"));
        
        //se consuye la entidad
        OdontogramaEstado estado = OdontogramaEstado.builder()
            .paciente(paciente)
            .piezaDental(pieza)
            .posicion(OdontogramaEstado.Posicion.valueOf(dto.posicion().toUpperCase()))
            .estado(OdontogramaEstado.Estado.valueOf(dto.estado().toUpperCase()))
            .notas(dto.notas())
            .build();
        
        OdontogramaEstado guardado = odontogramaEstadoRepository.save(estado);
        return mapearADTO(guardado);
        
    }

    @Transactional(readOnly =true)
    public List<OdontogramaEstadoDTO> obtenerOdontogramaPaciente(UUID idPaciente) {
        if (!usuarioRepository.existsById(idPaciente)) {
            throw new RuntimeException("Paciente no encontrado");
        }
        
        return odontogramaEstadoRepository.findByPacienteIdUsuario(idPaciente).stream()
            .map(this::mapearADTO)
            .collect(Collectors.toList());
    }

    private OdontogramaEstadoDTO mapearADTO(OdontogramaEstado estado) {
        return new OdontogramaEstadoDTO(
            estado.getId(),
            estado.getPaciente().getIdUsuario(),
            estado.getPiezaDental().getIdPieza(),
            estado.getPosicion().name(),
            estado.getEstado().name(),
            estado.getNotas(),
            estado.getFechaRegistro()
        );
    }

}


