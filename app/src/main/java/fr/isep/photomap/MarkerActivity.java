package fr.isep.photomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarkerActivity extends AppCompatActivity {

    private List<Map<String, Object>> locations = new ArrayList<Map<String,Object>>();
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences=getSharedPreferences("PhotoMapSession", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        username = preferences.getString("username","");
        editor.commit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

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
                    case R.id.log_out:
                        intent = new Intent(bottomNavigationView.getContext(), ConnectionActivity.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                locations.add(document.getData());
                            }
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            MarkerAdapter markerAdapter = new MarkerAdapter(locations);
                            recyclerView.setAdapter(markerAdapter);
                        } else {
                            Log.w("Data read error", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}