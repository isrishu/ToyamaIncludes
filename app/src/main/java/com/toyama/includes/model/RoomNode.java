package com.toyama.includes.model;

import java.util.ArrayList;

public class RoomNode {
	public int RoomNodeId,NodeId;
	public String NodeName="",NodeType="";
	public Byte MacIdHigher,MacIdLower,NodeIdHigher,NodeIdLower;
	public int Version; // was earlier address, which is standard 1 now. so using to distinguish between old and new switches
	public ArrayList<NodeSwitch> NodeSwitches;
	
	public RoomNode() {
		NodeSwitches=new ArrayList<NodeSwitch>();
	}

	public RoomNode(int roomNodeId, int nodeId, String nodeName, String nodeType, Byte macIdHigher, Byte macIdLower,
					Byte nodeIdHigher, Byte nodeIdLower, int version, ArrayList<NodeSwitch> nodeSwitches) {
		super();
		RoomNodeId = roomNodeId;
		NodeId = nodeId;
		NodeName = nodeName;
		NodeType = nodeType;
		MacIdHigher = macIdHigher;
		MacIdLower = macIdLower;
		NodeIdHigher = nodeIdHigher;
		NodeIdLower = nodeIdLower;
		Version = version;
		NodeSwitches = nodeSwitches;
	}

	public RoomNode(int roomNodeId, String nodeName,byte macIdHigher, byte macIdLower, byte nodeIdHigher,
			byte nodeIdLower, int version) {
		super();
		RoomNodeId = roomNodeId;
		NodeName = nodeName;
		MacIdHigher = macIdHigher;
		MacIdLower = macIdLower;
		NodeIdHigher = nodeIdHigher;
		NodeIdLower = nodeIdLower;
		Version = version;
		NodeSwitches=new ArrayList<NodeSwitch>();
	}
	
	@Override
	public String toString() {
		return NodeName;
	}
}