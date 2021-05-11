package com.nikita.sender;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentActivity;

import com.cunoraz.gifview.library.GifView;

public class ServiceActivity extends FragmentActivity implements ServiceConnection {

    LinearLayout runningState;
    TextView stopState;
    GifView wifiGif;
    Button startBtn;
    Boolean isRunning = false;

    public static WifiP2pService service;
    public static boolean isBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        InitComponents();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ServiceActivity.this, MainActivity.class);
                startActivity(intent);
                ServiceActivity.this.finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void InitComponents() {
        wifiGif = findViewById(R.id.wifiGif);
        wifiGif.setGifResource(R.drawable.wifi);
        runningState = findViewById(R.id.runningState);
        stopState = findViewById(R.id.stopState);
        startBtn = findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning) {
                    isRunning = false;
                    startBtn.setText(getText(R.string.start_service));
                    toggleState(isRunning);
                    stopWifiP2pService();
                } else {
                    isRunning = true;
                    startBtn.setText(getText(R.string.stop_service));
                    toggleState(isRunning);
                    startWifiP2pService();
                }
            }
        });
    }

    private void toggleState(Boolean flag) {
        if(flag) {
            runningState.setVisibility(View.VISIBLE);
            stopState.setVisibility(View.GONE);
            wifiGif.play();
        } else {
            wifiGif.pause();
            runningState.setVisibility(View.GONE);
            stopState.setVisibility(View.VISIBLE);
        }
    }

    private void startWifiP2pService() {
        Intent intent = new Intent(this, WifiP2pService.class);
        startService(intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void stopWifiP2pService() {
        Intent intent = new Intent(this, WifiP2pService.class);
        stopService(intent);
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.service = ((WifiP2pService) service).getService();
        isBind = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBind = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWifiP2pService();
    }
}