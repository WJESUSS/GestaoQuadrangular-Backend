package com.gestaoigrejaemcelula.demo.domain.enums;

public enum Perfil {

    ADMIN,
    PASTOR,
    LIDER_CELULA,
    SUPERINTENDENTE,
    SECRETARIO,
    TESOUREIRO;

    public String getRole() {
        return "ROLE_" + this.name();
    }
}
