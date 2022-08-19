package com.serpenssolida.discordbot.webserver.data;

import java.util.ArrayList;

public final class LogResponseBody
{
	private final ArrayList<String> lines;
	private final String updateDate;
	
	public LogResponseBody(ArrayList<String> lines, String updateDate)
	{
		this.lines = lines;
		this.updateDate = updateDate;
	}
	
	public ArrayList<String> getLines()
	{
		return this.lines;
	}
	
	public String getUpdateDate()
	{
		return this.updateDate;
	}
}
