package com.toyama.includes.model;

import com.toyama.includes.utilities.Utilities;

public class IRDevice {
	public int IRDeviceId=0,RoomId=0;
	public String DeviceName="",Category="",Location="",CustomValue="";
	
	public IRDevice() {
		super();
	}
	
	@Override
	public String toString() {
		this.Location=Utilities.getRoomName(this.RoomId);
		return this.DeviceName+" in "+this.Location;
	}

	public IRDevice(int iRDeviceId, int roomId, String deviceName, String category, String roomName,
			String customValue) {
		super();
		IRDeviceId = iRDeviceId;
		RoomId = roomId;
		DeviceName = deviceName;
		Category = category;
		Location = roomName;
		CustomValue = customValue;
	}
	
	
}