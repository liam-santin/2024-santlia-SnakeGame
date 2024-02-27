package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomGridView extends View {
    private static final int NBR_COLUMNS = 14;
    private static final int NBR_ROWS = 6;

    // Couleurs pour l'alternance
    private int color1 = Color.parseColor("#00FF00"); // Vert
    private int color2 = Color.parseColor("#00CC00"); // Un autre vert

    // Largeur et hauteur du cadrillage
    private int width = 1420;
    private int height = 680;

    private Paint gridPaint;

    public CustomGridView(Context context) {
        super(context);
        init();
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Initialiser le pinceau pour dessiner la grille
        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2); // Épaisseur de la ligne de la grille
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellWidth = width / NBR_COLUMNS;
        int cellHeight = height / NBR_ROWS;

        // Variable pour alterner entre les couleurs
        int colorIndex = 0;

        // Dessiner les cases
        for (int row = 0; row < NBR_ROWS; row++) {
            for (int col = 0; col < NBR_COLUMNS; col++) {
                int x = col * cellWidth;
                int y = row * cellHeight;

                // Sélectionner la couleur en alternance en fonction de la position de la case
                int color;
                if ((row + col) % 2 == 0) {
                    color = color1;
                } else {
                    color = color2;
                }

                // Dessiner la case avec la couleur appropriée
                Paint cellPaint = new Paint();
                cellPaint.setStyle(Paint.Style.FILL);
                cellPaint.setColor(color);
                canvas.drawRect(x, y, x + cellWidth, y + cellHeight, cellPaint);
            }
        }

    }
}
