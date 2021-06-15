package android.morlag.com;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.View;

public class PictureUtils {
    public static Bitmap getScaledBitmap (String path, int destWidth, int destHeight) {
        Log.d("CriminalIntent",
                String.format("ScaledBitmap is %1$d and %2$s", destWidth, destHeight));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float scrWidth = options.outWidth;
        float scrHeight = options.outHeight;

        int inSampleSize = 1;
        if( scrHeight > destHeight || scrWidth > destWidth) {
            float heightScale = scrHeight/destHeight;
            float widthScale = scrWidth/destWidth;
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        //Point size = new Point();
        //activity.getWindowManager().getDefaultDisplay().getSize(size);
        View photo = activity.findViewById(R.id.crime_photo);

        //Log.d("CriminalIntent",
        //        String.format("ScaledBitmap is %1$d and %2$s",size.x, size.y));
        //return getScaledBitmap(path, size.x, size.y);
        return getScaledBitmap(path, photo.getMeasuredWidth(), photo.getMeasuredHeight());
    }
}
