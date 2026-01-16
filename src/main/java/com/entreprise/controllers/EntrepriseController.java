package com.entreprise.controllers;

import com.entreprise.SceneManager;
import com.entreprise.models.Entreprise;
import com.entreprise.services.EntrepriseService;
import com.entreprise.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
public class EntrepriseController implements Initializable {

    @FXML private TableView<Entreprise> tableView;
    @FXML private TableColumn<Entreprise, Integer> colId;
    @FXML private TableColumn<Entreprise, String> colNom;
    @FXML private TableColumn<Entreprise, String> colSecteur;
    @FXML private TableColumn<Entreprise, String> colVille;
    @FXML private TableColumn<Entreprise, String> colPays;
    @FXML private TableColumn<Entreprise, LocalDate> colDateCreation;
    @FXML private TableColumn<Entreprise, Double> colChiffreAffaires;
    @FXML private TableColumn<Entreprise, Void> colActions;

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cbSecteur;
    @FXML private Label lblCount;

    private EntrepriseService entrepriseService;
    private ObservableList<Entreprise> entreprises;
    private ObservableList<String> secteurs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entrepriseService = new EntrepriseService();
        entreprises = FXCollections.observableArrayList();
        secteurs = FXCollections.observableArrayList();

        setupTableColumns();
        loadEntreprises();
        loadSecteurs();

