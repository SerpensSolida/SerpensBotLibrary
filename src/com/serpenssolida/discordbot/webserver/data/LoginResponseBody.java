package com.serpenssolida.discordbot.webserver.data;

import java.util.UUID;

public class LoginResponseBody
{
	private final UUID token;
	private final String message;
	
	public LoginResponseBody(UUID token, String message)
	{
		this.token = token;
	    this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public UUID getToken()
	{
		return this.token;
	}
}
