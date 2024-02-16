package com.example.snake;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BodySnake {

    private Handler handler;
    private Runnable runnable;

    /***
     * Fonction qui permet de convertir une valeur "dp" en pixel
     * @param context This
     * @param dp Int, Valeur à convertir en pixel
     * @return Int, la valeur convertie en pixel
     */
    public static int convertDpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
