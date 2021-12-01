package fr.isep.photomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MarkerInfoActivity extends AppCompatActivity {

    private String latitude;
    private String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.marker);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch (item.getItemId()){
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
                }
                return false;
            }
        });

        Intent intent = getIntent();

        String title = intent.getStringExtra("Title");
        String description = intent.getStringExtra("Description");
        float rating = Float.parseFloat(intent.getStringExtra("Rating"));
        String photo = intent.getStringExtra("Photo");
        latitude = intent.getStringExtra("Latitude");
        longitude = intent.getStringExtra("Longitude");

        TextView titleTextView = findViewById(R.id.marker_title);
        TextView descriptionTextView = findViewById(R.id.marker_description);
        RatingBar ratingRatingBar = findViewById(R.id.marker_rating);
        ImageView photoImageView = findViewById(R.id.marker_photo);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        ratingRatingBar.setRating(rating);
        photoImageView.setImageBitmap(decodeImage(photo));
    }

    private Bitmap decodeImage(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void onClickShowOnMap(View v){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("Latitude", latitude);
        intent.putExtra("Longitude", longitude);
        this.startActivity(intent);
    }
}