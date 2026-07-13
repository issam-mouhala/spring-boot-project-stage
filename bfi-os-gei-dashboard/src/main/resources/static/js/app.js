/* ============================================================
   BFI-OS GEI — Pilotage & Suivi de l'activité
   Spring Boot version — frontend JS consomme l'API REST /api/
   ============================================================ */

const API = '/api';

/* -----------------------------------------------------------
   1. UI HELPERS
   ----------------------------------------------------------- */
const $  = (s, c = document) => c.querySelector(s);
const $$ = (s, c = document) => Array.from(c.querySelectorAll(s));

function escapeHtml(str) {
  if (str == null) return '';
  return String(str).replace(/[&<>"']/g, c =>
    ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])
  );
}

function fmtDate(iso) {
  if (!iso) return '';
  const d = new Date(iso);
  return d.toLocaleDateString('fr-FR', { day:'2-digit', month:'short', year:'numeric' });
}

function etatBadgeClass(etat) {
  const map = {
    'EN_CREATION': 'badge-etat-creation',
    'RETOURNEE': 'badge-etat-retournee',
    'EN_COURS_CONTROLE': 'badge-etat-controle',
    'EN_COURS_TRAITEMENT': 'badge-etat-traitement',
    'EN_COURS_VALIDATION': 'badge-etat-validation',
    'EN_ATTENTE_DECISION': 'badge-etat-decision',
    'EN_ATTENTE_VALIDATION_DECISION': 'badge-etat-vdecision',
    'ANNULEE': 'badge-etat-annulee',
    'CLOTUREE': 'badge-etat-cloturee'
  };
  return map[etat] || 'badge-muted';
}

function etatLibelle(etat) {
  const map = {
    'EN_CREATION': 'EN CREATION',
    'RETOURNEE': 'RETOURNÉE',
    'EN_COURS_CONTROLE': 'EN COURS DE CONTRÔLE',
    'EN_COURS_TRAITEMENT': 'EN COURS DE TRAITEMENT',
    'EN_COURS_VALIDATION': 'EN COURS DE VALIDATION',
    'EN_ATTENTE_DECISION': 'EN ATTENTE DE DÉCISION',
    'EN_ATTENTE_VALIDATION_DECISION': 'EN ATTENTE DE VALIDATION DÉCISION',
    'ANNULEE': 'ANNULÉE',
    'CLOTUREE': 'CLÔTURÉE'
  };
  return map[etat] || etat;
}

function etapeLibelle(etape) {
  const map = {
    'CA_AC_INITIATION': 'CA/AC-INITIATION DE LA DEMANDE',
    'CHEF_D_CONTROLE': 'CHEF.D-CONTRÔLE ET VÉRIFICATION',
    'VERIFICATION': 'VÉRIFICATION ET COMPLÉTUDE DE LA DEMANDE',
    'VALIDATION': 'VALIDATION DE LA DEMANDE',
    'CA_DECISION': 'CA-DECISION SUR LA DEMANDE',
    'DMO_VALIDATION': 'DMO-VALIDATION DE LA DÉCISION',
    'HORS_WORKFLOW': 'HORS WORKFLOW'
  };
  return map[etape] || etape;
}

/* -----------------------------------------------------------
   2. FETCH HELPERS — appel API REST
   ----------------------------------------------------------- */
async function apiGet(path, params = {}) {
  // Construction robuste de l'URL — fonctionne en http:// ET en https://
  // (en file:// le fetch échouera naturellement, ce qui est attendu)
  const qs = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => {
    if (v != null && v !== '') qs.set(k, v);
  });
  const queryStr = qs.toString();
  const url = API + path + (queryStr ? '?' + queryStr : '');
  const resp = await fetch(url, { headers: { 'Accept': 'application/json' } });
  if (!resp.ok) throw new Error(`API ${path} → ${resp.status}`);
  return resp.json();
}

/* -----------------------------------------------------------
   3. STATE — filtres courants
   ----------------------------------------------------------- */

// === Mapping période → plage de dates ===
const PERIODS = {
  "Avril 2026":      { start: "2026-04-01", end: "2026-04-30" },
  "Mars 2026":       { start: "2026-03-01", end: "2026-03-31" },
  "Février 2026":    { start: "2026-02-01", end: "2026-02-28" },
  "Q1 2026":         { start: "2026-01-01", end: "2026-03-31" },
  "Année 2026":      { start: "2026-01-01", end: "2026-12-31" }
};
$('#dateDebut').value = '12-04-2026';

const currentFilters = () => ({
  dept: $('#fDept').value || null,
  nat:  $('#fNat').value || null,
  prod: $('#fProd').value || null,
  etat: $('#fEtat').value || null,
  etape: $('#fEtape').value || null,
  canal: $('#fCanal').value || null,
  fastTrack: $('#fFastTrack').value === '' ? null : $('#fFastTrack').value === 'true',
  search: $('#fSearch').value.trim() || null,
  // NOUVEAU : envoyer les dates au backend
  dateDebut: $('#dateDebut').value || null,
  dateFin:   $('#dateFin').value   || null
});
/* -----------------------------------------------------------
   4. POPULATE FILTERS — charge les référentiels au démarrage
   ----------------------------------------------------------- */
