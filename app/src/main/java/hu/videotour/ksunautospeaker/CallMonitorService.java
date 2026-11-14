package hu.videotour.ksunautospeaker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
public class CallMonitorService extends Service {
    private static final String TAG="KSUN-AutoSpeaker", CHANNEL="ksun_call_monitor";
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    private PhoneStateListener listener;
    private int previousState=TelephonyManager.CALL_STATE_IDLE;
    private final Handler handler=new Handler(Looper.getMainLooper());
    @Override public void onCreate() {
        super.onCreate(); audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        createNotificationChannel(); startForeground(1,buildNotification());
        Log.i(TAG,"CallMonitorService created");
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId) {
        if(checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG,"READ_PHONE_STATE permission missing; stopping."); stopSelf(); return START_NOT_STICKY;
        }
        if(telephonyManager==null) {
            telephonyManager=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            listener=new PhoneStateListener() {
                @Override public void onCallStateChanged(int state,String phoneNumber) {
                    Log.i(TAG,"Call state changed: "+state);
                    if(state==TelephonyManager.CALL_STATE_OFFHOOK && previousState!=TelephonyManager.CALL_STATE_OFFHOOK) requestSpeakerWithRetries();
                    previousState=state;
                }
            };
            telephonyManager.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
            Log.i(TAG,"Phone state listener registered");
        }
        return START_STICKY;
    }
    private void requestSpeakerWithRetries() {
        final int[] delays={350,800,1400,2200};
        for(int delay:delays) handler.postDelayed(() -> {
            if(audioManager==null)return;
            boolean wired=audioManager.isWiredHeadsetOn(), sco=audioManager.isBluetoothScoOn(), a2dp=audioManager.isBluetoothA2dpOn();
            Log.i(TAG,"Speaker route attempt: wired="+wired+", btSco="+sco+", btA2dp="+a2dp);
            if(!wired&&!sco&&!a2dp) { audioManager.setSpeakerphoneOn(true); Log.i(TAG,"Speakerphone ON requested"); }
            else Log.i(TAG,"External audio route detected; speaker request skipped");
        },delay);
    }
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel=new NotificationChannel(CHANNEL,"Hívásfigyelő",NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm=getSystemService(NotificationManager.class); nm.createNotificationChannel(channel);
        }
    }
    private Notification buildNotification() {
        if(Build.VERSION.SDK_INT>=26) return new Notification.Builder(this,CHANNEL).setContentTitle("KSUN AutoSpeaker").setContentText("Automatikus GSM-hívásfigyelés aktív").setSmallIcon(android.R.drawable.sym_call_incoming).setOngoing(true).build();
        return new Notification.Builder(this).setContentTitle("KSUN AutoSpeaker").setContentText("Automatikus GSM-hívásfigyelés aktív").setSmallIcon(android.R.drawable.sym_call_incoming).setOngoing(true).build();
    }
    @Override public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if(telephonyManager!=null&&listener!=null)telephonyManager.listen(listener,PhoneStateListener.LISTEN_NONE);
        Log.w(TAG,"CallMonitorService destroyed"); super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent){return null;}
}
