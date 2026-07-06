package com.bfios.gei.dashboard.model;

/**
 * Départements opérationnels BFI-OS GEI.
 */
public enum Departement {
    DOMESTIQUE("Domestique"),
    CREDITS("Crédits"),
    ADMINISTRATIF("Administratif"),
    TRADE("Trade");

    private final String libelle;

    Departement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
