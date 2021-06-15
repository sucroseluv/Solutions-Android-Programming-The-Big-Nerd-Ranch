package android.morlag.com;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.morlag.com.databinding.FragmentBeatBoxBinding;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    public static final String TAG = "BeatBox";
    public static final String SOUNDS_FOLDER = "sample_sounds";
    public static final int MAX_SOUNDS = 5;

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;
    private float mPlayBackSpeed = 1.0f;

    public List<Sound> getSounds() {
        return mSounds;
    }



    public BeatBox(Context context){
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC,0);
        mAssets = context.getAssets();
        String[] soundNames = null;
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
        }
        catch (IOException ex){
            Log.i(TAG,"Load list exception: " + ex.getMessage());
        }
        for(String filename : soundNames){
            try {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                mSounds.add(sound);
                load(sound);
            }catch (IOException ioe) {
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd,1);
        sound.setSoundId(soundId);
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if(soundId == null)
            return;
        mSoundPool.play(soundId,1.0f,1.0f,1,0,mPlayBackSpeed);
    }

    public void release() {
        mSoundPool.release();
    }

    public float getPlayBackSpeed() {
        return mPlayBackSpeed;
    }

    public void setPlayBackSpeed(float playBackSpeed) {
        mPlayBackSpeed = playBackSpeed;
    }
}
