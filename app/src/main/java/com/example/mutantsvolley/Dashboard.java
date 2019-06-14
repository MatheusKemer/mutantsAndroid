package com.example.mutantsvolley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {
    public static String userId;
    public static final String BASE_URL = "https://905d1ed5.ngrok.io";
    public static final String LOGIN_URL = BASE_URL + "/login";
    public static final String GENERAL_MUTANT_URL = BASE_URL + "/mutants/";
    Button createMutant, listMutants, searchMutants, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        setTitle("Mutante Dashboard");
        createMutant = findViewById(R.id.create);
        listMutants = findViewById(R.id.list);
        searchMutants = findViewById(R.id.search);
        exit = findViewById(R.id.exit);

        Intent it = getIntent();
        Dashboard.userId = it.getStringExtra("userId");
    }

    public void createMutant(View view){
        Intent intent = new Intent(this, MutantForm.class);
        startActivity(intent);
    }
    public void listMutants(View view){
        Intent intent = new Intent(this, ListMutants.class);
        startActivity(intent);
    }
    public void searchMutants(View view){
        Intent intent = new Intent(this, SearchMutants.class);
        //intent.putExtra("isSearch", true);
        startActivity(intent);
    }

    public void exit(View view){
        finish();
        System.exit(0);
    }
}