package com.example.sawaiparihar.wifidirectpoc;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by sawai on 07/09/16.
 */
public class FileTransfer extends AsyncTask<String, String, String> {
    public static Socket mSocket;
    public static final int ByteSize = 512;

    private Context mContext;
    private String mHost;
    private int mPort;
    private String mFileUri;
    private String mExtension;
    private String mFileLength;

    public FileTransfer(Context mContext, String mHost, int mPort, String mFileUri, String mExtension, String mFileLength) {
        this.mContext = mContext;
        this.mHost = mHost;
        this.mPort = mPort;
        this.mFileUri = mFileUri;
        this.mExtension = mExtension;
        this.mFileLength = mFileLength;
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            Socket mSocket = new Socket();
            mSocket.setReuseAddress(true);
            mSocket.bind(null);
            mSocket.connect((new InetSocketAddress(mHost, mPort)), 30000);

            OutputStream stream = mSocket.getOutputStream();
            ContentResolver cr = mContext.getContentResolver();

            Long FileLength = Long.parseLong(mFileLength);
            ObjectOutputStream oos = new ObjectOutputStream(stream);

            WiFiTransferModal transObj = new WiFiTransferModal(mExtension, FileLength);
            oos.writeObject(transObj);

            try {
                InputStream is = cr.openInputStream(Uri.parse(mFileUri));
                copyFile(is, stream, mFileLength);
            } catch (Exception e) {
                // file
            }


        } catch (Exception e) {
            // exception
            System.out.println(e);
        } finally {
//            try {
//                if (mSocket.isConnected()) {
//                    mSocket.close();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        return null;
    }

    public boolean copyFile(InputStream inputStream, OutputStream out, String flLength) {
        int percentage = 0;
        long total = 0;
        long test = 0;
        byte buf[] = new byte[ByteSize];
        int len;
        try {
            Long fileLength = Long.parseLong(flLength);
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                try {
                    total += len;
                    if (fileLength > 0) {
                        percentage = (int) ((total * 100) / fileLength);
                    }

                    System.out.println("xx: percentage" + percentage);
                } catch (Exception e) {
                    percentage = 0;
                }
            }

            out.close();
            inputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
