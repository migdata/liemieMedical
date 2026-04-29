package com.example.ppe4_papovo;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import android.provider.Settings;
import android.view.WindowManager;
import android.os.Build;
import android.widget.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Date;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.lang.reflect.Type;



class BooleanTypeAdapter implements JsonDeserializer<Boolean> {
    public Boolean deserialize(JsonElement json, Type typeOfT,
                               JsonDeserializationContext context) throws JsonParseException {
        if (((JsonPrimitive) json).isBoolean()) {
            return json.getAsBoolean();
        }
        if (((JsonPrimitive) json).isString()) {
            String jsonValue = json.getAsString();
            if (jsonValue.equalsIgnoreCase("true")) {
                return true;
            } else if (jsonValue.equalsIgnoreCase("false")) {
                return false;
            } else {
                return null;
            }
        }

        int code = json.getAsInt();
        return code == 0 ? false :
                code == 1 ? true : null;
    }
}

public class ActImport extends AppCompatActivity {

    private boolean permissionOverlayok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_act_import);
        // on récupère la permission d'affichage false si elle n'est pas accordée
        permissionOverlayok = getIntent().getBooleanExtra("permissionOverlayOk", false);

        Button btnImport = findViewById(R.id.btnImport);

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // appel du serveur pour importer les visites
                String url = "https://www.btssio-carcouet.fr/ppe4/public/mesvisites/3";

                // config des params pour la classe async
                String[] mesparams = new String[3];
                mesparams[0] = "2"; // pour differencier la connexion qui etait à 1
                mesparams[1] = url;
                mesparams[2] = "GET";

                //lancement du thread
                Async mThread = new Async(ActImport.this);
                mThread.execute(mesparams);

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });
    }

    public void alertmsg(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

        // Utilisation de la variable passée dans la valise pour gérer l'overlay
        if (permissionOverlayok && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }
            }
        }
        dialog.show();
    }

    public void retourImport(StringBuilder sb) {
        //alertmsg("retour Connexion", sb.toString());
        try {
            // acces à la bd avec le modele
            Modele vmodel = new Modele(this);
            // on recupère le json
            JsonElement json =  JsonParser.parseString(sb.toString());
            JsonArray varray = json.getAsJsonArray();
            // on parse les données avec le modele
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            ArrayList<Visite> listeVisite = new ArrayList<Visite>();
            ArrayList<Integer> lesPatients = new ArrayList<Integer>();
            // on nettoie la base de données
            vmodel.deleteVisite();
            vmodel.deletePatient();
            vmodel.deleteVisiteSoin();
            for (JsonElement obj : varray) {
                Visite visite = gson.fromJson(obj.getAsJsonObject(), Visite.class);

                // on vérifie que le patient n'est pas déjà dans la liste
                int idpatient = visite.getPatient();

                if (!lesPatients.contains(idpatient)) {
                    lesPatients.add(idpatient);
                }

                // gestion des soins par viiste
                String urlSoinVisite = "https://www.btssio-carcouet.fr/ppe4/public/visitesoins/".concat(Integer.toString(visite.getId()));
                Async mThreadSoinVisite = new Async(ActImport.this);
                mThreadSoinVisite.execute("4", urlSoinVisite, "GET");

                visite.setCompte_rendu_infirmiere("");
                visite.setDate_reelle(visite.getDate_prevue());
                listeVisite.add(visite);
            }

            vmodel.addVisite(listeVisite); // on insère les visites du json dans la base

            // On boucle sur les IDs uniques pour récupérer les détails de chaque patient
            for (Integer pId : lesPatients) {
                String urlP = "https://www.btssio-carcouet.fr/ppe4/public/personne/".concat(pId.toString());
                Async mThreadP = new Async(ActImport.this);
                mThreadP.execute("3", urlP, "GET");
            }

            // 4. IMPORT DU CATALOGUE GÉNÉRAL DES SOINS
            // On ne l'importe que si la table locale est vide
            if (vmodel.listeSoin().size() == 0) {
                String urlS = "https://www.btssio-carcouet.fr/ppe4/public/soins/";
                Async mThreadS = new Async(ActImport.this);
                mThreadS.execute("5", urlS, "GET");
            }

            alertmsg("Retour liste des patients", "Liste des patients : " + lesPatients.size());
            alertmsg("Retour", "Vos informations ont bien été importé avec succès !");


        } catch (Exception e) {
            alertmsg("Erreur retour import", e.getMessage());
        }
    }

    public void retourImportPatient(StringBuilder sb) {
        try {
            Modele vmodel = new Modele(this);
            // Configuration spécifique demandée par le superviseur
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .serializeNulls()
                    .create();

            // On transforme le flux JSON en objet Patient
            Patient unPatient = gson.fromJson(sb.toString(), Patient.class);

            // On enregistre dans DB4O
            vmodel.addUnPatient(unPatient);

            Log.d("Patient", "Import réussi pour : " + unPatient.getNom());

        } catch (JsonParseException e) {
            // Catch spécifique pour les erreurs de format JSON
            Log.d("Patient", "Erreur JSON : " + e.getMessage());
        } catch (Exception e) {
            // Catch général pour les autres erreurs (DB4O, NullPointer, etc.)
            Log.d("Patient", "Autre erreur : " + e.getMessage());
        }
    }

    public void retourImportSoinsVisite(StringBuilder sb) {
        try {
            Modele vmodel = new Modele(this);
            JsonElement json = JsonParser.parseString(sb.toString()); // Utilisation de parseString (plus moderne)
            JsonArray varray = json.getAsJsonArray();
            //Gson gson = new GsonBuilder().serializeNulls().create();
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
            Gson gson = builder.create();
            ArrayList<VisiteSoin> lvs = new ArrayList<VisiteSoin>();
            for (JsonElement obj : varray) {
                VisiteSoin vs = gson.fromJson(obj.getAsJsonObject(), VisiteSoin.class);
                // On s'assure que par défaut le soin n'est pas fait au moment de l'import
                // vs.setRealise(false);
                lvs.add(vs);
            }
            vmodel.addVisiteSoin(lvs);
            Log.d("VisiteSoin", "Import de " + lvs.size() + " soins de visite réussi.");
        } catch (Exception e) {
            Log.d("VisiteSoin", "Erreur import : " + e.getMessage());
        }
    }

    public void retourImportSoins(StringBuilder sb) {
        try {
            Modele vmodel = new Modele(this);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
            JsonElement json = new JsonParser().parse(sb.toString());
            JsonArray varray = json.getAsJsonArray();

            ArrayList<Soin> ls = new ArrayList<Soin>();
            for (JsonElement obj : varray) {
                ls.add(gson.fromJson(obj.getAsJsonObject(), Soin.class));
            }
            vmodel.addSoin(ls);
        } catch (JsonParseException e) {
            Log.d("Soin", "erreur json " + e.getMessage());
        }
    }
    private class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                return null;
            }
        }
    }
}

