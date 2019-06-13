package com.example.mutantsvolley;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MutantForm extends AppCompatActivity {
    private static final String TAG = "MutantForm";
    EditText mutantName, mutantSkill1, mutantSkill2, mutantSkill3;
    TextView userName;
    Button actionButton, deleteButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mutant_form);

        progressDialog = new ProgressDialog(this);

        mutantName = findViewById(R.id.name);
        mutantSkill1 = findViewById(R.id.skill1);
        mutantSkill2 = findViewById(R.id.skill2);
        mutantSkill3 = findViewById(R.id.skill3);
        actionButton = findViewById(R.id.actionButton);
        deleteButton = findViewById(R.id.deleteButton);
        userName = findViewById(R.id.usernameField);

        Intent it = getIntent();
        Boolean isEditing = it.getBooleanExtra("isEditing", false);

        if (isEditing == true){
            setTitle("Editando Mutante");
            mutantName.setText(it.getStringExtra("mutantName").toString());
            mutantSkill1.setText(it.getStringExtra("mutantPower1").toString());
            mutantSkill2.setText(it.getStringExtra("mutantPower2").toString());
            mutantSkill3.setText(it.getStringExtra("mutantPower3").toString());
            userName.setText("Criado por: " + it.getStringExtra("userName").toString());
            userName.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            setTitle("Novo Mutante");
        }
    }

    public void saveMutant(View view){
        JSONObject mutant = new JSONObject();
        try {
            mutant.put("name", mutantName.getText().toString());
            mutant.put("power1", mutantSkill1.getText().toString());
            mutant.put("power2", mutantSkill2.getText().toString());
            mutant.put("power3", mutantSkill3.getText().toString());
            mutant.put("user_id", MainActivity.userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent it = getIntent();
        Boolean isEditing = it.getBooleanExtra("isEditing", false);

        if (isEditing == true){
            updateMutantRequest(mutant);
        } else {
            createMutantRequest(mutant);
        }
    }

    public void deleteMutant(View view){
        Intent it = getIntent();
        String mutantId = String.valueOf(it.getIntExtra("mutantId", 0));

        deleteMutantRequest(mutantId);

    }

    public void displayFinishAlert(String title, String description, String button){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
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

    public void createMutantRequest(JSONObject params){
        String  REQUEST_TAG = "createMutantTag";
        progressDialog.setMessage("Salvando...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.POST, MainActivity.GENERAL_MUTANT_URL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante criado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                displayAlert("Erro!", "Não foi possível conectar ao servidor!", "Entendi!");
                progressDialog.hide();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void updateMutantRequest(JSONObject params){
        String  REQUEST_TAG = "updatingMutantTag";
        progressDialog.setMessage("Salvando...");
        progressDialog.show();
        Intent it = getIntent();
        String mutantId = String.valueOf(it.getIntExtra("mutantId", 0));
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.PUT, MainActivity.GENERAL_MUTANT_URL  + mutantId, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante aualizado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }

    public void deleteMutantRequest(String mutantId){
        String  REQUEST_TAG = "deleteMutantTag";
        progressDialog.setMessage("Excluindo...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(Request.Method.DELETE, MainActivity.GENERAL_MUTANT_URL  + mutantId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();

                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));

                            if (responseStatus == 200) {
                                displayFinishAlert("Sucesso", "Mutante deletado com sucesso!", "Finalizar");
                            } else {
                                displayAlert("Erro!", response.getString("erro"), "Entendi!");
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq,REQUEST_TAG);
    }
}
