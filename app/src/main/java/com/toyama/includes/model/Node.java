package com.toyama.includes.model;

public class Node {
	public int NodeId,CDeviceId,OnOffCount,DimmerCount;
	public String NodeName,NodeType;
	public boolean HasMaster=false;
	
	public Node() {
		
	}

	public Node(int nodeId, int deviceId, int onOffCount, int dimmerCount,String nodeName, String nodeType) {
		super();
		NodeId = nodeId;
		CDeviceId = deviceId;
		OnOffCount = onOffCount;
		DimmerCount = dimmerCount;
		NodeName = nodeName;
		NodeType = nodeType;
	}
	
	@Override
	public String toString() {
		return this.NodeName;
	}

	public int getNodeId() {
		return NodeId;
	}

	public void setNodeId(int nodeId) {
		NodeId = nodeId;
	}

	public int getDeviceId() {
		return CDeviceId;
	}

	public void setDeviceId(int deviceId) {
		CDeviceId = deviceId;
	}

	public int getOnOffCount() {
		return OnOffCount;
	}

	public void setOnOffCount(int onOffCount) {
		OnOffCount = onOffCount;
	}

	public int getDimmerCount() {
		return DimmerCount;
	}

	public void setDimmerCount(int dimmerCount) {
		DimmerCount = dimmerCount;
	}

	public String getNodeName() {
		return NodeName;
	}

	public void setNodeName(String nodeName) {
		NodeName = nodeName;
	}

	public String getNodeType() {
		return NodeType;
	}

	public void setNodeType(String nodeType) {
		NodeType = nodeType;
	}
	
	
	
}
