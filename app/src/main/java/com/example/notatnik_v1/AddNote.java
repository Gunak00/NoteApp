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
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class AddNote extends AppCompatActivity {

    private Button btSave;
    private TextView txTitle, txMultiLine;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getSupportActionBar().hide();

        btSave = findViewById(R.id.btSaveNote);
        txTitle = findViewById(R.id.editTextTitle);
        txMultiLine = findViewById(R.id.editTextTextMultiLine);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
                Intent intent = new Intent(AddNote.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addNote(){
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

            db.collection("notatki").document(title).set(note, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AddNote.this, "Pomyslnie dodano nową notatkę", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AddNote.this, "Cos poszlo nie tak", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}