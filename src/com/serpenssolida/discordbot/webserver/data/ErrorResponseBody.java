package com.serpenssolida.discordbot.webserver.data;

public class ErrorResponseBody
{
	private final String error;
	
	public ErrorResponseBody(String error)
	{
		this.error = error;
	}
	
	public String getError()
	{
		return this.error;
	}
}
