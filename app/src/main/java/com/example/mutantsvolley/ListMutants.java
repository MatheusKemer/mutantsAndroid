package com.example.mutantsvolley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ListMutants extends AppCompatActivity {
    public ArrayList<String> mutants = new ArrayList<String>();
    public JSONArray mutantsArray;
    ListView list;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mutants);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mutants.clear();
        mutantsArray = new JSONArray();
        progressDialog = new ProgressDialog(this);

        getMutants();
    }

    public void populateTable(){
        HashMap<String, String> applicationSettings = new HashMap<String,String>();
        for(int i=0; i<mutantsArray.length(); i++){
            try {
                String name = mutantsArray.getJSONObject(i).getString("name");
                mutants.add(name);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        ListCell adapter = new ListCell(ListMutants.this, mutants);
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
                    it.putExtra("mutantId", mutant.getInt("id"));
                    startActivity(it);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void getMutants(){
        final String  REQUEST_TAG = "listMutants";
        progressDialog.setMessage("Carregando mutantes...");
        progressDialog.show();

         final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(MainActivity.LIST_MUTANTS_URL,
                 new Response.Listener<JSONArray>() {
                     @Override
                     public void onResponse(JSONArray response) {
                         Log.d("listMutants", response.toString());
                         mutantsArray = response;
                         populateTable();
                         progressDialog.hide();
                     }
                 }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error){
                    VolleyLog.d("listMutants", "Error: " + error.getMessage());
                    progressDialog.hide();
                }
         });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }
}