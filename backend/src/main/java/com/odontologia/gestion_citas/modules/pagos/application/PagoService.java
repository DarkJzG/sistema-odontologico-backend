package com.odontologia.gestion_citas.modules.pagos.application;

import com.odontologia.gestion_citas.modules.pagos.application.dto.*;
import com.odontologia.gestion_citas.modules.pagos.domain.*;
import com.odontologia.gestion_citas.modules.pagos.infrastructure.persistence.*;
import com.odontologia.gestion_citas.persistence.entities.Cita;
import com.odontologia.gestion_citas.persistence.entities.Tratamiento;
import com.odontologia.gestion_citas.persistence.entities.Usuario;
import com.odontologia.gestion_citas.persistence.repositories.CitaRepository;
import com.odontologia.gestion_citas.persistence.repositories.TratamientoRepository;
import com.odontologia.gestion_citas.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/** Casos de uso del control de pagos: cuentas por cobrar y abonos (ODO-39). */
@Service
@RequiredArgsConstructor
@Transactional
public class PagoService {

    private static final ZoneId ZONA = ZoneId.of("America/Guayaquil");

    private final CuentaPorCobrarRepository cuentaRepository;
    private final AbonoRepository abonoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TratamientoRepository tratamientoRepository;
    private final CitaRepository citaRepository;

    // ---------- Consultas ----------

