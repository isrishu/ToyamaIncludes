package com.toyama.includes.model;

public class NodeSwitch {
	public int NodeSwitchId,IRDeviceId; // linked ir device which is connected to this socket or switch
	// switch name will also reflect the ir device name to which this nodeswitch is linked to if any
	public String SwitchName,RoomName;
	public String Type; // OnOff, Dimmer,Curtain etc
	// category also thinned to only light fan curtain directly belonging here. other categories will come from linked ir device if any
	public String Category; // Light,Fan,Curtain,AC,TV,Music System,Game Console etc..
	public int SwitchNumber;
	// below is dummy for stbs.. design changed as a reference to an already added IR device for below
	public String CustomValue; // Set Top Box Name like Airtel,TataSky etc.. for now
	public int Percent=-1; // to save the switch state of this node switch
	public boolean isOn=false;
	
	public NodeSwitch() {
		this.Percent=-1;
	}

	public NodeSwitch(int nodeSwitchId, String switchName, String roomName, String type, String category,
			int switchNumber, String customValue, int percent) {
		super();
		NodeSwitchId = nodeSwitchId;
		SwitchName = switchName;
		RoomName = roomName;
		Type = type;
		Category = category;
		SwitchNumber = switchNumber;
		CustomValue = customValue;
		Percent = percent;
	}

	public NodeSwitch(int nodeId, String switchName, String type,String category) {
		super();
		NodeSwitchId = nodeId;
		SwitchName = switchName;
		Type = type;
		Category = category;
		this.Percent=-1;
	}

	public int getNodeId() {
		return NodeSwitchId;
	}

	public void setNodeId(int nodeId) {
		NodeSwitchId = nodeId;
	}

	public String getSwitchName() {
		return SwitchName;
	}

	public void setSwitchName(String switchName) {
		SwitchName = switchName;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}
	
	@Override
	public String toString() {
		return this.SwitchName+" in "+this.RoomName;
	}
}