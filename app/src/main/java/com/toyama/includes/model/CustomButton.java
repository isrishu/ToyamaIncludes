package com.toyama.includes.model;

public class CustomButton {
	public int CustomButtonId,IRDeviceId;
	public String ButtonName,RemoteButton;
	
	public CustomButton() {
		
	}

	public CustomButton(int customButtonId, int deviceId, String buttonName, String remoteButton) {
		super();
		CustomButtonId = customButtonId;
		IRDeviceId = deviceId;
		ButtonName = buttonName;
		RemoteButton = remoteButton;
	}
}