    @Transactional(readOnly = true)
    public ResumenPagosResponse resumenPaciente(UUID pacienteId) {
        buscarPaciente(pacienteId);
        List<CuentaResponse> cuentas = cuentaRepository.findByPaciente_IdOrderByCreadoEnDesc(pacienteId)
                .stream().map(this::aRespuesta).toList();

        BigDecimal total = BigDecimal.ZERO, pagado = BigDecimal.ZERO, pendiente = BigDecimal.ZERO, saldo = BigDecimal.ZERO;
        for (CuentaResponse c : cuentas) {
            if (EstadoCuenta.ANULADA.name().equals(c.estado())) {
                continue;
            }
            total = total.add(c.montoTotal());
            pagado = pagado.add(c.totalPagado());
            pendiente = pendiente.add(c.totalPendiente());
            saldo = saldo.add(c.saldo());
        }
        return new ResumenPagosResponse(pacienteId, ReglasPago.normalizar(total), ReglasPago.normalizar(pagado),
                ReglasPago.normalizar(pendiente), ReglasPago.normalizar(saldo), cuentas);
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtenerCuenta(UUID cuentaId) {
        return aRespuesta(buscarCuenta(cuentaId));
    }

    // ---------- Cuentas ----------

    public CuentaResponse crearCuenta(CrearCuentaRequest req, Usuario actor) {
        ReglasPago.validarCuenta(req.montoTotal(), req.entrada(), req.cuotaSugerida());

        CuentaPorCobrarEntity cuenta = new CuentaPorCobrarEntity();
        cuenta.setPaciente(buscarPaciente(req.pacienteId()));
        if (req.tratamientoId() != null) {
            Tratamiento tratamiento = tratamientoRepository.findById(req.tratamientoId())
                    .orElseThrow(() -> new PagoNoEncontradoException("Tratamiento no encontrado."));
            cuenta.setTratamiento(tratamiento);
        }
        cuenta.setDescripcion(req.descripcion().trim());
        cuenta.setMontoTotal(ReglasPago.normalizar(req.montoTotal()));
        cuenta.setEntrada(ReglasPago.normalizar(req.entrada()));
        cuenta.setCuotaSugerida(req.cuotaSugerida() == null ? null : ReglasPago.normalizar(req.cuotaSugerida()));
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setCreadoPor(actor);
        return aRespuesta(cuentaRepository.save(cuenta));
    }

    public CuentaResponse anularCuenta(UUID cuentaId, String motivo, Usuario actor) {
        CuentaPorCobrarEntity cuenta = cuentaRepository.findByIdParaActualizar(cuentaId)
                .orElseThrow(() -> new PagoNoEncontradoException("Cuenta no encontrada."));
        if (cuenta.getEstado() == EstadoCuenta.ANULADA) {
            throw new ReglaPagoException("La cuenta ya está anulada.");
        }
        boolean tieneAbonosVigentes = abonos(cuenta).stream().anyMatch(a -> !a.isAnulado());
        if (tieneAbonosVigentes) {
            throw new ReglaPagoException("Anule primero los abonos vigentes de esta cuenta.");
        }
        cuenta.setEstado(EstadoCuenta.ANULADA);
        cuenta.setMotivoAnulacion(ReglasPago.validarMotivo(motivo));
        cuenta.setAnuladoPor(actor);
        cuenta.setAnuladoEn(ahora());
        return aRespuesta(cuentaRepository.save(cuenta));
    }

    // ---------- Abonos ----------

    public CuentaResponse registrarAbono(UUID cuentaId, RegistrarAbonoRequest req, Usuario actor) {
        CuentaPorCobrarEntity cuenta = cuentaRepository.findByIdParaActualizar(cuentaId)
                .orElseThrow(() -> new PagoNoEncontradoException("Cuenta no encontrada."));
        if (cuenta.getEstado() != EstadoCuenta.ACTIVA) {
            throw new ReglaPagoException("Solo se registran abonos en cuentas ACTIVA (estado actual: " + cuenta.getEstado() + ").");
        }
        ReglasPago.validarNuevoAbono(req.monto(), cuenta.getMontoTotal(), comprometido(cuenta));

        AbonoEntity abono = new AbonoEntity();
        abono.setCuenta(cuenta);
        abono.setFecha(req.fecha() != null ? req.fecha() : LocalDate.now(ZONA));
        abono.setMonto(ReglasPago.normalizar(req.monto()));
        abono.setObservacion(req.observacion() == null || req.observacion().isBlank() ? null : req.observacion().trim());
        abono.setRegistradoPor(actor);
        if (req.citaId() != null) {
            Cita cita = citaRepository.findById(req.citaId())
                    .orElseThrow(() -> new PagoNoEncontradoException("Cita no encontrada."));
            if (!cita.getPaciente().getId().equals(cuenta.getPaciente().getId())) {
                throw new ReglaPagoException("La cita no pertenece al paciente de la cuenta.");
            }
            abono.setCita(cita);
        }
        abono.setEstado(req.estado());
        if (req.estado() == EstadoAbono.PAGADO) {
            abono.setAprobadoPor(actor);
            abono.setAprobadoEn(ahora());
        }
        abonoRepository.save(abono);
        actualizarEstado(cuenta);
        return aRespuesta(cuenta);
    }

    /** Pasa un abono de PENDIENTE_DE_PAGO a PAGADO y registra quién y cuándo (reemplaza la firma en papel). */
    public CuentaResponse aprobarAbono(UUID abonoId, Usuario actor) {
        AbonoEntity abono = buscarAbono(abonoId);
        CuentaPorCobrarEntity cuenta = cuentaRepository.findByIdParaActualizar(abono.getCuenta().getId())
                .orElseThrow(() -> new PagoNoEncontradoException("Cuenta no encontrada."));
        if (cuenta.getEstado() == EstadoCuenta.ANULADA) {
            throw new ReglaPagoException("La cuenta está anulada.");
        }
        if (abono.isAnulado()) {
            throw new ReglaPagoException("El abono está anulado.");
        }
        if (abono.getEstado() == EstadoAbono.PAGADO) {
            throw new ReglaPagoException("El abono ya está aprobado como PAGADO.");
        }
        abono.setEstado(EstadoAbono.PAGADO);
        abono.setAprobadoPor(actor);
        abono.setAprobadoEn(ahora());
        abonoRepository.save(abono);
        actualizarEstado(cuenta);
        return aRespuesta(cuenta);
    }

    public CuentaResponse anularAbono(UUID abonoId, String motivo, Usuario actor) {
        AbonoEntity abono = buscarAbono(abonoId);
        CuentaPorCobrarEntity cuenta = cuentaRepository.findByIdParaActualizar(abono.getCuenta().getId())
                .orElseThrow(() -> new PagoNoEncontradoException("Cuenta no encontrada."));
        if (abono.isAnulado()) {
            throw new ReglaPagoException("El abono ya está anulado.");
        }
        abono.setAnulado(true);
        abono.setMotivoAnulacion(ReglasPago.validarMotivo(motivo));
        abono.setAnuladoPor(actor);
        abono.setAnuladoEn(ahora());
        abonoRepository.save(abono);
        actualizarEstado(cuenta);
        return aRespuesta(cuenta);
    }

    // ---------- Apoyo ----------

    private void actualizarEstado(CuentaPorCobrarEntity cuenta) {
        BigDecimal saldo = ReglasPago.saldo(cuenta.getMontoTotal(), pagado(cuenta));
        EstadoCuenta nuevo = ReglasPago.estadoSegunSaldo(cuenta.getEstado(), saldo);
        if (nuevo != cuenta.getEstado()) {
            cuenta.setEstado(nuevo);
            cuentaRepository.save(cuenta);
        }
    }

    private List<AbonoEntity> abonos(CuentaPorCobrarEntity cuenta) {
        return cuenta.getId() == null ? List.of() : abonoRepository.findByCuenta_IdOrderByFechaAscCreadoEnAsc(cuenta.getId());
    }

    private BigDecimal pagado(CuentaPorCobrarEntity cuenta) {
        return sumar(abonos(cuenta), EstadoAbono.PAGADO);
    }

    private BigDecimal comprometido(CuentaPorCobrarEntity cuenta) {
        return abonos(cuenta).stream().filter(a -> !a.isAnulado())
                .map(AbonoEntity::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal sumar(List<AbonoEntity> lista, EstadoAbono estado) {
        return lista.stream().filter(a -> !a.isAnulado() && a.getEstado() == estado)
                .map(AbonoEntity::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Usuario buscarPaciente(UUID pacienteId) {
        Usuario paciente = usuarioRepository.findById(pacienteId)
                .orElseThrow(() -> new PagoNoEncontradoException("Paciente no encontrado."));
        if (paciente.getRol() != Usuario.Rol.PACIENTE) {
            throw new ReglaPagoException("El usuario indicado no es un paciente.");
        }
        return paciente;
    }

    private CuentaPorCobrarEntity buscarCuenta(UUID id) {
        return cuentaRepository.findById(id).orElseThrow(() -> new PagoNoEncontradoException("Cuenta no encontrada."));
    }

    private AbonoEntity buscarAbono(UUID id) {
        return abonoRepository.findById(id).orElseThrow(() -> new PagoNoEncontradoException("Abono no encontrado."));
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    private static String nombre(Usuario u) {
        return u == null ? null : (u.getNombres() + " " + u.getApellidos()).trim();
    }

    private CuentaResponse aRespuesta(CuentaPorCobrarEntity c) {
        List<AbonoEntity> lista = abonos(c);
        BigDecimal pagado = ReglasPago.normalizar(sumar(lista, EstadoAbono.PAGADO));
        BigDecimal pendiente = ReglasPago.normalizar(sumar(lista, EstadoAbono.PENDIENTE_DE_PAGO));
        List<AbonoResponse> abonos = lista.stream().map(a -> new AbonoResponse(
                a.getId(), c.getId(), a.getFecha(), a.getMonto(),
                a.getCita() == null ? null : a.getCita().getId(),
                a.getObservacion(), a.getEstado().name(),
                nombre(a.getRegistradoPor()), nombre(a.getAprobadoPor()), a.getAprobadoEn(),
                a.isAnulado(), a.getMotivoAnulacion(), nombre(a.getAnuladoPor()), a.getAnuladoEn(), a.getCreadoEn()
        )).toList();
        return new CuentaResponse(
                c.getId(), c.getPaciente().getId(),
                c.getTratamiento() == null ? null : c.getTratamiento().getId(),
                c.getTratamiento() == null ? null : c.getTratamiento().getNombre(),
                c.getDescripcion(), c.getMontoTotal(), c.getEntrada(), c.getCuotaSugerida(),
                pagado, pendiente, ReglasPago.saldo(c.getMontoTotal(), pagado),
                c.getEstado().name(), nombre(c.getCreadoPor()), c.getCreadoEn(), c.getMotivoAnulacion(), abonos);
    }
}
