package com.entreprise;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Analyse des Relations Inter-Entreprises - " + title);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthodes de navigation
    public void switchToDashboardView() {
        loadView("/views/dashboard_view.fxml", "Tableau de Bord");
    }

    public void switchToEntrepriseView() {
        loadView("/views/entreprise_view.fxml", "Gestion des Entreprises");
    }

    public void switchToRelationView() {
        loadView("/views/relation_view.fxml", "Gestion des Relations");
    }

    // AJOUTEZ CETTE MÉTHODE POUR CORRIGER L'ERREUR
    public void switchToHomeView() {
        // Redirige vers le dashboard (qui est maintenant notre "home")
        switchToDashboardView();
    }

    public void closeApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
}