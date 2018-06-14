package com.toyama.includes.model;


import java.util.ArrayList;

public class ToyamaSwitch {
	public String type="",macstr="";
	public byte deviceId;
	public byte subIdPre;
	public byte[] macId=new byte[2];
	public byte[] nodeId=new byte[2];
	//public ArrayList<String> Nodes=new ArrayList<String>();
	
	public ToyamaSwitch() {
		
	}
	
	public ToyamaSwitch(byte[] macid,byte[] switchid,ArrayList<String> nodes) {
		this.macId=macid;
		this.nodeId=switchid;
		//this.Nodes=nodes;
	}
	
	public ToyamaSwitch(byte[] macid,byte[] nodeid,byte subidpre,byte deviceid,String type,String macstr) {
		this.macId=macid;
		this.nodeId=nodeid;
		this.subIdPre=subidpre;
		this.deviceId=deviceid;
		this.type=type;
		this.macstr=macstr;
	}
	
	/*public ToyamaSwitch(String hmacid,String lmacid,String hswitchid,String lswitchid) {
		this.MacId=new bytemacid;
		this.SwitchId=switchid;
	}*/
}
