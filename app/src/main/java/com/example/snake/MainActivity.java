package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnJouer;
    private Button btnQuitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnJouer = findViewById(R.id.btnJouer);
        btnQuitter = findViewById(R.id.btnQuitter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Crée une instance de jeu de snake et passe de l'écran d'accueil au jeu
                Intent jeuActivity = new Intent(MainActivity.this, JeuActivity.class);
                startActivity(jeuActivity);
            }
        });

        btnQuitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Quitte le jeu
                finish();
            }
        });
    }
}
