package com.example.snake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

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
 *  Taille de l'écran 1420x680
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Constante
    private final static int GAME_WIDTH = 1420;
    private final static int GAME_HEIGHT = 680;
    private final int HAUTEUR_CELL = GAME_WIDTH / 14;
    private final int LARGEUR_CELL = GAME_HEIGHT / 6;
    private final int NBR_COLUMN = 14;
    private final int NBR_ROW = 6;
    private final int MAX_HEIGHT = 6 * 100;
    private final int MAX_WIDTH = 1300;
    private final double SENSIBILITY_SENSOR = 3;
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
    ArrayList<Integer> listPosX = new ArrayList<>();
    // Position Y
    ArrayList<Integer> listPosY = new ArrayList<>();

    // Déplacer l'ImageView de 200 pixels sur l'axe x
    RelativeLayout.LayoutParams layoutParams;

    // Empêchement de revenir sur lui meme

    public enum Direction {
        HAUT, BAS, GAUCHE, DROITE
    }


    Direction deplacementPreced = Direction.DROITE;

    ArrayList<Integer> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("-----------------------------------------------------------------------------------");


        // Récupère les valeurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        snakeImage = findViewById(R.id.snakeImg);
        layoutParams = (RelativeLayout.LayoutParams) snakeImage.getLayoutParams();
        pommeImage = findViewById(R.id.pommeImg);

        // Ajout des positions de l'axe X dans un tableau
        for (int i = 0; i < NBR_COLUMN; i++) {
            listPosX.add(HAUTEUR_CELL * i);
        }
        for (int i = 0; i < NBR_ROW; i++) {
            listPosY.add(LARGEUR_CELL * i);
        }

        System.out.println(listPosX);
        System.out.println(listPosY);

        int cellWidth = GAME_WIDTH / 14;
        int cellHeight = GAME_HEIGHT / 6;

        // Position de la pomme
        pommeImage.setX(cellWidth * 3);
        pommeImage.setY(cellHeight * 3);
        // Position de la tete
