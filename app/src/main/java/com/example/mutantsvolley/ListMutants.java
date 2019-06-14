package com.example.mutantsvolley;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class ListMutants extends AppCompatActivity {
    public ArrayList<String> mutants = new ArrayList<String>();
    public ArrayList<String> picsUrl = new ArrayList<String>();
    public ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    public JSONArray mutantsArray;
    ListView list;
    ProgressDialog progressDialog;
    LinearLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mutants);
        setTitle("Mutantes");
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mutants.clear();
        mutantsArray = new JSONArray();
        progressDialog = new ProgressDialog(this);
        //searchBar = findViewById(R.id.searchBar);

        Intent it = getIntent();
        Boolean isSearch = it.getBooleanExtra("isSearch", false);

        if (isSearch == true){
            searchBar.setVisibility(View.VISIBLE);
        } else {
            getMutants();
        }
    }

    public void populateTable(){
        String picUrl;
        HashMap<String, String> applicationSettings = new HashMap<String,String>();
        for(int i=0; i<mutantsArray.length(); i++){
            try {
                String name = mutantsArray.getJSONObject(i).getString("name");
                mutants.add(name);
                picUrl = mutantsArray.getJSONObject(i).getString("picture");
                picsUrl.add(Objects.toString(picUrl, ""));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        ListCell adapter = new ListCell(ListMutants.this, mutants, picsUrl);
        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject mutant = (JSONObject) mutantsArray.get(position);
                    Intent it = new Intent(ListMutants.this, MutantForm.class);
                    it.putExtra("isEditing", true);
                    it.putExtra("mutantName", mutant.getString("name").toString());
                    it.putExtra("mutantPower1", mutant.getString("power1").toString());
                    it.putExtra("mutantPower2", mutant.getString("power2").toString());
                    it.putExtra("mutantPower3", mutant.getString("power3").toString());
                    it.putExtra("userName", mutant.getString("username").toString());
                    it.putExtra("mutantId", mutant.getInt("id"));
                    startActivity(it);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void displayAlert(String title, String description, String button){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void getMutants(){
        final String  REQUEST_TAG = "listMutants";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando mutantes...");
        progressDialog.show();

         final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(MainActivity.GENERAL_MUTANT_URL,
                 new Response.Listener<JSONArray>() {
                     @Override
                     public void onResponse(JSONArray response) {
                         Log.d("listMutants", response.toString());
                         mutantsArray = response;
                         populateTable();
                         progressDialog.dismiss();
                     }
                 }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error){
                    VolleyLog.d("listMutants", "Error: " + error.getMessage());
                    displayAlert("Erro!", "Erro de conexão com servidor.", "Entendi!");
                    progressDialog.dismiss();
                }
         });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }
}