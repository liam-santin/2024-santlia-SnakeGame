package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnJouer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnJouer = findViewById(R.id.btnJouer);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnJouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Crée une instance de jeu de snake et lance cette instance pour démarrer le jeu
                Intent jeuActivity = new Intent(MainActivity.this, JeuActivity.class);
                startActivity(jeuActivity);
            }
        });
    }
}
