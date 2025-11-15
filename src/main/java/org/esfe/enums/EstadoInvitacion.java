package org.esfe.enums;

public enum EstadoInvitacion {
    PENDIENTE("pendiente"),
    ACEPTADA("aceptada"),
    RECHAZADA("rechazada"),
    CANCELADA("cancelada");

    private final String valor;

    EstadoInvitacion(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}