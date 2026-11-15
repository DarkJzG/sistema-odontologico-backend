package com.odontologia.gestion_citas.modules.pagos.infrastructure.web;

import com.odontologia.gestion_citas.modules.pagos.application.PagoService;
import com.odontologia.gestion_citas.modules.pagos.application.dto.*;
import com.odontologia.gestion_citas.modules.pagos.infrastructure.security.UsuarioActual;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Control de pagos (CLAUDE.md §7). Solo DOCTOR y ASISTENTE; el rol se valida en el backend.
 * Fuera del contrato original: no desplegar hasta que la solicitud de cambio (ODO-37) esté firmada.
 */
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DOCTOR','ASISTENTE')")
public class PagoController {

    private final PagoService pagoService;
    private final UsuarioActual usuarioActual;

    @GetMapping("/pacientes/{pacienteId}/cuentas")
    public ResponseEntity<ResumenPagosResponse> resumenPaciente(@PathVariable("pacienteId") UUID pacienteId) {
        return ResponseEntity.ok(pagoService.resumenPaciente(pacienteId));
    }

    @PostMapping("/cuentas")
    public ResponseEntity<CuentaResponse> crearCuenta(@Valid @RequestBody CrearCuentaRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(pagoService.crearCuenta(request, usuarioActual.desde(jwt)), HttpStatus.CREATED);
    }

    @GetMapping("/cuentas/{cuentaId}")
    public ResponseEntity<CuentaResponse> obtenerCuenta(@PathVariable("cuentaId") UUID cuentaId) {
        return ResponseEntity.ok(pagoService.obtenerCuenta(cuentaId));
    }

    @PutMapping("/cuentas/{cuentaId}/anular")
    public ResponseEntity<CuentaResponse> anularCuenta(@PathVariable("cuentaId") UUID cuentaId,
                                                       @Valid @RequestBody AnularRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pagoService.anularCuenta(cuentaId, request.motivo(), usuarioActual.desde(jwt)));
    }

    @PostMapping("/cuentas/{cuentaId}/abonos")
    public ResponseEntity<CuentaResponse> registrarAbono(@PathVariable("cuentaId") UUID cuentaId,
                                                         @Valid @RequestBody RegistrarAbonoRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(pagoService.registrarAbono(cuentaId, request, usuarioActual.desde(jwt)), HttpStatus.CREATED);
    }

    @PutMapping("/abonos/{abonoId}/aprobar")
    public ResponseEntity<CuentaResponse> aprobarAbono(@PathVariable("abonoId") UUID abonoId,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pagoService.aprobarAbono(abonoId, usuarioActual.desde(jwt)));
    }

    @PutMapping("/abonos/{abonoId}/anular")
    public ResponseEntity<CuentaResponse> anularAbono(@PathVariable("abonoId") UUID abonoId,
                                                      @Valid @RequestBody AnularRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pagoService.anularAbono(abonoId, request.motivo(), usuarioActual.desde(jwt)));
    }
}
