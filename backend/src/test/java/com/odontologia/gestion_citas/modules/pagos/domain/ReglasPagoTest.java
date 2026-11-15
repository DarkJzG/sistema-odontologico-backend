package com.odontologia.gestion_citas.modules.pagos.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Pruebas de las reglas de pagos con montos ficticios (sin Spring ni base de datos). */
class ReglasPagoTest {

    private static BigDecimal m(String v) {
        return new BigDecimal(v);
    }

    @Test
    void saldoEsTotalMenosPagado() {
        assertEquals(m("950.00"), ReglasPago.saldo(m("1200"), m("250")));
    }

    @Test
    void saldoNuncaEsNegativo() {
        assertEquals(m("0.00"), ReglasPago.saldo(m("100"), m("150")));
    }

    @Test
    void abonoQueSuperaElSaldoSeRechaza() {
        ReglaPagoException ex = assertThrows(ReglaPagoException.class,
                () -> ReglasPago.validarNuevoAbono(m("60"), m("100"), m("50")));
        assertTrue(ex.getMessage().contains("supera el saldo"));
    }

    @Test
    void abonoExactoAlSaldoSeAcepta() {
        assertDoesNotThrow(() -> ReglasPago.validarNuevoAbono(m("50"), m("100"), m("50")));
    }

    @Test
    void abonoCeroONegativoSeRechaza() {
        assertThrows(ReglaPagoException.class, () -> ReglasPago.validarNuevoAbono(m("0"), m("100"), m("0")));
        assertThrows(ReglaPagoException.class, () -> ReglasPago.validarNuevoAbono(m("-5"), m("100"), m("0")));
    }

    @Test
    void masDeDosDecimalesSeRechaza() {
        assertThrows(ReglaPagoException.class, () -> ReglasPago.normalizar(m("10.005")));
        assertEquals(m("10.50"), ReglasPago.normalizar(m("10.5")));
    }

    @Test
    void cuentaConEntradaMayorAlTotalSeRechaza() {
        assertThrows(ReglaPagoException.class, () -> ReglasPago.validarCuenta(m("100"), m("150"), null));
        assertThrows(ReglaPagoException.class, () -> ReglasPago.validarCuenta(m("0"), null, null));
        assertDoesNotThrow(() -> ReglasPago.validarCuenta(m("1200"), m("200"), m("25")));
    }

    @Test
    void cuentaSeSaldaCuandoElSaldoLlegaACeroYSeReactivaSiSube() {
        assertEquals(EstadoCuenta.SALDADA, ReglasPago.estadoSegunSaldo(EstadoCuenta.ACTIVA, m("0")));
        assertEquals(EstadoCuenta.ACTIVA, ReglasPago.estadoSegunSaldo(EstadoCuenta.SALDADA, m("25")));
        assertEquals(EstadoCuenta.ANULADA, ReglasPago.estadoSegunSaldo(EstadoCuenta.ANULADA, m("0")));
    }

    @Test
    void motivoDeAnulacionEsObligatorio() {
        assertThrows(ReglaPagoException.class, () -> ReglasPago.validarMotivo("  "));
        assertEquals("Error de digitación", ReglasPago.validarMotivo("  Error de digitación "));
    }
}
