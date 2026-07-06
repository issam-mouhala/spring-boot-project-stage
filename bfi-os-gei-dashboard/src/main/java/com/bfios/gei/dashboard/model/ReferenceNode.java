package com.bfios.gei.dashboard.model;

import java.util.List;

/**
 * Noeud de l'arbre référentiel : Département → Nature → Produit.
 */
public class ReferenceNode {

    private final String code;
    private final String libelle;
    private final List<String> enfants;

    public ReferenceNode(String code, String libelle, List<String> enfants) {
        this.code = code;
        this.libelle = libelle;
        this.enfants = enfants;
    }

    public String getCode() { return code; }
    public String getLibelle() { return libelle; }
    public List<String> getEnfants() { return enfants; }
}
