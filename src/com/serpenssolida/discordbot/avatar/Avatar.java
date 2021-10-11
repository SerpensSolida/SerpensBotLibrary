package com.serpenssolida.discordbot.avatar;
public class Avatar
{
  public final String ownerID;
  public final String url;
  public final String id;
  public final String file;
  
  public Avatar(String ownerID, String id, String url, String file)
  {
    this.file = file;
    this.url = url;
    this.id = id;
    this.ownerID = ownerID;
  }
}
