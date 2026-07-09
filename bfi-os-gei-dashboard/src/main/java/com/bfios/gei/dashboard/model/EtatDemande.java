package com.bfios.gei.dashboard.model;

/**
 * États possibles d'une demande GEI dans son cycle de vie OnBase.
 * 9 valeurs, dans l'ordre logique du workflow.
 */
public enum EtatDemande {
    EN_CREATION("EN CREATION", "creation"),
    RETOURNEE("RETOURNÉE", "retournee"),
    EN_COURS_CONTROLE("EN COURS DE CONTRÔLE", "controle"),
    EN_COURS_TRAITEMENT("EN COURS DE TRAITEMENT", "traitement"),
    EN_COURS_VALIDATION("EN COURS DE VALIDATION", "validation"),
    EN_ATTENTE_DECISION("EN ATTENTE DE DÉCISION", "decision"),
    EN_ATTENTE_VALIDATION_DECISION("EN ATTENTE DE VALIDATION DÉCISION", "vdecision"),
    ANNULEE("ANNULÉE", "annulee"),
    CLOTUREE("CLÔTURÉE", "cloturee");

    private final String libelle;
    private final String badgeKey;

    EtatDemande(String libelle, String badgeKey) {
        this.libelle = libelle;
        this.badgeKey = badgeKey;
    }

    
    
        public String getLibelle() {
        return libelle;
    }

    /** Clé CSS pour le badge (ex. "cloturee" → badge-etat-cloturee). */
    public String getBadgeKey() {
        return badgeKey;
    }

    public boolean isCloturee() {
        return this == CLOTUREE;
    }

    public boolean isAnnulee() {
        return this == ANNULEE;
    }

    public boolean isEnCours() {
        return !isCloturee() && !isAnnulee();
    }
}
