package hu.videotour.ksunautospeaker;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
public class MainActivity extends Activity {
    private static final int REQ_PHONE = 100;
    private TextView status;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32,48,32,32);
        TextView title = new TextView(this);
        title.setText("KSUN AutoSpeaker"); title.setTextSize(22); title.setTextColor(Color.BLACK);
        layout.addView(title);
        status = new TextView(this); status.setTextSize(16); status.setTextColor(Color.DKGRAY);
        status.setPadding(0,24,0,0); layout.addView(status);
        setContentView(layout); ensurePermissionAndStart();
    }
    private void ensurePermissionAndStart() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            status.setText("A Telefon engedély szükséges az automatikus működéshez.");
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQ_PHONE); return;
        }
        startMonitor();
    }
    private void startMonitor() {
        Intent service = new Intent(this, CallMonitorService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(service); else startService(service);
        status.setText("Automatikus hívásfigyelés aktív. Rendszerindításkor magától elindul.");
    }
    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if (requestCode == REQ_PHONE && results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) startMonitor();
        else if (requestCode == REQ_PHONE && status != null) status.setText("A Telefon engedély nélkül az automatikus kihangosítás nem működik.");
    }
}
