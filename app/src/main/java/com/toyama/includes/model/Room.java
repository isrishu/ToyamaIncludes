package com.toyama.includes.model;

import java.util.ArrayList;

public class Room {
	public int RoomId;
	public String RoomName;
	public ArrayList<RoomNode> RoomNodes;
	
	public int getRoomId() {
		return RoomId;
	}

	public void setRoomId(int roomId) {
		RoomId = roomId;
	}

	public String getRoomName() {
		return RoomName;
	}

	public void setRoomName(String roomName) {
		RoomName = roomName;
	}

	public Room() {
		RoomNodes=new ArrayList<RoomNode>();
	}
	
	public Room(int rid,String rname) {
		this.RoomId=rid;
		this.RoomName=rname;
		RoomNodes=new ArrayList<RoomNode>();
	}
	
	@Override
	public String toString() {
		return this.RoomName;
	}
}