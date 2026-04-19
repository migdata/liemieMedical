package com.example.ppe4_papovo;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ppe4_papovo.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.pm.PackageManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Menu lemenu;

    // Déclaration des constantes et variables
    //private String[] permissions = { Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS};
    private String[] permissions = { Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE};
    private static final int MULTIPLE_PERMISSIONS = 10;
    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private boolean permissionOverlayAsked = false;
    private boolean permissionOK = false;
    private  String nomInfimiere;
    private String prenomInfimiere;

    private String currentIdSaisi;
    private String currentMotDePasseSaisi;

    public String getPrenomInfimiere() {
        return prenomInfimiere;
    }

    public String getNomInfimiere() {
        return nomInfimiere;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // super.onResume() n'a rien à faire ici, onStart suffit
        if (!permissionOverlayAsked && !Settings.canDrawOverlays(this)) {
            checkPermissionAlert();
            permissionOverlayAsked= true;
        }
        checkPermissions(); // Correction du nom de la méthode
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                permissionOK = false;
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[0]), MULTIPLE_PERMISSIONS);
            } else {
                permissionOK = true;
            }
        } else {
            permissionOK = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissionsList, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults);
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0) {
                StringBuilder permissionsDenied = new StringBuilder();
                for (int i = 0; i < permissionsList.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied.append("\n").append(permissionsList[i]);
                    }
                }

                if (permissionsDenied.length() > 0) {
                    permissionOK = false;
                    Toast.makeText(getApplicationContext(),
                            "Permissions nécessaires pour continuer :" + permissionsDenied.toString(),
                            Toast.LENGTH_LONG).show();
                } else {
                    permissionOK = true;
                }
            }
        }
    }

    public void checkPermissionAlert() {
        permissionOverlayAsked = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    alertmsg("Permission ALERT", "Permission d'affichage activé");
                } else {
                    Toast.makeText(this, "Pbs l'affichage est desactivé", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void alertmsg(String title, String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setTitle(title)
                .setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();

        // Paramétrage pour l'overlay selon la version d'Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    // methode declenchée quand le serveur répond

    public void retourConnexion(StringBuilder sb) {
        // sb contient tout le json renvoyé par le site web
        // analyser le json reçu via Gson
        //JsonElement root
        //alertmsg("retour connexion ", sb.toString()); // je recupère toutes les données en format json


        try {

            JsonElement root = JsonParser.parseString(sb.toString());
            JsonObject jsonObject = root.getAsJsonObject();

            // verif de la clé

            if (jsonObject.has("status")) {
                alertmsg("erreur d'authentification", "Echec de connexion ");
            } else {
                // on recupere les données mais avant on vérifie
                if (jsonObject.has("nom") && jsonObject.has("prenom")) {
                    this.nomInfimiere = jsonObject.get("nom").getAsString();
                    this.prenomInfimiere = jsonObject.get("prenom").getAsString();
                   // Log.d("DONNES", "nom : " + this.nomInfimiere + " prenom : " + this.prenomInfimiere);
                   if(currentIdSaisi != null && currentMotDePasseSaisi != null) {
                       // ecriture des données dans les shared preferences
                       SharedPreferences sp = getSharedPreferences("Mes Prefs", MODE_PRIVATE);
                       SharedPreferences.Editor editor = sp.edit();
                       editor.putString("DernierId",currentIdSaisi);
                       editor.putString("DernierMotDePasse", securiteKeyMD5(currentMotDePasseSaisi));
                       editor.putString("DernierNom", this.nomInfimiere);
                       editor.putString("DernierPrenom", this.prenomInfimiere);
                       editor.apply();
                   }
                   else {
                       Log.e("DEBUG", " les variables sont nulles  ");
                   }


                }


                // Dans ce cas on lui donne l'acces à au fragment 3
                menuConnecte();
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                        .navigate(R.id.action_SecondFragment_to_ThirdFragment);
            }
        }
        catch (Exception e) {
           // Log.e("Erreur de données", "Erreur de données " + e.getMessage());
            alertmsg("Erreur de données ", "le serveur ne repond pas ");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.lemenu = menu;
        menuDeconnecte(); // tant que l'user nest pas connecte
        return true;
    }

    public void menuConnecte(){
        if(lemenu != null){
            lemenu.findItem(R.id.menu_connect).setVisible(false);
            lemenu.findItem(R.id.menu_list).setVisible(true);
            lemenu.findItem(R.id.menu_import).setVisible(true);
            lemenu.findItem(R.id.menu_export).setVisible(true);
            lemenu.findItem(R.id.menu_deconnect).setVisible(true);
            lemenu.findItem(R.id.action_settings).setVisible(false);
        }
    }

    public void menuDeconnecte(){
        if(lemenu != null){
            lemenu.findItem(R.id.menu_connect).setVisible(true);
            lemenu.findItem(R.id.menu_list).setVisible(false);
            lemenu.findItem(R.id.menu_import).setVisible(false);
            lemenu.findItem(R.id.menu_export).setVisible(false);
            lemenu.findItem(R.id.menu_deconnect).setVisible(false);
            lemenu.findItem(R.id.action_settings).setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_connect){
            // verif du frag actif

            if (!permissionOK){
                // pourquoi ça bloque
               alertmsg("Permission manquantes", "Permission non accordée");
               checkPermissions(); // on relance la vérification des permissions
            }
            else{
                boolean isFirstFrag = (Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getCurrentDestination().getId() == R.id.FirstFragment);
                if (isFirstFrag){
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_SecondFragment);
                }
            }

            return true;
        }
        if (id == R.id.menu_list){
            Toast.makeText(this, "Clic sur liste", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, AfficheListeVisite.class);
            startActivity(intent);
            return true;

        }
        if (id == R.id.menu_export){
            Toast.makeText(this, "Clic sur import", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_deconnect){
            // verif que l'actif est bien frag 3 ( bienvenue )

            boolean isThirdActive = (Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getCurrentDestination().getId() == R.id.ThirdFragment);
            if (isThirdActive){
                menuDeconnecte();

                // nav vers grag 1
                 Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_ThirdFragment_to_FirstFragment);
                Toast.makeText(this, "Vous etes deconnecté", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Deconnexion impossible ", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_import){
            Intent intent = new Intent(this, ActImport.class);
            //on met en attente la réponse de l'activité
            intent.putExtra("permissionOverlayOk", this.permissionOK);  // on envoie la permission d'affichage
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // fonction de hachage MD5
    public static String securiteKeyMD5(String s) {
        try{
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes());
            BigInteger bi = new BigInteger(1, array);

            String hashText = bi.toString(16);
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;
        }
        catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // testMotDePasse
    public void testMotDePasse(String vid, String vmotDePasse) {

        // dans un premier temps il faut stocker dans la mémoire la saisie de l'user
        this.currentIdSaisi = vid;
        this.currentMotDePasseSaisi = vmotDePasse;
        // on récupère les données stockées
        SharedPreferences sp = getSharedPreferences("Mes Prefs", MODE_PRIVATE);
        String mesIdStocker = sp.getString("DernierId", "");
        String mesMotDePasseStocker = sp.getString("DernierMotDePasse", "");

        // on hash le mdp saisi
        String mdpSaisiHash = securiteKeyMD5(vmotDePasse);

        Log.d("Authentification", "mdp saisi hash : " + mdpSaisiHash);

        // verif local des données

        if(vid.equals(mesIdStocker) && mdpSaisiHash.equals(mesMotDePasseStocker)){
            Log.d("Authentification", "Connexion reussi avec shared preferences");

            // on recupère aussi le nom et prenom stockés pour le fragment 3
            this.nomInfimiere = sp.getString("DernierNom", "");
            this.prenomInfimiere = sp.getString("DernierPrenom", "");

            menuConnecte();
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_SecondFragment_to_ThirdFragment);
        }
        else{
            // si pas de correspondance , on appelle la méthode de connexion avec le serveur ( webService)
            Log.d("Authentification", "les identifiants ne correspondent pas à ceux de shared preferences");
            String url = "https://www.btssio-carcouet.fr/ppe4/public/connect2/"
                    + vid + "/" + vmotDePasse + "/infirmiere";

            String [] mesparams = new String[3];
            mesparams[0] = "1";
            mesparams[1] = url;
            mesparams[2] = "GET";

            Async mThreadCon = new Async(this);
            mThreadCon.execute(mesparams);

        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}