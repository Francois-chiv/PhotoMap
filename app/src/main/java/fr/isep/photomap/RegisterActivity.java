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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    boolean isDuplicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void createUser(View v) {
        EditText username = findViewById(R.id.register_username);
        EditText password = findViewById(R.id.password);
        EditText password_to_match = findViewById(R.id.password_to_match);

        if (password.getText().toString().equals(password_to_match.getText().toString())
                && !isDuplicate(username.getText().toString())) {
            Map<String, Object> user = new HashMap<>();
            user.put("username", username.getText().toString());
            user.put("password", md5(password.getText().toString()));
            FirebaseFirestore db = FirebaseFirestore.getInstance();

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
            password_to_match.setError("Password doesn't match");
        }
    }

    protected boolean isDuplicate(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            isDuplicate = task.getResult().isEmpty();
                        }
                    }
                });

        return !isDuplicate;
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

    public void connectionPageButton(View v) {
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
    }
}
