package com.example.mutantsvolley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutantsvolley.R;

import org.json.JSONArray;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListCell extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> mutants;
    ArrayList<String> mutantsPicturesUrls;
    ArrayList<Bitmap> images;
    ProgressDialog progressDialog;

    public ListCell(Activity context, ArrayList<String> mutants, ArrayList<String> mutantsPicturesUrls){
        super(context, R.layout.list_cell, mutants);
        this.context = context;
        this.mutants = mutants;
        this.mutantsPicturesUrls = mutantsPicturesUrls;
        this.images = images;
        progressDialog = new ProgressDialog(context);
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cell, null ,true);
        TextView mutantName = rowView.findViewById(R.id.mutantName);
        ImageView mutantPicture = rowView.findViewById(R.id.mutantPicture);
        String name = mutants.get(position);
        mutantName.setText(name);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (mutantsPicturesUrls.get(position).length() > 0) {
            try {
                progressDialog.setMessage("Carregando imagem do mutante " + name);
                progressDialog.show();
                String path = mutantsPicturesUrls.get(position);
                URL url = new URL(MainActivity.BASE_URL + path);
                System.out.println("Url da imagem: " + url.toString());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                mutantPicture.setImageBitmap(bmp);
                progressDialog.dismiss();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return rowView;
    }
}