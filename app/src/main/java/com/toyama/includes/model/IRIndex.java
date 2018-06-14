package com.toyama.includes.model;


public class IRIndex {
	public int IRIndexId,IRDeviceId,IRChannel,Value;
	public byte IRIndex;
	public String RemoteButton;
	
	public IRIndex() {
		super();
	}

	public IRIndex(int iRIndexId, int iRBlasterId, int deviceId, int channel, int value, byte iRIndex,
			String remoteButton) {
		super();
		IRIndexId = iRIndexId;
		IRDeviceId = deviceId;
		IRChannel = channel;
		Value = value;
		IRIndex = iRIndex;
		RemoteButton = remoteButton;
	}
	
}
