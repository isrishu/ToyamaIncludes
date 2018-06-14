package com.toyama.includes.model;


import java.util.ArrayList;

public class SetTopBox {
	public int SetTopBoxId;
	public String Name,Make,Model;
	public ArrayList<STBChannel> Channels;
	
	public SetTopBox() {
		this.Channels=new ArrayList<STBChannel>();
	}
}
