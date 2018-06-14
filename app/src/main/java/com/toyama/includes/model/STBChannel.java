package com.toyama.includes.model;


public class STBChannel {
	public int STBChannelId;
	public int Number;
	public String Name,Filename,Language,Category;

	public STBChannel() {
		
	}

	public STBChannel(int channelId, int number, String name, String filename, String language, String category) {
		super();
		STBChannelId = channelId;
		Number = number;
		Name = name;
		Filename = filename;
		Language = language;
		Category = category;
	}
}
