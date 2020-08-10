package lab.nicc.kioskyoungcheon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by user on 2018-05-08.
 */

public class DataUpdatedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("lab.nicc1")) {
            if (intent.getBooleanExtra("updated", false)&&!MainActivity.getIsSeatPopup()) {
                context.sendBroadcast(new Intent("updated"));
                Log.d("updated", String.valueOf(intent.getBooleanExtra("updated", false)));
            }

            if (intent.getBooleanExtra("serverAddr", false)&&!MainActivity.getIsSeatPopup()) {
                context.sendBroadcast(new Intent("getServerAddr"));
            }

        } else if(intent.getAction().equals("firstBootSetting")) {
            context.sendBroadcast(new Intent("firstBootReceiver"));
        }
    }
}