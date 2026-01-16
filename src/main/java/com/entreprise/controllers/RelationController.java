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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RelationController implements Initializable {

    @FXML private TableView<Relation> tableView;
    @FXML private TableColumn<Relation, Integer> colId;
    @FXML private TableColumn<Relation, String> colSource;
    @FXML private TableColumn<Relation, String> colCible;
    @FXML private TableColumn<Relation, String> colType;
    @FXML private TableColumn<Relation, LocalDate> colDateDebut;
    @FXML private TableColumn<Relation, LocalDate> colDateFin;
    @FXML private TableColumn<Relation, String> colDescription;
    @FXML private TableColumn<Relation, Void> colActions;

    @FXML private ComboBox<String> cbTypeRelation;
    @FXML private ComboBox<Entreprise> cbEntrepriseSource;
    @FXML private ComboBox<Entreprise> cbEntrepriseCible;
    @FXML private Label lblCount;

    @FXML private Label lblTotalRelations;
    @FXML private Label lblPartenariats;
    @FXML private Label lblCollaborations;
    @FXML private Label lblAcquisitions;

    private RelationService relationService;
    private EntrepriseService entrepriseService;
    private ObservableList<Relation> relations;
    private ObservableList<Entreprise> entreprises;
    private ObservableList<String> typesRelation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        relationService = new RelationService();
        entrepriseService = new EntrepriseService();
        relations = FXCollections.observableArrayList();
        entreprises = FXCollections.observableArrayList();
        typesRelation = FXCollections.observableArrayList("partenariat", "collaboration", "acquisition");

        setupTableColumns();
        loadRelations();
        loadEntreprises();
        loadTypes();
        updateStats();

        cbTypeRelation.setItems(typesRelation);
        cbEntrepriseSource.setItems(entreprises);
        cbEntrepriseCible.setItems(entreprises);

        cbEntrepriseSource.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });

        cbEntrepriseCible.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSource.setCellValueFactory(cellData -> {
            Entreprise source = cellData.getValue().getEntrepriseSource();
            return new javafx.beans.property.SimpleStringProperty(
                    source.getNom() + " (" + source.getSecteur() + ")");
        });
        colCible.setCellValueFactory(cellData -> {
            Entreprise cible = cellData.getValue().getEntrepriseCible();
            return new javafx.beans.property.SimpleStringProperty(
                    cible.getNom() + " (" + cible.getSecteur() + ")");
        });
        colType.setCellValueFactory(new PropertyValueFactory<>("typeRelation"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colActions.setCellFactory(new Callback<TableColumn<Relation, Void>, TableCell<Relation, Void>>() {
            @Override
            public TableCell<Relation, Void> call(TableColumn<Relation, Void> param) {
                return new TableCell<Relation, Void>() {
                    private final Button btnEdit = new Button("✏️");
                    private final Button btnDelete = new Button("🗑️");
                    private final HBox hbox = new HBox(5, btnEdit, btnDelete);

                    {
                        btnEdit.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                        btnEdit.setOnAction(event -> {
                            Relation relation = getTableView().getItems().get(getIndex());
                            editRelation(relation);
                        });

                        btnDelete.setOnAction(event -> {
                            Relation relation = getTableView().getItems().get(getIndex());
                            deleteRelation(relation);
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

    private void loadRelations() {
        relations.clear();
        relations.addAll(relationService.getAllRelations());
        tableView.setItems(relations);
        updateCount();
    }

    private void loadEntreprises() {
        entreprises.clear();
        entreprises.addAll(relationService.getAllEntreprisesForCombo());
    }

    private void loadTypes() {
        cbTypeRelation.setItems(typesRelation);
    }

    private void updateCount() {
        lblCount.setText("(" + relations.size() + " relations)");
    }

    private void updateStats() {
        try {
            List<String[]> stats = relationService.getRelationStats();
            int total = relations.size();
            int partenariats = 0;
            int collaborations = 0;
            int acquisitions = 0;

            for (String[] stat : stats) {
                switch (stat[0]) {
                    case "partenariat":
                        partenariats = Integer.parseInt(stat[1]);
                        break;
                    case "collaboration":
                        collaborations = Integer.parseInt(stat[1]);
                        break;
                    case "acquisition":
                        acquisitions = Integer.parseInt(stat[1]);
                        break;
                }
            }

            lblTotalRelations.setText(String.valueOf(total));
            lblPartenariats.setText(String.valueOf(partenariats));
            lblCollaborations.setText(String.valueOf(collaborations));
            lblAcquisitions.setText(String.valueOf(acquisitions));
        } catch (Exception e) {
            AlertUtils.showError("Erreur", "Impossible de charger les statistiques: " + e.getMessage());
        }
    }

    @FXML
    private void goToHome() {
        SceneManager.getInstance().switchToHomeView();
    }

    @FXML
    private void onFilterType() {
        String type = cbTypeRelation.getValue();
        if (type == null || type.isEmpty()) {
            loadRelations();
        } else {
            relations.clear();
            relations.addAll(relationService.getRelationsByType(type));
            updateCount();
        }
    }

    @FXML
    private void onFilterEntreprise() {
        Entreprise source = cbEntrepriseSource.getValue();
        Entreprise cible = cbEntrepriseCible.getValue();

        if (source == null && cible == null) {
            loadRelations();
            return;
        }

        relations.clear();
        List<Relation> allRelations = relationService.getAllRelations();

        for (Relation relation : allRelations) {
            boolean matches = true;

            if (source != null && relation.getEntrepriseSource().getId() != source.getId()) {
                matches = false;
            }

            if (cible != null && relation.getEntrepriseCible().getId() != cible.getId()) {
                matches = false;
            }

            if (matches) {
                relations.add(relation);
            }
        }

        updateCount();
    }

    @FXML
    private void resetFilters() {
        cbTypeRelation.setValue(null);
        cbEntrepriseSource.setValue(null);
        cbEntrepriseCible.setValue(null);
        loadRelations();
    }

    @FXML
    private void showAddDialog() {
        Dialog<Relation> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une relation");
        dialog.setHeaderText("Définissez une nouvelle relation entre entreprises");

        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Entreprise> cbSource = new ComboBox<>(entreprises);
        cbSource.setPromptText("Entreprise source");
        cbSource.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });

        ComboBox<Entreprise> cbCible = new ComboBox<>(entreprises);
        cbCible.setPromptText("Entreprise cible");
        cbCible.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });

        ComboBox<String> cbType = new ComboBox<>(typesRelation);
        cbType.setPromptText("Type de relation");

        DatePicker dpDateDebut = new DatePicker();
        dpDateDebut.setValue(LocalDate.now());
        DatePicker dpDateFin = new DatePicker();
        TextArea txtDescription = new TextArea();
        txtDescription.setPromptText("Description de la relation");
        txtDescription.setPrefRowCount(3);

        grid.add(new Label("Entreprise source:"), 0, 0);
        grid.add(cbSource, 1, 0);
        grid.add(new Label("Entreprise cible:"), 0, 1);
        grid.add(cbCible, 1, 1);
        grid.add(new Label("Type de relation:"), 0, 2);
        grid.add(cbType, 1, 2);
        grid.add(new Label("Date début:"), 0, 3);
        grid.add(dpDateDebut, 1, 3);
        grid.add(new Label("Date fin (optionnel):"), 0, 4);
        grid.add(dpDateFin, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(txtDescription, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    if (cbSource.getValue() == null) {
                        AlertUtils.showError("Erreur", "Veuillez sélectionner une entreprise source");
                        return null;
                    }

                    if (cbCible.getValue() == null) {
                        AlertUtils.showError("Erreur", "Veuillez sélectionner une entreprise cible");
                        return null;
                    }

                    if (cbSource.getValue().getId() == cbCible.getValue().getId()) {
                        AlertUtils.showError("Erreur", "Une entreprise ne peut pas avoir de relation avec elle-même");
                        return null;
                    }

                    if (cbType.getValue() == null) {
                        AlertUtils.showError("Erreur", "Veuillez sélectionner un type de relation");
                        return null;
                    }

                    if (dpDateDebut.getValue() == null) {
                        AlertUtils.showError("Erreur", "Veuillez sélectionner une date de début");
                        return null;
                    }

                    if (dpDateFin.getValue() != null && dpDateFin.getValue().isBefore(dpDateDebut.getValue())) {
                        AlertUtils.showError("Erreur", "La date de fin doit être postérieure à la date de début");
                        return null;
                    }

                    Relation relation = new Relation();
                    relation.setEntrepriseSource(cbSource.getValue());
                    relation.setEntrepriseCible(cbCible.getValue());
                    relation.setTypeRelation(cbType.getValue());
                    relation.setDateDebut(dpDateDebut.getValue());
                    relation.setDateFin(dpDateFin.getValue());
                    relation.setDescription(txtDescription.getText());

                    return relation;
                } catch (Exception e) {
                    AlertUtils.showError("Erreur", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Relation> result = dialog.showAndWait();
        result.ifPresent(relation -> {
            try {
                if (relationService.addRelation(relation)) {
                    AlertUtils.showInfo("Succès", "Relation ajoutée avec succès!");
                    loadRelations();
                    updateStats();
                } else {
                    AlertUtils.showError("Erreur", "Impossible d'ajouter la relation");
                }
            } catch (IllegalArgumentException e) {
                AlertUtils.showError("Erreur de validation", e.getMessage());
            }
        });
    }

    private void editRelation(Relation relation) {
        Dialog<Relation> dialog = new Dialog<>();
        dialog.setTitle("Modifier la relation");
        dialog.setHeaderText("Modifiez les informations de la relation");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Entreprise> cbSource = new ComboBox<>(entreprises);
        cbSource.setValue(relation.getEntrepriseSource());
        cbSource.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });

        ComboBox<Entreprise> cbCible = new ComboBox<>(entreprises);
        cbCible.setValue(relation.getEntrepriseCible());
        cbCible.setConverter(new StringConverter<Entreprise>() {
            @Override
            public String toString(Entreprise entreprise) {
                return entreprise != null ? entreprise.getNom() + " (" + entreprise.getSecteur() + ")" : "";
            }

            @Override
            public Entreprise fromString(String string) {
                return null;
            }
        });

        ComboBox<String> cbType = new ComboBox<>(typesRelation);
        cbType.setValue(relation.getTypeRelation());

        DatePicker dpDateDebut = new DatePicker(relation.getDateDebut());
        DatePicker dpDateFin = new DatePicker(relation.getDateFin());
        TextArea txtDescription = new TextArea(relation.getDescription());
        txtDescription.setPrefRowCount(3);

        grid.add(new Label("Entreprise source:"), 0, 0);
        grid.add(cbSource, 1, 0);
        grid.add(new Label("Entreprise cible:"), 0, 1);
        grid.add(cbCible, 1, 1);
        grid.add(new Label("Type de relation:"), 0, 2);
        grid.add(cbType, 1, 2);
        grid.add(new Label("Date début:"), 0, 3);
        grid.add(dpDateDebut, 1, 3);
        grid.add(new Label("Date fin (optionnel):"), 0, 4);
        grid.add(dpDateFin, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(txtDescription, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    if (cbSource.getValue() == null || cbCible.getValue() == null || cbType.getValue() == null) {
                        AlertUtils.showError("Erreur", "Tous les champs obligatoires doivent être remplis");
                        return null;
                    }

                    if (cbSource.getValue().getId() == cbCible.getValue().getId()) {
                        AlertUtils.showError("Erreur", "Une entreprise ne peut pas avoir de relation avec elle-même");
                        return null;
                    }

                    if (dpDateDebut.getValue() == null) {
                        AlertUtils.showError("Erreur", "La date de début est obligatoire");
                        return null;
                    }

                    if (dpDateFin.getValue() != null && dpDateFin.getValue().isBefore(dpDateDebut.getValue())) {
                        AlertUtils.showError("Erreur", "La date de fin doit être postérieure à la date de début");
                        return null;
                    }

                    Relation updated = new Relation();
                    updated.setId(relation.getId());
                    updated.setEntrepriseSource(cbSource.getValue());
                    updated.setEntrepriseCible(cbCible.getValue());
                    updated.setTypeRelation(cbType.getValue());
                    updated.setDateDebut(dpDateDebut.getValue());
                    updated.setDateFin(dpDateFin.getValue());
                    updated.setDescription(txtDescription.getText());

                    return updated;
                } catch (Exception e) {
                    AlertUtils.showError("Erreur", e.getMessage());
                    return null;
                }
            }
            return null;
        });

        Optional<Relation> result = dialog.showAndWait();
        result.ifPresent(updatedRelation -> {
            try {
                if (relationService.updateRelation(updatedRelation)) {
                    AlertUtils.showInfo("Succès", "Relation modifiée avec succès!");
                    loadRelations();
                    updateStats();
                } else {
                    AlertUtils.showError("Erreur", "Impossible de modifier la relation");
                }
            } catch (IllegalArgumentException e) {
                AlertUtils.showError("Erreur de validation", e.getMessage());
            }
        });
    }

    private void deleteRelation(Relation relation) {
        if (AlertUtils.showConfirmation("Confirmation",
                "Voulez-vous vraiment supprimer cette relation?\n" +
                        relation.getEntrepriseSource().getNom() + " → " +
                        relation.getEntrepriseCible().getNom() + " (" +
                        relation.getTypeRelation() + ")")) {
            try {
                if (relationService.deleteRelation(relation.getId())) {
                    AlertUtils.showInfo("Succès", "Relation supprimée avec succès!");
                    loadRelations();
                    updateStats();
                } else {
                    AlertUtils.showError("Erreur", "Impossible de supprimer la relation");
                }
            } catch (Exception e) {
                AlertUtils.showError("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }




}