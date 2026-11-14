package hu.videotour.ksunautospeaker;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG="KSUN-AutoSpeaker";
    @Override public void onReceive(Context context,Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent service=new Intent(context,CallMonitorService.class);
            try {
                if(Build.VERSION.SDK_INT>=26)context.startForegroundService(service); else context.startService(service);
                Log.i(TAG,"Service start requested after BOOT_COMPLETED");
            } catch(Exception e) { Log.e(TAG,"Could not start service after boot",e); }
        }
    }
}
