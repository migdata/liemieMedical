package com.example.ppe4_papovo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class Async extends AsyncTask<String, String, Boolean> {

    // Référence faible vers l'activité appelante pour éviter les fuites mémoire
    private WeakReference<Activity> activityAppelante;
    private String classActivityAppelante;
    private StringBuilder stringBuilder = new StringBuilder();
    private String urlAppelee;
    private String typeAppel;
    private int numAppel;

    public Async(Activity pActivity) {
        activityAppelante = new WeakReference<>(pActivity);
        classActivityAppelante = pActivity.getClass().toString();
    }

    @Override
    protected void onPreExecute() {
        // Au lancement, on envoie un message à l'appelant
        Activity activity = activityAppelante.get();
        if (activity != null) {
            Toast.makeText(activity, "Thread démarré", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Activity activity = activityAppelante.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (!result) {
            Toast.makeText(activity, "Échec de l'appel serveur", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(activity, "Fin ok", Toast.LENGTH_SHORT).show();

        // Distribution des résultats selon le numAppel
        switch (numAppel) {
            case 1:
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).retourConnexion(stringBuilder);
                }
                break;
            case 2:
                if (activity instanceof ActImport) {
                    ((ActImport) activity).retourImport(stringBuilder);
                }
                break;
            case 3:
                if (activity instanceof ActImport) {
                    ((ActImport) activity).retourImportPatient(stringBuilder);
                }
                break;
            case 4: // Affichage des soins d'une visite
                if (activity instanceof ActImport) {
                    ((ActImport) activity).retourImportSoinsVisite(stringBuilder);
                }
                break;
            case 5: // Catalogue général des soins
                if (activity instanceof ActImport) {
                    ((ActImport) activity).retourImportSoins(stringBuilder);
                }
                break;


            default:
                Toast.makeText(activity, "Action inconnue", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // Exécution en arrière-plan
        numAppel = Integer.parseInt(params[0]);
        urlAppelee = params[1];
        typeAppel = params[2];

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlAppelee);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(4000);
            urlConnection.setReadTimeout(4000);

            if (typeAppel.equals("POST")) {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                // Envoi des paramètres POST (à compléter selon le numAppel si besoin)
                try (OutputStreamWriter out = new OutputStreamWriter(
                        urlConnection.getOutputStream(), "utf-8")) {
                    /* Exemple d'utilisation pour passer des paramètres en JSON :
                    if (numAppel == X) {
                        JSONObject jsonParam = new JSONObject();
                        jsonParam.put("cle1", valeur1);
                        jsonParam.put("cle2", valeur2);
                        out.write(jsonParam.toString());
                        out.flush();
                    }
                    */
                }
            } else {
                urlConnection.setRequestMethod("GET");
            }

            // Récupération de la réponse du serveur
            int httpResult = urlConnection.getResponseCode();
            if (httpResult == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                }
            } else {
                String[] vstring0 = {"Erreur", urlConnection.getResponseMessage()};
                publishProgress(vstring0);
                return false;
            }
        } catch (MalformedURLException e) {
            String[] vstring0 = {"Erreur", "URL malformée"};
            publishProgress(vstring0);
            return false;
        } catch (java.net.SocketTimeoutException e) {
            String[] vstring0 = {"Erreur", "Temps de réponse trop long"};
            publishProgress(vstring0);
            return false;
        } catch (IOException e) {
            String[] vstring0 = {"Erreur", "Problème E/S -> " + e.getMessage()};
            publishProgress(vstring0);
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... param) {
        // Affichage de messages pendant le doInBackground
        Activity activity = activityAppelante.get();
        if (activity != null) {
            Toast.makeText(activity, param[0] + " --> " + param[1], Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        Activity activity = activityAppelante.get();
        if (activity != null) {
            Toast.makeText(activity, "Annulation", Toast.LENGTH_SHORT).show();
        }
    }
}