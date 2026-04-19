package com.example.ppe4_papovo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.content.Context;
import java.util.List;
import android.widget.TextView;
import android.graphics.Color;
import android.text.format.DateFormat;

public class VisiteAdapter extends BaseAdapter {

    private List<Visite> listVisite;
    private LayoutInflater layoutInflater; //Cet attribut a pour mission de charger notre fichier XML de la vue pour l'item.

    private DateFormat df = new DateFormat();

    private class ViewHolder {
        TextView textViewVisite;
        TextView textViewPatient;
        TextView textViewDate;
        TextView textViewDuree;
    }


    @Override
    public int getCount() {
        return listVisite.size(); // retourne la taille de la liste
    }

    @Override
    public Object getItem(int position) {
        return listVisite.get(position); // retourne l'item à la position donnée
    }

    @Override
    public long getItemId(int position) {
        return listVisite.get(position).getId(); // retourne l'id de l'item à la position donnée;
    }

    public VisiteAdapter(Context context, List<Visite> vListVisite) {
        super();
        layoutInflater = LayoutInflater.from(context);
        listVisite = vListVisite;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // si la vue n'existe pas on la crée à partir du layout XML
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.vuevisite, null);
            // on récupère les éléments de la vue en fonction de leur id
            holder.textViewVisite = (TextView) convertView.findViewById(R.id.vuevisite);
            holder.textViewPatient = (TextView) convertView.findViewById(R.id.vuepatient);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.vuedateprevue);
            holder.textViewDuree = (TextView) convertView.findViewById(R.id.vueduree);
            // on mémorise le holder à la vue
            convertView.setTag(holder);
        } else {
            // sinon on récupère le holder de la vue
            holder = (ViewHolder) convertView.getTag();
        }
        // couleur de fond des lignes de la listView
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.rgb(238, 233, 233));
        } else {
            convertView.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        /*****Affichage des propriétés dans la ligne de la listView ****/
        holder.textViewVisite.setText("Visite ID : " + listVisite.get(position).getId() + ", ");
        holder.textViewPatient.setText("Avec le patient : " + listVisite.get(position).getPatient() + ", ");
        holder.textViewDate.setText("Date :"+ df.format("dd/MM/yyyy",listVisite.get(position).getDate_reelle()).toString().concat(" à ").concat(df.format("HH:mm",listVisite.get(position).getDate_reelle()).toString()));
        holder.textViewDuree.setText("Durée : "+listVisite.get(position).getDuree()+" min");

        /********* COULEURS DU TEXTE DE LA LISTVIEW ******************/
        holder.textViewVisite.setTextColor(Color.BLACK);
        holder.textViewPatient.setTextColor(Color.BLACK);
        holder.textViewDate.setTextColor(Color.BLACK);
        holder.textViewDuree.setTextColor(Color.BLACK);


        /******* Taille du texte de la listView ********************/
        holder.textViewVisite.setTextSize(17);
        holder.textViewPatient.setTextSize(17);
        holder.textViewDate.setTextSize(17);
        holder.textViewDuree.setTextSize(17);


        return convertView;
    }



}
