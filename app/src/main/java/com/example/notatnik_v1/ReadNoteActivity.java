package com.example.notatnik_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReadNoteActivity extends AppCompatActivity {

    private Button btBack;
    private TextView txTitle, txMultiLine;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_note);
        getSupportActionBar().hide();

        btBack = findViewById(R.id.btReadBack);
        txTitle = findViewById(R.id.editTextReadTitle);
        txMultiLine = findViewById(R.id.editTextReadTextMultiLine);

        Intent intent = getIntent();
        String title = intent.getStringExtra(ListFragment.key);
        read(title);
        txTitle.setText(title);


        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNote(title);

                Intent intent = new Intent(ReadNoteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void read(String title){
        FirebaseFirestore.getInstance().collection("notatki").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        if(title.equals(documentSnapshot.getId())){
                            Map<String, Object> city = documentSnapshot.getData();
                            txMultiLine.setText(city.get("tresc").toString());
                        }
                    }
                }
            }
        });
    }

    private void editNote(String oldTitle){
        String title, content, date;

        title = txTitle.getText().toString();
        content = txMultiLine.getText().toString();
        date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        if(title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Puste pola!", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String, Object> note = new HashMap<>();
            note.put("tresc", content);
            note.put("data", date);
            note.put("tytul", title);
            if(title.equals(oldTitle)){
                save(title, note, db);
            }else{
                delete(db, oldTitle);
                save(title, note, db);
            }

        }
    }

    private void save(String title, HashMap<String, Object> note, FirebaseFirestore db){
        db.collection("notatki").document(title).set(note, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(ReadNoteActivity.this, "Cos poszlo nie tak", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void delete(FirebaseFirestore db, String title){
        db.collection("notatki").document(title).delete();
    }
}