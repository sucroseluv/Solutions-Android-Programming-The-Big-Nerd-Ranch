package com.morlag.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.ListFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BoxDrawingView extends View {
    public static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    public BoxDrawingView(Context context) {
        super(context, null);
    }

    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // Прямоугольники рисуются полупрозрачным красным цветом (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);
        // Фон закрашивается серовато-белым цветом
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }


    private int primaryId = -1;
    private int secondId = -1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(),event.getY());
        String action = "";
        //int index = event.getActionIndex();
        //int thisId = event.getPointerId(index);
        int index = MotionEventCompat.getActionIndex(event);
        int thisId = MotionEventCompat.getPointerId(event,index);


        //switch (event.getActionMasked()) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN ID:" + thisId;
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                primaryId = event.getPointerId(index);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "ACTION_POINTER_DOWN ID:" + thisId;
                secondId = event.getPointerId(index);
                break;

            case MotionEvent.ACTION_UP:
                action = "ACTION_UP ID:" + thisId;
                primaryId = -1;
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "ACTION_POINTER_UP ID:" + thisId;
                secondId = -1;
                break;

            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE ID:" + thisId;

                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;

            default:
                action = String.valueOf(event.getAction());
                break;
        }

        Log.i(TAG, action + " at x=" + current.x +
                ", y=" + current.y);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPaint(mBackgroundPaint);
        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    @Nullable @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        float[] f = new float[mBoxen.size()*4];
        int offset = 0;
        for(Box box : mBoxen){
            f[offset] = ( box.getOrigin().x);
            f[offset+1] = ( box.getOrigin().y);
            f[offset+2] = ( box.getCurrent().x);
            f[offset+3] = ( box.getCurrent().y);
            offset += 4;
        }
        bundle.putFloatArray("LOL", f);
        //bundle.putAll(parcelable);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            float[] floats = bundle.getFloatArray("LOL");
            for (int i = 0; i < floats.length; i += 4) {
                Box box = new Box(new PointF(floats[i], floats[i + 1]));
                box.setCurrent(new PointF(floats[i + 2], floats[i + 3]));
                mBoxen.add(box);
            }
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}
