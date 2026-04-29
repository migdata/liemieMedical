package com.example.ppe4_papovo;

import android.os.Environment;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

public class Modele {
    private String db4oFileName;
    private ObjectContainer dataBase;
    private File appDir;

    // ouverture de la base de données ( mon import de Db40 dans le libs
    public void open() {

        try {
            db4oFileName = appDir.getAbsolutePath() + "/BasePPE4.db4o";
            // On vérifie si dataBase n'est pas déjà ouvert et actif
            if (dataBase == null || dataBase.ext().isClosed()) {
                dataBase = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), db4oFileName);
            }
        } catch (Exception e) {
            Log.e("Modele", "Erreur critique ouverture base : " + e.getMessage());
        }
    }



    // construction de la base de données || initialisation de l'environnement de stockage sinon creation de la base
    public Modele(Context ct) {
        appDir = new File(ct.getFilesDir()
                + "/baseDB4o");
        if (!appDir.exists() && !appDir.isDirectory()) {
            appDir.mkdirs();

        }
        try {
            File testFile = new File(appDir, "test_presence.txt");
            testFile.createNewFile();
            android.util.Log.d("DossierBD", "Fichier de test créé dans : " + testFile.getAbsolutePath());
        } catch (Exception e) {
            android.util.Log.e("DossierDB", "Erreur de création : " + e.getMessage());
        }
    }

    // listeVisite  qui permet de renvoyer une ArrayList des objets de la classe Visite
    // reviens a un select All de la base
    public ArrayList<Visite> listeVisite() {
        open();
        ArrayList<Visite> listeVisite = new ArrayList<Visite>();
        ObjectSet<Visite> result = dataBase.queryByExample(Visite.class);
        while (result.hasNext()) {
            listeVisite.add(result.next());
        }
        dataBase.close();
        return listeVisite;
    }

    // trouveVisite  qui permet de renvoyer une instance de Visite à partir de son identifiant ;
    // reviens a un select Where de la base
    public Visite trouveVisite (int id) {
        open();
        Visite vretour = new Visite();
        vretour.setId(id);
        ObjectSet<Visite> result = dataBase.queryByExample(vretour);
        vretour = (Visite) result.next();
        dataBase.close();
        return vretour;
    }

    //saveVisite avec comme argument une instance de Visite qui permet de sauvegarder cette instance
// (c.f. attention : si cette Visite existe déjà il faut mettre à jour celui-ci dans
// la base via un appel à la méthode recopieVisite de Visite)
    // update ou insert de la base
    public void saveVisite(Visite Visite) {
        open();
        Visite vretour = new Visite();
        vretour.setId(Visite.getId());
        ObjectSet<Visite> result = dataBase.queryByExample(vretour);
        if (result.size() == 0) {
            dataBase.store(Visite);
        } else {
            vretour = (Visite) result.next();
            vretour.recopieVisite(Visite);
            dataBase.store(vretour);
        }
        dataBase.close();
    }

    // deleteVisite() qui permet de supprimer toutes les instances de la classe Visite
    public void deleteVisite() {
        open();
        ObjectSet<Visite> result = dataBase.queryByExample(Visite.class);
        while (result.hasNext()) {
            dataBase.delete(result.next());
        }
        dataBase.close();
    }

    // addVisite(ArrayList<Visite> vVisite) qui, à partir d'une collection de Visite,
    // va les ajouter à DB4o. Nous ne testerons pas l'existence de ces objets puisque
    // c'est une création (appel de la méthode après l'appel de deleteVisite()).
    public void addVisite(ArrayList<Visite> vVisite) {
        open();
        for (Visite v : vVisite) {
            dataBase.store(v);
        }
        dataBase.close();
    }

    // patient
    /*
     *   Méthodes Spécifiques à la classe Patient
     */

    // Trouve un patient à partir de son id
    public Patient trouvePatient(int id) {
        open();
        Patient vretour = new Patient();
        vretour.setId(id);
        ObjectSet<Patient> result = dataBase.queryByExample(vretour);
        if (result.hasNext()) {
            vretour = (Patient) result.next();
        }
        else {
            vretour = null;
        }

        dataBase.close();
        return vretour;
    }

    // deletePatient() qui permet de supprimer toutes les instances de la classe Patient
    public void deletePatient() {
        open();
        ObjectSet<Patient> result = dataBase.queryByExample(Patient.class);
        while (result.hasNext()) {
            dataBase.delete(result.next());
        }
        dataBase.close();
    }
    // addPatient(ArrayList<Patient> vPatient) qui, à partir d'une collection de Patient, va les ajouter à DB4o. Nous ne testerons pas l'existence de ces objets puisque c'est une création (appel de la méthode après l'appel de deletePatient()).
    public void addPatient(ArrayList<Patient> vPatient) {
        open();
        for (Patient v : vPatient) {
            dataBase.store(v);
        }
        dataBase.close();
    }

    // Ajoute à DB4o un objet de la classe patient
    public void addUnPatient(Patient vPatient) {
        open();
        dataBase.store(vPatient);
        dataBase.commit();
        dataBase.close();
    }

    /*
     *   Méthodes Spécifiques à la classe Soin
     */
