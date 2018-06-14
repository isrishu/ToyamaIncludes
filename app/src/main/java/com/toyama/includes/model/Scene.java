package com.toyama.includes.model;


import java.util.ArrayList;

public class Scene {
	public int SceneId;
	public String SceneName;
	public ArrayList<SceneSwitch> SceneSwitches;
	public ArrayList<SceneSensor> SceneSensors;
	public ArrayList<SceneIRIndex> SceneIRIndices;
	
	public Scene() {
		super();
		SceneSwitches=new ArrayList<SceneSwitch>();
		SceneSensors=new ArrayList<SceneSensor>();
		SceneIRIndices=new ArrayList<SceneIRIndex>();
	}
	@Override
	public String toString() {
		return this.SceneName;
	}	
}