async function populateFilters() {
  // Départements
  const depts = await apiGet('/referentiel/departements');
  depts.forEach(d => {
    const opt = document.createElement('option');
    opt.value = d; opt.textContent = d.charAt(0) + d.slice(1).toLowerCase();
    $('#fDept').appendChild(opt);
      // NOUVEAU : listeners pour les date pickers
  $('#dateDebut').addEventListener('change', refreshCurrentView);
  $('#dateFin').addEventListener('change', refreshCurrentView);
  $('#periodReset').addEventListener('click', () => {
    $('#dateDebut').value = '';
    $('#dateFin').value = '';
    refreshCurrentView();  });
  });

  // États
  const etats = await apiGet('/referentiel/etats');
  etats.forEach(e => {
    const opt = document.createElement('option');
    opt.value = e; opt.textContent = etatLibelle(e);
    $('#fEtat').appendChild(opt);
  });

  // Étapes
  const etapes = await apiGet('/referentiel/etapes');
  etapes.forEach(s => {
    const opt = document.createElement('option');
    opt.value = s; opt.textContent = etapeLibelle(s);
    $('#fEtape').appendChild(opt);
  });

  // Cascade : departement → nature → produit
  $('#fDept').addEventListener('change', async () => {
    const dept = $('#fDept').value;
    const fNat = $('#fNat'), fProd = $('#fProd');
    fNat.innerHTML = '<option value="">Tous</option>';
    fProd.innerHTML = '<option value="">Tous</option>';
    if (!dept) { fNat.disabled = true; fProd.disabled = true; }
    else {
      fNat.disabled = false; fProd.disabled = true;
      const nats = await apiGet('/referentiel/natures', { dept });
      nats.forEach(n => {
        const opt = document.createElement('option');
        opt.value = n; opt.textContent = n;
        fNat.appendChild(opt);
      });
    }
    refreshCurrentView();
  });

  $('#fNat').addEventListener('change', async () => {
    const dept = $('#fDept').value, nat = $('#fNat').value;
    const fProd = $('#fProd');
    fProd.innerHTML = '<option value="">Tous</option>';
    if (!dept || !nat) { fProd.disabled = true; }
    else {
      fProd.disabled = false;
      const prods = await apiGet('/referentiel/produits', { dept, nature: nat });
      prods.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p; opt.textContent = p;
        fProd.appendChild(opt);
      });
    }
    refreshCurrentView();
  });

  ['#fProd', '#fEtat', '#fEtape', '#fCanal', '#fFastTrack', '#fSearch'].forEach(s => {
    $(s).addEventListener('input', refreshCurrentView);
    $(s).addEventListener('change', refreshCurrentView);
  });

  $('#resetFilters').addEventListener('click', () => {
    ['#fDept','#fNat','#fProd','#fEtat','#fEtape','#fCanal','#fFastTrack','#fSearch'].forEach(s => {
      $(s).value = '';
      if (s === '#fNat' || s === '#fProd') $(s).disabled = true;
    });
    refreshCurrentView();
  });
}

/* -----------------------------------------------------------
   5. NAVIGATION
   ----------------------------------------------------------- */
const VIEW_META = {
  tdb1: { title: 'TdB1 — Demandes détaillées', sub: 'Pilotage & suivi de l\'activité BFI-OS GEI (détaillé par demande)' },
  tdb2: { title: 'TdB2 — Synthèse',            sub: 'Suivi du stock des demandes par département, par statut, par nature ou par produit' },
  tdb3: { title: 'TdB3 — SLAs',                sub: 'SLAs paramétrés par étape du processus et temps de traitement réel' },
  tdb4: { title: 'TdB4 — CA / Senior Banker',  sub: 'Vue détaillée par demande pour Chargés d\'affaires, Senior banker, Resp MO, DGEI' },
  resp: { title: 'Vision Responsable A',       sub: 'Activité clients et productivité des agents (variations sur période)' },
  coverage: { title: 'Vision consolidée Coverage GE', sub: 'Couverture clients et délais par département' },
  dg: { title: 'Vision consolidée DG',         sub: 'Vue globale pour la Direction Générale' }
};

let currentView = 'tdb1';

function activateView(view) {
  currentView = view;
  $$('.nav-item').forEach(n => n.classList.toggle('active', n.dataset.view === view));
  $$('.view').forEach(v => v.classList.toggle('active', v.id === 'view-' + view));
  const meta = VIEW_META[view] || VIEW_META.tdb1;
  $('#viewTitle').textContent = meta.title;
  $('#viewSub').textContent = meta.sub;

  $('#kpiRow').style.display = (view === 'tdb1' || view === 'tdb4') ? '' : 'none';
  $('#filtersBar').style.display = ['tdb1','tdb2','tdb3','tdb4','resp'].includes(view) ? '' : 'none';

  refreshCurrentView();
}

