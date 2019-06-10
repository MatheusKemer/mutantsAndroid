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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ListMutants extends AppCompatActivity {
    public String[] mutantsName = {"Coritiba", "Grêmio", "Atlético MG"};
    public ArrayList<String> mutants = new ArrayList<String>();
    ListView list;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mutants);

        progressDialog = new ProgressDialog(this);

        getMutants();
    }

    public void getMutants(){
        String  REQUEST_TAG = "listMutants";
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(MainActivity.LIST_MUTANTS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("listMutants", response.toString());
                        progressDialog.hide();
                        try {
                            Iterator<String> temp = response.keys();
                            while (temp.hasNext()) {
                                String key = temp.next();
                                Log.d("verMutantes", key);
                                JSONObject value = (JSONObject) response.get(key);
                                mutants.add(value.getString("name").toString());
                            }

                            ListCell adapter = new ListCell(ListMutants.this, mutants);
                            list = findViewById(R.id.list);
                            list.setAdapter(adapter);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //Intent it = new Intent(this, TeamDetailActivity.class);
                                    //it.putExtra("teamId", position);
                                    //startActivity(it);
                                }
                            });

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("listMutants", "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }
}