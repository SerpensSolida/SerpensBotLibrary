package com.serpenssolida.discordbot.avatar;
public class Avatar
{
  public final String url;
  public final String id;
  public final String file;
  
  public Avatar(String id, String url, String file)
  {
    this.file = file;
    this.url = url;
    this.id = id;
  }
}
