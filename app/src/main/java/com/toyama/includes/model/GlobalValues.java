package com.toyama.includes.model;

public class GlobalValues {
	public int CustomerId;
	public String Username,Firstname,Lastname,DBVersion;
	public int BaudRate,IsActive;
	
	public GlobalValues(int customerId, String username, String firstname, String lastname, String version,
			int baudRate, int isActive) {
		super();
		CustomerId = customerId;
		Username = username;
		Firstname = firstname;
		Lastname = lastname;
		DBVersion = version;
		BaudRate = baudRate;
		IsActive = isActive;
	}
}
