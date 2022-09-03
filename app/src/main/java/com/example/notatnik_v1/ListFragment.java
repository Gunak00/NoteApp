package com.example.notatnik_v1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ListFragment extends Fragment {

    ListView listView;
    public final static String key = "firebaseNoteTitle";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        listView = view.findViewById(R.id.listView);

        final ArrayList<NoteListItem> noteListItems = new ArrayList<>();
        final NoteAdapter adapter = new NoteAdapter(noteListItems, view.getContext());
        listView.setAdapter(adapter);
        read(noteListItems, adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ReadNoteActivity.class);
                NoteListItem note = (NoteListItem) listView.getItemAtPosition(i);
                String title = note.getName();
                intent.putExtra(key, title);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int postition, long l) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
                alertDialog.setMessage("Czy na pewno chcesz usunąć notatkę?");

                alertDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(postition);
                        read(noteListItems, adapter);
                        adapter.notifyDataSetChanged();
//                        startActivity(new Intent(getContext(), MainActivity.class));
//                        getActivity().finish();
                    }
                });

                alertDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "No i całe szczęście, może ci sie jeszcze przyda :)", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                return true;
            }
        });

        return view;


    }

    private void delete(int position){
        NoteListItem note = (NoteListItem) listView.getItemAtPosition(position);
        String title = note.getName();
        FirebaseFirestore.getInstance().collection("notatki").document(title).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Pomyślnie usunięto notatkę.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Coś poszło nie tak :(", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void read(ArrayList<NoteListItem> noteListItems, NoteAdapter adapter){
        adapter.clear();
        db.collection("notatki").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String title = documentSnapshot.getId();
                        Map<String, Object> note = documentSnapshot.getData();
                        String date = note.get("data").toString();
                        String content = note.get("tresc").toString();
                        noteListItems.add(new NoteListItem(title, date, content));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}