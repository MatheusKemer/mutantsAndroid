package com.example.mutantsvolley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Dashboard extends AppCompatActivity {
    Button createMutant, listMutants, searchMutants, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        createMutant = findViewById(R.id.create);
        listMutants = findViewById(R.id.list);
        searchMutants = findViewById(R.id.search);
        exit = findViewById(R.id.exit);
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
        Intent intent = new Intent(this, ListMutants.class);
        intent.putExtra("isSearch", true);
        startActivity(intent);
    }

    public void exit(View view){
        finishAffinity();
    }
}