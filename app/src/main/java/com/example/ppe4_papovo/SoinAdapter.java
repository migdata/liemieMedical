package com.example.ppe4_papovo;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


public class SoinAdapter extends android.widget.BaseAdapter {

    private List<VisiteSoin> listSoin;
    private LayoutInflater layoutInflater; //Cet attribut a pour mission de charger notre fichier XML de la vue pour l'item.
    private DateFormat df = new DateFormat();
    private Modele vmodel;

    // Structure du ViewHolder pour optimiser la memoire
    static class ViewHolder {
        TextView textLibelle;
        CheckBox checkRealise;
    }

    // surcharge constructeur

    public SoinAdapter(Context context, List<VisiteSoin> vListSoin) {
        super();
        layoutInflater = LayoutInflater.from(context);
        listSoin = vListSoin;
        vmodel=new Modele(context);

    }

    @Override
    public int getCount() {
        return listSoin.size();
    }

    @Override
    public Object getItem(int position) {
        return listSoin.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        // Si la vue n'existe pas encore, on l'inflate depuis le XML
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.vueunevisite, parent, false);

            // On crée le ViewHolder et on y stocke les références aux vues
            holder = new ViewHolder();
            holder.textLibelle  = convertView.findViewById(R.id.vuesoinslibel);
            holder.checkRealise = convertView.findViewById(R.id.vuesoinsrealise);

            // On attache le holder à la vue pour le réutiliser
            convertView.setTag(holder);
        } else {
            // La vue existe déjà : on récupère le holder sans refaire findViewById
            holder = (ViewHolder) convertView.getTag();
        }

        // Récupération du soin correspondant à cette position
        VisiteSoin soin = listSoin.get(position);

        // Remplissage des vues avec les données du soin
        holder.textLibelle.setText("Soin n° " + soin.getId_soins());
        holder.checkRealise.setChecked(soin.isRealise());

        // Couleur de fond alternée pour améliorer la lisibilité
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.parseColor("#F0F0F0"));
        }

        // Sauvegarde du soin lors du clic sur la checkbox
        holder.checkRealise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = v.findViewById(R.id.vuesoinsrealise);
                listSoin.get(position).setRealise(cb.isChecked());
                vmodel.saveVisiteSoin(listSoin.get(position));
                Log.d("SoinAdapter", "Soin sauvegardé : position=" + position
                        + " réalisé=" + cb.isChecked());
            }
        });

        return convertView;
    }
}