package com.example.mutantsvolley;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutantsvolley.R;

import java.util.ArrayList;

public class ListCell extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> mutants;
    //private final Integer[] mutantsPictures;

    public ListCell(Activity context, ArrayList<String> mutants){
        super(context, R.layout.list_cell, mutants);
        this.context = context;
        this.mutants = mutants;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cell, null ,true);
        TextView teamName = rowView.findViewById(R.id.mutantName);
        teamName.setText(mutants.get(position));
        return rowView;
    }
}