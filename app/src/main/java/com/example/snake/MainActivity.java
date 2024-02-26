package com.example.snake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
 *  Liste de position X : [0, 101, 202, 303, 404, 505, 606, 707, 808, 909, 1010, 1111, 1212, 1313]
 *  Liste de position Y : [0, 113, 226, 339, 452, 565]
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
    private final int MAX_WIDTH = 1313;
    private final double SENSIBILITY_SENSOR = 3;

    // Déplacement
    private int VITESSE_SNAKE = 350 ;
    private int marginLeft = 0;
    private int marginTop = 0;
    public enum Direction {
        HAUT, BAS, GAUCHE, DROITE
    }
    Direction directionActuel = Direction.DROITE;
    Direction deplacementPreced = Direction.DROITE;

    // Objet
    private Handler handler;
    private Runnable runnable;
    private ImageView snakeHeadImg;
    private ImageView pommeImage;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private CustomGridView customGridView;

    // Position X
    ArrayList<Integer> listPosX = new ArrayList<>();
    // Position Y
    ArrayList<Integer> listPosY = new ArrayList<>();

    /** SERPENT **/
    // Liste de corps de serpent
    ArrayList<ImageView> listBodySnake = new ArrayList<>();
    // Position X et Y du dernier corps à suivre
    ArrayList<ArrayList<Integer>> listAncienPos = new ArrayList<>();
    // Définit la taille du serpent, au debut à 1 car il y a la tête
    int sizeSnake = 1;

    // Déplacer l'ImageView de 200 pixels sur l'axe x
    RelativeLayout.LayoutParams layoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("-----------------------------------------------------------------------------------");


        // Récupère les valeurs
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        snakeHeadImg = findViewById(R.id.snakeImg);
        layoutParams = (RelativeLayout.LayoutParams) snakeHeadImg.getLayoutParams();
        pommeImage = findViewById(R.id.pommeImg);

        // Ajout des positions de l'axe X dans un tableau
        for (int i = 0; i < NBR_COLUMN; i++) {
            listPosX.add(HAUTEUR_CELL * i);
        }
        for (int i = 0; i < NBR_ROW; i++) {
            listPosY.add(LARGEUR_CELL * i);
        }

        // Position de la pomme
        int cellWidth = GAME_WIDTH / 14;
        int cellHeight = GAME_HEIGHT / 6;
        pommeImage.setX(cellWidth * 3);
        pommeImage.setY(cellHeight * 3);

        customGridView = new CustomGridView(this);

        listBodySnake.add(snakeHeadImg);

        // Initialisation du handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                ancienPos();

                if (collisionSurSerpent()) {
                    System.out.println("---------------------------------------------------------------------------------------");
                    stopHandler();
                }

                moveSnake();
                mangerPomme();

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

        if (testCollisionBord(marginLeft, marginTop)){
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                // Handle gravity sensor data changes
                float xGravity = event.values[0];
                float yGravity = event.values[1];

                // Déplacement vers le bas
                if (xGravity > SENSIBILITY_SENSOR && deplacementPreced != Direction.HAUT) {
                    directionActuel = Direction.BAS;

                    // haut
                } else if (xGravity < -SENSIBILITY_SENSOR && deplacementPreced != Direction.BAS){
                    directionActuel = Direction.HAUT;

                }

                // Déplacement vers la gauche
                if (yGravity > SENSIBILITY_SENSOR && deplacementPreced != Direction.GAUCHE) {
                    directionActuel = Direction.DROITE;

                    // droite
                } else if (yGravity < -SENSIBILITY_SENSOR && deplacementPreced != Direction.DROITE) {
                    directionActuel = Direction.GAUCHE;

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
        snakeHeadImg.setLayoutParams(layoutParams);
    }

    /***
     * Fonction qui génère un tableau de position aléatoire
     * @return Array, un tableau de position aléatoire
     */
    public ArrayList<Integer> genereRandomPos() {
        ArrayList<Integer> posAleatoire = new ArrayList<>();

        Random random = new Random();

        // Génération alétoire de la position X
        int randomPosX = 0;
        do {
            randomPosX = listPosX.get(random.nextInt(listPosX.size()));
        } while(!listPosX.contains(randomPosX));
        posAleatoire.add(randomPosX);

        // Génération aléatoire de la position Y
        int randomPosY = 0;
        do {
            randomPosY = listPosY.get(random.nextInt(listPosY.size()));
        } while(!listPosY.contains(randomPosY));
        posAleatoire.add(randomPosY);

        return posAleatoire;
    }

    /***
     * Fonction qui permet de détecter si le serpent passe sur la pomme,
     * ce qui fait qu'il la manger et ensuite supprimer cette pomme
     */
    public void mangerPomme() {
        // Si le serpent est dessus la pomme
        if ((pommeImage.getY() == snakeHeadImg.getY()) && (pommeImage.getX() == snakeHeadImg.getX())){
            System.out.println("Pomme mangée");

            // Ajout d'un corps de serpent
            ajouterCorps();

            // Position aléatoire de la pomme
            ArrayList<Integer> randomPos = genereRandomPos();
            pommeImage.setX(randomPos.get(0));
            pommeImage.setY(randomPos.get(1));

            // Augmente la vitesse du serpent de 10
            VITESSE_SNAKE -= 10;

        }
    }


    /***
     * Fonction qui ajoute un corps au serpent (ImageView)
     * @param x Int, position X du corps à suivre
     * @param y Int, position Y du corps à suivre
     */
    public void ajouterCorps() {
        sizeSnake++;

        ImageView corps = new ImageView(this);
        // Set les propriétés de l'image
        corps.setImageResource(R.drawable.body_snake);
        corps.setAdjustViewBounds(true);
        corps.setMaxHeight(convertDpToPx(this, 40));
        corps.setMaxWidth(convertDpToPx(this, 40));
        corps.setMinimumWidth(convertDpToPx(this, 40));
        corps.setMinimumHeight(convertDpToPx(this, 40));
        RelativeLayout containerLayout = findViewById(R.id.idLayout);
        containerLayout.addView(corps);

        // Positionne le corps du serpent à la suite du serpent
        // -2 correspond au corps à suivre
        corps.setX(listAncienPos.get(sizeSnake - 2).get(0));
        corps.setY(listAncienPos.get(sizeSnake - 2).get(1));

        // Ajoute le corps a la liste et place le corps après à la suite
        listBodySnake.add(corps);

    }


    /**
     * Fonction de déplacement du serpent
     */
    public void moveSnake() {


        // Contrôle de la tête avec le curseur de gravité
        switch (directionActuel) {
            case DROITE:
                changerOrientationSerpent(Direction.DROITE);
                deplacementPreced = Direction.DROITE;
                break;

            case BAS:
                changerOrientationSerpent(Direction.BAS);
                deplacementPreced = Direction.BAS;
                break;

            case HAUT:
                changerOrientationSerpent(Direction.HAUT);
                deplacementPreced = Direction.HAUT;
                break;

            case GAUCHE:
                changerOrientationSerpent(Direction.GAUCHE);
                deplacementPreced = Direction.GAUCHE;
                break;
        }

        ArrayList<ArrayList<Integer>> oldPos = listAncienPos;
        for (int i = 0; i < listBodySnake.size(); i++) {
            if (i > 0) {
                // i - 1 pour avoir la position du corps devant quand
                // ce n'est pas la tête du serpent
                listBodySnake.get(i).setX(oldPos.get(i - 1).get(0));
                listBodySnake.get(i).setY(oldPos.get(i - 1).get(1));
            }
        }

    }

    /***
     * Fonction qui permet de mettre à jour la liste de liste d'ancienne
     * position.
     * Cette fonction est appelée dans le Run() du Handler.
     */
    public void ancienPos() {
        // Reset la liste avant d'ajouter de nouvelle position
        listAncienPos.clear();

        for (int i = 0; i<listBodySnake.size();i++){
            ArrayList<Integer> pos = new ArrayList<>();
            pos.add((int)listBodySnake.get(i).getX());
            pos.add((int)listBodySnake.get(i).getY());
            listAncienPos.add(pos);
        }
    }


    public boolean collisionSurSerpent() {
        // si la position de la tete du serpent est déjà present dans
        // la liste de position

        // Position de la tête du serpent
        ArrayList<Integer> posSnakeHead = new ArrayList<>();
        posSnakeHead.add((int)snakeHeadImg.getX());
        posSnakeHead.add((int)snakeHeadImg.getY());

        for (int i = 0; i < listAncienPos.size(); i++) {
            if (i != 0) {
                System.out.println(i + "-" + listAncienPos.get(i));
                System.out.println("Tete :" + posSnakeHead);
                if (posSnakeHead.equals(listAncienPos.get(i))) {
                    System.out.println("fin");
                    return true;
                }
            }
        }

        return false;
    }

}
