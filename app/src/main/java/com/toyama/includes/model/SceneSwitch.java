package com.toyama.includes.model;


public class SceneSwitch {
	public int SceneSwitchId,NodeSwitchId;
	public int Level;
	public boolean IsOn;
	public boolean IsIncluded;
	
	public SceneSwitch() {
		super();
		IsIncluded=true;
	}
}