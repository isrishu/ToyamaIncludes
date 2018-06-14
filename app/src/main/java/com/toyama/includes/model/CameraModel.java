package com.toyama.includes.model;

public class CameraModel {
	public int ModelId;
	public String Model,PlayStorePackage;
	
	public CameraModel() {
	}
	
	public CameraModel(int mid,String model,String psPackage) {
		super();
		this.ModelId = mid;
		this.Model=model;
		this.PlayStorePackage=psPackage;
	}
	
	@Override
	public String toString() {
		return this.Model;
	}
}