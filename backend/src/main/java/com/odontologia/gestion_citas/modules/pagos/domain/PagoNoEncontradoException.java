package com.odontologia.gestion_citas.modules.pagos.domain;

/** Recurso de pagos inexistente: el GlobalExceptionHandler responde 404. */
public class PagoNoEncontradoException extends RuntimeException {
    public PagoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
