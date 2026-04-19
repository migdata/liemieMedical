package com.example.ppe4_papovo;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

        btnImport.setOnClickListener (new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // appel du serveur pour importer les visites
                String url = "https://www.btssio-carcouet.fr/ppe4/public/mesvisites/3";

                // config des params pour la classe async
                String [] mesparams = new String[3];
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

    public void retourImport(StringBuilder sb)
    {
        //alertmsg("retour Connexion", sb.toString());
        try {
            // acces à la bd avec le modele
            Modele vmodel = new Modele(this);
            // on recupère le json
            JsonElement json = new JsonParser().parse(sb.toString());
            JsonArray varray = json.getAsJsonArray();
            // on parse les données avec le modele
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            ArrayList<Visite> listeVisite = new ArrayList<Visite>();
            for (JsonElement obj : varray) {
                Visite visite = gson.fromJson(obj.getAsJsonObject(), Visite.class);
                visite.setCompte_rendu_infirmiere("");
                visite.setDate_reelle(visite.getDate_prevue());
                listeVisite.add(visite);
            }
            vmodel.deleteVisite(); // on supprime les visites existantes pour eviter les doublons
            vmodel.addVisite(listeVisite); // on insère les visites du json dans la base
            alertmsg("Retour", "Vos informations ont bien été importé avec succès !");
        }
        catch (Exception e) {
            alertmsg("Erreur retour import", e.getMessage());
        }
    }

}
