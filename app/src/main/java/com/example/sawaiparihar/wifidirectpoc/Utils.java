package com.example.sawaiparihar.wifidirectpoc;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.nio.ByteOrder;

/**
 * Created by sawai on 18/08/16.
 */
public class Utils {
    public static String Tag = Utils.class.getName();

    public static String getPath(Uri uri, Context context) {
        if (uri == null) {
            Utils.e("uri is null");
            return null;
        }
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        Utils.e("get path method->> after cursor init");
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        Utils.e("get path method->> after cursor");
        Utils.e("get path method->> " + uri.getPath());
        return uri.getPath();
    }

    public static void e(String message) {
        Log.e(Tag, message);
    }

    public static void DisplayToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getMeNthParamInString(String p_text,
                                               String p_seperator, int nThParam) {
        String retStrThirdParam = new String("");
        int index = -1;
        int prevIdx = 0;
        int loopNM = 1;
        boolean loopBool = true;
        while (loopBool) {
            try {
                index = p_text.indexOf(p_seperator, prevIdx);
                if (loopNM >= nThParam) {
                    if (index >= 0) {
                        retStrThirdParam = p_text.substring(prevIdx, index);
                    } else // /-1
                    {
                        retStrThirdParam = p_text.substring(prevIdx);
                    }
                    loopBool = false;
                    break;
                } else {
                    if (index < 0) // /-1
                    {
                        loopBool = false;
                        retStrThirdParam = "";
                        break;
                    }
                }
                loopNM++;
                prevIdx = index + 1;
            } catch (Exception ex) {
                loopBool = false;
                retStrThirdParam = "";
                break;
            }
        } // /while
        if (retStrThirdParam.trim().length() <= 0) {
            retStrThirdParam = "";
        }
        return retStrThirdParam;
    }

    public static long AvailableMemory(Context context){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 );
        return megAvailable;
    }

    // get wifi ip address
    static String getWiFiIPAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = getDottedDecimalIP(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    private static String getDottedDecimalIP(int ipAddr) {

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddr = Integer.reverseBytes(ipAddr);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddr).toByteArray();

        //convert to dotted decimal notation:
        return getDottedDecimalIP(ipByteArray);
    }

    private static String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }
}
