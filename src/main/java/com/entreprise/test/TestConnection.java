package com.entreprise.test;

import com.entreprise.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("=== Test de Connexion SQL Server ===");

        try {
            DatabaseConnection db = DatabaseConnection.getInstance();

            // Test 1: Obtenir une connexion
            System.out.println("1. Tentative de connexion...");
            try (Connection conn = db.getConnection()) {
                if (conn != null && !conn.isClosed()) {
                    System.out.println("   ✅ Connexion établie");

                    // Test 2: Vérifier la base de données
                    System.out.println("2. Vérification de la base de données...");
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT DB_NAME() as dbname")) {
                        if (rs.next()) {
                            System.out.println("   ✅ Base connectée: " + rs.getString("dbname"));
                        }
                    }

                    // Test 3: Compter les entreprises
                    System.out.println("3. Test des tables...");
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Entreprise")) {
                        if (rs.next()) {
                            System.out.println("   ✅ Table Entreprise: " + rs.getInt("count") + " enregistrement(s)");
                        }
                    }

                    // Test 4: Compter les relations
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Relation")) {
                        if (rs.next()) {
                            System.out.println("   ✅ Table Relation: " + rs.getInt("count") + " enregistrement(s)");
                        }
                    }
                } else {
                    System.out.println("   ❌ Échec de connexion");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du test:");
            e.printStackTrace();
        }

        System.out.println("=== Fin du test ===");
    }
}