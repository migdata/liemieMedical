package com.example.ppe4_papovo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class Map extends AppCompatActivity implements LocationListener {

    // Variables osmdroid
    private MapView myOpenMapView = null;
    private IMapController mapController;
    private Context ctx;
    private GeoPoint startPoint, geoPatient, geoInfirmiere;

    // Variables géolocalisation
    private double positionClient_latt, positionClient_long,
            positionAgent_latt, positionAgent_long;
    private boolean reussiGeolocalisationAgent = false,
            reussiGeolocalisationClient = false;
    private String provider, adresseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_map);

        myOpenMapView = (MapView) findViewById(R.id.map);
        myOpenMapView.setTileSource(TileSourceFactory.MAPNIK);
        myOpenMapView.setMultiTouchControls(true);
        mapController = myOpenMapView.getController();

        adresseClient = getIntent().getStringExtra("adresse");

        // On tente d'abord le Geocoder Android natif
        recupPositionClient();

        // Si le Geocoder Android a échoué, on bascule sur l'API Google
        if (!reussiGeolocalisationClient) {
            Log.d("map", "Geocoder Android KO → tentative API Google Geocoding");
            //appelApiGoogleGeocodage();
            // Note : affiche() sera appelée dans retourImport() après la réponse du serveur
        } else {
            // Geocoder Android OK : on continue normalement


        }
        recupPositionAgent();
        affiche();
    }

    // -------------------------------------------------------------------------
    // Géocodage via l'API Android Geocoder (natif, sans clé)
    // -------------------------------------------------------------------------
    public void recupPositionClient() {
        Log.d("map", "geocoder tentative sur adresse client " + adresseClient);
        if (!Geocoder.isPresent()) {
            Log.d("map", "geocoder absent");
        } else {
            Log.d("map", "geocoder ok");
            Geocoder fwdGeocoder = new Geocoder(this, Locale.FRANCE);
            List<Address> locations = null;
            try {
                locations = fwdGeocoder.getFromLocationName(adresseClient, 10);
            } catch (IOException e) {
                Log.d("map", "Pbs fonctionnement du geocoder avec l'adresse client");
            }
            if (locations == null || locations.isEmpty()) {
                Log.d("map", "Pbs geocoder Adresse client inconnu !");
            } else {
                positionClient_latt = locations.get(0).getLatitude();
                positionClient_long = locations.get(0).getLongitude();
                reussiGeolocalisationClient = true;
                Log.d("map", "Récupération adresse client Latitude "
                        + positionClient_latt + " Longitude " + positionClient_long);
            }
        }
    }


    // -------------------------------------------------------------------------
    // Récupération de la position GPS de l'infirmière
    // -------------------------------------------------------------------------
    public void recupPositionAgent() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            provider = locationManager.getBestProvider(criteria, false);
        }

        Log.d("map", "recupPositionAgent provider : " + provider);

        if (provider != null && !provider.equals("")) {
            try {
                locationManager.requestLocationUpdates(provider, 2000, 0, this);
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    positionAgent_latt = location.getLatitude();
                    positionAgent_long = location.getLongitude();
                    reussiGeolocalisationAgent = true;
                    Log.d("map", "recupPositionAgent ok lattitude : "
                            + positionAgent_latt + " longitude : " + positionAgent_long);
                } else {
                    // Fallback réseau
                    provider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(provider, 0, 0, this);
                    location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        positionAgent_latt = location.getLatitude();
                        positionAgent_long = location.getLongitude();
                        reussiGeolocalisationAgent = true;
                        Log.d("map", "recupPositionAgent network ok lattitude : "
                                + positionAgent_latt + " longitude : " + positionAgent_long);
                    } else {
                        Log.d("map", "recupPositionAgent via network KO");
                    }
                }
            } catch (SecurityException ex) {
                Log.d("map", "recupPositionAgent KO pbs provider");
            }
        } else {
            Log.d("map", "recupPositionAgent KO pas provider disponible");
        }
    }

    // -------------------------------------------------------------------------
    // Affichage des marqueurs et centrage de la carte
    // -------------------------------------------------------------------------
    public void affiche() {
        // On efface les anciens marqueurs pour éviter les doublons
        myOpenMapView.getOverlays().clear();

        // Marqueur patient
        if (reussiGeolocalisationClient) {
            Drawable icon = getResources().getDrawable(R.drawable.map1);
            Marker marker1 = new Marker(myOpenMapView);
            marker1.setTitle("Patient");
            geoPatient = new GeoPoint(positionClient_latt, positionClient_long);
            marker1.setPosition(geoPatient);
            marker1.setIcon(icon);
            marker1.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    marker.showInfoWindow();
                    return true;
                }
            });
            Log.d("map", "posiclientok- " + positionClient_latt + "/" + positionClient_long);
            myOpenMapView.getOverlays().add(marker1);
        }

        // Marqueur infirmière
        if (reussiGeolocalisationAgent) {
            Drawable icon = getResources().getDrawable(R.drawable.pointer);
            Marker marker1 = new Marker(myOpenMapView);
            marker1.setTitle("Vous");
            geoInfirmiere = new GeoPoint(positionAgent_latt, positionAgent_long);
            marker1.setPosition(geoInfirmiere);
            marker1.setIcon(icon);
            marker1.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    marker.showInfoWindow();
                    return true;
                }
            });
            Log.d("map", "posiAgentok- " + positionAgent_latt + "/" + positionAgent_long);
            myOpenMapView.getOverlays().add(marker1);
        }

        // Zoom et centrage
        mapController.setZoom(17.0);
        if (reussiGeolocalisationAgent && reussiGeolocalisationClient) {
            mapController.setCenter(getmiddle(geoInfirmiere, geoPatient));
        } else if (reussiGeolocalisationAgent) {
            mapController.setCenter(geoInfirmiere);
            Toast.makeText(getApplicationContext(), "Géocodage impossible", Toast.LENGTH_LONG).show();
        } else if (reussiGeolocalisationClient) {
            mapController.setCenter(geoPatient);
            Toast.makeText(getApplicationContext(), "Localisation impossible", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Géocodage et localisation impossible", Toast.LENGTH_LONG).show();
        }
    }

    // Calcul du point central entre deux GeoPoints
    public GeoPoint getmiddle(GeoPoint a, GeoPoint b) {
        GeoPoint g = new GeoPoint(0.0, 0.0);
        double N = (a.getLongitude() + b.getLongitude()) / 2;
        if (a.getLongitude() + b.getLongitude() < 180.0) {
            g.setLongitude((a.getLongitude() + b.getLongitude()) / 2);
        } else {
            if (N < 0) {
                g.setLongitude(180 + N);
            } else if (N > 0) {
                g.setLongitude(-180 + N);
            } else {
                g.setLongitude(180);
            }
        }
        g.setLatitude((a.getLatitude() + b.getLatitude()) / 2);
        return g;
    }

    // Appelée automatiquement quand le GPS obtient une nouvelle position
    @Override
    public void onLocationChanged(Location location) {
        double nouvelleLat = location.getLatitude();
        double nouvelleLon = location.getLongitude();

        // On ne rafraîchit que si la position a réellement changé
        if (!reussiGeolocalisationAgent
                || Math.abs(nouvelleLat - positionAgent_latt) > 0.0001
                || Math.abs(nouvelleLon - positionAgent_long) > 0.0001) {

            positionAgent_latt = nouvelleLat;
            positionAgent_long = nouvelleLon;
            reussiGeolocalisationAgent = true;
            Log.d("map", "onLocationChanged lattitude : "
                    + positionAgent_latt + " longitude : " + positionAgent_long);
            affiche();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onPause() {
        super.onPause();
        myOpenMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        myOpenMapView.onResume();
    }
}