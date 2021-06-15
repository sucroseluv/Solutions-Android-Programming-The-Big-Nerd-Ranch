package android.morlag.photogallery;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageCache extends LruCache<String, Bitmap> {

    public ImageCache(int maxSize) {
        super(maxSize);
    }

    public Bitmap getBitmapFromMemory(String key) {
        if(key!=null)
            return this.get(key);
        return null;
    }

    public void putIntoMemory(String key, Bitmap drawable){
        if(getBitmapFromMemory(key)==null){
            this.put(key,drawable);
        }
    }
}
