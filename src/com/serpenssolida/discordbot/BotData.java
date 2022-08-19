package com.serpenssolida.discordbot;

public class BotData
{
	private String token;
	private String owner;
	private int apiPort;
	private String apiPassword;
	
	public String getToken()
	{
		return this.token;
	}
	
	public String getOwner()
	{
		return this.owner;
	}
	
	public int getApiPort()
	{
		return this.apiPort;
	}
	
	public String getApiPassword()
	{
		return this.apiPassword;
	}
}
