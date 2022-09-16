package com.serpenssolida.discordbot.webserver.data;

import java.util.List;

public final class LogResponseBody
{
	private final List<String> lines;
	private final String updateDate;
	
	public LogResponseBody(List<String> lines, String updateDate)
	{
		this.lines = lines;
		this.updateDate = updateDate;
	}
	
	public List<String> getLines()
	{
		return this.lines;
	}
	
	public String getUpdateDate()
	{
		return this.updateDate;
	}
}
