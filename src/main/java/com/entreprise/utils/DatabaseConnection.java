package com.entreprise.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;

    // Configuration SQL Server
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=GestionEntreprises;"
            + "encrypt=true;"
            + "trustServerCertificate=true;"
            + "loginTimeout=30;"; // Ajout d'un timeout de connexion

    private static final String USER = "sa";
    private static final String PASSWORD = "Sa@123456";

    static {
        try {
            // Charger le driver une fois au démarrage
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver SQL Server chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("ERREUR: Driver SQL Server non trouné!");
            e.printStackTrace();
        }
    }

    private DatabaseConnection() {
        // Constructeur privé
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Crée une nouvelle connexion à chaque appel
     */
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // Configurer la connexion
            connection.setAutoCommit(true);
            System.out.println("✅ Nouvelle connexion SQL Server créée");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à SQL Server:");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return connection;
    }

    /**
     * Teste la connexion
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Test de connexion échoué: " + e.getMessage());
            return false;
        }
    }
}