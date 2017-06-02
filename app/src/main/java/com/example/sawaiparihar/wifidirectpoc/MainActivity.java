package com.example.sawaiparihar.wifidirectpoc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WifiP2pManager.PeerListListener,
        WifiP2pManager.ConnectionInfoListener, ServerCallback {
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mWifiP2pChannel;

    private RecyclerView mRecyclerView;
    private Adapter mAdapter;

    private boolean isWifiDirectEnabled;

    private List<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();

    private final IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver receiver;
    private List<String> mClientIpList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiP2pChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(mWifiP2pManager, mWifiP2pChannel, this);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new Adapter(this, mPeers);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.find).setOnClickListener(this);
        findViewById(R.id.send_ip_to_server).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find:

                mWifiP2pManager.discoverPeers(mWifiP2pChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        mWifiP2pManager.requestPeers(mWifiP2pChannel, MainActivity.this);
                        Toast.makeText(MainActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });


                break;

            case R.id.send_ip_to_server:
                initiateHandShake();
                break;
        }
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        mPeers.clear();
        mPeers.addAll(peers.getDeviceList());

        mAdapter.notifyDataSetChanged();
    }

    public void connectToPeer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mWifiP2pManager.connect(mWifiP2pChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "connectToPeerSUCCESS",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "connectToPeerFAILURE",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disconnectFromPeer() {
        mWifiP2pManager.cancelConnect(mWifiP2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "disconnectFromPeerSUCCESS",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "disconnectFromPeerFAILURE",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (receiver != null) {
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public void requestConnectionInfo(Intent intent) {
        if (mWifiP2pManager != null) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mWifiP2pManager.requestConnectionInfo(mWifiP2pChannel, this);
            }
        }
    }

    public static WifiP2pInfo mConnectionInfo;
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        mConnectionInfo = wifiP2pInfo;

        ((TextView) findViewById(R.id.group_owner)).setText("IS GROUP_OWNER: " + wifiP2pInfo.isGroupOwner);

//        if (mConnectionInfo.groupFormed && mConnectionInfo.isGroupOwner) {
            Server serverAsyncTask = new Server(this, this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                serverAsyncTask.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, new String[] { null });
            } else {
                serverAsyncTask.execute();
            }
//        }
    }

    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                String selectedFilePath = Utils.getPath(uri, this);
                File file = new File(selectedFilePath);
                Long fileLength = file.length();
                String extension = file.getName();
                int port = 8888;
                String targetIp;
                if (mConnectionInfo.isGroupOwner) {
                    for (int i = 0; i < mClientIpList.size(); i++) {
                        targetIp = mClientIpList.get(0);
                        FileTransfer fileTransferAsyncTask = new FileTransfer(this, targetIp, port,
                                uri.toString(), extension, fileLength+"");

                        fileTransferAsyncTask.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, new String[] { null });
                    }
                } else {
                    targetIp = mConnectionInfo.groupOwnerAddress.getHostAddress();
                    FileTransfer fileTransferAsyncTask = new FileTransfer(this, targetIp, port,
                            uri.toString(), extension, fileLength+"");

                    fileTransferAsyncTask.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, new String[] { null });
                }


            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void initiateHandShake() {
        try {
            int port = 8888;
            String targetIp = mConnectionInfo.groupOwnerAddress.getHostAddress();
            String myIp = Utils.getWiFiIPAddress(this);

            HandShake handShake = new HandShake(this, targetIp, port, myIp);
            handShake.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, new String[] { null });

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    @Override
    public void onIpRetrieval(final String ipAddress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = ((TextView) findViewById(R.id.group_owner));
                String text = tv.getText() + "\n" + "CLIENT_IP_IS: " + ipAddress;
                tv.setText(text);
            }
        });

    }

    @Override
    public void updateClientIpList(String ipAddress) {
        mClientIpList.add(ipAddress);
    }
}
