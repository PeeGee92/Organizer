package peegee.fullorganizer.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import peegee.fullorganizer.alarm.RingtonePlayingService;

public class JobSchedulerService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Intent serviceIntent = new Intent(getApplicationContext(), RingtonePlayingService.class);
        getApplicationContext().startService(serviceIntent);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
