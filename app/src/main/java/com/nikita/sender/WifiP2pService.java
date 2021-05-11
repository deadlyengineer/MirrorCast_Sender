package com.nikita.sender;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class WifiP2pService extends Service implements ConnectionInfoListener, ActionListener {

    private static final String TAG = "NIKITA";
    private static final int PORT = 9216;
    private static final int DISPLAY_WIDTH = 1280;
    private static final int DISPLAY_HEIGHT = 720;

    private IBinder binder = new Binder();
    private WifiP2pManager manager;
    private Channel channel;
    private IntentFilter intentFilter;
    private WifiP2pReceiver receiver;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter();
        addActionsToIntentFilter();

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getApplicationContext(), Looper.getMainLooper(), null);

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();

        //registerP2pService();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(receiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

    }

    private void addActionsToIntentFilter() {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    public WifiP2pService getService() {
        return this;
    }

    @Override
    public void onSuccess() {
        // Socket modules
        Toast.makeText(getApplicationContext(), "Succeeded to connect.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int reason) {

    }

    /*
    private void registerP2pService() {
        Map record = new HashMap();
        record.put("port", String.valueOf(PORT));
        record.put("buddyname", TAG);
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_mirrorcast", "_presence._tcp", record);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionHelper.PERMISSION_FINE_LOCATION);
            return;
        }
        manager.addLocalService(channel, serviceInfo, new ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
                //Toast.makeText()
            }
        });
    }
    */

    private class WifiP2pReceiver extends BroadcastReceiver {

        private WifiP2pManager manager;
        private Channel channel;
        private ConnectionInfoListener connectionInfoListener;

        public WifiP2pReceiver(WifiP2pManager manager, Channel channel, ConnectionInfoListener connectionInfoListener) {
            this.manager = manager;
            this.channel = channel;
            this.connectionInfoListener = connectionInfoListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
