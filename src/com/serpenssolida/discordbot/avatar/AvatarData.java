package com.serpenssolida.discordbot.avatar;

import java.util.HashMap;

public class AvatarData
{
	private final HashMap<String, Avatar> avatars = new HashMap<>();
	
	public HashMap<String, Avatar> getAvatars()
	{
		return this.avatars;
	}
}