        cbSecteur.setItems(secteurs);
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSecteur.setCellValueFactory(new PropertyValueFactory<>("secteur"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colPays.setCellValueFactory(new PropertyValueFactory<>("pays"));
        colDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colChiffreAffaires.setCellValueFactory(new PropertyValueFactory<>("chiffreAffaires"));

        colChiffreAffaires.setCellFactory(column -> new TableCell<Entreprise, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f €", item));
                }
            }
        });

        colActions.setCellFactory(new Callback<TableColumn<Entreprise, Void>, TableCell<Entreprise, Void>>() {
            @Override
            public TableCell<Entreprise, Void> call(TableColumn<Entreprise, Void> param) {
                return new TableCell<Entreprise, Void>() {
                    private final Button btnEdit = new Button("✏️");
                    private final Button btnDelete = new Button("🗑️");
                    private final HBox hbox = new HBox(5, btnEdit, btnDelete);

                    {
                        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                        btnEdit.setOnAction(event -> {
                            Entreprise entreprise = getTableView().getItems().get(getIndex());
                            editEntreprise(entreprise);
                        });

                        btnDelete.setOnAction(event -> {
                            Entreprise entreprise = getTableView().getItems().get(getIndex());
                            deleteEntreprise(entreprise);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }

    private void loadEntreprises() {
        entreprises.clear();
        entreprises.addAll(entrepriseService.getAllEntreprises());
        tableView.setItems(entreprises);
        updateCount();
    }

    private void loadSecteurs() {
        secteurs.clear();
        secteurs.addAll(entrepriseService.getUniqueSectors());
    }

    private void updateCount() {
        lblCount.setText("(" + entreprises.size() + " entreprises)");
    }

    @FXML
    private void goToHome() {
        SceneManager.getInstance().switchToHomeView();
    }

    @FXML
    private void onSearch() {
        String keyword = txtSearch.getText();
        entreprises.clear();
        entreprises.addAll(entrepriseService.searchEntreprises(keyword));
        updateCount();
    }

    @FXML
    private void onSearchAction() {
        onSearch();
    }

    @FXML
    private void onFilterSecteur() {
        String secteur = cbSecteur.getValue();
        if (secteur == null || secteur.isEmpty()) {
            loadEntreprises();
        } else {
            entreprises.clear();
            entreprises.addAll(entrepriseService.getAllEntreprises().stream()
                    .filter(e -> secteur.equals(e.getSecteur()))
                    .toList());
            updateCount();
        }
    }

    @FXML
    private void resetFilters() {
        txtSearch.clear();
        cbSecteur.setValue(null);
        loadEntreprises();
    }

    @FXML
    private void showAddDialog() {
        Dialog<Entreprise> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une entreprise");
        dialog.setHeaderText("Entrez les informations de la nouvelle entreprise");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField txtNom = new TextField();
        txtNom.setPromptText("Nom");
        TextField txtSecteur = new TextField();
        txtSecteur.setPromptText("Secteur");
        TextField txtVille = new TextField();
        txtVille.setPromptText("Ville");
        TextField txtPays = new TextField();
        txtPays.setPromptText("Pays");
        DatePicker dpDateCreation = new DatePicker();
        dpDateCreation.setValue(LocalDate.now());
        TextField txtChiffreAffaires = new TextField();
        txtChiffreAffaires.setPromptText("Chiffre d'affaires");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Secteur:"), 0, 1);
        grid.add(txtSecteur, 1, 1);
        grid.add(new Label("Ville:"), 0, 2);
        grid.add(txtVille, 1, 2);
        grid.add(new Label("Pays:"), 0, 3);
        grid.add(txtPays, 1, 3);
        grid.add(new Label("Date création:"), 0, 4);
        grid.add(dpDateCreation, 1, 4);
        grid.add(new Label("Chiffre d'affaires:"), 0, 5);
        grid.add(txtChiffreAffaires, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Entreprise entreprise = new Entreprise();
                    entreprise.setNom(txtNom.getText());
                    entreprise.setSecteur(txtSecteur.getText());
                    entreprise.setVille(txtVille.getText());
                    entreprise.setPays(txtPays.getText());
                    entreprise.setDateCreation(dpDateCreation.getValue());

                    if (!txtChiffreAffaires.getText().isEmpty()) {
                        entreprise.setChiffreAffaires(Double.parseDouble(txtChiffreAffaires.getText()));
                    }

                    return entreprise;
                } catch (NumberFormatException e) {
                    AlertUtils.showError("Erreur", "Le chiffre d'affaires doit être un nombre valide");
                    return null;
                }
            }
            return null;
        });

        Optional<Entreprise> result = dialog.showAndWait();
        result.ifPresent(entreprise -> {
            try {
                if (entrepriseService.addEntreprise(entreprise)) {
                    AlertUtils.showInfo("Succès", "Entreprise ajoutée avec succès!");
                    loadEntreprises();
                } else {
                    AlertUtils.showError("Erreur", "Impossible d'ajouter l'entreprise");
                }
            } catch (IllegalArgumentException e) {
                AlertUtils.showError("Erreur de validation", e.getMessage());
            }
        });
    }

    private void editEntreprise(Entreprise entreprise) {
        Dialog<Entreprise> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'entreprise");
        dialog.setHeaderText("Modifiez les informations de l'entreprise");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField txtNom = new TextField(entreprise.getNom());
        TextField txtSecteur = new TextField(entreprise.getSecteur());
        TextField txtVille = new TextField(entreprise.getVille());
        TextField txtPays = new TextField(entreprise.getPays());
        DatePicker dpDateCreation = new DatePicker(entreprise.getDateCreation());
        TextField txtChiffreAffaires = new TextField(String.valueOf(entreprise.getChiffreAffaires()));

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(txtNom, 1, 0);
        grid.add(new Label("Secteur:"), 0, 1);
        grid.add(txtSecteur, 1, 1);
        grid.add(new Label("Ville:"), 0, 2);
        grid.add(txtVille, 1, 2);
        grid.add(new Label("Pays:"), 0, 3);
        grid.add(txtPays, 1, 3);
        grid.add(new Label("Date création:"), 0, 4);
        grid.add(dpDateCreation, 1, 4);
        grid.add(new Label("Chiffre d'affaires:"), 0, 5);
        grid.add(txtChiffreAffaires, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Entreprise updated = new Entreprise();
                    updated.setId(entreprise.getId());
                    updated.setNom(txtNom.getText());
                    updated.setSecteur(txtSecteur.getText());
                    updated.setVille(txtVille.getText());
                    updated.setPays(txtPays.getText());
                    updated.setDateCreation(dpDateCreation.getValue());
                    updated.setChiffreAffaires(Double.parseDouble(txtChiffreAffaires.getText()));
                    return updated;
                } catch (NumberFormatException e) {
                    AlertUtils.showError("Erreur", "Le chiffre d'affaires doit être un nombre valide");
                    return null;
                }
            }
            return null;
        });

        Optional<Entreprise> result = dialog.showAndWait();
        result.ifPresent(updatedEntreprise -> {
            try {
                if (entrepriseService.updateEntreprise(updatedEntreprise)) {
                    AlertUtils.showInfo("Succès", "Entreprise modifiée avec succès!");
                    loadEntreprises();
                } else {
                    AlertUtils.showError("Erreur", "Impossible de modifier l'entreprise");
                }
            } catch (IllegalArgumentException e) {
                AlertUtils.showError("Erreur de validation", e.getMessage());
            }
        });
    }

    private void deleteEntreprise(Entreprise entreprise) {
        if (AlertUtils.showConfirmation("Confirmation",
                "Voulez-vous vraiment supprimer l'entreprise '" + entreprise.getNom() + "'?")) {
            try {
                if (entrepriseService.deleteEntreprise(entreprise.getId())) {
                    AlertUtils.showInfo("Succès", "Entreprise supprimée avec succès!");
                    loadEntreprises();
                } else {
                    AlertUtils.showError("Erreur", "Impossible de supprimer l'entreprise");
                }
            } catch (Exception e) {
                AlertUtils.showError("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    @FXML
    private void exportToCSV() {
        try {
            // Créer un FileChooser pour sélectionner l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter les entreprises en CSV");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
            );
            fileChooser.setInitialFileName("entreprises_export_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv");

            File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

            if (file != null) {
                // Écrire les données dans le fichier CSV
                try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
                    // En-têtes
                    writer.println("ID;Nom;Secteur;Ville;Pays;Date Création;Chiffre d'Affaires (€)");

                    // Données
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    for (Entreprise entreprise : entreprises) {
                        String dateStr = entreprise.getDateCreation() != null ?
                                entreprise.getDateCreation().format(dateFormatter) : "";

                        writer.println(String.format("%d;%s;%s;%s;%s;%s;%.2f",
                                entreprise.getId(),
                                escapeCsv(entreprise.getNom()),
                                escapeCsv(entreprise.getSecteur()),
                                escapeCsv(entreprise.getVille()),
                                escapeCsv(entreprise.getPays()),
                                dateStr,
                                entreprise.getChiffreAffaires()
                        ));
                    }

                    AlertUtils.showInfo("Export CSV",
                            "Export réussi !\n" +
                                    entreprises.size() + " entreprises exportées.\n" +
                                    "Fichier : " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            AlertUtils.showError("Erreur d'export",
                    "Impossible d'exporter les données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // AJOUTEZ CETTE MÉTHODE POUR ÉCHAPPER LES CHAMPS CSV
    private String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        // Si le champ contient des guillemets, des points-virgules ou des retours à la ligne
        if (field.contains("\"") || field.contains(";") || field.contains("\n")) {
            // Remplacer les guillemets par deux guillemets et entourer de guillemets
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }



}