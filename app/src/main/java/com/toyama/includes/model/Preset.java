package com.toyama.includes.model;


public class Preset {
	
	//data members only based upon Presets no dependcies yet
	public String presetName;
	public int presetId;
	public int addedById;
	public int presetIsActive;
	public int dateAdded;
	
	public Preset() {
	
	}

	public Preset(int PresetID,String PName,int IsActive) {
		this.presetId=PresetID;
		this.presetName=PName;
		this.presetIsActive=IsActive;
	}
	
	
}
