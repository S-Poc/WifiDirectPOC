package com.example.sawaiparihar.wifidirectpoc;

import java.io.Serializable;

/**
 * Created by sawai.parihar on 02/06/17.
 */

public class HandShakeModel implements Serializable {
    String ip;
    Long fileLength;

    public HandShakeModel(String ip, Long fileLength) {
        this.ip = ip;
        this.fileLength = fileLength;
    }
}