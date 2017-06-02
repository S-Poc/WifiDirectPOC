package com.example.sawaiparihar.wifidirectpoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by sawai on 01/09/16.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:  // Wifi Direct on/Off broadcast event
                System.out.println("xxx: WIFI_P2P_STATE_CHANGED_ACTION");
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                boolean isWifiDirectEnabled = false;
                switch (state) {
                    case WifiP2pManager.WIFI_P2P_STATE_ENABLED:
                        isWifiDirectEnabled = true;
                        break;

                    case WifiP2pManager.WIFI_P2P_STATE_DISABLED:
                        isWifiDirectEnabled = false;
                        break;
                }
//                activity.setWifiDirectEnabled(isWifiDirectEnabled);

                break;

            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION: // Peers list change, Update it
                System.out.println("xxx: WIFI_P2P_PEERS_CHANGED_ACTION");
//                activity.requestPeerList();

                break;

            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION: // new connection is setup. we are connected with the other device, request connection
                // info to find group owner IP
                System.out.println("xxx: WIFI_P2P_CONNECTION_CHANGED_ACTION");

                activity.requestConnectionInfo(intent);

                break;
        }
    }
}
