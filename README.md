# SerpensBot
Command bot for Discord

# How to use
1) Clone the repository.
2) In your IDE import the folder as project.
3) Add maven as your framework.
4) Mark "src" folder as source.
6) Create a json file named "bot.json" with this format:

    ```json
    {
       "token" : "place your token here.", 
       "owner" : "place your discord id here."
    }
    ```
    
7) You can start coding.

# Getting started.

This bot works in modules that listen for interaction from a discord user (messages, slash commands, iteraction, reaction, ecc).
To create a module you must extend the class BotListener like this:

```java
import com.serpenssolida.discordbot.module.BotListener;

public class PingPongListener extends BotListener
{ 
   public PingPongListener()
   {
        super("pingpong"); //Internal id of the module.
        this.setModuleName("PingPong"); //Display name for the module.

        //Now we add a simple ping command
        BotCommand command = new BotCommand("ping", "The bot will respond to this command with pong.");
        
        //We set the command callback, this will be called when a user send the command /ping.
        command.setAction((event, guild, channel, author) ->
        {
            this.pongCommand(event, guild, channel, author);
            return true;
        });

        //Add the command to the listener.
        this.addBotCommand(command);
    }
    
    //The bot is based on this lib: https://github.com/DV8FromTheWorld/JDA
    //See its javadoc on how to use these parameters. 
    public void pongCommand(SlashCommandInteractionEvent event, Guild guild, MessageChannel channel, User author)
    {
        //Reply to the event
        event.reply("Pong!").setEphemeral(false).queue();
    }
}
```

Now that we have module we can add it to the BotMain.java:

```java
package com.serpenssolida.discordbot;

public class BotMain
{
	public static void main(String[] args)
	{
		SerpensBot.start();
		
		//Add modules here with: SerpensBot.addModule(listener)
		SerpensBot.addModule(new PingPongListener());
	}
}
```

Now if we send the command /ping the bot will respond with the message "Pong!".
