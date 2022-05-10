package com.example.testversion.fragment4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class MytableTextView extends androidx.appcompat.widget.AppCompatTextView {

    Paint paint = new Paint();
    public MytableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
       int color = Color.parseColor("#D1D1D1");
        // 为边框设置颜色
        paint.setColor(color);
            }
       @Override
       protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画TextView的4个边
        canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
        canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
        canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint);
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
             }


}
