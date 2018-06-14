package com.toyama.includes.model;

import java.util.ArrayList;

/**
 * Created by Srishu Indrakanti on 27-01-2018.
 */

public class RoomNodeToToggle {
    public int RoomNodeId;
    public Byte MacIdHigher,MacIdLower,NodeIdHigher,NodeIdLower,DeviceId,CDeviceId;
    public int Version;

    public byte[] switchStates=new byte[9];

    public RoomNodeToToggle() {
        this.switchStates=new byte[9];
        for(int i=0;i<switchStates.length;i++) {
            switchStates[i]=(byte)0x00;
        }
    }
}
