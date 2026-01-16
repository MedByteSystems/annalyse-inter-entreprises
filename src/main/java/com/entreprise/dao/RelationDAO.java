package com.entreprise.dao;

import com.entreprise.models.Entreprise;
import com.entreprise.models.Relation;
import com.entreprise.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelationDAO {
    private EntrepriseDAO entrepriseDAO = new EntrepriseDAO();

    // Méthode pour récupérer toutes les relations
    public List<Relation> getAllRelations() {
        List<Relation> relations = new ArrayList<>();
        String query = "SELECT r.*, "
                + "e1.nom as source_nom, e1.secteur as source_secteur, "
                + "e2.nom as cible_nom, e2.secteur as cible_secteur "
                + "FROM Relation r "
                + "JOIN Entreprise e1 ON r.entreprise_source_id = e1.id "
                + "JOIN Entreprise e2 ON r.entreprise_cible_id = e2.id "
                + "ORDER BY r.date_debut DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Relation relation = new Relation();
                relation.setId(rs.getInt("id"));

                // Créer l'entreprise source
                Entreprise source = new Entreprise();
                source.setId(rs.getInt("entreprise_source_id"));
                source.setNom(rs.getString("source_nom"));
                source.setSecteur(rs.getString("source_secteur"));
                relation.setEntrepriseSource(source);

                // Créer l'entreprise cible
                Entreprise cible = new Entreprise();
                cible.setId(rs.getInt("entreprise_cible_id"));
                cible.setNom(rs.getString("cible_nom"));
                cible.setSecteur(rs.getString("cible_secteur"));
                relation.setEntrepriseCible(cible);

                relation.setTypeRelation(rs.getString("type_relation"));

                Date dateDebut = rs.getDate("date_debut");
                if (dateDebut != null) {
                    relation.setDateDebut(dateDebut.toLocalDate());
                }

                Date dateFin = rs.getDate("date_fin");
                if (dateFin != null) {
                    relation.setDateFin(dateFin.toLocalDate());
                }

                relation.setDescription(rs.getString("description"));
                relations.add(relation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des relations: " + e.getMessage());
        }
        return relations;
    }

    // Méthode pour ajouter une relation
    public boolean addRelation(Relation relation) {
        String query = "INSERT INTO Relation (entreprise_source_id, entreprise_cible_id, "
                + "type_relation, date_debut, date_fin, description) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, relation.getEntrepriseSource().getId());
            pstmt.setInt(2, relation.getEntrepriseCible().getId());
            pstmt.setString(3, relation.getTypeRelation());

            if (relation.getDateDebut() != null) {
                pstmt.setDate(4, Date.valueOf(relation.getDateDebut()));
            } else {
                pstmt.setDate(4, null);
            }

            if (relation.getDateFin() != null) {
                pstmt.setDate(5, Date.valueOf(relation.getDateFin()));
            } else {
                pstmt.setDate(5, null);
            }

            pstmt.setString(6, relation.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    relation.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la relation: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour mettre à jour une relation
    public boolean updateRelation(Relation relation) {
        String query = "UPDATE Relation SET entreprise_source_id = ?, entreprise_cible_id = ?, "
                + "type_relation = ?, date_debut = ?, date_fin = ?, description = ? "
                + "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, relation.getEntrepriseSource().getId());
            pstmt.setInt(2, relation.getEntrepriseCible().getId());
            pstmt.setString(3, relation.getTypeRelation());

            if (relation.getDateDebut() != null) {
                pstmt.setDate(4, Date.valueOf(relation.getDateDebut()));
            } else {
                pstmt.setDate(4, null);
            }

            if (relation.getDateFin() != null) {
                pstmt.setDate(5, Date.valueOf(relation.getDateFin()));
            } else {
                pstmt.setDate(5, null);
            }

            pstmt.setString(6, relation.getDescription());
            pstmt.setInt(7, relation.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la relation: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour supprimer une relation
    public boolean deleteRelation(int id) {
        String query = "DELETE FROM Relation WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la relation: " + e.getMessage());
        }
        return false;
    }

    // Méthode pour récupérer les relations par type
    public List<Relation> getRelationsByType(String type) {
        List<Relation> relations = new ArrayList<>();
        String query = "SELECT * FROM Relation WHERE type_relation = ? ORDER BY date_debut DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Relation relation = new Relation();
                relation.setId(rs.getInt("id"));
                relation.setEntrepriseSource(entrepriseDAO.getEntrepriseById(rs.getInt("entreprise_source_id")));
                relation.setEntrepriseCible(entrepriseDAO.getEntrepriseById(rs.getInt("entreprise_cible_id")));
                relation.setTypeRelation(rs.getString("type_relation"));

                Date dateDebut = rs.getDate("date_debut");
                if (dateDebut != null) {
                    relation.setDateDebut(dateDebut.toLocalDate());
                }

                Date dateFin = rs.getDate("date_fin");
                if (dateFin != null) {
                    relation.setDateFin(dateFin.toLocalDate());
                }

                relation.setDescription(rs.getString("description"));
                relations.add(relation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des relations par type: " + e.getMessage());
        }
        return relations;
    }

    // Méthode pour analyser les relations (statistiques)
    public List<String[]> getRelationStats() {
        List<String[]> stats = new ArrayList<>();
        String query = "SELECT type_relation, COUNT(*) as nombre "
                + "FROM Relation GROUP BY type_relation";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String[] stat = new String[2];
                stat[0] = rs.getString("type_relation");
                stat[1] = String.valueOf(rs.getInt("nombre"));
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }
        return stats;
    }
}