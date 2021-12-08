package fr.isep.photomap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void createUser(View v) {
        EditText username = findViewById(R.id.register_username);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username.getText().toString());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (!isDuplicate(username.getText().toString())) {
            db.collection("user")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("PhotoMapSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", username.getText().toString());
                            editor.commit();
                        }
                    });
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            username.setError("Please check username field again");
        }
    }

    protected boolean isDuplicate(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final boolean[] isDuplicate = {false};
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getString("username").equals(username)) {
                                    isDuplicate[0] = true;
                                }
                            }
                        }
                    }
                });
        return isDuplicate[0];
    }

    public void connectionPageButton(View v) {
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
    }
}
