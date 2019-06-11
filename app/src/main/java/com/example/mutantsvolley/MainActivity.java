package com.example.mutantsvolley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://62e14167.ngrok.io";
    private static final String LOGIN_URL = BASE_URL + "/login";
    public static final String GENERAL_MUTANT_URL = BASE_URL + "/mutants/";
    public static String userId;

    ProgressDialog progressDialog;
    private static final String TAG = "MainActivity";
    private Button loginButton;
    private View showDialogView;
    private TextView outputTextView;
    private ImageView outputImageView;
    private EditText usernameValue, passwordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);

        usernameValue = findViewById(R.id.username);
        passwordValue = findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String paramsUrl = LOGIN_URL + "?login=" + usernameValue.getText().toString() +
                        "&password=" + passwordValue.getText().toString();
                doLoginRequest(paramsUrl);
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

    private void goToDashboard(String userId){
        MainActivity.userId = userId;

        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void doLoginRequest(String url){
        String  REQUEST_TAG = "loginRequest";
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        progressDialog.hide();
                        try {
                            int responseStatus = Integer.valueOf(response.getString("code"));
                            if (responseStatus == 200) {
                                displayAlert("Logado com sucesso!", "Bem vindo, " + response.getString("username"),
                                        "Continuar");
                                goToDashboard(response.getString("id"));
                            } else {
                                displayAlert("Erro no login", "Usuário ou senha incorretos",
                                        "Tentar Novamente");
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
