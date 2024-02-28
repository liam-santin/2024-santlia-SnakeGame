package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ParamActivity extends AppCompatActivity {

    private ImageView imgPomme, imgFraise, imgBanane, fruitChoisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        imgPomme = findViewById(R.id.imgPomme);
        imgFraise = findViewById(R.id.imgFraise);
        imgBanane = findViewById(R.id.imgBanane);
        fruitChoisi = findViewById(R.id.fruitChoisi);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Clique sur l'image de la pomme
        imgPomme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JeuActivity.FRUIT = 1;
                fruitChoisi.setImageResource(R.drawable.pomme);
            }
        });

        // Clique sur l'image de la fraise
        imgFraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JeuActivity.FRUIT = 2;
                fruitChoisi.setImageResource(R.drawable.fraise);
            }
        });

        // Clique sur l'image de la banane
        imgBanane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JeuActivity.FRUIT = 3;
                fruitChoisi.setImageResource(R.drawable.banane);
            }
        });

    }

}
