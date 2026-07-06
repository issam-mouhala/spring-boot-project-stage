package com.bfios.gei.dashboard.model;

/**
 * Détail SLA d'une demande pour une étape donnée.
 * Triplet (SLA théorique, délai réalisé, écart) + statut.
 */
public class DemandeSla {

    private final EtapeWorkflow etape;
    private final Integer sla;          // en jours (null si étape non applicable)
    private final Integer realise;      // en jours (null si étape non atteinte)
    private final Integer ecart;        // realise - sla (null si non applicable)

    public DemandeSla(EtapeWorkflow etape, Integer sla, Integer realise) {
        this.etape = etape;
        this.sla = sla;
        this.realise = realise;
        this.ecart = (sla != null && realise != null) ? realise - sla : null;
    }

    public EtapeWorkflow getEtape() { return etape; }
    public Integer getSla() { return sla; }
    public Integer getRealise() { return realise; }
    public Integer getEcart() { return ecart; }

    /** true si le SLA est respecté (écart ≤ 0), false si dépassé, null si non applicable. */
    public Boolean isOk() {
        return ecart != null ? ecart <= 0 : null;
    }
}
