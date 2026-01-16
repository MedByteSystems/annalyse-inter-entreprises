package com.entreprise.controllers;

import com.entreprise.SceneManager;
import com.entreprise.services.EntrepriseService;
import com.entreprise.services.RelationService;
import com.entreprise.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Label lblTotalEntreprises;
    @FXML private Label lblTotalRelations;
    @FXML private Label lblRelationsActives;

    private EntrepriseService entrepriseService;
    private RelationService relationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entrepriseService = new EntrepriseService();
        relationService = new RelationService();
        updateStats();
    }

    private void updateStats() {
        try {
            // Mettre à jour les statistiques
            int totalEntreprises = entrepriseService.getAllEntreprises().size();
            int totalRelations = relationService.getAllRelations().size();
            int activeRelations = relationService.getActiveRelations().size();

            lblTotalEntreprises.setText(String.valueOf(totalEntreprises));
            lblTotalRelations.setText(String.valueOf(totalRelations));
            lblRelationsActives.setText(String.valueOf(activeRelations));
        } catch (Exception e) {
            AlertUtils.showError("Erreur", "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    @FXML
    private void goToEntrepriseView() {
        SceneManager.getInstance().switchToEntrepriseView();
    }

    @FXML
    private void goToRelationView() {
        SceneManager.getInstance().switchToRelationView();
    }

    @FXML
    private void goToAnalyseView() {
        AlertUtils.showInfo("Analyse", "Fonctionnalité d'analyse en cours de développement...");
    }

    @FXML
    private void quitApplication() {
        if (AlertUtils.showConfirmation("Quitter", "Voulez-vous vraiment quitter l'application?")) {
            SceneManager.getInstance().closeApplication();
        }
    }
}