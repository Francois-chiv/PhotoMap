package fr.isep.photomap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupCreateActivity extends AppCompatActivity {

    private List<String> usernames = new ArrayList<String>();
    private String currentUsername;
    private List<String> selectedUsernames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        SharedPreferences preferences=getSharedPreferences("PhotoMapSession", MODE_PRIVATE);
        currentUsername = preferences.getString("username","");

        selectedUsernames.add(currentUsername);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (!document.getString("username").equals(currentUsername)){
                                    usernames.add(document.getString("username").toString());
                                }
                            }
                            MultichoiceSpinner multichoiceSpinner = (MultichoiceSpinner) findViewById(R.id.multichoice_spinner);
                            MultichoiceSpinner.MultichoiceSpinnerListener listener = new MultichoiceSpinner.MultichoiceSpinnerListener() {
                                @Override
                                public void onItemsSelected(boolean[] selected) {

                                }
                            };
                            multichoiceSpinner.setItems(usernames, "No user selected", listener);
                        }

                    }
                });



    }

    public void createGroup(View v){
        EditText nameEditText = findViewById(R.id.group_name);
        EditText descriptionEditText = findViewById(R.id.group_description);
        MultichoiceSpinner multichoiceSpinner = (MultichoiceSpinner) findViewById(R.id.multichoice_spinner);

        Log.d("Name", nameEditText.getText().toString());
        Log.d("Description", descriptionEditText.getText().toString());
        for (int i = 0 ; i < usernames.size() ; i++) {
            if (multichoiceSpinner.getSelected()[i] == true) {
                Log.d("Username", usernames.get(i));
                selectedUsernames.add(usernames.get(i));
            }
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> group = new HashMap<>();
        group.put("name", nameEditText.getText().toString());
        group.put("description", descriptionEditText.getText().toString());
        group.put("members", selectedUsernames);


        db.collection("group")
                .add(group)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("save_group", "Group added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("save_group", "Error adding group", e);
                    }
                });

        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }
}