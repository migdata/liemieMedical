package com.example.ppe4_papovo;

import java.util.Date; // import de la classe Date
public class Visite {
    private int id,patient,infirmiere,duree ;
    private Date date_prevue;
    private String compte_rendu_patient;
    /*
     * Données à saisir
     */
    private Date date_reelle;
    private String compte_rendu_infirmiere;

    // generer les getters et setter
    public int getId() {
        return id;
    }
    public void setId(int vid) {
        this.id = vid;
    }
    public int getPatient() {
        return patient;
    }
    public void setPatient(int vpatient) {
        this.patient = vpatient;
    }
    public int getInfirmiere() {
        return infirmiere;
    }
    public void setInfirmiere(int vinfirmiere) {
        this.infirmiere = vinfirmiere;
    }
    public int getDuree() {
        return duree;
    }
    public void setDuree(int vduree) {
        this.duree = vduree;
    }
    public Date getDate_prevue() {
        return date_prevue;
    }
    public void setDate_prevue(Date vdate_prevue) {
        this.date_prevue = vdate_prevue;
    }
    public String getCompte_rendu_patient() {
        return compte_rendu_patient;
    }
    public void setCompte_rendu_patient(String vcompte_rendu_patient) {
        this.compte_rendu_patient = vcompte_rendu_patient;
    }
    public Date getDate_reelle() {
        return date_reelle;
    }
    public void setDate_reelle(Date vdate_reelle) {
        this.date_reelle = vdate_reelle;
    }
    public String getCompte_rendu_infirmiere() {
        return compte_rendu_infirmiere;
    }
    public void setCompte_rendu_infirmiere(String vcompte_rendu_infirmiere) {
        this.compte_rendu_infirmiere = vcompte_rendu_infirmiere;
    }

    // Constructeur
    public Visite() {

    }
    public Visite(int vid,int vpatient,int vinfirmiere,Date vdate_prevue ,int vduree,String vcompte_rendu_patient) {
        id=vid;
        patient=vpatient;
        infirmiere=vinfirmiere;
        date_prevue=vdate_prevue;
        duree=vduree;
        compte_rendu_patient=vcompte_rendu_patient;
        date_reelle=vdate_prevue;
        compte_rendu_infirmiere="";
    }

    // methode pour la mise à jour dezs infos de la classe Visite à partir DB4o
    public void recopieVisite(Visite visite) {
        id = visite.getId();
        patient = visite.getPatient();
        infirmiere = visite.getInfirmiere();
        date_prevue = visite.getDate_prevue();
        duree = visite.getDuree();
        compte_rendu_patient = visite.getCompte_rendu_patient();
        date_reelle = visite.getDate_reelle();
        compte_rendu_infirmiere = visite.getCompte_rendu_infirmiere();
    }

}
