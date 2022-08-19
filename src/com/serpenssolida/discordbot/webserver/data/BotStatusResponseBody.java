package com.serpenssolida.discordbot.webserver.data;

import net.dv8tion.jda.api.JDA;

public class BotStatusResponseBody
{
	private final JDA.Status status;
	
	public BotStatusResponseBody(JDA.Status status)
	{
		this.status = status;
	}
	
	public JDA.Status getStatus()
	{
		return status;
	}
}
