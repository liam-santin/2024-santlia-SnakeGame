package com.example.snake;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.SingleDateSelector;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Runnable runnable;
    private ImageView snakeImage;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private CustomGridView customGridView;
    private GridView gridView;
    private GridLayout gridLayout;
    private int marginLeft = 0;
    private int marginTop = 0;
    // Déplacer l'ImageView de 200 pixels sur l'axe x
    RelativeLayout.LayoutParams layoutParams;

    // Déplacement
    private boolean haut = false;
    private boolean bas = false;
    private boolean gauche = false;
    private boolean droite = true;

    private enum DeplacementPreced {
        HAUT,
        BAS,
        GAUCHE,
        DROITE
    }

    DeplacementPreced deplacementPreced = DeplacementPreced.DROITE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sensor gravity
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        snakeImage = findViewById(R.id.snakeImg);

        layoutParams = (RelativeLayout.LayoutParams) snakeImage.getLayoutParams();

        // Initialiser le Handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                moveSnake();

                handler.postDelayed(this, 500);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Commencer à appeler la fonction toutes les 2 secondes
        handler.postDelayed(runnable, 2000);

        // Register the listener when the activity is resumed
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Arrêter d'appeler la fonction lorsque l'activité est en pause
        handler.removeCallbacks(runnable);

        // Unregister the listener when the activity is paused
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        customGridView = new CustomGridView(this);

        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            // Handle gravity sensor data changes
            float xGravity = event.values[0];
            float yGravity = event.values[1];

            // Déplacement vers le bas
            if (xGravity > 7.5 && deplacementPreced != DeplacementPreced.HAUT) {
                droite = false;
                bas = true;
                haut = false;
                gauche = false;
                System.out.println(deplacementPreced);

                // haut
            } else if (xGravity < -7.5 && deplacementPreced != DeplacementPreced.BAS){
                System.out.println(deplacementPreced);
                droite = false;
                bas = false;
                haut = true;
                gauche = false;

            }

            // Déplacement vers la gauche
            if (yGravity > 7.5 && deplacementPreced != DeplacementPreced.GAUCHE) {
                System.out.println(deplacementPreced);
                droite = true;
                bas = false;
                haut = false;
                gauche = false;

                // droite
            } else if (yGravity < -7.5 && deplacementPreced != DeplacementPreced.DROITE) {
                System.out.println(deplacementPreced);
                droite = false;
                bas = false;
                haut = false;
                gauche = true;

            }
        }



    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    public void deplacementVersBas() {
        marginTop += 120;
        System.out.println(marginTop);
        layoutParams.topMargin = marginTop;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersDroite() {
        marginLeft += 100;
        System.out.println(marginLeft);
        layoutParams.leftMargin = marginLeft;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersGauche() {
        marginLeft -= 100;
        layoutParams.leftMargin = marginLeft;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersHaut() {
        marginTop -=120;
        layoutParams.topMargin = marginTop;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void moveSnake() {

        System.out.println(bas);

        if (bas ) {
            deplacementVersBas();
            deplacementPreced = DeplacementPreced.BAS;

        } else if (haut) {
            deplacementVersHaut();
            deplacementPreced = DeplacementPreced.HAUT;

        } else if (gauche) {
            deplacementVersGauche();
            deplacementPreced = DeplacementPreced.GAUCHE;

        } else if (droite) {
            deplacementVersDroite();
            deplacementPreced = DeplacementPreced.DROITE;
        }

        int cellWidth = 100;
        System.out.println(cellWidth);

    }

}
