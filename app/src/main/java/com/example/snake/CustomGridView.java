package com.example.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CustomGridView extends View {
    private static final int NUM_COLUMNS = 6;
    private static final int NUM_ROWS = 16;

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

        // Largeur et hauteur de la vue
        int width = getWidth();
        int height = getHeight();

        // Largeur et hauteur d'une cellule de la grille
        int cellWidth = width / NUM_COLUMNS;
        int cellHeight = height / NUM_ROWS;

        // Dessiner les lignes verticales
        for (int col = 1; col < NUM_COLUMNS; col++) {
            int x = col * cellWidth;
            canvas.drawLine(x, 0, x, height, gridPaint);
        }

        // Dessiner les lignes horizontales
        for (int row = 1; row < NUM_ROWS; row++) {
            int y = row * cellHeight;
            canvas.drawLine(0, y, width, y, gridPaint);
        }
    }
}
