package com.example.snake;

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Handler handler;
    private Runnable runnable;
    private ImageView snake;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private CustomGridView customGridView;
    private int marginLeft = 0;
    private int marginTop = 0;
    // Déplacer l'ImageView de 200 pixels sur l'axe x
    RelativeLayout.LayoutParams layoutParams;

    // Déplacement
    private boolean haut = false;
    private boolean bas = false;
    private boolean gauche = false;
    private boolean droite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snake = findViewById(R.id.snake);

        // Sensor gravity
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        layoutParams = (RelativeLayout.LayoutParams) snake.getLayoutParams();

        // Initialiser le Handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                moveSnake();

                handler.postDelayed(this, 1000);
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

            // Déplacement vers le haut et vers le bas
            if (xGravity > 7.5) {
                System.out.println("Déplacement vers le bas");
                bas = true;

            } else if (xGravity < -7.5){
                System.out.println("Déplacement vers le haut");
            }

            // Déplacement vers la droite et la gauche
            if (yGravity > 7.5) {
                System.out.println("Déplacement vers la droite");
            } else if (yGravity < -7.5) {
                System.out.println("Déplacement vers la gauche");
            }
        }



    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    public void deplacementVersBas() {
        marginTop += customGridView.getCellWidth();
        System.out.println(marginTop);
        layoutParams.topMargin = marginTop;
        snake.setLayoutParams(layoutParams);
    }

    public void moveSnake() {



        // déplacement axe x
        marginLeft += 100;
        System.out.println(marginLeft);
        layoutParams.leftMargin = marginLeft;
        snake.setLayoutParams(layoutParams);

        // Déplacement vers le bas
        if (bas) {
            deplacementVersBas();
        }




    }

}
