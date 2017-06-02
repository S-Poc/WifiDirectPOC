package com.example.sawaiparihar.wifidirectpoc;

/**
 * Created by sawai.parihar on 02/06/17.
 */

public interface ServerCallback {
    void onIpRetrieval(String ipAddress);
    void updateClientIpList(String ipAddress);
}