function refreshCurrentView() {
  switch (currentView) {
    case 'tdb1': loadTdB1(); loadKpis(); break;
    case 'tdb2': loadTdB2(); break;
    case 'tdb3': loadTdB3(); break;
    case 'tdb4': loadTdB4(); loadKpis(); break;
    case 'resp': loadResp(); break;
    case 'coverage': loadCoverage(); break;
    case 'dg': loadDG(); break;
  }
}

/* -----------------------------------------------------------
   6. KPIs
   ----------------------------------------------------------- */
async function loadKpis() {
  try {
    const k = await apiGet('/dashboard/kpis', currentFilters());
    $('#kpiTotal').textContent     = k.total;
    $('#kpiCloturees').textContent = k.cloturees;
    $('#kpiEnCours').textContent   = k.enCours;
    $('#kpiAnnulees').textContent  = k.annulees;
    $('#kpiSla').textContent       = k.slaPct + '%';
    $('#kpiDelai').textContent     = k.delaiMoyen + ' j';
  } catch (e) { console.error('KPIs:', e); }
}

/* -----------------------------------------------------------
   7. TdB1 — Liste détaillée
   ----------------------------------------------------------- */
async function loadTdB1() {
  const tbody = $('#tdb1Table tbody');
  try {
    const data = await apiGet('/dashboard/demandes', currentFilters());
    $('#tdb1Count').textContent = data.length + ' demande(s)';
    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="14" style="text-align:center;padding:30px;color:var(--c-text-3);">Aucune demande ne correspond aux filtres</td></tr>`;
      return;
    }
    const totalMontant = data.reduce((sum, r) => sum + r.montant, 0);

    $('#kpiMontant').textContent   = totalMontant + ' DH'
        tbody.innerHTML = data.map(r => `
      <tr>
        <td class="num">${escapeHtml(r.numDemande)}</td>
        <td>${escapeHtml(r.agent)}</td>
        <td>${escapeHtml(r.departement)}</td>
        <td>${escapeHtml(r.natureOperation)}</td>
        <td>${escapeHtml(r.produitService)}</td>
        <td class="num">${escapeHtml(r.radicalClient)}</td>
        <td>${escapeHtml(r.intituleClient)}</td>
        <td><span class="badge ${r.fastTrack ? 'badge-fast-yes' : 'badge-fast-no'}">${r.fastTrack ? 'OUI' : 'NON'}</span></td>
        <td>${fmtDate(r.dateDemande)}</td>
        <td><span class="badge ${etatBadgeClass(r.etat)}">${etatLibelle(r.etat)}</span></td>
        <td>${escapeHtml(etapeLibelle(r.etape))}</td>
        <td class="num">${r.nbIterations}</td>
        <td><span class="badge ${r.canal === 'DIGITAL' ? 'badge-canal-digital' : 'badge-canal-physique'}">${r.canal}</span></td>
        <td class="num">${r.delaiTraitement} j</td>
        <td class="num">${r.montant} DH</td>
        <td>${r.slaOk
          ? '<span class="badge" style="background:var(--c-green-soft);color:var(--c-green)">Dans les délais</span>'
          : '<span class="badge" style="background:var(--c-red-soft);color:var(--c-red)">Hors délais</span>'}</td>
      </tr>
    `).join('');
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="14" style="color:var(--c-red)">Erreur API : ${e.message}</td></tr>`;
  }
}

/* -----------------------------------------------------------
   8. TdB2 — Synthèse
   ----------------------------------------------------------- */
