package com.toyama.includes.model;


public class User {
	public int UserId=0,Role=2;
	public String Username="",Password="";
	public String Firstname="",Lastname="",Email="";
	//public boolean IsMaster=false;
	
	public User() {
		this.UserId=0;
		this.Role=2;
	}
	
	@Override
	public String toString() {
		return this.Username+" "+this.Firstname+" "+this.Lastname;
	}
}
