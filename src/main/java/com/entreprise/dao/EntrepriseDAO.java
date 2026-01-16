package com.entreprise.dao;

import com.entreprise.models.Entreprise;
import com.entreprise.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseDAO {

    // Méthode pour récupérer toutes les entreprises
    public List<Entreprise> getAllEntreprises() {
        List<Entreprise> entreprises = new ArrayList<>();
        String query = "SELECT * FROM Entreprise ORDER BY nom";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Entreprise entreprise = new Entreprise();
                entreprise.setId(rs.getInt("id"));
                entreprise.setNom(rs.getString("nom"));
                entreprise.setSecteur(rs.getString("secteur"));
                entreprise.setVille(rs.getString("ville"));
                entreprise.setPays(rs.getString("pays"));

                Date dateCreation = rs.getDate("date_creation");
                if (dateCreation != null) {
                    entreprise.setDateCreation(dateCreation.toLocalDate());
                }

                entreprise.setChiffreAffaires(rs.getDouble("chiffre_affaires"));
                entreprises.add(entreprise);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des entreprises: " + e.getMessage());
        }
        return entreprises;
    }

    // Méthode pour récupérer une entreprise par son ID
    public Entreprise getEntrepriseById(int id) {
        String query = "SELECT * FROM Entreprise WHERE id = ?";
        Entreprise entreprise = null;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                entreprise = new Entreprise();
                entreprise.setId(rs.getInt("id"));
                entreprise.setNom(rs.getString("nom"));
                entreprise.setSecteur(rs.getString("secteur"));
                entreprise.setVille(rs.getString("ville"));
                entreprise.setPays(rs.getString("pays"));

                Date dateCreation = rs.getDate("date_creation");
                if (dateCreation != null) {
                    entreprise.setDateCreation(dateCreation.toLocalDate());
                }

                entreprise.setChiffreAffaires(rs.getDouble("chiffre_affaires"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'entreprise: " + e.getMessage());
        }
        return entreprise;
    }

    // Méthode pour ajouter une entreprise
    public boolean addEntreprise(Entreprise entreprise) {
        String query = "INSERT INTO Entreprise (nom, secteur, ville, pays, date_creation, chiffre_affaires) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entreprise.getNom());
            pstmt.setString(2, entreprise.getSecteur());
            pstmt.setString(3, entreprise.getVille());
            pstmt.setString(4, entreprise.getPays());

            if (entreprise.getDateCreation() != null) {
                pstmt.setDate(5, Date.valueOf(entreprise.getDateCreation()));
            } else {
                pstmt.setDate(5, null);
            }

            pstmt.setDouble(6, entreprise.getChiffreAffaires());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    entreprise.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'entreprise: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour mettre à jour une entreprise
    public boolean updateEntreprise(Entreprise entreprise) {
        String query = "UPDATE Entreprise SET nom = ?, secteur = ?, ville = ?, pays = ?, "
                + "date_creation = ?, chiffre_affaires = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entreprise.getNom());
            pstmt.setString(2, entreprise.getSecteur());
            pstmt.setString(3, entreprise.getVille());
            pstmt.setString(4, entreprise.getPays());

            if (entreprise.getDateCreation() != null) {
                pstmt.setDate(5, Date.valueOf(entreprise.getDateCreation()));
            } else {
                pstmt.setDate(5, null);
            }

            pstmt.setDouble(6, entreprise.getChiffreAffaires());
            pstmt.setInt(7, entreprise.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'entreprise: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour supprimer une entreprise
    public boolean deleteEntreprise(int id) {
        String query = "DELETE FROM Entreprise WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'entreprise: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour rechercher des entreprises
    public List<Entreprise> searchEntreprises(String keyword) {
        List<Entreprise> entreprises = new ArrayList<>();
        String query = "SELECT * FROM Entreprise WHERE nom LIKE ? OR secteur LIKE ? OR ville LIKE ? ORDER BY nom";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Entreprise entreprise = new Entreprise();
                entreprise.setId(rs.getInt("id"));
                entreprise.setNom(rs.getString("nom"));
                entreprise.setSecteur(rs.getString("secteur"));
                entreprise.setVille(rs.getString("ville"));
                entreprise.setPays(rs.getString("pays"));

                Date dateCreation = rs.getDate("date_creation");
                if (dateCreation != null) {
                    entreprise.setDateCreation(dateCreation.toLocalDate());
                }

                entreprise.setChiffreAffaires(rs.getDouble("chiffre_affaires"));
                entreprises.add(entreprise);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'entreprises: " + e.getMessage());
        }
        return entreprises;
    }
}