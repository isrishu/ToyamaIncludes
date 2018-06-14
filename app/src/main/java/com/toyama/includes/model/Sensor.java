package com.toyama.includes.model;

import com.toyama.includes.utilities.Utilities;

public class Sensor {
	public int SensorId=0,RoomId=0;
	public String SensorName="",Location="",Type="";
	public byte NodeIdHigher,NodeIdLower,MacIdHigher,MacIdLower,CDeviceId;
	public int Version =0;
	public boolean IsActive=false,IsArmed=false;
	
	public Sensor(int sensorId, String sensorName, String location,
			byte nodeIdHigher, byte nodeIdLower, byte macIdHigher,
			byte macIdLower, byte cDeviceId) {
		super();
		SensorId = sensorId;
		SensorName = sensorName;
		Location = location;
		NodeIdHigher = nodeIdHigher;
		NodeIdLower = nodeIdLower;
		MacIdHigher = macIdHigher;
		MacIdLower = macIdLower;
		CDeviceId = cDeviceId;
	}
	
	public Sensor() {
		super();
	}

	@Override
	public String toString() {
		this.Location=Utilities.getRoomName(this.RoomId);
		return this.SensorName+" at "+this.Location;
	}
}