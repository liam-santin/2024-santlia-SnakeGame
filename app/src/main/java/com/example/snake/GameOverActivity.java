package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    private Button btnRejouer;
    private Button btnMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);

        btnRejouer = findViewById(R.id.btnRejouer);
        btnMenu = findViewById(R.id.btnMenu);

    }

    @Override
    protected void onStart() {
        super.onStart();

        btnRejouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jeuActivity = new Intent(GameOverActivity.this, JeuActivity.class);
                startActivity(jeuActivity);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(mainActivity);
            }
        });

    }



}
