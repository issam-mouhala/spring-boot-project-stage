# BFI-OS GEI Dashboard — Spring Boot Web

Application web Spring Boot servant les **tableaux de bord de pilotage & suivi de l'activité BFI-OS GEI**.

## Stack technique

| Couche | Techno |
|--------|--------|
| Backend | **Spring Boot 3.3.5** + Java 17 (compatible Java 21) |
| Build | Maven |
| API | REST + Jackson (JSON) |
| Frontend | HTML5 + CSS3 + JavaScript ES6 (Chart.js 4.x via CDN) |
| Données | Repository in-memory (exemple) — prêt pour branchement JPA/PostgreSQL |

## Structure du projet

```
bfi-os-gei-dashboard/
├── pom.xml                                    # Dependencies Maven
├── README.md
└── src/
    ├── main/
    │   ├── java/com/bfios/gei/dashboard/
    │   │   ├── DashboardApplication.java       # Point d'entrée Spring Boot
    │   │   ├── config/
    │   │   │   └── WebConfig.java              # CORS
    │   │   ├── controller/
    │   │   │   ├── PageController.java         # GET / → index.html
    │   │   │   ├── ReferentielApiController.java  # /api/referentiel/*
    │   │   │   └── DashboardApiController.java    # /api/dashboard/*
    │   │   ├── model/                          # Entités + enums
    │   │   │   ├── DemandeGei.java
    │   │   │   ├── DemandeSla.java
    │   │   │   ├── Departement.java (enum)
    │   │   │   ├── EtatDemande.java (enum)
    │   │   │   ├── EtapeWorkflow.java (enum)
    │   │   │   ├── Canal.java (enum)
    │   │   │   ├── ReferenceNode.java
    │   │   │   └── dto/                        # Data Transfer Objects
    │   │   ├── repository/
    │   │   │   └── InMemoryDemandeRepository.java  # Données d'exemple (22 demandes)
    │   │   └── service/
    │   │       ├── DashboardService.java        # Logique métier (filtres, KPIs, SLA…)
    │   │       ├── ReferenceDataService.java    # Arbre Dépt → Nature → Produit
    │   │       └── SlaService.java              # Calcul SLA par étape
    │   └── resources/
    │       ├── application.yml                  # Config Spring Boot
    │       └── static/
    │           ├── index.html                   # SPA unique
    │           ├── css/styles.css               # Design system banque corporate
    │           └── js/app.js                    # Frontend — fetch API REST
    └── test/
        └── java/com/bfios/gei/dashboard/
            └── DashboardApplicationTests.java
```

## Démarrage rapide

### Prérequis
- **Java 17+** (testé avec Java 21)
- **Maven 3.6+** (ou utilisez le wrapper `mvnw` après `mvn -N io.takari:maven:wrapper`)

### Lancer l'application
```bash
cd bfi-os-gei-dashboard
mvn spring-boot:run
```
L'application démarre sur **http://localhost:8080**.

### Build & package
```bash
mvn clean package
java -jar target/bfi-os-gei-dashboard-1.0.0.jar
```

### Tests
```bash
mvn test
```

## API REST

### Référentiels
| Endpoint | Description |
|----------|-------------|
| `GET /api/referentiel/departements` | Liste des 4 départements |
| `GET /api/referentiel/natures?dept=DOMESTIQUE` | Natures d'un département |
| `GET /api/referentiel/produits?dept=DOMESTIQUE&nature=Effet` | Produits d'une nature |
| `GET /api/referentiel/etats` | 9 états possibles |
| `GET /api/referentiel/etapes` | 7 étapes du workflow |
| `GET /api/referentiel/canals` | DIGITAL, PHYSIQUE |
| `GET /api/referentiel/arbre` | Vue arborescente complète |

### Dashboard (avec filtres optionnels en query params)
| Endpoint | Description |
|----------|-------------|
| `GET /api/dashboard/demandes` | TdB1 — Liste détaillée filtrée |
| `GET /api/dashboard/kpis` | KPIs (total, clôturées, en cours, SLA, délai moyen) |
| `GET /api/dashboard/synthese?dim=dept\|nat\|prod` | TdB2 — Synthèse pivot |
| `GET /api/dashboard/sla` | TdB3 — SLA par étape |
| `GET /api/dashboard/demandes-ca` | TdB4 — Vue CA/Senior Banker |
| `GET /api/dashboard/responsable-a/productivite` | Vision Resp A |
| `GET /api/dashboard/responsable-a/top-clients` | Top 10 clients |
| `GET /api/dashboard/dg/summary` | Vision DG consolidée |

### Filtres (query params optionnels)
```
?dept=DOMESTIQUE&nat=Effet&prod=Escompte+d'effet
&etat=CLOTUREE&etape=HORS_WORKFLOW
&canal=DIGITAL&fastTrack=true&search=200000047
```

## Exemple de requête

```bash
# KPIs globaux
curl http://localhost:8080/api/dashboard/kpis

# Demandes du département Trade
curl 'http://localhost:8080/api/dashboard/demandes?dept=TRADE'

# Synthèse par produit
curl 'http://localhost:8080/api/dashboard/synthese?dim=prod'

# Détail SLA
curl http://localhost:8080/api/dashboard/sla
```

## Frontend

Le frontend est une SPA servie par Spring Boot (`/static/index.html`). Il consomme l'API REST via `fetch()`. Pour développer le frontend séparément (ex. avec Vite), activez le CORS — déjà configuré dans `WebConfig.java`.

### Vues disponibles (sidebar)
1. **TdB1** — Liste détaillée des demandes
2. **TdB2** — Synthèse pivot (Dépt/Nature/Produit × États)
3. **TdB3** — Matrice SLA par étape (code couleur vert/rouge)
4. **TdB4** — Vue Chargé d'affaires / Senior Banker
5. **Vision Responsable A** — Productivité agents + top clients
6. **Vision Coverage GE** — Couverture & délais par département
7. **Vision DG** — KPIs globaux + top produits

## Migration vers PostgreSQL/JPA

Le repository `InMemoryDemandeRepository` est conçu pour être remplacé par un repository JPA. Étapes :

1. Ajouter les dépendances dans `pom.xml` :
   ```xml
   <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-data-jpa</artifactId></dependency>
   <dependency><groupId>org.postgresql</groupId><artifactId>postgresql</artifactId><scope>runtime</scope></dependency>
   ```
2. Ajouter dans `application.yml` :
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/bfi_gei
       username: bfi
       password: secret
     jpa:
       hibernate.ddl-auto: update
   ```
3. Annoter `DemandeGei` avec `@Entity`, `@Id`, `@Column`, etc.
4. Créer une interface `DemandeRepository extends JpaRepository<DemandeGei, String>`.
5. Remplacer l'injection de `InMemoryDemandeRepository` par `DemandeRepository` dans `DashboardService`.

## Sécurité (à venir)

Pour la production, ajouter :
- `spring-boot-starter-security`
- SSO OAuth2 / LDAP corporate
- RBAC par profil (CA, Resp MO, DGEI, DG) via `@PreAuthorize`

## Documentation associée

- **Document de conception** : `../BFI-OS-GEI-Document-Conception.pdf`
- **Maquette statique originale** : `../index.html` (HTML/CSS/JS standalone)
