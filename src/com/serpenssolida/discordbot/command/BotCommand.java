package com.serpenssolida.discordbot.command;

import com.serpenssolida.discordbot.SerpensBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a SlashCommand the user can call from the discord app. <br>
 *
 * <p>When a given command is sent to the chat the listener will automatically call the {@link BotCommandAction} assigned to
 * the given command. <br></p>
 *
 * <p>Specifically a BotCommand works as a wrapper for a sub command that will be inserted into the main command genereted automatically by the listener, in fact
 * to call a command the user will type for example {@code /prefix command-id} where {@code prefix} is the prefix of the listener
 * and {@code command-id} is the id of the command given when instantiating one BotCommand. </p>
 *
 * <p>The method {@code getSubcommand} is used to add arguments and parameters to the command.</p>
 * <p><h2>Example</h2>
 * <pre>{@code
 * 	//Create a simple hello command.
 * 	BotCommand command = new BotCommand("hello", "The bot will say hello.");
 *
 * 	//We add the parameter name to the command.
 * 	command.getSubCommand().addOption(OptionType.STRING, "name", "The name to greet.", true)
 *
 * 	//We set the command callback, this will be called when a user send the command /prefix hello.
 * 	command.setAction((event, guild, channel, author) ->
 * 	{
 * 		//We get the value of the parameter "name".
 * 		OptionMapping nameArg = event.getOption("name")
 *
 * 		//We send a message greeting the user.
 * 		event.reply("Hello " + nameArg.getAsString()).queue();
 * 	});
 *
 * 	//Add the command to the listener.
 * 	this.addBotCommand(command);
 * }<pre/></p>
 *
 */
public class BotCommand
{
	private String id; //ID of the command, used to identify unequivocally a command.
	private SubcommandData subcommand; //The subcommand used by this BotCommand.
	private BotCommandAction action; //Callback that is called when the command is sent to the chat.
	
	private static Logger logger = LoggerFactory.getLogger(BotCommand.class);
	
	/**
	 * @param id
	 * 		id of the command used to execute the command from chat.
	 * @param description
	 * 		Description of the command that will be shown to the user when typing it.
	 */
	public BotCommand(String id, String description)
	{
		this.id = id;
		this.subcommand = new SubcommandData(this.id, description);
		this.action = null;
	}
	
	/**
	 * Set the callback that is called when the command is sent to the chat.
	 *
	 * @param action
	 * 		The callback that will be called when the cmmand is sent to the chat.
	 *
	 * @return The command.
	 */
	public BotCommand setAction(BotCommandAction action)
	{
		this.action = action;
		return this;
	}
	
	/**
	 * Calls the callback of the command, if no collback is set the function return false.
	 *
	 * @param event
	 * 		The event being performed.
	 *
	 */
	public void doAction(SlashCommandEvent event)
	{
		if (this.action != null)
		{
			this.action.doAction(event, event.getGuild(), event.getChannel(), event.getUser());
			return;
		}
		
		logger.error(SerpensBot.getMessage("botcommand_action_not_set_log", this.getId()));
	}
	
	public String getId()
	{
		return this.id;
	}
	
	/**
	 * The {@link SubcommandData} used to set up command arguments.
	 *
	 * <h2>Example</h2>
	 * <pre>{@code //Add a requred string argument "name" to the BotCommand.
	 * command.getSubCommand().addOption(OptionType.STRING, "name", "The name to greet.", true)}
	 * </pre>
	 *
	 * @return
	 * 		The subcommand that this BotCommand wraps.
	 */
	public SubcommandData getSubcommand()
	{
		return this.subcommand;
	}
}
