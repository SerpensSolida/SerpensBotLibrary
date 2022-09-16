package com.serpenssolida.discordbot.avatar;

import java.util.HashMap;
import java.util.Map;

public class AvatarData
{
	private final Map<String, Avatar> avatars = new HashMap<>();
	
	public Map<String, Avatar> getAvatars()
	{
		return this.avatars;
	}
}
