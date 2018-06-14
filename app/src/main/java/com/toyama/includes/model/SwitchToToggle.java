package com.toyama.includes.model;

public class SwitchToToggle {
	public Byte MacIdHigher,MacIdLower,NodeIdHigher,NodeIdLower,DeviceId,CDeviceId;
	public int Version;
	
	public String SwitchName;
	public String Type; // OnOff, Dimmer,Curtain etc
	public String Category; // Light,Fan,Curtain,AC etc..
	public int SwitchNumber,NodeSwitchId;
	
	public String Mode;
	public int Level;
	public boolean IsOn;
	
	public SwitchToToggle() {
		
	}
}
