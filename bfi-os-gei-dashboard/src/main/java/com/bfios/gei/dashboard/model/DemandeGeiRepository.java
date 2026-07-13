package com.bfios.gei.dashboard.model;

import com.bfios.gei.dashboard.model.DemandeGei;
import com.bfios.gei.dashboard.model.Departement;
import com.bfios.gei.dashboard.model.EtatDemande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository JPA pour l'entité DemandeGei.
 *
 * Spring Data JPA génère automatiquement l'implémentation à partir de cette
 * interface au démarrage. Toutes les méthodes CRUD de base sont héritées de
 * JpaRepository : save, findById, findAll, deleteById, count, etc.
 *
 * On ajoute ci-dessous des méthodes de recherche personnalisées :
 *  1. Soit par dérivation de nom (Spring génère la requête SQL)
 *  2. Soit via @Query pour les cas plus complexes
 *
 * Cette interface REMPLACE InMemoryDemandeRepository.
 */
@Repository
public interface DemandeGeiRepository extends JpaRepository<DemandeGei, String> {

    // ─── Requêtes dérivées (Spring génère le SQL automatiquement) ───
    List<DemandeGei> findByDepartement(Departement departement);

    List<DemandeGei> findByEtat(EtatDemande etat);

    List<DemandeGei> findByDepartementAndEtat(Departement departement, EtatDemande etat);

    long countByEtat(EtatDemande etat);

    long countBySlaOk(boolean slaOk);

    // ─── Requêtes avec filtre période (JPQL) ───
    List<DemandeGei> findByDateDemandeBetween(LocalDate dateDebut, LocalDate dateFin);

    // ─── Requête personnalisée multi-critères (JPQL) ───
    // Utilisée par DashboardService si on veut pousser les filtres en base
    // plutôt que de filtrer en mémoire côté Java.
    @Query("SELECT d FROM DemandeGei d WHERE " +
           "(:dept IS NULL OR d.departement = :dept) AND " +
           "(:etat IS NULL OR d.etat = :etat) AND " +
           "(:dateDebut IS NULL OR d.dateDemande >= :dateDebut) AND " +
           "(:dateFin IS NULL OR d.dateDemande <= :dateFin) " +
           "ORDER BY d.dateDemande DESC")
    List<DemandeGei> findFiltered(@Param("dept") Departement dept,
                                   @Param("etat") EtatDemande etat,
                                   @Param("dateDebut") LocalDate dateDebut,
                                   @Param("dateFin") LocalDate dateFin);
   @Query("select departement,count(*) dep,sum(delaiTraitement) delai from DemandeGei group by departement")
   List<Object[]> getDep();
}
