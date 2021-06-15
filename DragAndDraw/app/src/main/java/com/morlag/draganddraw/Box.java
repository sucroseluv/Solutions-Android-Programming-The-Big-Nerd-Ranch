package com.morlag.draganddraw;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;

    private final String ORIGIN="ORIGIN";
    private final String CURRENT="CURRENT";

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setCurrent(PointF mCurrent) {
        this.mCurrent = mCurrent;
    }

    public void setOrigin(PointF mOrigin) {
        this.mOrigin = mOrigin;
    }

}
