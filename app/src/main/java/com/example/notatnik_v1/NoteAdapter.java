package com.example.notatnik_v1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<NoteListItem>  {
    private ArrayList<NoteListItem> noteListItems;
    Context mContext;

//    @Override
//    public void onClick(View view) {
//        int position = (Integer) view.getTag();
//        Object object = getItem(position);
//        NoteListItem note = (NoteListItem) object;
//    }

    private static class ViewHolder{
        TextView txtName, txtDate;
    }

    public NoteAdapter( ArrayList<NoteListItem> noteListItems, Context context) {
        super(context, R.layout.row_item, noteListItems);
        this.noteListItems = noteListItems;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NoteListItem note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.date);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(note.getName());
        viewHolder.txtDate.setText(note.getDate());
        // Return the completed view to render on screen
        return result;
    }
}
