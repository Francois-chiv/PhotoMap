package fr.isep.photomap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import fr.isep.photomap.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    private GoogleMap map;
    private ActivityMapsBinding binding;
    private static final int PERMISSION_REQUEST_LOCATION_CODE = 99;
    private MarkerOptions currentPositionMarker;
    private int defaultZoom = 15;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences=getSharedPreferences("PhotoMapSession", MODE_PRIVATE);
        username = preferences.getString("username","");

        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.map);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()) {
                    case R.id.marker:
                        intent = new Intent(bottomNavigationView.getContext(), MarkerActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.map:
                        intent = new Intent(bottomNavigationView.getContext(), MapsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.group:
                        intent = new Intent(bottomNavigationView.getContext(), GroupActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.log_out:
                        intent = new Intent(bottomNavigationView.getContext(), ConnectionActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
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
        map = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_CODE);
        } else {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());

            currentPositionMarker = new MarkerOptions();
            currentPositionMarker.position(latlng);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationListener);

            this.displayMarkersOnMap();
            Intent intent = getIntent();
            if (intent.hasExtra("Latitude") && intent.hasExtra("Longitude")) {
                Log.d("Centering Camera", "Calling center camera function");
                LatLng latLng = new LatLng(Double.parseDouble(intent.getStringExtra("Latitude")), Double.parseDouble(intent.getStringExtra("Longitude")));
                centerCamera(latLng);
            }
        }
    }

    //TODO:A supprimer si inutile
    private final LocationListener LocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            currentPositionMarker.position(latlng);
        }
    };

    public void centerCameraToCurrentPosition(View v) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPositionMarker.getPosition(), defaultZoom));
    }

    public void centerCamera(LatLng latLng) {
        Log.d("Centering Camera", "Trying to center camera");
        Log.d("Centering Camera", latLng.toString());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));
    }

    public void onClickCamera(View v) {
        Intent intent = new Intent(this, MyCameraActivity.class);
        intent.putExtra("lat", "" + currentPositionMarker.getPosition().latitude);
        intent.putExtra("lng", "" + currentPositionMarker.getPosition().longitude);
        startActivity(intent);
    }

    public void displayMarkersOnMap() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                GeoPoint position = document.getGeoPoint("position");
                                double lat = position.getLatitude();
                                double lng = position.getLongitude();
                                LatLng latLng = new LatLng(lat, lng);
                                map.addMarker(new MarkerOptions().position(latLng).title(document.getString("title")).snippet(document.getString("description")).icon(decodeImage(document.getString("photo"))));
                            }
                        } else {
                            Log.w("Data read error", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private BitmapDescriptor decodeImage(String encodedImage){
        int height = 100;
        int width = 100;
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Bitmap smallBitmap = Bitmap.createScaledBitmap(decodedByte, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallBitmap);
    }
}