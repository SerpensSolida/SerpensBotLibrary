# SerpensBot
This library was developed to aid at developing discord bots using the [JDA](https://github.com/discord-jda/JDA) library.
It features easy to use command definition and implementation and adds lots of feature such as a web interface for reading logs
or starting and stopping the bot.

# Installation

To import the library you can use Maven. Firstly add the maven repository:

```xml
<repositories>
    [...]
    <repository>
        <id>serpenssolida</id>
        <name>serpensbot-library</name>
        <url>http://ddns.serpenssolida.com:3035/releases</url>
    </repository>
</repositories>
```

And add the library to the dependencies:

```xml
<dependencies>
    [...]
    <dependency>
        <groupId>com.serpenssolida.discordbot</groupId>
        <artifactId>serpensbot-library</artifactId>
        <version>1.3.8</version>
    </dependency>
</dependencies>
``` 

Then you need to create a file named "bot.json" in the root directory of the project (same level as src folder) that the bot will use to retrieve the token, set up the bot connection and the web interface.

The file must have this format:
```json
{
  "token": "Your bot token.",
  "owner": "Discord id of the owner of the bot (used to indentify bot owner).",
  "apiPort": "Port of the web interface of the bot.",
  "apiPassword": "Password of the web interface of the bot."
}
```

# Getting started

This bot works in modules that listen for interaction from a discord user (messages, slash commands, iteraction, reaction, ecc).

First of all the bot must be started, you can do this by calling *start* method of **SerpensBot** class:

```java
import com.serpenssolida.discordbot.SerpensBot;

public class BotMain
{
    public static void main(String[] args)
    {
        SerpensBot.start();
    }
}
```

Now you can start adding modules to the bot.

For this example we will create a ping pong module. A module has a main command and all the commands for the modules are
subcommand of the main command. To create a module you must extend the class BotListener like this:

```java
import com.serpenssolida.discordbot.module.BotListener;

public class PingPongListener extends BotListener
{ 
    public PingPongListener()
    {
        super("pingpong"); //Internal id of the module. Must be unique.
        this.setModuleName("PingPong"); //Display name for the module.
    }
}
```

Inside the constructor you can start defining commands. In this case we define the command */ping* using the class **BotCommand**,
the command will call the *pingCommand* method (that will be defined down) when the user uses the command:

```java
//Command for creating a filter.
BotCommand command = new BotCommand("ping", "The bot will respond to this command with pong. It will append the given parameter to the message");

//This is the action of the command. When a user tries to use the command it will call the given method/callback.
command.setAction(this::pingCommand);
```

We can set command arguments by using the *getCommandData()* method that retrives the
[SubcommandData](https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/interactions/commands/build/SubcommandData.html) of the command.
```java
//To add parameters to the command we can use "command.getCommanData" and use the addOption method.
//In this case we are creating an optional argument of type string named "append".
command.getCommandData()
    .addOption(OptionType.STRING, "append", "", false);
```

Finally, we add the command to the module by using the *addBotCommand* method:

```java
//Add the command to the module.
this.addBotCommand(command);
```

Now we define the callback used by the *ping* command (for reference on how to use SlashCommandInteractionEvent,
Guild, ecc... refer to [JDA](https://github.com/discord-jda/JDA)):

```java
public class PingPongListener extends BotListener
{ 
    //[...]
    public void pingCommand(SlashCommandInteractionEvent event, Guild guild, MessageChannel channel, User author) 
    {
        //Retrive the append parameter as String (set the string to empty if no parameter has been given).
        OptionMapping appendArg = event.getOption("append");
        String append = appendArg != null ? appendArg.getAsString() : "";

        //Reply to the event with pong.
        event.reply("Pong! " + append).setEphemeral(false).queue();
    }
}
```

Now that we have a module we can add it to the BotMain.java:

```java
import com.serpenssolida.discordbot.SerpensBot;

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

Now if we send the command "/pingpong ping" the bot will respond with the message "Pong!" followed by the string passed as parameter.

Full _PingPongListener_ class (minus all the import statements) should look like this:

```java
public class PingPongListener extends BotListener
{
	public PingPongListener()
	{
		super("pingpong"); //Internal id of the module. Must be unique.
		this.setModuleName("PingPong"); //Display name for the module.
		
		//Command for creating a filter.
		BotCommand command = new BotCommand("ping", "The bot will respond to this command with pong. It will append the given parameter to the message");
		//This is the action of the command. When a user tries to use the command it will call the given method/callback.
		command.setAction(this::pingCommand);
		//To add parameters to the command we can use "command.getCommanData" and use the addOption method.
		//In this case we are creating an optional argument of type string named "append".
		command.getCommandData()
				.addOption(OptionType.STRING, "append", "", false);
		//Add the command to the module.
		this.addBotCommand(command);
	}
	
	public void pingCommand(SlashCommandInteractionEvent event, Guild guild, MessageChannel channel, User author)
	{
		//Retrive the append parameter as String (set the string to empty if no parameter has been given).
		OptionMapping appendArg = event.getOption("append");
		String append = appendArg != null ? appendArg.getAsString() : "";
		
		//Reply to the event with pong.
		event.reply("Pong! " + append).setEphemeral(false).queue();
	}
}
```
