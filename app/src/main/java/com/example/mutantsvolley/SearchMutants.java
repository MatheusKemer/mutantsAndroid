package com.example.mutantsvolley;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchMutants extends AppCompatActivity {
    ListView list;
    public JSONArray mutantsArray;
    EditText searchField;
    ProgressDialog progressDialogForSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_mutants);

        setTitle("Busca Mutante");

        progressDialogForSearch = new ProgressDialog(this);

        list = findViewById(R.id.list);

        List<String> mutants = new ArrayList<String>();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutants);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
            }
        });
    }

    public void searchMutant(View view){
        searchField = findViewById(R.id.searchField);
        String query = searchField.getText().toString();

        searchMutantsRequest(query);
    }

    public void populateTable(){
        ArrayList<String> mutants = new ArrayList<String>();
        HashMap<String, String> applicationSettings = new HashMap<String,String>();
        for(int i=0; i<mutantsArray.length(); i++){
            try {
                String name = mutantsArray.getJSONObject(i).getString("name");
                mutants.add(name);
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutants);
        list.setAdapter(arrayAdapter);
    }

    public void searchMutantsRequest(String query){
        final String  REQUEST_TAG = "searchMutants";
        progressDialogForSearch = new ProgressDialog(this);
        progressDialogForSearch.setMessage("Pesquisando mutantes...");
        progressDialogForSearch.show();

        final JsonArrayRequest jsonArrayReq = new JsonArrayRequest(MainActivity.BASE_URL + "/buscar?q=" + query,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("listMutants", response.toString());
                        mutantsArray = response;
                        populateTable();
                        progressDialogForSearch.dismiss();
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error){
                VolleyLog.d("listMutants", "Error: " + error.getMessage());
                progressDialogForSearch.dismiss();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayReq, REQUEST_TAG);
    }
}