package com.entreprise.services;

import com.entreprise.dao.EntrepriseDAO;
import com.entreprise.models.Entreprise;

import java.util.List;

public class EntrepriseService {
    private EntrepriseDAO entrepriseDAO;

    public EntrepriseService() {
        this.entrepriseDAO = new EntrepriseDAO();
    }

    // Récupérer toutes les entreprises
    public List<Entreprise> getAllEntreprises() {
        return entrepriseDAO.getAllEntreprises();
    }

    // Récupérer une entreprise par son ID
    public Entreprise getEntrepriseById(int id) {
        return entrepriseDAO.getEntrepriseById(id);
    }

    // Ajouter une entreprise
    public boolean addEntreprise(Entreprise entreprise) {
        // Validation des données
        if (entreprise.getNom() == null || entreprise.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }

        if (entreprise.getSecteur() == null || entreprise.getSecteur().trim().isEmpty()) {
            throw new IllegalArgumentException("Le secteur d'activité est obligatoire");
        }

        return entrepriseDAO.addEntreprise(entreprise);
    }

    // Mettre à jour une entreprise
    public boolean updateEntreprise(Entreprise entreprise) {
        // Validation des données
        if (entreprise.getNom() == null || entreprise.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'entreprise est obligatoire");
        }

        if (entreprise.getSecteur() == null || entreprise.getSecteur().trim().isEmpty()) {
            throw new IllegalArgumentException("Le secteur d'activité est obligatoire");
        }

        return entrepriseDAO.updateEntreprise(entreprise);
    }

    // Supprimer une entreprise
    public boolean deleteEntreprise(int id) {
        return entrepriseDAO.deleteEntreprise(id);
    }

    // Rechercher des entreprises
    public List<Entreprise> searchEntreprises(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEntreprises();
        }
        return entrepriseDAO.searchEntreprises(keyword.trim());
    }

    // Obtenir les secteurs uniques
    public List<String> getUniqueSectors() {
        List<Entreprise> entreprises = getAllEntreprises();
        return entreprises.stream()
                .map(Entreprise::getSecteur)
                .distinct()
                .sorted()
                .toList();
    }
}