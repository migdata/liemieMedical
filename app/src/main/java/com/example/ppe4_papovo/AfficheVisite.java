package com.example.ppe4_papovo;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Utilise SimpleDateFormat pour plus de simplicité
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AfficheVisite extends AppCompatActivity {

    private ListView listView;
    private Date ddatereelle;
    private List<VisiteSoin> listeSoin;
    private Visite laVisite;
    private Modele vmodel;

    // On définit le format de date une fois pour toutes
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

    private Calendar myCalendar = Calendar.getInstance();
    private EditText datereelle;
    private int idVisite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_visite);

        // 1. Récupération de l'ID passé par la liste
        idVisite = getIntent().getIntExtra("idVisite", 0);
        vmodel = new Modele(this);
        laVisite = vmodel.trouveVisite(idVisite);

        // 2. Initialisation du champ Date Réelle
        datereelle = (EditText) findViewById(R.id.visiteDateReelle);

        if (laVisite.getDate_reelle() == null) {
            ddatereelle = new Date();
        } else {
            ddatereelle = laVisite.getDate_reelle();
        }

        // On affiche la date au propre
        datereelle.setText(sdf.format(ddatereelle));

        // Listener pour le calendrier
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Mise à jour du texte et de la variable
                datereelle.setText(sdf.format(myCalendar.getTime()));
                ddatereelle = myCalendar.getTime();
            }
        };

        // Ouverture du calendrier au clic sur l'EditText
        datereelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AfficheVisite.this, dateListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // 3. Remplissage des champs (Patient/Visite)
        remplirChamps();

        // 4. Bouton Save
        Button btnSave = findViewById(R.id.visitesave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauvegarder();
            }
        });
    }

    private void remplirChamps() {
        Patient lePatient = vmodel.trouvePatient(laVisite.getPatient());

        TextView tvId = findViewById(R.id.t1);
        tvId.setText("Visite n° " + laVisite.getId());

        TextView tvDateP = findViewById(R.id.visiteDatePrevue);
        // On formate la date prévue aussi
        tvDateP.setText(sdf.format(laVisite.getDate_prevue()));

        // les infos du patient
        if (lePatient != null) {
            TextView tvNom = findViewById(R.id.visiteNom);
            TextView tvPrenom = findViewById(R.id.visitePrenom);
            tvNom.setText(lePatient.getNom());
            tvPrenom.setText(lePatient.getPrenom());

            // On remplit les adresses si elles existent dans ton Layout
            TextView tvAd1 = findViewById(R.id.visitead1);
            tvAd1.setText(lePatient.getAd1());
            TextView tvCp = findViewById(R.id.visitecp);
            tvCp.setText("CP : " + lePatient.getCp());
            TextView tvVille = findViewById(R.id.visiteville);
            tvVille.setText(lePatient.getVille());
            TextView tvNumPort = findViewById(R.id.visitenumport);
            tvNumPort.setText("Port : " + lePatient.getTel_port());
            TextView tvNumFixe = findViewById(R.id.visitenumfixe);
            tvNumFixe.setText("Fixe : " + lePatient.getTel_fixe());
        } else {
            Toast.makeText(this, "Patient introuvable", Toast.LENGTH_SHORT).show();
        }

        EditText etCommentaire = findViewById(R.id.visitecommentaire);
        etCommentaire.setText(laVisite.getCompte_rendu_infirmiere());
    }

    private void sauvegarder() {
        EditText etCommentaire = findViewById(R.id.visitecommentaire);

        // Mise à jour de l'objet avant sauvegarde
        laVisite.setCompte_rendu_infirmiere(etCommentaire.getText().toString());
        laVisite.setDate_reelle(ddatereelle);

        try {

            // Enregistrement via le Modele (DB4O)
            vmodel.saveVisite(laVisite);

            Toast.makeText(this, "Modifications enregistrées !", Toast.LENGTH_SHORT).show();
            finish(); // Retour à la liste
        }
        catch (Exception e )
        {
            Toast.makeText(this, "Erreur lors de la sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }
}