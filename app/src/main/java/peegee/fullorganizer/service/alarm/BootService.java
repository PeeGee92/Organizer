package peegee.fullorganizer.service.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

class BootService extends IntentService {

    public BootService() {
        super("BootService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
