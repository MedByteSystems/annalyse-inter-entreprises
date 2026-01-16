package com.entreprise.models;

import java.time.LocalDate;

public class Entreprise {
    private int id;
    private String nom;
    private String secteur;
    private String ville;
    private String pays;
    private LocalDate dateCreation;
    private double chiffreAffaires;

    // Constructeurs
    public Entreprise() {}

    public Entreprise(int id, String nom, String secteur, String ville,
                      String pays, LocalDate dateCreation, double chiffreAffaires) {
        this.id = id;
        this.nom = nom;
        this.secteur = secteur;
        this.ville = ville;
        this.pays = pays;
        this.dateCreation = dateCreation;
        this.chiffreAffaires = chiffreAffaires;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public LocalDate getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDate dateCreation) { this.dateCreation = dateCreation; }

    public double getChiffreAffaires() { return chiffreAffaires; }
    public void setChiffreAffaires(double chiffreAffaires) { this.chiffreAffaires = chiffreAffaires; }

    @Override
    public String toString() {
        return nom + " (" + secteur + ")";
    }
}