package com.example.notatnik_v1;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<TaskListItem> {
    private ArrayList<TaskListItem> taskListItems;
    Context mContext;

    private static class ViewHolder{
         TextView txtName, txtDate;
         CheckBox checkBox;

    }

    public TaskAdapter(ArrayList<TaskListItem> taskListItems, Context mContext) {
        super(mContext, R.layout.row_item_task, taskListItems);
        this.taskListItems = taskListItems;
        this.mContext = mContext;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TaskListItem task = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        TaskAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_task, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.nameToDo);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateToDo);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxToDo);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        String name = task.getName();
        viewHolder.txtName.setText(task.getName());
        viewHolder.txtDate.setText(task.getDate());
        viewHolder.checkBox.setChecked(task.isDone());
//        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if()
//                System.out.println("DUUUUUUUUUUUUUUUUUUUUUUUUUUUPA");
//                editInDb(b, viewHolder.txtName.getText().toString());
//                task.setDone(b);
//                changeView(viewHolder);
//            }
//        });
        changeView(viewHolder);

        // Return the completed view to render on screen
        return result;
    }

    private void changeView(ViewHolder viewHolder){
        System.out.println("CHHHHHHHHHHHHHHHHHHHHHHHHEEEEEEEEEEEEEEEEEEEEE");
        if(viewHolder.checkBox.isChecked()){
            viewHolder.txtName.setPaintFlags(viewHolder.txtName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.txtName.setTextColor(ContextCompat.getColor(getContext(),R.color.mint));
            viewHolder.txtDate.setTextColor(ContextCompat.getColor(getContext(),R.color.mint));
        }else{
            viewHolder.txtName.setPaintFlags(viewHolder.txtName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.txtName.setTextColor(ContextCompat.getColor(getContext(),R.color.darkMint));
            viewHolder.txtDate.setTextColor(ContextCompat.getColor(getContext(),R.color.darkMint));
        }
    }
}
