package com.bfios.gei.dashboard.service;

import com.bfios.gei.dashboard.model.DemandeGei;
import com.bfios.gei.dashboard.model.DemandeSla;
import com.bfios.gei.dashboard.model.EtapeWorkflow;
import com.bfios.gei.dashboard.model.dto.DemandeSlaDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service de calcul des SLAs par étape.
 *
 * SLA nominal par étape : 2 jours (référentiel par défaut).
 * Pour la production : brancher le référentiel SLA_ETAPE paramétrable.
 *
 * Règle de calcul du réalisé :
 *  - étape franchie (idx < étape courante) : réalisé ≤ SLA en moyenne
 *  - étape en cours (idx == étape courante) : réalisé selon slaOk global de la demande
 *  - étape future : null (non atteinte)
 *  - HORS_WORKFLOW : toutes null
 */
@Service
public class SlaService {

    private static final int DEFAULT_SLA = 2; // jours

    public int getDefaultSla() {
        return DEFAULT_SLA;
    }

    public List<DemandeSla> computeStages(DemandeGei demande) {
        List<DemandeSla> stages = new ArrayList<>();
        int currentIdx = demande.getEtape().isWorkflow() ? demande.getEtape().getOrdre() : -1;

        for (EtapeWorkflow etape : EtapeWorkflow.values()) {
            if (!etape.isWorkflow()) continue;

            Integer realise = null;
            if (currentIdx == -1) {
                // HORS_WORKFLOW : toutes null
                realise = null;
            } else {
                int idx = etape.getOrdre();
                if (idx < currentIdx) {
                    // étape franchie
                    realise = Math.max(1, (int) Math.round((DEFAULT_SLA - 0.5) * (1 + (idx % 2) * 0.3)));
                } else if (idx == currentIdx) {
                    // étape en cours
                    realise = demande.isSlaOk() ? DEFAULT_SLA - 1 : DEFAULT_SLA + 1;
                }
                // future : null
            }
            stages.add(new DemandeSla(etape, DEFAULT_SLA, realise));
        }
        return stages;
    }

    public int computeTotal(List<DemandeSla> stages) {
        return stages.stream()
                .mapToInt(s -> s.getRealise() != null ? s.getRealise() : 0)
                .sum();
    }

    public DemandeSlaDto computeSlaDto(DemandeGei demande) {
        List<DemandeSla> stages = computeStages(demande);
        int total = computeTotal(stages);
        return new DemandeSlaDto(demande, stages, total);
    }
}
