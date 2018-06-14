package com.toyama.includes.model;


public class IRChannel {
	public String Channel;
	public int ChannelNumber;
	
	public IRChannel() {
		super();
	}
	
	public IRChannel(String c,int cn) {
		this.Channel=c;
		this.ChannelNumber=cn;
	}
	
	@Override
	public String toString() {
		return this.Channel;
	}
}

