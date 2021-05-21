package com.nikita.sender;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiP2pService extends Service {

    private static final String TAG = "NIKITA";
    private static final int PORT = 9216;
    private static final int DISPLAY_WIDTH = 1280;
    private static final int DISPLAY_HEIGHT = 720;

    private IBinder binder = new Binder();
    private WifiP2pManager manager;
    private Channel channel;
    private IntentFilter intentFilter;
    private WifiP2pReceiver receiver;


    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter();
        addActionsToIntentFilter();

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getApplicationContext(), Looper.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                WifiP2pService.this.channel = manager.initialize(WifiP2pService.this, getMainLooper(), this);
            }
        });

        //registerP2pService();
        receiver = new WifiP2pReceiver(this, manager, channel);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.discoverPeers(channel, discoveryListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(receiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
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


    private class WifiP2pReceiver extends BroadcastReceiver {

        private WifiP2pService service;
        private WifiP2pManager manager;
        private Channel channel;

        public WifiP2pReceiver(WifiP2pService service, WifiP2pManager manager, Channel channel) {
            this.service = service;
            this.manager = manager;
            this.channel = channel;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiP2pStateChangedAction(intent);
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                handleWifiP2pDiscoveryChangedAction(intent);
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                handleWifiP2pPeersChangedAction();
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                handleWifiP2pConnectionChangedAction(intent);
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                handleWifiP2pThisDeviceChangedAction(intent);
            }
        }
        private void handleWifiP2pStateChangedAction(Intent intent) {
            int wifiState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (wifiState == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(getApplicationContext(), "WIFI ENABLED.", Toast.LENGTH_LONG).show();
            } else if (wifiState == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                Toast.makeText(getApplicationContext(), "WIFI DISABLED.", Toast.LENGTH_LONG).show();
            }
        }

        private void handleWifiP2pDiscoveryChangedAction(Intent intent) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                Toast.makeText(getApplicationContext(), "SEARCHING DEVICE HAS STARTED.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "SEARCHING DEVICE HAS ENDED.", Toast.LENGTH_LONG).show();
            }
        }


        private void handleWifiP2pPeersChangedAction() {
            if (manager != null) {
                if (ActivityCompat.checkSelfPermission(WifiP2pService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.requestPeers(channel, peerListListener);
            }

        }

        private void handleWifiP2pConnectionChangedAction(Intent intent) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()) {
                Toast.makeText(getApplicationContext(), "Connection detected.", Toast.LENGTH_LONG).show();
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                    }
                });
            }
        }

        private void handleWifiP2pThisDeviceChangedAction(Intent intent) {

        }

        private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();

                if(refreshedPeers.size() == 0) {

                }
                else {

                }
            }
        };
    }

    public WifiP2pManager.ActionListener discoveryListener = new WifiP2pManager.ActionListener() {
        @Override
        public void onSuccess() {
            //toggleLoadingVisibility(false);
            //searchBtn.setEnabled(true);
        }

        @Override
        public void onFailure(int reason) {
            Toast.makeText(getApplicationContext(), "There is an error in finding devices.", Toast.LENGTH_LONG).show();
        }
    };
}
