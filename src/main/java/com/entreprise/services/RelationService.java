package com.entreprise.services;

import com.entreprise.dao.RelationDAO;
import com.entreprise.models.Entreprise;
import com.entreprise.models.Relation;

import java.time.LocalDate;
import java.util.List;

public class RelationService {
    private RelationDAO relationDAO;
    private EntrepriseService entrepriseService;

    public RelationService() {
        this.relationDAO = new RelationDAO();
        this.entrepriseService = new EntrepriseService();
    }

    // Récupérer toutes les relations
    public List<Relation> getAllRelations() {
        return relationDAO.getAllRelations();
    }

    // Ajouter une relation
    public boolean addRelation(Relation relation) {
        // Validation des données
        if (relation.getEntrepriseSource() == null || relation.getEntrepriseCible() == null) {
            throw new IllegalArgumentException("Les entreprises source et cible sont obligatoires");
        }

        if (relation.getEntrepriseSource().getId() == relation.getEntrepriseCible().getId()) {
            throw new IllegalArgumentException("Une entreprise ne peut pas avoir de relation avec elle-même");
        }

        if (relation.getTypeRelation() == null || relation.getTypeRelation().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de relation est obligatoire");
        }

        if (relation.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }

        if (relation.getDateFin() != null && relation.getDateFin().isBefore(relation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }

        return relationDAO.addRelation(relation);
    }

    // Mettre à jour une relation
    public boolean updateRelation(Relation relation) {
        // Validation des données
        if (relation.getEntrepriseSource() == null || relation.getEntrepriseCible() == null) {
            throw new IllegalArgumentException("Les entreprises source et cible sont obligatoires");
        }

        if (relation.getEntrepriseSource().getId() == relation.getEntrepriseCible().getId()) {
            throw new IllegalArgumentException("Une entreprise ne peut pas avoir de relation avec elle-même");
        }

        if (relation.getTypeRelation() == null || relation.getTypeRelation().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de relation est obligatoire");
        }

        if (relation.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début est obligatoire");
        }

        if (relation.getDateFin() != null && relation.getDateFin().isBefore(relation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début");
        }

        return relationDAO.updateRelation(relation);
    }

    // Supprimer une relation
    public boolean deleteRelation(int id) {
        return relationDAO.deleteRelation(id);
    }

    // Récupérer les relations par type
    public List<Relation> getRelationsByType(String type) {
        return relationDAO.getRelationsByType(type);
    }

    // Obtenir les statistiques des relations
    public List<String[]> getRelationStats() {
        return relationDAO.getRelationStats();
    }

    // Obtenir toutes les entreprises pour les combos
    public List<Entreprise> getAllEntreprisesForCombo() {
        return entrepriseService.getAllEntreprises();
    }

    // Analyser les relations actives
    public List<Relation> getActiveRelations() {
        List<Relation> allRelations = getAllRelations();
        LocalDate today = LocalDate.now();

        return allRelations.stream()
                .filter(relation -> {
                    // Relation active si pas de date de fin ou date de fin dans le futur
                    return relation.getDateFin() == null ||
                            relation.getDateFin().isAfter(today) ||
                            relation.getDateFin().isEqual(today);
                })
                .toList();
    }
}