let chartEtat,chartSla, chartDept;
async function loadTdB2() {
  const dim = document.querySelector('input[name="tdb2Dim"]:checked').value;
  const data = await apiGet('/dashboard/synthese', { ...currentFilters(), dim });
  const tbody = $('#tdb2Table tbody');
  let grandTotal = 0;
  let totalEnCreation = 0;
  let totalRetournee = 0;
  let totalEnCoursControle = 0;
  let totalEnCoursTraitement = 0;
  let totalEnCoursValidation = 0;
  let totalEnAttenteDecision = 0;
  let totalEnAttenteValidationDecision = 0;
  let totalAnnulee = 0;
  let totalCloturee = 0;
  
  const tdb2_content = data.map(g => {
  
      grandTotal += Number(g.total || 0);
      totalEnCreation += Number(g.enCreation || 0);
      totalRetournee += Number(g.retournee || 0);
      totalEnCoursControle += Number(g.enCoursControle || 0);
      totalEnCoursTraitement += Number(g.enCoursTraitement || 0);
      totalEnCoursValidation += Number(g.enCoursValidation || 0);
      totalEnAttenteDecision += Number(g.enAttenteDecision || 0);
      totalEnAttenteValidationDecision += Number(g.enAttenteValidationDecision || 0);
      totalAnnulee += Number(g.annulee || 0);
      totalCloturee += Number(g.cloturee || 0);
  
      return `
          <tr>
              <td><strong>${escapeHtml(g.dimension)}</strong></td>
              <td class="num">${g.total}</td>
              <td class="num">${g.enCreation}</td>
              <td class="num">${g.retournee}</td>
              <td class="num">${g.enCoursControle}</td>
              <td class="num">${g.enCoursTraitement}</td>
              <td class="num">${g.enCoursValidation}</td>
              <td class="num">${g.enAttenteDecision}</td>
              <td class="num">${g.enAttenteValidationDecision}</td>
              <td class="num">${g.annulee}</td>
              <td class="num">${g.cloturee}</td>
          </tr>
      `;
  }).join("");
  
  tbody.innerHTML = tdb2_content 
  $('tfoot').innerHTML= `
  <tr>
      <td><strong class="totale">TOTAL GLOBALE</strong></td>
      <td class="num"><strong>${grandTotal}</strong></td>
      <td class="num"><strong>${totalEnCreation}</strong></td>
      <td class="num"><strong>${totalRetournee}</strong></td>
      <td class="num"><strong>${totalEnCoursControle}</strong></td>
      <td class="num"><strong>${totalEnCoursTraitement}</strong></td>
      <td class="num"><strong>${totalEnCoursValidation}</strong></td>
      <td class="num"><strong>${totalEnAttenteDecision}</strong></td>
      <td class="num"><strong>${totalEnAttenteValidationDecision}</strong></td>
      <td class="num"><strong>${totalAnnulee}</strong></td>
      <td class="num"><strong>${totalCloturee}</strong></td>
  </tr>
  `;
  $('#tdb2Total') && ($('#tdb2Total').textContent = grandTotal);

  // Charts — recharger les données pour les graphiques
  const allData = await apiGet('/dashboard/demandes', currentFilters());
  renderChartEtat(allData);
  renderChartDept(allData);
  renderTdB2AgentTable()
}
async function renderTdB2AgentTable() {
  // Filtrer sur Département Domestique
  const dim = document.querySelector('input[name="tdb2Dim"]:checked').value;

  const data = await apiGet('/dashboard/demandes', { ...currentFilters(), dim });
 const domestique = data
 $('#dep').textContent= ($('#fDept').value || null)
  const agents = [...new Set(domestique.map(r => r.agent).filter(Boolean))].sort();

  const tbody = $('#tdb2AgentTable tbody');
  tbody.innerHTML = '';

  // Totaux cumulés
  const totals = {
    total: 0, creation: 0, retour: 0, controle: 0, traitement: 0,
    validation: 0, decision: 0, vdecision: 0, annulee: 0, cloturee: 0
  };
  let allDelays = [];

  agents.forEach(agent => {
    const items = domestique.filter(r => r.agent === agent);
    const total = items.length;
    const count = (e) => items.filter(r => r.etat === e).length;

    const cCreation  = count("EN_CREATION");
    const cRetour    = count("RETOURNEE");
    const cControle  = count("EN_COURS_CONTROLE");
    const cTraitement = count("EN_COURS_TRAITEMENT");
    const cValidation = count("EN_COURS_VALIDATION");
    const cDecision  = count("EN_ATTENTE_DECISION");
    const cVDecision = count("EN_ATTENTE_VALIDATION_DECISION");
    const cAnnulee   = count("ANNULEE");
    const cCloturee  = count("CLOTUREE");

    totals.total      += total;
    totals.creation   += cCreation;
    totals.retour     += cRetour;
    totals.controle   += cControle;
    totals.traitement += cTraitement;
    totals.validation += cValidation;
    totals.decision   += cDecision;
    totals.vdecision  += cVDecision;
    totals.annulee    += cAnnulee;
    totals.cloturee   += cCloturee;

    // Calcul des délais
    const delays = items.map(r => r.delaiTraitement || 0).filter(d => d > 0);
    console.log(delays)
    const dMoy = delays.length ? (delays.reduce((a, b) => a + b, 0) / delays.length).toFixed(1) : "—";
    const dMax = delays.length ? Math.max(...delays) : "—";
    const dMin = delays.length ? Math.min(...delays) : "—";
    if (delays.length) allDelays = allDelays.concat(delays);

    // Couleur délai moyen (SLA = 2 jours)
    const dMoyNum = parseFloat(dMoy);
    const delaiCls = isNaN(dMoyNum) ? "" : (dMoyNum <= 2 ? "delai-ok" : "delai-ko");

    tbody.insertAdjacentHTML("beforeend", `
      <tr>
      <td><strong>${escapeHtml(agent[0].toUpperCase() + agent.slice(1))}</strong></td>
      <td>${total}</td>
        <td>${cCreation}</td>
        <td>${cRetour}</td>
        <td>${cControle}</td>
        <td>${cTraitement}</td>
        <td>${cValidation}</td>
        <td>${cDecision}</td>
        <td>${cVDecision}</td>
        <td>${cAnnulee}</td>
        <td>${cCloturee}</td>
        <td class="col-delai ${delaiCls}">${dMoy}${isNaN(dMoyNum) ? "" : " j"}</td>
        <td class="col-delai">${dMax}${dMax === "—" ? "" : " j"}</td>
        <td class="col-delai">${dMin}${dMin === "—" ? "" : " j"}</td>
      </tr>
    `);
  });

  // Ligne TOTAL GLOBALE
  $('#tdb2AgentCount').textContent = agents.length + " agent(s)";
  $('#tdb2AgentTotal').innerHTML = `<strong>${totals.total}</strong>`;
  $('#tdb2AgTotCreation').textContent   = totals.creation;
  $('#tdb2AgTotRetour').textContent     = totals.retour;
  $('#tdb2AgTotControle').textContent   = totals.controle;
  $('#tdb2AgTotTraitement').textContent = totals.traitement;
  $('#tdb2AgTotValidation').textContent = totals.validation;
  $('#tdb2AgTotDecision').textContent   = totals.decision;
  $('#tdb2AgTotVDecision').textContent  = totals.vdecision;
  $('#tdb2AgTotAnnulee').textContent    = totals.annulee;
  $('#tdb2AgTotCloturee').textContent   = totals.cloturee;

  if (allDelays.length) {
    const moy = (allDelays.reduce((a, b) => a + b, 0) / allDelays.length).toFixed(1);
    const max = Math.max(...allDelays);
    const min = Math.min(...allDelays);
    const moyNum = parseFloat(moy);
    const cls = moyNum <= 2 ? "delai-ok" : "delai-ko";
    $('#tdb2AgentDelaiMoy').innerHTML = `<span class="${cls}">${moy} j</span>`;
    $('#tdb2AgentDelaiMax').textContent = max + " j";
    $('#tdb2AgentDelaiMin').textContent = min + " j";
  } else {
    $('#tdb2AgentDelaiMoy').textContent = "—";
    $('#tdb2AgentDelaiMax').textContent = "—";
    $('#tdb2AgentDelaiMin').textContent = "—";
  }
}
function renderChartEtat(data) {
  const counts = {};
  data.forEach(r => { counts[r.etat] = (counts[r.etat] || 0) + 1; });
  const labels = Object.keys(counts).map(etatLibelle);
  const values = Object.values(counts);
  const colors = ["#1976d2","#f59f00","#6b46a1","#1976d2","#6b46a1","#f59f00","#f59f00","#c62828","#2e7d32"];
  if (chartEtat) chartEtat.destroy();
  chartEtat = new Chart($('#chartEtat'), {
    type: "doughnut",
    data: { labels, datasets: [{ data: values, backgroundColor: colors, borderWidth: 0 }] },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { position: "right", labels: { boxWidth: 10, font: { size: 11 } } } }
    }
  });
}
function renderChartSla(data) {

  const counts = {
      "Dans SLA": 0,
      "Hors SLA": 0
  };

  data.forEach(r => {
      if (r.slaOk) {
          counts["Dans SLA"]++;
      } else {
          counts["Hors SLA"]++;
      }
  });

  const labels = Object.keys(counts);
  const values = Object.values(counts);

  const colors = [
      "#2e7d32", // Vert = Dans SLA
      "#c62828"  // Rouge = Hors SLA
  ];

  if (chartSla) chartSla.destroy();

  chartSla = new Chart($("#chartSla"), {
      type: "doughnut",
      data: {
          labels: labels,
          datasets: [{
              data: values,
              backgroundColor: colors,
              borderWidth: 0
          }]
      },
      options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
              legend: {
                  position: "right",
                  labels: {
                      boxWidth: 12,
                      font: {
                          size: 11
                      }
                  }
              }
          }
      }
  });
}

