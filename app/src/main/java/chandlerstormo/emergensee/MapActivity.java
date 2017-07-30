package chandlerstormo.emergensee;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = MapActivity.class.getSimpleName();
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place/search/json?location=";
    private final String API_KEY = "AIzaSyB72PQvGTc7CKjSS_pKmMpcdlpQ4F4crno";
    public static final String POS_KEY = "position_key";
    public static final String NEW_EMERGENCY_KEY = "new_emergency_key";

    Button mBack;
    Button mForward;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    double phoneLongitude;
    double phoneLatitude;
    double markerLongitude = 0;
    double markerLatitude = 0;

    int pos;
    String newEmergency;
    LatLng nearestHospital;
    String hospitalName;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mBack = (Button) findViewById(R.id.toChooseEmergency);
        mForward = (Button) findViewById(R.id.toGoogle);

        Intent i = getIntent();
        pos = i.getIntExtra(POS_KEY, 0);
        newEmergency = i.getStringExtra(NEW_EMERGENCY_KEY);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // get phone's location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            phoneLongitude = location.getLongitude();
            phoneLatitude = location.getLatitude();
        } else {
            Toast.makeText(getApplicationContext(), "Location not found", Toast.LENGTH_SHORT).show();
        }

        // add location listener
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null) {
                    Log.d(TAG, "Location is null!");
                }
                if (location != null) {
                    phoneLongitude = location.getLongitude();
                    phoneLatitude = location.getLatitude();
                    LatLng phoneLocation = new LatLng(phoneLatitude, phoneLongitude);
                    mMap.addMarker(new MarkerOptions().position(phoneLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    if (nearestHospital != null) {
                        mMap.addMarker(new MarkerOptions().position(nearestHospital).title(hospitalName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        // make map show both markers on the map
                        builder.include(nearestHospital);
                        builder.include(phoneLocation);
                        LatLngBounds bounds = builder.build();
                        int padding = 100;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        // animate the movement of camera
                        mMap.animateCamera(cu);
                    }
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
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // request list of nearest hospitals from Google Places
        queue = Volley.newRequestQueue(this);
        requestJSONParse(BASE_URL + phoneLatitude + "," + phoneLongitude + "&rankby=distance&type=hospital&key=" + API_KEY);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GoogleActivity.class);
                i.putExtra(GoogleActivity.POS_KEY, pos);
                i.putExtra(GoogleActivity.NEW_EMERGENCY_KEY, newEmergency);
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // parse through returned JSON and get first hospital since they are ranked by distance
    public void requestJSONParse(String reqURL) {
        JsonObjectRequest request = new JsonObjectRequest(reqURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray resultsJSON = response.getJSONArray("results");
                            JSONObject object = resultsJSON.getJSONObject(0);
                            if (object.has("geometry")) {
                                JSONObject object1 = object.getJSONObject("geometry");
                                if (object1.has("location")) {
                                    JSONObject object2 = object1.getJSONObject("location");
                                    if (object2.has("lat")) {
                                        markerLatitude = object2.getDouble("lat");
                                    }
                                    if (object2.has("lng")) {
                                        markerLongitude = object2.getDouble("lng");
                                    }
                                }
                            }
                            if (object.has("name")) {
                                hospitalName = object.getString("name");
                            }
                            // set markers for hospital and phone location
                            nearestHospital = new LatLng(markerLatitude, markerLongitude);
                            mMap.addMarker(new MarkerOptions().position(nearestHospital).title(hospitalName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            LatLng phoneLocation = new LatLng(phoneLatitude, phoneLongitude);
                            mMap.addMarker(new MarkerOptions().position(phoneLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            // make map show both markers on the map
                            builder.include(nearestHospital);
                            builder.include(phoneLocation);
                            LatLngBounds bounds = builder.build();
                            int padding = 100;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            // animate the movement of camera
                            mMap.animateCamera(cu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.getLocalizedMessage());
                    }
                }
        );
        queue.add(request);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
