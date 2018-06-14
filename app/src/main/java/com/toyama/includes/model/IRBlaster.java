package com.toyama.includes.model;

import java.util.ArrayList;

public class IRBlaster {
	public int IRBlasterId;
	public byte NodeIdHigher,NodeIdLower,MacIdHigher,MacIdLower,CDeviceId;
	public int Version,RoomId;
	public String RoomName;
	public ArrayList<IRIndex> IRIndices;
	
	public IRBlaster() {
		super();
		this.IRIndices=new ArrayList<IRIndex>();
	}
	
	@Override
	public String toString() {
		return "IR Blaster in "+this.RoomName;
	}

	public IRBlaster(int iRBlasterId, byte nodeIdHigher, byte nodeIdLower, byte macIdHigher, byte macIdLower,
                     byte cDeviceId, int version, int roomId, String roomName) {
		super();
		IRBlasterId = iRBlasterId;
		NodeIdHigher = nodeIdHigher;
		NodeIdLower = nodeIdLower;
		MacIdHigher = macIdHigher;
		MacIdLower = macIdLower;
		CDeviceId = cDeviceId;
		Version = version;
		RoomId = roomId;
		RoomName = roomName;
	}

	
}