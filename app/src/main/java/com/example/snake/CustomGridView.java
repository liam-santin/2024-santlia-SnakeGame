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

        // Dessiner les lignes verticales
        for (int col = 1; col < NBR_COLUMNS; col++) {
            int x = col * cellWidth;
            canvas.drawLine(x, 0, x, height, gridPaint);
        }

        // Dessiner les lignes horizontales
        for (int row = 1; row < NBR_ROWS; row++) {
            int y = row * cellHeight;
            canvas.drawLine(0, y, width, y, gridPaint);
        }
    }
}
