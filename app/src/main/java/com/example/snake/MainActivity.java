package com.example.snake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;

/**
 * Cadrillage du jeu snake 6 lignes et 14 colonnes
 *___________________________________________________________
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------|
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------|
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------| Axe Y
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------|
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------|
 * |    |   |   |   |   |   |   |   |   |   |   |   |   |   |
 * ---------------------------------------------------------|
 *                      Axe X
 *
 *  La hauteur d'une cellule est 120dp --> axe Y --> variable marginTop
 *  La largeur d'une cellule est 100dp --> axe X --> variable marginLeft
 *
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Constante
    private final int HAUTEUR_CELL = 100;
    private final int LARGEUR_CELL = 120;
    private final int MAX_HEIGHT = 6 * 100;
    private final int MAX_WIDTH = 1300;
    private final double SENSIBILITY_SENSOR = 5;
    private final int VITESSE_SNAKE = 350 ;

    // Déplacement
    private int marginLeft = 0;
    private int marginTop = 0;
    private boolean haut = false;
    private boolean bas = false;
    private boolean gauche = false;
    private boolean droite = true;

    // Objet
    private Handler handler;
    private Runnable runnable;
    private ImageView snakeImage;
    private ImageView pommeImage;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private CustomGridView customGridView;

    // Position X
    private int[] posAxeY = {0,120,240,360,480,600,720};
    // Position Y
    private int[] posAxeX = {0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300};

    // Déplacer l'ImageView de 200 pixels sur l'axe x
    RelativeLayout.LayoutParams layoutParams;

    // Empêchement de revenir sur lui meme
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

        System.out.println("-----------------------------------------------------------------------------------");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        snakeImage = findViewById(R.id.snakeImg);
        layoutParams = (RelativeLayout.LayoutParams) snakeImage.getLayoutParams();
        pommeImage = findViewById(R.id.pommeImg);
        customGridView = new CustomGridView(this);

        // Taille de l'écran 1406x720
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = ScreenUtils.getScreenWidth(getApplicationContext());
        int screenHeight = ScreenUtils.getScreenHeight(getApplicationContext());

        // Position de la pomme
        pommeImage.setX(100);
        pommeImage.setY(100);

        // Initialisation du handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                moveSnake();
                handler.postDelayed(this, VITESSE_SNAKE);
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

    /**
     * Arrête le handler
     */
    private void stopHandler() {
        // Arrête l'exécution du Runnable
        handler.removeCallbacks(runnable);
        System.out.println("Fin du handler");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        System.out.println("Margin top : " + marginTop);
        System.out.println("Margin left : " + marginLeft);

        if (testCollisionBord(marginLeft, marginTop)){
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                // Handle gravity sensor data changes
                float xGravity = event.values[0];
                float yGravity = event.values[1];

                // Déplacement vers le bas
                if (xGravity > SENSIBILITY_SENSOR && deplacementPreced != DeplacementPreced.HAUT) {
                    droite = false;
                    bas = true;
                    haut = false;
                    gauche = false;
                    System.out.println(deplacementPreced);

                    // haut
                } else if (xGravity < -SENSIBILITY_SENSOR && deplacementPreced != DeplacementPreced.BAS){
                    System.out.println(deplacementPreced);
                    droite = false;
                    bas = false;
                    haut = true;
                    gauche = false;

                }

                // Déplacement vers la gauche
                if (yGravity > SENSIBILITY_SENSOR && deplacementPreced != DeplacementPreced.GAUCHE) {
                    System.out.println(deplacementPreced);
                    droite = true;
                    bas = false;
                    haut = false;
                    gauche = false;

                    // droite
                } else if (yGravity < -SENSIBILITY_SENSOR && deplacementPreced != DeplacementPreced.DROITE) {
                    System.out.println(deplacementPreced);
                    droite = false;
                    bas = false;
                    haut = false;
                    gauche = true;

                }
            }

        } else {
            stopHandler();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    /***
     * Fonction qui permet de tester si le serpent est en bord de scene
     * ou non
     * @param marginLeft Int, Marge de gauche (axe x)
     * @param marginTop Int, Marge du haut (axe y)
     * @return Bool, vrai si le serpent ne touche pas les bords et
     *               faux si le serpent touche les bords
     */
    public boolean testCollisionBord(int marginLeft, int marginTop) {

        // Si le serpent collisionne avec le bord du bas et de droite
        if (marginTop < MAX_HEIGHT + 1 && marginLeft < MAX_WIDTH + 1) {
            // Si le serpent collisionne avec le bord gauche et le bord du haut
            if (marginTop >= 0 && marginLeft >= 0) {
                return true;
            }
        }

        return false;
    }

    public void deplacementVersBas() {
        marginTop += LARGEUR_CELL;
        System.out.println(marginTop);
        layoutParams.topMargin = marginTop;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersDroite() {
        marginLeft += HAUTEUR_CELL;
        System.out.println(marginLeft);
        layoutParams.leftMargin = marginLeft;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersGauche() {
        marginLeft -= HAUTEUR_CELL;
        layoutParams.leftMargin = marginLeft;
        snakeImage.setLayoutParams(layoutParams);
    }

    public void deplacementVersHaut() {
        marginTop -= LARGEUR_CELL;
        layoutParams.topMargin = marginTop;
        snakeImage.setLayoutParams(layoutParams);
    }

    /**
     * Fonction de déplacement du serpent
     */
    public void moveSnake() {


        if (bas) {
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
