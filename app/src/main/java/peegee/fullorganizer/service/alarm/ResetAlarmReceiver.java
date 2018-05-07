package peegee.fullorganizer.service.alarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ResetAlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent resetServiceIntent = new Intent(context, BootService.class);
            context.startService(resetServiceIntent);

        }
    }

}
