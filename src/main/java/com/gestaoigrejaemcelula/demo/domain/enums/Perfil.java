package com.gestaoigrejaemcelula.demo.domain.enums;

public enum Perfil {

    ADMIN,
    PASTOR,
    LIDER_CELULA,
    SECRETARIO,
    TESOUREIRO;

    public String getRole() {
        return "ROLE_" + this.name();
    }
}
