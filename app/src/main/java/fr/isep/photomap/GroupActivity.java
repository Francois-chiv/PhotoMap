package fr.isep.photomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.group);

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
                }
                return false;
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("group")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("Data read", document.getId() + " => " + document.getData());
                                groups.add(document.getData());
                            }
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                            // set a LinearLayoutManager with default vertical orientation
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(linearLayoutManager);
                            //  call the constructor of CustomAdapter to send the reference and data to Adapter
                            GroupAdapter groupAdapter = new GroupAdapter(groups);
                            recyclerView.setAdapter(groupAdapter); // set the Adapter to RecyclerView
                        } else {
                            Log.w("Data read error", "Error getting documents.", task.getException());
                        }
                    }
                });


    }

}