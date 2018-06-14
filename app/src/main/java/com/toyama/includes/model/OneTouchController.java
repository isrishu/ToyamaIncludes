package com.toyama.includes.model;

import java.util.ArrayList;

public class OneTouchController {
	public int OneTouchControllerId;
	public Byte MacIdHigher,MacIdLower,NodeIdHigher,NodeIdLower;
	public ArrayList<OneTouchControllerMapping> Mappings;
	public int[] SwitchStates;
	
	public OneTouchController() {
		this.Mappings=new ArrayList<OneTouchControllerMapping>();
		this.SwitchStates=new int[9];
	}
}