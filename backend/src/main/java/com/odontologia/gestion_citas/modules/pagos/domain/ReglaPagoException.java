package com.odontologia.gestion_citas.modules.pagos.domain;

/** Regla de negocio de pagos incumplida: el GlobalExceptionHandler responde 400. */
public class ReglaPagoException extends RuntimeException {
    public ReglaPagoException(String mensaje) {
        super(mensaje);
    }
}
