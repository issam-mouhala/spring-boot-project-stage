package com.bfios.gei.dashboard.model;

/**
 * Étapes du workflow OnBase GEI.
 * 7 valeurs — 6 étapes actives + HORS_WORKFLOW.
 */
public enum EtapeWorkflow {
    CA_AC_INITIATION("CA/AC-INITIATION DE LA DEMANDE", 0),
    CHEF_D_CONTROLE("CHEF.D-CONTRÔLE ET VÉRIFICATION", 1),
    VERIFICATION("VÉRIFICATION ET COMPLÉTUDE DE LA DEMANDE", 2),
    VALIDATION("VALIDATION DE LA DEMANDE", 3),
    CA_DECISION("CA-DECISION SUR LA DEMANDE", 4),
    DMO_VALIDATION("DMO-VALIDATION DE LA DÉCISION", 5),
    HORS_WORKFLOW("HORS WORKFLOW", -1);

    private final String libelle;
    private final int ordre;

    EtapeWorkflow(String libelle, int ordre) {
        this.libelle = libelle;
        this.ordre = ordre;
    }

    public String getLibelle() {
        return libelle;
    }

    public int getOrdre() {
        return ordre;
    }

    /** Indique si cette étape correspond à une étape active du workflow (hors HORS_WORKFLOW). */
    public boolean isWorkflow() {
        return ordre >= 0;
    }
}
