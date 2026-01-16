package com.entreprise.models;

import java.time.LocalDate;

public class Relation {
    private int id;
    private Entreprise entrepriseSource;
    private Entreprise entrepriseCible;
    private String typeRelation; // "partenariat", "collaboration", "acquisition"
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;

    // Constructeurs
    public Relation() {}

    public Relation(int id, Entreprise entrepriseSource, Entreprise entrepriseCible,
                    String typeRelation, LocalDate dateDebut, LocalDate dateFin, String description) {
        this.id = id;
        this.entrepriseSource = entrepriseSource;
        this.entrepriseCible = entrepriseCible;
        this.typeRelation = typeRelation;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Entreprise getEntrepriseSource() { return entrepriseSource; }
    public void setEntrepriseSource(Entreprise entrepriseSource) { this.entrepriseSource = entrepriseSource; }

    public Entreprise getEntrepriseCible() { return entrepriseCible; }
    public void setEntrepriseCible(Entreprise entrepriseCible) { this.entrepriseCible = entrepriseCible; }

    public String getTypeRelation() { return typeRelation; }
    public void setTypeRelation(String typeRelation) { this.typeRelation = typeRelation; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return entrepriseSource.getNom() + " → " + entrepriseCible.getNom() + " (" + typeRelation + ")";
    }
}