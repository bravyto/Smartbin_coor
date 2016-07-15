package com.sendbird.android.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GetPosition getPosition = new GetPosition();
                getPosition.execute((Void) null);
            }
        }, 0, 10000);
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private ArrayList<Marker> marker = new ArrayList<Marker>();;
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetPosition extends AsyncTask<Void, Void, String> {

        Exception mException = null;

        GetPosition() {
        }

        private Exception exception;
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(Void... params) {
            String urlString = "http://mmcrajawali.com/getPosisiAll.php";
//            String urlString = "http://mmcrajawali.com/smartbin/public/login?username=" + mEmail + "&password=" + mPassword;
//            String urlString = "http://mmcrajawali.com/smartbin/public/loginapi?username=" + mEmail + "&password=" + mPassword;
//            String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=-6.366026,106.8279491&destination=-6.402457,106.8300367&sensor=false&mode=driving&alternatives=true&key=AIzaSyDjkNXLI4j-k4ZhdSA3WkHxLUyXagm5aH8";
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(urlString);
            return json;
        }

        public String stripHtml(String html) {
            return Html.fromHtml(html).toString();
        }

        @Override
        protected void onPostExecute(final String success) {
            final JSONObject json;
            String status = null, role = null, id = null, name = null;
            try {
                json = new JSONObject(stripHtml(success));
                String encodedString = json.getString("status");
                status = encodedString;
                if (status.equals("sukses")) {
                    JSONArray pos = json.getJSONArray("posisi");
                    for (int i = 0; i < marker.size(); i++) {
                        marker.get(i).remove();
                    }
                    marker.clear();
                    for (int i = 0; i < pos.length(); i++) {
                        JSONObject e = pos.getJSONObject(i);
                        String id_truck = e.getString("id_truck");
                        String nomor_truck = e.getString("truck_number");
                        String supir_truck = e.getString("driver_name");
                        String latitude = e.getString("latitude");
                        String longitude = e.getString("longitude");
                        String status_truck = e.getString("truck_status");
                        if (status_truck.equals("idle")) {
                            LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            Marker mark = mMap.addMarker(new MarkerOptions().position(sydney).title(supir_truck).snippet(nomor_truck).alpha(0.5f));
                            marker.add(mark);
                        } else {
                            LatLng sydney = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            Marker mark = mMap.addMarker(new MarkerOptions().position(sydney).title(supir_truck).snippet(nomor_truck).alpha(1f));
                            marker.add(mark);
                        }
                    }
                } else {
                    Snackbar.make(findViewById(R.id.map), "Failed fetching truck position",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {

        }
    }
}
