package com.entreprise.controllers;

import com.entreprise.SceneManager;
import com.entreprise.models.Entreprise;
import com.entreprise.models.Relation;
import com.entreprise.services.EntrepriseService;
import com.entreprise.services.RelationService;
import com.entreprise.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    // Labels de statistiques
    @FXML private Label lblTotalEntreprises;
    @FXML private Label lblTotalRelations;
    @FXML private Label lblRelationsActives;
    @FXML private Label lblSecteurs;
    @FXML private Label lblPartenariats;
    @FXML private Label lblCollaborations;
    @FXML private Label lblRecentCount;

    // Conteneurs
    @FXML private VBox vboxSecteurs;
    @FXML private VBox vboxStats;

    // Tableau
    @FXML private TableView<Map<String, String>> tableViewRecent;

    // Services
    private EntrepriseService entrepriseService;
    private RelationService relationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entrepriseService = new EntrepriseService();
        relationService = new RelationService();

        // Configurer le tableau
        setupTable();

        // Charger et afficher les données
        refreshData();
    }

    private void setupTable() {
        // Configurer les colonnes
        TableColumn<Map<String, String>, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("date")));
        colDate.setPrefWidth(100);

        TableColumn<Map<String, String>, String> colSource = new TableColumn<>("Entreprise Source");
        colSource.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("source")));
        colSource.setPrefWidth(200);

        TableColumn<Map<String, String>, String> colCible = new TableColumn<>("Entreprise Cible");
        colCible.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("cible")));
        colCible.setPrefWidth(200);

        TableColumn<Map<String, String>, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("type")));
        colType.setPrefWidth(120);

        TableColumn<Map<String, String>, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get("statut")));
        colStatut.setPrefWidth(100);

        TableColumn<Map<String, String>, String> colActions = new TableColumn<>("Actions");
        colActions.setCellFactory(param -> new TableCell<Map<String, String>, String>() {
            private final Button btnView = new Button("👁️ Voir");

            {
                btnView.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 3 10;");
                btnView.setOnAction(event -> {
                    Map<String, String> data = getTableView().getItems().get(getIndex());
                    AlertUtils.showInfo("Détails",
                            "Relation entre " + data.get("source") + " et " + data.get("cible") +
                                    "\nType: " + data.get("type") +
                                    "\nDate: " + data.get("date") +
                                    "\nStatut: " + data.get("statut"));
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(btnView);
                    hbox.setSpacing(5);
                    setGraphic(hbox);
                }
            }
        });
        colActions.setPrefWidth(150);

        tableViewRecent.getColumns().setAll(colDate, colSource, colCible, colType, colStatut, colActions);
    }

    @FXML
    private void refreshData() {
        try {
            List<Entreprise> entreprises = entrepriseService.getAllEntreprises();
            List<Relation> relations = relationService.getAllRelations();

            // Mettre à jour les statistiques
            updateStats(entreprises, relations);

            // Mettre à jour le tableau
            updateRecentTable(relations);

            // Mettre à jour l'analyse par secteur
            updateSectorAnalysis(entreprises, relations);

            // Mettre à jour les statistiques détaillées
            updateDetailedStats(entreprises, relations);

            // SUPPRIMER CETTE LIGNE ↓↓↓
            // AlertUtils.showInfo("Actualisation", "Données actualisées avec succès !");

        } catch (Exception e) {
            AlertUtils.showError("Erreur", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void updateStats(List<Entreprise> entreprises, List<Relation> relations) {
        lblTotalEntreprises.setText(String.valueOf(entreprises.size()));
        lblTotalRelations.setText(String.valueOf(relations.size()));

        // Relations actives
        long activeRelations = relations.stream()
                .filter(r -> r.getDateFin() == null ||
                        r.getDateFin().isAfter(LocalDate.now()) ||
                        r.getDateFin().isEqual(LocalDate.now()))
                .count();
        lblRelationsActives.setText(String.valueOf(activeRelations));

        // Secteurs uniques
        long uniqueSectors = entreprises.stream()
                .map(Entreprise::getSecteur)
                .filter(s -> s != null && !s.isEmpty())
                .distinct()
                .count();
        lblSecteurs.setText(String.valueOf(uniqueSectors));

        // Compter par type
        long partenariats = relations.stream()
                .filter(r -> "partenariat".equals(r.getTypeRelation()))
                .count();
        long collaborations = relations.stream()
                .filter(r -> "collaboration".equals(r.getTypeRelation()))
                .count();

        lblPartenariats.setText(String.valueOf(partenariats));
        lblCollaborations.setText(String.valueOf(collaborations));
    }

    private void updateRecentTable(List<Relation> relations) {
        ObservableList<Map<String, String>> recentData = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Trier par date (plus récentes d'abord) et limiter à 10
        List<Relation> sortedRelations = relations.stream()
                .sorted((r1, r2) -> r2.getDateDebut().compareTo(r1.getDateDebut()))
                .limit(10)
                .collect(Collectors.toList());

        for (Relation relation : sortedRelations) {
            Map<String, String> row = new HashMap<>();
            row.put("date", relation.getDateDebut().format(formatter));
            row.put("source", relation.getEntrepriseSource().getNom());
            row.put("cible", relation.getEntrepriseCible().getNom());
            row.put("type", relation.getTypeRelation());

            // Déterminer le statut
            String statut = "Active";
            if (relation.getDateFin() != null) {
                if (relation.getDateFin().isBefore(LocalDate.now())) {
                    statut = "Terminée";
                } else if (relation.getDateFin().isAfter(LocalDate.now())) {
                    statut = "En cours";
                }
            }
            row.put("statut", statut);

            recentData.add(row);
        }

        tableViewRecent.setItems(recentData);
        lblRecentCount.setText("(" + recentData.size() + " relations récentes)");
    }

    private void updateSectorAnalysis(List<Entreprise> entreprises, List<Relation> relations) {
        vboxSecteurs.getChildren().clear();

        // Grouper les entreprises par secteur
        Map<String, Long> sectorCounts = entreprises.stream()
                .filter(e -> e.getSecteur() != null && !e.getSecteur().isEmpty())
                .collect(Collectors.groupingBy(Entreprise::getSecteur, Collectors.counting()));

        // Trier par nombre décroissant
        List<Map.Entry<String, Long>> sortedSectors = sectorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Créer une barre pour chaque secteur
        for (Map.Entry<String, Long> entry : sortedSectors) {
            String secteur = entry.getKey();
            Long count = entry.getValue();

            HBox sectorRow = new HBox(10);
            sectorRow.setStyle("-fx-padding: 8 0;");

            // Nom du secteur
            Label lblSector = new Label(secteur);
            lblSector.setStyle("-fx-font-weight: bold; -fx-min-width: 120;");

            // Barre de progression
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(count.doubleValue() / entreprises.size());
            progressBar.setPrefWidth(200);
            progressBar.setStyle("-fx-accent: #3498db;");
            HBox.setHgrow(progressBar, Priority.ALWAYS);

            // Compteur
            Label lblCount = new Label(count.toString());
            lblCount.setStyle("-fx-font-weight: bold; -fx-min-width: 40; -fx-text-fill: #2c3e50;");

            sectorRow.getChildren().addAll(lblSector, progressBar, lblCount);
            vboxSecteurs.getChildren().add(sectorRow);
        }
    }

    private void updateDetailedStats(List<Entreprise> entreprises, List<Relation> relations) {
        vboxStats.getChildren().clear();

        // 1. Top 3 des entreprises les plus connectées
        Map<String, Long> enterpriseConnections = new HashMap<>();
        for (Relation relation : relations) {
            String source = relation.getEntrepriseSource().getNom();
            String target = relation.getEntrepriseCible().getNom();

            enterpriseConnections.put(source,
                    enterpriseConnections.getOrDefault(source, 0L) + 1);
            enterpriseConnections.put(target,
                    enterpriseConnections.getOrDefault(target, 0L) + 1);
        }

        List<Map.Entry<String, Long>> top3 = enterpriseConnections.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        VBox topBox = new VBox(5);
        topBox.setStyle("-fx-padding: 0 0 15 0;");
        Label lblTopTitle = new Label("🏆 TOP 3 ENTREPRISES CONNECTÉES");
        lblTopTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        topBox.getChildren().add(lblTopTitle);

        for (int i = 0; i < top3.size(); i++) {
            Map.Entry<String, Long> entry = top3.get(i);
            HBox row = new HBox(10);

            // Médailles
            String medal = "";
            switch (i) {
                case 0: medal = "🥇"; break;
                case 1: medal = "🥈"; break;
                case 2: medal = "🥉"; break;
            }

            Label lblMedal = new Label(medal);
            Label lblName = new Label(entry.getKey());
            lblName.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(lblName, Priority.ALWAYS);

            Label lblConnections = new Label(entry.getValue() + " connexions");
            lblConnections.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");

            row.getChildren().addAll(lblMedal, lblName, lblConnections);
            topBox.getChildren().add(row);
        }

        // 2. Relations par mois (derniers 6 mois)
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        Map<String, Long> monthlyRelations = relations.stream()
                .filter(r -> !r.getDateDebut().isBefore(sixMonthsAgo))
                .collect(Collectors.groupingBy(
                        r -> r.getDateDebut().getMonthValue() + "/" + r.getDateDebut().getYear(),
                        Collectors.counting()
                ));

        VBox monthlyBox = new VBox(5);
        monthlyBox.setStyle("-fx-padding: 15 0;");
        Label lblMonthlyTitle = new Label("📅 ACTIVITÉ RÉCENTE (6 MOIS)");
        lblMonthlyTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        monthlyBox.getChildren().add(lblMonthlyTitle);

        for (Map.Entry<String, Long> entry : monthlyRelations.entrySet()) {
            HBox row = new HBox(10);

            Label lblMonth = new Label(entry.getKey());
            lblMonth.setPrefWidth(80);

            ProgressBar progress = new ProgressBar();
            progress.setProgress(entry.getValue().doubleValue() /
                    monthlyRelations.values().stream().max(Long::compare).orElse(1L));
            progress.setPrefWidth(150);
            progress.setStyle("-fx-accent: #2ecc71;");

            Label lblCount = new Label(entry.getValue().toString());
            lblCount.setStyle("-fx-font-weight: bold;");

            row.getChildren().addAll(lblMonth, progress, lblCount);
            monthlyBox.getChildren().add(row);
        }

        // 3. Types de relations
        Map<String, Long> typeCounts = relations.stream()
                .collect(Collectors.groupingBy(Relation::getTypeRelation, Collectors.counting()));

        VBox typeBox = new VBox(5);
        typeBox.setStyle("-fx-padding: 15 0;");
        Label lblTypeTitle = new Label("📊 RÉPARTITION PAR TYPE");
        lblTypeTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        typeBox.getChildren().add(lblTypeTitle);

        for (Map.Entry<String, Long> entry : typeCounts.entrySet()) {
            HBox row = new HBox(10);

            // Couleur selon le type
            String color = "";
            String emoji = "";
            switch (entry.getKey()) {
                case "partenariat":
                    color = "#27ae60"; emoji = "🤝";
                    break;
                case "collaboration":
                    color = "#f39c12"; emoji = "👥";
                    break;
                case "acquisition":
                    color = "#e74c3c"; emoji = "💰";
                    break;
            }

            // Petit carré de couleur
            Rectangle colorRect = new Rectangle(10, 10);
            colorRect.setFill(Color.web(color));

            Label lblType = new Label(emoji + " " + entry.getKey());
            lblType.setStyle("-fx-font-weight: bold;");
            HBox.setHgrow(lblType, Priority.ALWAYS);

            Label lblPercent = new Label(
                    String.format("%.1f%%",
                            entry.getValue().doubleValue() / relations.size() * 100)
            );
            lblPercent.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

            row.getChildren().addAll(colorRect, lblType, lblPercent);
            typeBox.getChildren().add(row);
        }

        vboxStats.getChildren().addAll(topBox, monthlyBox, typeBox);
    }

    @FXML
    private void goToDashboard() {
        refreshData();
    }

    @FXML
    private void goToEntrepriseView() {
        SceneManager.getInstance().switchToEntrepriseView();
    }

    @FXML
    private void goToRelationView() {
        SceneManager.getInstance().switchToRelationView();
    }

}