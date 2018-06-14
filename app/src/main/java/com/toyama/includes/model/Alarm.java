package com.toyama.includes.model;

public class Alarm {
	public int AlarmId,RoomId;
	public String AlarmName,Location,Type;
	public byte NodeIdHigher,NodeIdLower,MacIdHigher,MacIdLower,CDeviceId;
	public int Version;
	
	public Alarm() {
		super();
	}
	
	@Override
	public String toString() {
		return this.AlarmName+" at "+this.Location;
	}
}
