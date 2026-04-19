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

public class Modele {
    private String db4oFileName;
    private ObjectContainer dataBase;
    private File appDir;

    // ouverture de la base de données ( mon import de Db40 dans le libs
    public void open() {

        db4oFileName = appDir.getAbsolutePath()
                + "/BasePPE4.db4o";
        dataBase = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(),
                db4oFileName);
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

}
