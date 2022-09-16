package com.serpenssolida.discordbot.webserver.data;

import java.util.Set;
import java.util.UUID;

public class TokenData
{
	private final Set<UUID> tokens;
	
	public TokenData(Set<UUID> tokens)
	{
		this.tokens = tokens;
	}
	
	public Set<UUID> getTokens()
	{
		return tokens;
	}
}