function renderChartDept(data) {
  const counts = {};
  data.forEach(r => { counts[r.departement] = (counts[r.departement] || 0) + 1; });
  const labels = Object.keys(counts);
  const values = Object.values(counts);
  if (chartDept) chartDept.destroy();
  chartDept = new Chart($('#chartDept'), {
    type: "bar",
    data: { labels, datasets: [{ label: "Demandes", data: values,
      backgroundColor: ["#0b3b6f","#1d5fa8","#c9a227","#6b46a1"], borderRadius: 4 }] },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, grid: { color: "rgba(0,0,0,.05)" } }, x: { grid: { display: false } } }
    }
  });
}

/* -----------------------------------------------------------
   9. TdB3 — SLAs
   ----------------------------------------------------------- */
async function loadTdB3() {
  const tbody = $('#tdb3Table tbody');
  try {
    const data = await apiGet('/dashboard/sla', currentFilters());
    const allData = await apiGet('/dashboard/demandes', currentFilters());
    renderChartSla(allData);

    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="22" style="text-align:center;padding:30px;color:var(--c-text-3);">Aucune donnée</td></tr>`;
      return;
    }
    tbody.innerHTML = data.map(r => {
      const cells = r.stages.map(s => {
        if (s.realise == null) return `<td class="sla-na">—</td><td class="sla-na">—</td><td class="sla-na">—</td>`;
        const cls = s.ecart > 0 ? 'sla-ko' : 'sla-ok';
        return `<td>${s.sla}</td><td>${s.realise}</td><td class="${cls}">${s.ecart > 0 ? '+' + s.ecart : s.ecart}</td>`;
      }).join('');
      const d = r.demande;
      return `
        <tr>
          <td>${escapeHtml(d.agent)}</td>
          <td class="num">${escapeHtml(d.numDemande)}</td>
          <td>${escapeHtml(d.intituleClient)}</td>
          <td class="num">${escapeHtml(d.radicalClient)}</td>
          <td>${escapeHtml(etapeLibelle(d.etape))}</td>
          ${cells}
          <td class="num" style="font-weight:700;">${r.total}</td>
        </tr>
      `;
    }).join('');
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="22" style="color:var(--c-red)">Erreur : ${e.message}</td></tr>`;
  }
}

