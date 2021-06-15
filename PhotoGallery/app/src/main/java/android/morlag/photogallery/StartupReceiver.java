package android.morlag.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver{
    public static final String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + intent);
        boolean shouldStartAlarm = QueryPreferences.isAlarmOn(context);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            PollService.setServiceAlarm(context, shouldStartAlarm);
        else
            PollJobService.setJobSchedule(context, shouldStartAlarm);
    }
}
