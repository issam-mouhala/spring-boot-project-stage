package com.bfios.gei.dashboard.model.dto;

import com.bfios.gei.dashboard.model.DemandeGei;
import com.bfios.gei.dashboard.model.DemandeSla;

import java.util.List;

/**
 * Vue SLA d'une demande (TdB3) : une ligne par demande avec son détail SLA par étape.
 */
public class DemandeSlaDto {

    private final DemandeGei demande;
    private final List<DemandeSla> stages;
    private final int total;

    public DemandeSlaDto(DemandeGei demande, List<DemandeSla> stages, int total) {
        this.demande = demande;
        this.stages = stages;
        this.total = total;
    }

    public DemandeGei getDemande() { return demande; }
    public List<DemandeSla> getStages() { return stages; }
    public int getTotal() { return total; }
}
