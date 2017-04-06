package com.lee.restaurant.Activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Lee on 2016/9/9.
 */
public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);

        int count = parent.getChildCount();

        for(int i = 0;i < count;i++){
            View view = parent.getChildAt(i);

            float X = view.getX();
            float Y = view.getY();
            float height = view.getHeight();
            float width = view.getWidth();

            c.drawLine(X,Y + height,X + width,Y + height,paint);

//            if (i % 2 == 0){
//                c.drawLine(X + width,Y - height,X + width,Y + height,paint);
//                c.drawLine(X,Y + height,X + width,Y + height,paint);
//            }else{
//                c.drawLine(X,Y + height,X + width,Y + height,paint);
//            }
        }
    }
}