//        snakeImage.setX(10);
//        snakeImage.setY(10);

        customGridView = new CustomGridView(this);

        System.out.println(snakeImage.getHeight());
        System.out.println(snakeImage.getWidth());

        /*
        // Corps du serpent
        ImageView corpsSnake = new ImageView(this);
        corpsSnake.setImageResource(R.drawable.snake_img);
        corpsSnake.setAdjustViewBounds(true);


        corpsSnake.setMaxHeight(convertDpToPx(this,40));
        corpsSnake.setMaxWidth(convertDpToPx(this,40));
        corpsSnake.setMinimumWidth(convertDpToPx(this,40));
        corpsSnake.setMinimumHeight(convertDpToPx(this,40));
        RelativeLayout containerLayout = findViewById(R.id.idLayout);
        containerLayout.addView(corpsSnake);
        */

        // Initialisation du handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                System.out.println("Pomme - X:" + pommeImage.getX() + " | Y:" + pommeImage.getY());
                System.out.println("Snake - X:" + snakeImage.getX() + " | Y:" + snakeImage.getY());


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
                if (xGravity > SENSIBILITY_SENSOR && deplacementPreced != Direction.HAUT) {
                    droite = false;
                    bas = true;
                    haut = false;
                    gauche = false;
                    System.out.println(deplacementPreced);

                    // haut
                } else if (xGravity < -SENSIBILITY_SENSOR && deplacementPreced != Direction.BAS){
                    System.out.println(deplacementPreced);
                    droite = false;
                    bas = false;
                    haut = true;
                    gauche = false;

                }

                // Déplacement vers la gauche
                if (yGravity > SENSIBILITY_SENSOR && deplacementPreced != Direction.GAUCHE) {
                    System.out.println(deplacementPreced);
                    droite = true;
                    bas = false;
                    haut = false;
                    gauche = false;

                    // droite
                } else if (yGravity < -SENSIBILITY_SENSOR && deplacementPreced != Direction.DROITE) {
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
     * Fonction qui permet de convertir une valeur "dp" en pixel
     * @param context This
     * @param dp Int, Valeur à convertir en pixel
     * @return Int, la valeur convertie en pixel
     */
    public static int convertDpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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

    /**
     * Fonction qui permet de changer l'orientation du serpent selon l'ENUM
     * passé en paramètre
     * @param direction ENUM, direction haut,bas,gauche et droite
     */
    public void changerOrientationSerpent(Direction direction) {
        int deplacementTop = 0;
        int deplacementLeft = 0;

        switch (direction) {
            case HAUT:
                deplacementTop = -LARGEUR_CELL;
                break;
            case BAS:
                deplacementTop = LARGEUR_CELL;
                break;
            case GAUCHE:
                deplacementLeft = -HAUTEUR_CELL;
                break;
            case DROITE:
                deplacementLeft = HAUTEUR_CELL;
                break;
        }

        marginTop += deplacementTop;
        marginLeft += deplacementLeft;
        layoutParams.topMargin = marginTop;
        layoutParams.leftMargin = marginLeft;
        snakeImage.setLayoutParams(layoutParams);
    }

    /***
     * Fonction qui génère un tableau de position aléatoire
     * @return Array, un tableau de position aléatoire
     */
    public ArrayList<Integer> genereRandomPos() {
        ArrayList<Integer> posAleatoire = new ArrayList<>();

        Random random = new Random();
        // Génère la position x aléatoire
        int indexRandomPosX = random.nextInt(listPosX.size());
        int posXRandom = listPosX.get(indexRandomPosX);
        posAleatoire.add(posXRandom);

        // Génère la position y aléatoire
        int indexRandomPosY = random.nextInt(listPosY.size());
        int posYRandom = listPosY.get(indexRandomPosY);
        posAleatoire.add(posYRandom);

        return posAleatoire;
    }

    /***
     * Fonction qui permet de détecter si le serpent passe sur la pomme,
     * ce qui fait qu'il la manger et ensuite supprimer cette pomme
     */
    public void mangerPomme() {
        // Si le serpent est dessus la pomme
        if ((pommeImage.getY() == snakeImage.getY()) && (pommeImage.getX() == snakeImage.getX())){
            System.out.println("Pomme mangée");

            // Position aléatoire de la pomme
            ArrayList<Integer> randomPos = genereRandomPos();
            pommeImage.setX(randomPos.get(0));
            pommeImage.setY(randomPos.get(1));

            // Ajout d'un corps de serpent
            ajouterCorps((int)snakeImage.getX(), (int)snakeImage.getY());

        }
    }


    /***
     * Fonction qui ajoute un corps au serpent (ImageView)
     * @param x Int, position X du corps à suivre
     * @param y Int, position Y du corps à suivre
     */
    public void ajouterCorps(int x, int y) {
        ImageView corps = new ImageView(this);
        corps.setImageResource(R.drawable.snake_img);
        corps.setAdjustViewBounds(true);
        corps.setMaxHeight(convertDpToPx(this,40));
        corps.setMaxWidth(convertDpToPx(this,40));
        corps.setMinimumWidth(convertDpToPx(this,40));
        corps.setMinimumHeight(convertDpToPx(this,40));
        RelativeLayout containerLayout = findViewById(R.id.idLayout);
        containerLayout.addView(corps);

        corps.setX(x);
        corps.setY(y);

    }

    /**
     * Fonction de déplacement du serpent
     */
    public void moveSnake() {

        mangerPomme();

        if (bas) {
            changerOrientationSerpent(Direction.BAS);
            deplacementPreced = Direction.BAS;

        } else if (haut) {
            changerOrientationSerpent(Direction.HAUT);
            deplacementPreced = Direction.HAUT;

        } else if (gauche) {
            changerOrientationSerpent(Direction.GAUCHE);
            deplacementPreced = Direction.GAUCHE;

        } else if (droite) {
            changerOrientationSerpent(Direction.DROITE);
            deplacementPreced = Direction.DROITE;
        }

    }

}
