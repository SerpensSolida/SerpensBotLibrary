package com.serpenssolida.discordbot.webserver.data;

public final class LoginRequestBody
{
	private final String password;
	
	public LoginRequestBody(String password)
	{
		this.password = password;
	}
	
	public String getPassword()
	{
		return this.password;
	}
}
