package com.example.ppe4_papovo;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private Menu lemenu;

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

            boolean isActive = (Navigation.findNavController(this, R.id.nav_host_fragment_content_main).getCurrentDestination().getId() == R.id.FirstFragment);
            if (isActive){
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
            else {
                Toast.makeText(this, "Vous etes sur la page de connexion", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (id == R.id.menu_list){
            Toast.makeText(this, "Clic sur liste", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_export){
            Toast.makeText(this, "Clic sur import", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.menu_deconnect){
            // verif que l'actif est bien frag 3 ( bienvneu )

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}