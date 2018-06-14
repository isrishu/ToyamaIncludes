package com.toyama.includes.model;

import com.toyama.includes.utilities.Utilities;

public class Camera {
	public String serialNumber="",lastConnectedIP="",username="",password="",cameraModel="",cameraName="",
			customValue="";
	public boolean isConnected=false;

	public int cameraId=0,roomId=0,playPort=-1,cameraNumber=0;
	public boolean isActive=false,isVisitorCamera=false;
	
	public Camera() {

	}

	@Override
	public String toString() {
		return this.cameraModel+" "+this.cameraName+" "+this.serialNumber
				+" in "+Utilities.getThisRoom(this.roomId).RoomName;
	}
}