// listeSoin retourne la liste de tous les soins
    public ArrayList<Soin> listeSoin() {
        open();
        ArrayList<Soin> listeSoin = new ArrayList<Soin>();
        ObjectSet<Soin> result = dataBase.queryByExample(Soin.class);
        while (result.hasNext()) {
            listeSoin.add(result.next());
        }
        dataBase.close();
        return listeSoin;
    }
    // trouveSoin retourne le soin à partir de sa clé (3 int passés en paramètre
    public Soin trouveSoin(int id_categ_soins, int id_type_soins, int id) {
        open();
        Soin critere = new Soin();
        critere.setId_categ_soins(id_categ_soins);
        critere.setId_type_soins(id_type_soins);
        critere.setId(id);

        ObjectSet<Soin> result = dataBase.queryByExample(critere);
        Soin vretour = null;

        if (result.hasNext()) {
            vretour = (Soin) result.next();
            Log.d("Soins", "Recherche soins ok");
        } else {
            Log.d("Soins", "Recherche soins introuvable : " + id_categ_soins + "/" + id_type_soins + "/" + id);
        }

        dataBase.close();
        return vretour;
    }
    // deleteSoin() qui permet de supprimer toutes les instances de la classe Soin
    public void deleteSoin() {
        open();
        ObjectSet<Soin> result = dataBase.queryByExample(Soin.class);
        while (result.hasNext()) {
            dataBase.delete(result.next());
        }
        dataBase.close();
    }
    // addSoin(ArrayList<Soin> vSoin) qui, à partir d'une collection de Soin, va les ajouter à DB4o. Nous ne testerons pas l'existence de ces objets puisque c'est une création (appel de la méthode après l'appel de deleteSoin()).
    public void addSoin(ArrayList<Soin> vSoin) {
        open();
        for (Soin v : vSoin) {
            dataBase.store(v);
        }
        dataBase.close();
    }

    // addUnSoin ajoute un soin à DB4o à partir d'un objet de la classe Soin
    public void addUnSoin(Soin vSoin) {
        open();
        dataBase.store(vSoin);

        dataBase.close();
    }

    /*
     *   Méthodes Spécifiques à la classe VisiteSoin
     */
// listeVisiteSoin retourne la liste de tous les objet VisiteSoins
    public ArrayList<VisiteSoin> listeVisiteSoin() {
        open();
        ArrayList<VisiteSoin> listeVisiteSoin = new ArrayList<VisiteSoin>();
        ObjectSet<VisiteSoin> result = dataBase.queryByExample(VisiteSoin.class);
        while (result.hasNext()) {
            listeVisiteSoin.add(result.next());
        }
        dataBase.close();
        return listeVisiteSoin;
    }
    // trouveVisiteSoin retourne l'objet de la classe VisiteSin à partir de sa clé (4 int passés en paramètre)
    public VisiteSoin trouveVisiteSoin(int visite, int id_categ_soins, int id_type_soins, int id) {
        open();
        VisiteSoin vretour = new VisiteSoin();
        vretour.setVisite(visite);
        vretour.setId_categ_soins(id_categ_soins);
        vretour.setId_type_soins(id_type_soins);
        vretour.setId_soins(id);
        ObjectSet<VisiteSoin> result = dataBase.queryByExample(vretour);
        vretour = (VisiteSoin) result.next();
        dataBase.close();
        return vretour;
    }
    // trouveSoinsUneVisite retourne la collection de tous les objets VisiteSoin d'une visite à parir de son identifiant
    public ArrayList<VisiteSoin> trouveSoinsUneVisite(int visite) {
        open();
        VisiteSoin vretour = new VisiteSoin();
        vretour.setVisite(visite);
        ArrayList<VisiteSoin> listeVisiteSoin = new ArrayList<VisiteSoin>();
        ObjectSet<VisiteSoin> result = dataBase.queryByExample(vretour);
        while (result.hasNext()) {
            listeVisiteSoin.add(result.next());
        }
        dataBase.close();
        return listeVisiteSoin;
    }
    //  saveVisiteSoin sauvegarde un objet de la classe VisiteSoin dans DB4O en vérifiant son existence
    public void saveVisiteSoin(VisiteSoin VisiteSoin) {
        open();
        VisiteSoin vretour = new VisiteSoin();
        vretour.setVisite(VisiteSoin.getVisite());
        vretour.setId_soins(VisiteSoin.getId_soins());
        vretour.setId_categ_soins(VisiteSoin.getId_categ_soins());
        vretour.setId_type_soins(VisiteSoin.getId_type_soins());
        ObjectSet<VisiteSoin> result = dataBase.queryByExample(vretour);
        if (result.size() == 0) {
            dataBase.store(VisiteSoin);
        } else {
            vretour = (VisiteSoin) result.next();
            vretour.recopieVisiteSoin(VisiteSoin);
            dataBase.store(vretour);
        }
        dataBase.close();
    }
    // deleteVisiteSoin() qui permet de supprimer toutes les instances de la classe VisiteSoin
    public void deleteVisiteSoin() {
        open();
        ObjectSet<VisiteSoin> result = dataBase.queryByExample(VisiteSoin.class);
        while (result.hasNext()) {
            dataBase.delete(result.next());
        }
        dataBase.close();
    }
    // addVisiteSoin(ArrayList<VisiteSoin> vVisiteSoin) qui, à partir d'une collection de VisiteSoin, va les ajouter à DB4o. Nous ne testerons pas l'existence de ces objets puisque c'est une création (appel de la méthode après l'appel de deleteVisiteSoin()).
    public void addVisiteSoin(ArrayList<VisiteSoin> vVisiteSoin) {
        open(); //
        try {
            for (VisiteSoin v : vVisiteSoin) {
                dataBase.store(v); //
            }
            dataBase.commit(); // FORCE l'écriture physique sur le stockage
        } finally {
            dataBase.close(); //
        }
    }




}
