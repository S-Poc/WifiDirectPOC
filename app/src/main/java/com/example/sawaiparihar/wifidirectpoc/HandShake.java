package com.example.sawaiparihar.wifidirectpoc;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by sawai on 07/09/16.
 */
public class HandShake extends AsyncTask<String, String, String> {
    public static Socket mSocket;
    public static final int ByteSize = 512;

    private Context mContext;
    private String mHost;
    private int mPort;
    private String mIp;

    public HandShake(Context mContext, String mHost, int mPort, String ip) {
        this.mContext = mContext;
        this.mHost = mHost;
        this.mPort = mPort;
        this.mIp = ip;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            Socket mSocket = new Socket();
            mSocket.setReuseAddress(true);
            mSocket.bind(null);
            mSocket.connect((new InetSocketAddress(mHost, mPort)), 30000);

            OutputStream stream = mSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(stream);

            HandShakeModel handShakeObj = new HandShakeModel(mIp, Long.parseLong(mIp.getBytes().length + ""));
            oos.writeObject(handShakeObj);


        } catch (Exception e) {
            // exception
            System.out.println(e);
        }

        return null;
    }
}
