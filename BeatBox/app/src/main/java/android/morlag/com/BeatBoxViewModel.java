package android.morlag.com;

import android.morlag.com.databinding.FragmentBeatBoxBinding;
import android.view.View;
import android.widget.SeekBar;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class BeatBoxViewModel extends BaseObservable {
    private BeatBox mBeatBox;

    public BeatBoxViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    @Bindable
    public String getInfo(){
        String info = "Playback Speed: " + mBeatBox.getPlayBackSpeed() * 100 + "%";
        return info;
    }

    public void onValueChanged(SeekBar seekBar, int progresValue, boolean fromUser){
        mBeatBox.setPlayBackSpeed((float) progresValue / 100);
        notifyPropertyChanged(BR.info);
    }

    /*public void onSpeedChanged(View v) {
        int value = ((SeekBar)v).setOnSeekBarChangeListener();
        mBeatBox.setPlayBackSpeed(value/100);
    }*/
}
