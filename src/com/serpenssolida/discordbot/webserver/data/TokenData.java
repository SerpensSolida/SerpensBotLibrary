package com.serpenssolida.discordbot.webserver.data;

import java.util.HashSet;
import java.util.UUID;

public class TokenData
{
	private final HashSet<UUID> tokens;
	
	public TokenData(HashSet<UUID> tokens)
	{
		this.tokens = tokens;
	}
	
	public HashSet<UUID> getTokens()
	{
		return tokens;
	}
}
