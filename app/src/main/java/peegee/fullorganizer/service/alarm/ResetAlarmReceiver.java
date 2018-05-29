package peegee.fullorganizer.service.alarm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Class extending BroadcastReceiver
 * Called when the device reboots
 */
public class ResetAlarmReceiver extends BroadcastReceiver{

    /**
     * onReceive method
     * <p>
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {

            Intent resetServiceIntent = new Intent(context, BootService.class);
            context.startService(resetServiceIntent);

        }
    }

}
