package com.toyama.includes.model;

import com.toyama.includes.utilities.Utilities;

public class OneTouchControllerMapping {
	public int MappingId;
	public int SwitchNumber,Value,SwitchState;
	public String Key;
	
	public OneTouchControllerMapping() {
		
	}
	
	public String toString() {
		Scene s=Utilities.getScene(this.Value);
		return "Switch No: "+String.valueOf(this.SwitchNumber)+" with state: "+String.valueOf(this.SwitchState)
		+" to "+this.Key+" "+s.SceneName; //String.valueOf(this.Value);
	}
}