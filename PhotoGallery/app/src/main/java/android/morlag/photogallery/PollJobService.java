package android.morlag.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {
    static final int JOB_ID = 18324;
    static final String TAG = "PollJobService";
    public static final String ACTION_SHOW_NOTIFICATION =
            "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE =
            "com.bignerdranch.android.photogallery.PRIVATE";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    PollTask task;
    @Override
    public boolean onStartJob(JobParameters params) {
        task = new PollTask();
        Log.i(TAG, "onStartJob: execute starting..");
        task.execute(params);
        Log.i(TAG, "onStartJob: execute started..");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(task!=null){
            task.cancel(true);
            Log.i(TAG, "onStopJob: PollJobService stopped");
        }
        return false;
    }

    class PollTask extends AsyncTask<JobParameters,Void,Void> {

        @Override
        protected Void doInBackground(JobParameters... jobParameters) {
            JobParameters param = jobParameters[0];
            Context context = getApplicationContext();

            String query = QueryPreferences.getStoredQuery(context);
            String lastResultId = QueryPreferences.getLastResultId(context);
            List<GalleryItem> items;

            if(query == null){
                items = new FlickrFetchr().fetchRecentPhotos();
            } else {
                items = new FlickrFetchr().searchPhotos(query);
            }

            if(items.size() == 0)
                return null;

            if(items.get(0).getId().equals(lastResultId)){
                Log.i(TAG, "doInBackground: first item equals last first item");
                return null;
            }

            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(context);
            PendingIntent pi = PendingIntent.getActivity(context,0,i,0);

            Notification notification = new NotificationCompat.Builder(context)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            /*
            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            notificationManager.notify(0,notification);
            sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);
            */
            QueryPreferences.setLastResultId(context,items.get(0).getId());

            jobFinished(param,false);
            showBackgroundNotification(0,notification);
            Log.i(TAG, "doInBackground: PollTask background done");

            return null;
        }
    }
    private void showBackgroundNotification(int requestCode, Notification notification)
    {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null,
                Activity.RESULT_OK, null, null);
    }
    public static void setJobSchedule(Context context, boolean isOn){
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if(!isOn){
            scheduler.cancel(JOB_ID);
            Log.i(TAG, "setJobSchedule: job canceled");
            QueryPreferences.setAlarmOn(context, isOn);
            return;
        }

        JobInfo info = new JobInfo.Builder(JOB_ID,
                new ComponentName(context,PollJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(1000*60*15)
                .setPersisted(true)
                .build();
        if(isJobSchedule(context))
            scheduler.cancel(JOB_ID);
        scheduler.schedule(info);
        QueryPreferences.setAlarmOn(context, isOn);
    }
    public static boolean isJobSchedule(Context context){
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_ID) {
                hasBeenScheduled = true;
            }
        }
        Log.i(TAG, "isJobSchedule: returned "  + hasBeenScheduled);
        return hasBeenScheduled;
    }
}