/* -----------------------------------------------------------
   10. TdB4 — CA / Senior Banker
   ----------------------------------------------------------- */
async function loadTdB4() {
  const tbody = $('#tdb4Table tbody');
  try {
    const data = await apiGet('/dashboard/demandes-ca', currentFilters());
    $('#tdb4Count').textContent = data.length;
    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="17" style="text-align:center;padding:30px;color:var(--c-text-3);">Aucune demande</td></tr>`;
      return;
    }
    tbody.innerHTML = data.map(r => `
      <tr>
        <td class="num">${escapeHtml(r.numDemande)}</td>
        <td>${escapeHtml(r.departement)}</td>
        <td>${escapeHtml(r.natureOperation)}</td>
        <td>${escapeHtml(r.produitService)}</td>
        <td>${escapeHtml(r.chargeAffaires || '')}</td>
        <td class="num">${escapeHtml(r.radicalClient)}</td>
        <td>${escapeHtml(r.intituleClient)}</td>
        <td><span class="badge ${r.fastTrack ? 'badge-fast-yes' : 'badge-fast-no'}">${r.fastTrack ? 'OUI' : 'NON'}</span></td>
        <td>${fmtDate(r.dateDemande)}</td>
        <td><span class="badge ${etatBadgeClass(r.etat)}">${etatLibelle(r.etat)}</span></td>
        <td>${escapeHtml(etapeLibelle(r.etape))}</td>
        <td class="num">${r.nbIterations}</td>
        <td><span class="badge ${r.canal === 'DIGITAL' ? 'badge-canal-digital' : 'badge-canal-physique'}">${r.canal}</span></td>
        <td>${escapeHtml(r.chargeAffaires || '—')}</td>
        <td>${escapeHtml(r.agent)}</td>
        <td class="num">${r.delaiTraitement} j</td>
        <td>${r.slaOk
          ? '<span class="badge" style="background:var(--c-green-soft);color:var(--c-green)">Dans délais</span>'
          : '<span class="badge" style="background:var(--c-red-soft);color:var(--c-red)">Hors délais</span>'}</td>
      </tr>
    `).join('');
  } catch (e) {
    tbody.innerHTML = `<tr><td colspan="17" style="color:var(--c-red)">Erreur : ${e.message}</td></tr>`;
  }
}

/* -----------------------------------------------------------
   11. Vision Responsable A
   ----------------------------------------------------------- */
let chartAgents, chartClients, chartDelaiGlobal;
async function loadResp() {
  const [productivite, topClients] = await Promise.all([
    apiGet('/dashboard/responsable-a/productivite', currentFilters()),
    apiGet('/dashboard/responsable-a/top-clients', currentFilters())
  ]);

  const tbody = $('#respTable tbody');
  tbody.innerHTML = productivite.map(p => `
    <tr>
      <td><strong>${escapeHtml(p.agent)}</strong></td>
      <td class="num">${p.mois1.toFixed(2)}</td>
      <td class="num">${p.mois2.toFixed(2)}</td>
      <td class="num trend-${p.tendance}">${p.variation >= 0 ? '+' : ''}${p.variation.toFixed(2)} (${p.variationPct.toFixed(0)}%)</td>
      <td><span class="trend-${p.tendance}">${p.tendance === 'up' ? '▲' : '▼'}</span></td>
    </tr>
  `).join('');

  if (chartAgents) chartAgents.destroy();
  chartAgents = new Chart($('#chartAgents'), {
    type: "bar",
    data: { labels: productivite.map(p => p.agent),
      datasets: [
        { label: "Mois 1", data: productivite.map(p => p.mois1), backgroundColor: "#1d5fa8", borderRadius: 4 },
        { label: "Mois 2", data: productivite.map(p => p.mois2), backgroundColor: "#c9a227", borderRadius: 4 }
      ]},
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { position: "top", labels: { font: { size: 11 } } } },
      scales: { y: { beginAtZero: true, grid: { color: "rgba(0,0,0,.05)" } }, x: { grid: { display: false } } }
    }
  });

  const topLabels = Object.keys(topClients);
  const topValues = Object.values(topClients);
  if (chartClients) chartClients.destroy();
  chartClients = new Chart($('#chartClients'), {
    type: "bar",
    data: { labels: topLabels, datasets: [{ label: "Demandes", data: topValues, backgroundColor: "#0b3b6f", borderRadius: 4 }] },
    options: {
      indexAxis: "y", responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { x: { beginAtZero: true, grid: { color: "rgba(0,0,0,.05)" } }, y: { grid: { display: false } } }
    }
  });

  const months = ["Jan","Fév","Mar","Avr","Mai","Juin"];
  const delais = [4.2, 4.0, 3.8, 3.5, 3.3, 3.2];
  if (chartDelaiGlobal) chartDelaiGlobal.destroy();
  chartDelaiGlobal = new Chart($('#chartDelaiGlobal'), {
    type: "line",
    data: { labels: productivite.map(p => p.agent),
      datasets: [
        { label: "Mois 1", data: productivite.map(p => p.mois1), backgroundColor: "#1d5fa8", borderRadius: 4 },
        { label: "Mois 2", data: productivite.map(p => p.mois2), backgroundColor: "#c9a227", borderRadius: 4 }
      ]},
    options: { responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: false, grid: { color: "rgba(0,0,0,.05)" } }, x: { grid: { display: false } } } }
  });
}

/* -----------------------------------------------------------
   12. Vision Coverage GE
   ----------------------------------------------------------- */
let chartCoverage;
async function loadCoverage() {
  const data = await apiGet('/dashboard/demandes', {});
  const depts = [...new Set(data.map(r => r.departement))];
  const coverage = depts.map(d => 70 + Math.round(Math.random() * 25));
  const delais = depts.map(d => {
    const subset = data.filter(r => r.departement === d);
    return subset.length ? +(subset.reduce((a,r) => a + r.delaiTraitement, 0)/subset.length).toFixed(1) : 0;
  });
  if (chartCoverage) chartCoverage.destroy();
  chartCoverage = new Chart($('#chartCoverage'), {
    type: "bar",
    data: { labels: depts, datasets: [
      { label: "Couverture (%)", data: coverage, backgroundColor: "#1d5fa8", borderRadius: 4, yAxisID: "y" },
      { label: "Délai moyen (j)", data: delais, backgroundColor: "#c9a227", borderRadius: 4, yAxisID: "y1" }
    ]},
    options: { responsive: true, maintainAspectRatio: false,
      plugins: { legend: { position: "top", labels: { font: { size: 11 } } } },
      scales: {
        y:  { type: "linear", position: "left",  beginAtZero: true, max: 100, grid: { color: "rgba(0,0,0,.05)" }, title: { display: true, text: "Couverture (%)" } },
        y1: { type: "linear", position: "right", beginAtZero: true, grid: { drawOnChartArea: false }, title: { display: true, text: "Délai (j)" } },
        x:  { grid: { display: false } }
      }
    }
  });
}

/* -----------------------------------------------------------
   13. Vision DG
   ----------------------------------------------------------- */
let chartDgVolume, chartDgSla;
async function loadDG() {
  const summary = await apiGet('/dashboard/dg/summary', {});
  const k = summary.kpis;
  const clotPct = k.total > 0 ? Math.round((k.cloturees / k.total) * 100) : 0;
  $('#dgTotal').textContent   = k.total;
  $('#dgCloture').textContent = clotPct + '%';
  $('#dgSla').textContent     = k.slaPct + '%';
  $('#dgDelai').textContent   = k.delaiMoyen + ' j';

  const months = ["Jan","Fév","Mar","Avr","Mai","Juin"];
  const vols = [120, 145, 158, 167, 178, k.total || 180];
  if (chartDgVolume) chartDgVolume.destroy();
  chartDgVolume = new Chart($('#chartDgVolume'), {
    type: "line",
    data: { labels: months, datasets: [{ label: "Volume", data: vols,
      borderColor: "#0b3b6f", backgroundColor: "rgba(11,59,111,.1)", fill: true, tension: .35 }] },
    options: { responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: false, grid: { color: "rgba(0,0,0,.05)" } }, x: { grid: { display: false } } } }
  });

  const {slaByDept} = summary;
  const labels = Object.keys(slaByDept);
  const values = Object.values(slaByDept);
  if (chartDgSla) chartDgSla.destroy();
  chartDgSla = new Chart($('#chartDgSla'), {
    type: "bar",
    data: { labels, datasets: [{ label: "Respect SLA (%)", data: values,
      backgroundColor: ["#2e7d32","#1976d2","#c9a227","#6b46a1"], borderRadius: 4 }] },
    options: { responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: { y: { beginAtZero: true, max: 100, grid: { color: "rgba(0,0,0,.05)" } }, x: { grid: { display: false } } } }
  });

  const tbody = $('#dgTopTable tbody');
  tbody.innerHTML = summary.topProduits.map(t => `
    <tr>
      <td><strong>${escapeHtml(t.produit)}</strong></td>
      <td class="num">${t.volume}</td>
      <td class="num">${t.delaiMoyen} j</td>
      <td>${t.slaPct >= 80
        ? '<span class="badge" style="background:var(--c-green-soft);color:var(--c-green)">'+t.slaPct+'%</span>'
        : '<span class="badge" style="background:var(--c-amber-soft);color:var(--c-amber)">'+t.slaPct+'%</span>'}</td>
      <td>
        <div class="bar-mini">
          <div class="bar-mini-fill" style="width:${t.partVolumePct}%"></div>
          <span>${t.partVolumePct}%</span>
        </div>
      </td>
    </tr>
  `).join('');
}

/* -----------------------------------------------------------
   14. CSV EXPORT — TdB1
   ----------------------------------------------------------- */
$('#exportCsvTdb1') && $('#exportCsvTdb1').addEventListener('click', async () => {
  const data = await apiGet('/dashboard/demandes', currentFilters());
  const headers = ['N° demande','Département','Nature','Produit','Radical','Client','Fast Track','Date','État','Étape','Itér','Canal','Délai','Analyse'];
  const lines = [headers.join(';')];
  data.forEach(r => {
    lines.push([r.numDemande, r.departement, r.natureOperation, r.produitService,
                r.radicalClient, r.intituleClient, r.fastTrack ? 'OUI' : 'NON',
                r.dateDemande, r.etat, r.etape, r.nbIterations, r.canal,
                r.delaiTraitement, r.slaOk ? 'Dans délais' : 'Hors délais'].join(';'));
  });
  const csv = '\uFEFF' + lines.join('\n');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url; a.download = 'TdB1_demandes.csv';
  document.body.appendChild(a); a.click(); a.remove();
  URL.revokeObjectURL(url);
});

/* -----------------------------------------------------------
   15. NAV SETUP
   ----------------------------------------------------------- */
function setupNav() {
  // Pré-remplir avec le mois courant
const today = new Date();
const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
 $('#dateDebut').value = firstDay.toISOString().split('T')[0];
 $('#dateFin').value = today.toISOString().split('T')[0];
  $$('.nav-item').forEach(n => n.addEventListener('click', e => {
    e.preventDefault();
    activateView(n.dataset.view);
  }));
  $('#btnFilters').addEventListener('click', () => $('.sidebar').classList.toggle('open'));
  $('#btnRefresh').addEventListener('click', refreshCurrentView);
  $$('input[name="tdb2Dim"]').forEach(r =>
    r.addEventListener('change', () => currentView === 'tdb2' && loadTdB2()));
    $$('.nav-item').forEach(n => n.addEventListener('click', e => {
      e.preventDefault();
      activateView(n.dataset.view);
    }));
  
    // ====== NOUVEAU : Toggle sidebar ======
    $('#btnToggleSidebar').addEventListener('click', () => {
      document.body.classList.toggle('sidebar-collapsed');
      document.querySelector('.app').classList.toggle('sidebar-collapsed');
      // Mémoriser l'état dans localStorage
      const collapsed = document.querySelector('.app').classList.contains('sidebar-collapsed');
      localStorage.setItem('sidebar-collapsed', collapsed);
    });
  
    // Restaurer l'état au chargement
    if (localStorage.getItem('sidebar-collapsed') === 'true') {
      document.body.classList.add('sidebar-collapsed');
      document.querySelector('.app').classList.add('sidebar-collapsed');
    }
  
    // Sur mobile : fermer la sidebar en cliquant sur un lien
    $$('.nav-item').forEach(n => n.addEventListener('click', () => {
      if (window.innerWidth <= 960) {
        document.querySelector('.app').classList.remove('sidebar-collapsed');
        document.body.classList.remove('sidebar-collapsed');
      }
    }));
  
    $('#btnFilters').addEventListener('click', () => $('.sidebar').classList.toggle('open'));
    $('#btnRefresh').addEventListener('click', refreshCurrentView);
    $$('input[name="tdb2Dim"]').forEach(r =>
      r.addEventListener('change', () => currentView === 'tdb2' && loadTdB2()));
}

/* -----------------------------------------------------------
   16. INIT
   ----------------------------------------------------------- */
document.addEventListener('DOMContentLoaded', async () => {
  try {
    await populateFilters();
    setupNav();
    refreshCurrentView();
  } catch (e) {
    console.error('Init error:', e);
    document.body.insertAdjacentHTML('afterbegin',
      `<div style="background:#c62828;color:#fff;padding:12px;font-family:monospace">
        Erreur d'initialisation — vérifiez que l'API Spring Boot est démarrée sur ${API}<br>${e.message}
      </div>`);
  }
});
