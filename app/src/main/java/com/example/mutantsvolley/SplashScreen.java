package com.example.mutantsvolley;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends Activity implements Runnable {
    private final int DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Toast.makeText(this, "Aguarde o carregamento do aplicativo dos Mutantes.", Toast.LENGTH_SHORT).show();
        Handler h = new Handler();
        h.postDelayed(this, DELAY);
    }

    @Override
    public void run() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
