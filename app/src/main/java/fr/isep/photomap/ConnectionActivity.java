package fr.isep.photomap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class ConnectionActivity extends AppCompatActivity {
    List<String> usernames = new ArrayList<>();
    Boolean userExist = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
    }

    public void connectionButton(View v) {
        EditText username = findViewById(R.id.connection_username);
        EditText password = findViewById(R.id.password);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query = db.collection("user")
                .whereEqualTo("username", username.getText().toString())
                .whereEqualTo("password", md5(password.getText().toString()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userExist = task.getResult().isEmpty();
                        }
                    }
                });

        if (userExist) {
            username.setError("Invalid identifier!");
        } else {
            if (isDuplicate(username.getText().toString())) {
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("PhotoMapSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", username.getText().toString());
                editor.commit();
                Intent intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
            }
        }
    }

    protected boolean isDuplicate(String username) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                usernames.add(document.getString("username").toString());
                            }
                        }
                    }
                });

        return usernames.contains(username);
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void registerPageButton(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
