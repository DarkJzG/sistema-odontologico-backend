package com.odontologia.gestion_citas.modules.pagos.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Reglas de negocio del control de pagos (CLAUDE.md §7). Sin dependencias de Spring ni JPA.
 * - Dinero con 2 decimales.
 * - Saldo = monto total - abonos PAGADO no anulados (se calcula, no se guarda).
 * - Un abono no puede superar lo que falta por cubrir (se cuentan también los pendientes).
 */
public final class ReglasPago {

    public static final int ESCALA = 2;
    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(ESCALA);
    private static final BigDecimal MAXIMO = new BigDecimal("99999999.99"); // DECIMAL(10,2)

    private ReglasPago() {
    }

    public static BigDecimal normalizar(BigDecimal monto) {
        if (monto == null) {
            return CERO;
        }
        if (monto.stripTrailingZeros().scale() > ESCALA) {
            throw new ReglaPagoException("Los montos admiten como máximo 2 decimales.");
        }
        return monto.setScale(ESCALA, RoundingMode.UNNECESSARY);
    }

    public static BigDecimal saldo(BigDecimal montoTotal, BigDecimal totalPagado) {
        BigDecimal saldo = normalizar(montoTotal).subtract(normalizar(totalPagado));
        return saldo.signum() < 0 ? CERO : saldo;
    }

    public static void validarCuenta(BigDecimal montoTotal, BigDecimal entrada, BigDecimal cuotaSugerida) {
        BigDecimal total = normalizar(montoTotal);
        if (total.signum() <= 0) {
            throw new ReglaPagoException("El monto total debe ser mayor que cero.");
        }
        if (total.compareTo(MAXIMO) > 0) {
            throw new ReglaPagoException("El monto total supera el máximo permitido.");
        }
        BigDecimal ent = normalizar(entrada);
        if (ent.signum() < 0 || ent.compareTo(total) > 0) {
            throw new ReglaPagoException("La entrada debe estar entre 0 y el monto total.");
        }
        BigDecimal cuota = normalizar(cuotaSugerida);
        if (cuota.signum() < 0 || cuota.compareTo(total) > 0) {
            throw new ReglaPagoException("La cuota sugerida debe estar entre 0 y el monto total.");
        }
    }

    /**
     * @param comprometido suma de abonos no anulados (PAGADO y PENDIENTE_DE_PAGO)
     */
    public static void validarNuevoAbono(BigDecimal monto, BigDecimal montoTotal, BigDecimal comprometido) {
        BigDecimal m = normalizar(monto);
        if (m.signum() <= 0) {
            throw new ReglaPagoException("El abono debe ser mayor que cero.");
        }
        BigDecimal disponible = normalizar(montoTotal).subtract(normalizar(comprometido));
        if (m.compareTo(disponible) > 0) {
            throw new ReglaPagoException("El abono (" + m + ") supera el saldo por cubrir (" + disponible.max(CERO) + ").");
        }
    }

    public static EstadoCuenta estadoSegunSaldo(EstadoCuenta actual, BigDecimal saldo) {
        if (actual == EstadoCuenta.ANULADA) {
            return actual;
        }
        return normalizar(saldo).signum() == 0 ? EstadoCuenta.SALDADA : EstadoCuenta.ACTIVA;
    }

    public static String validarMotivo(String motivo) {
        String m = motivo == null ? "" : motivo.trim();
        if (m.length() < 5) {
            throw new ReglaPagoException("Indique el motivo de la anulación (mínimo 5 caracteres).");
        }
        return m;
    }
}
