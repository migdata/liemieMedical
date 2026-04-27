package com.example.ppe4_papovo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import java.util.List;


public class AfficheListeVisite extends AppCompatActivity {


    private ListView listView;
    private List<Visite> listeVisite;
    private Modele vmodele;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_affiche_liste_visite);
        // on récupère la liste des visites depuis la base de données
        vmodele=new Modele(this);
        listeVisite = vmodele.listeVisite();
        // on affiche la liste dans la listView
        listView = (ListView)findViewById(R.id.lvListe);
        // on passe la liste des visites à l'adapter
        VisiteAdapter visiteAdapter = new VisiteAdapter(this, listeVisite);
        // on attache l'adapter à la listView
        listView.setAdapter(visiteAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
               // on recupère la visite sur laquelle l'infireme à clique
                Visite laVisite = listeVisite.get(position);
                // on prépare le chargement de la page
                Intent intent = new Intent(getApplicationContext(), AfficheVisite.class);
                // on met l'id de la visite dans l'intent pour que la page suivante la récupère
                intent.putExtra("idVisite", laVisite.getId());
                // on lance l'intent
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Choix : "+listeVisite.get(position).getId(), Toast.LENGTH_LONG).show();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}