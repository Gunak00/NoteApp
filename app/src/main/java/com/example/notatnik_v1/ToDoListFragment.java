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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ToDoListFragment extends Fragment {

    ListView listView;
    public final static String key = "firebaseTaskTitle";

    public ToDoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        listView = view.findViewById(R.id.listViewToDo);

        final ArrayList<TaskListItem> taskListItems = new ArrayList<>();
        final TaskAdapter adapter = new TaskAdapter(taskListItems, view.getContext());
        listView.setAdapter(adapter);

        read(taskListItems, adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TaskListItem taskListItem =(TaskListItem) listView.getItemAtPosition(i);
                boolean state = taskListItem.isDone();
                editInDb(!state, taskListItem.getName());
                read(taskListItems, adapter);
                adapter.notifyDataSetChanged();
//                startActivity(new Intent(getContext(), ToDoActivity.class));
//                getActivity().finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int postition, long l) {
                return longClick(postition, taskListItems, adapter);
            }
        });

        return view;
    }

    private void read(ArrayList<TaskListItem> taskListItems, TaskAdapter adapter){
        adapter.clear();
        FirebaseFirestore.getInstance().collection("zadania").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        String title = documentSnapshot.getId();
                        Map<String, Object> taskItem = documentSnapshot.getData();
                        String date = taskItem.get("data").toString();
                        boolean isDone = (boolean)taskItem.get("czyWykonane");
                        taskListItems.add(new TaskListItem(title, date, isDone));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void editInDb(boolean state, String name){
        DocumentReference ref = FirebaseFirestore.getInstance().collection("zadania").document(name);
        ref.update("czyWykonane", state);
        System.out.println("EEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDIIIIIIIIIIIIITTTTT");
    }

    private void delete(int position){
        TaskListItem taskListItem = (TaskListItem) listView.getItemAtPosition(position);
        String title = taskListItem.getName();
        FirebaseFirestore.getInstance().collection("zadania").document(title).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Pomyślnie usunięto zadanie.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Coś poszło nie tak :(", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean longClick(int postition, ArrayList<TaskListItem> taskListItems, TaskAdapter adapter){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        alertDialog.setMessage("Czy na pewno już ukończłeś zadanie?");

        alertDialog.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete(postition);
                read(taskListItems, adapter);
                adapter.notifyDataSetChanged();
//                startActivity(new Intent(getContext(), ToDoActivity.class));
//                getActivity().finish();
            }
        });

        alertDialog.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "To bierz się do pracy :)", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();

        return true;
    }

}