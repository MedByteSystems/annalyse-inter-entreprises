package com.entreprise;

import com.entreprise.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Tester la connexion à la base de données
        if (!testDatabaseConnection()) {
            showConnectionError();
            return;
        }

        // Initialiser le gestionnaire de scènes
        SceneManager.getInstance().setPrimaryStage(primaryStage);

        // Démarrer directement sur le dashboard d'analyse
        SceneManager.getInstance().switchToDashboardView();
    }

    private boolean testDatabaseConnection() {
        System.out.println("🔍 Test de la connexion à la base de données...");

        try {
            DatabaseConnection db = DatabaseConnection.getInstance();

            // Test 1: Créer une connexion
            try (Connection conn = db.getConnection()) {
                if (conn == null || conn.isClosed()) {
                    System.err.println("❌ La connexion est null ou fermée");
                    return false;
                }

                // Test 2: Exécuter une requête simple
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeQuery("SELECT 1 as test");
                }

                System.out.println("✅ Connexion à la base de données réussie!");
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion à la base de données:");
            e.printStackTrace();
            return false;
        }
    }

    private void showConnectionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de Connexion");
        alert.setHeaderText("Impossible de se connecter à la base de données");
        alert.setContentText("Veuillez vérifier:\n" +
                "1. SQL Server est-il démarré?\n" +
                "2. La base de données 'GestionEntreprises' existe-t-elle?\n" +
                "3. Les identifiants sont-ils corrects?\n\n" +
                "Détails: sa / Sa@123456 @ localhost:1433");

        alert.getDialogPane().setPrefSize(400, 250);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage de l'application d'analyse des relations...");
        System.out.println("📊 JavaFX version: " + System.getProperty("javafx.version"));
        System.out.println("☕ Java version: " + System.getProperty("java.version"));

        launch(args);
    }
}