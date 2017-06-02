package com.example.sawaiparihar.wifidirectpoc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sawai on 07/09/16.
 */
public class Server extends AsyncTask<String, String, String> {
    private Context mContext;
    public static final int PORT = 8888;
    public static final int SOCKET_TIMEOUT = 5000;
    public static Socket mSocket;
    public static String mClientIpAddress;
    public static String FolderName = "aaX";
    public ServerCallback mServerCallback;


    public Server(Context context, ServerCallback serverCallback) {
        mContext = context;
        mServerCallback = serverCallback;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT));
            mSocket = serverSocket.accept();

            ObjectInputStream OIS = new ObjectInputStream(mSocket.getInputStream());

            String returnValue = "demo";
            try {
                Object obj = OIS.readObject();
                if (obj instanceof HandShakeModel) {
                    HandShakeModel model = ((HandShakeModel) obj);
                    mServerCallback.onIpRetrieval(model.ip);
                    mServerCallback.updateClientIpList(model.ip);

                } else {
                    WiFiTransferModal model = (WiFiTransferModal) obj;
                    mClientIpAddress = model.getInetAddress();

                    String fileTransferSocketIpAddress = "";
                    try {
                        fileTransferSocketIpAddress = FileTransfer.mSocket.getInetAddress().getHostAddress();
                    } catch (Exception e) {
                        //Exception
                    }

                    if (mClientIpAddress != null && mClientIpAddress.equals(fileTransferSocketIpAddress)) {
                        OIS.close();
                        serverSocket.close();

                        return returnValue;
                    }

                    if (!TextUtils.isEmpty(model.getFileName())) {
                        final File file = new File(Environment.getExternalStorageDirectory() + "/"
                                + FolderName + "/" + model.getFileName());
                        File dirs = new File(file.getParent());
                        if (!dirs.exists()) {
                            dirs.mkdirs();
                        }
                        file.createNewFile();

                        InputStream IS = mSocket.getInputStream();
                        copyReceivedFile(IS, new FileOutputStream(file), model.getFileLength());

                        returnValue = file.getAbsolutePath();
                    }

                    OIS.close();
                    serverSocket.close();

                    return returnValue;
                }
            } catch (Exception e) {
                // exception
                System.out.println(e);
            }

        } catch (Exception e) {
            // exception
            System.out.println(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            if (!result.equalsIgnoreCase("demo")) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                mContext.startActivity(intent);
            } else {
                Server serverAsyncTask = new Server(mContext, mServerCallback);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    serverAsyncTask.executeOnExecutor (AsyncTask.THREAD_POOL_EXECUTOR, new String[] { null });
                } else {
                    serverAsyncTask.execute();
                }
            }
        }
    }

    public boolean copyReceivedFile(InputStream inputStream, OutputStream out, Long length) {
        byte buf[] = new byte[512];
        int len;
        long total = 0;
        int progressPercentage = 0;
        int maxProgressUntil = 0;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                try {
                    out.write(buf, 0, len);
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    total += len;
                    if (length > 0) {
                        progressPercentage = (int) ((total * 100) / length);
                    }
                    if (maxProgressUntil < progressPercentage) {
                        maxProgressUntil = progressPercentage;
                        System.out.println(maxProgressUntil);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
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
