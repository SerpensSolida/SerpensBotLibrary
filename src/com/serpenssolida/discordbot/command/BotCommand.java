package com.serpenssolida.discordbot.command;

import com.serpenssolida.discordbot.SerpensBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a command that can be sent to the chat.
 */
public class BotCommand
{
	private String id; //ID of the command, used to identify unequivocally a command.
	private SubcommandData subcommand; //The subcommand used by this BotCommand.
	private BotCommandAction action; //Callback that is called when the command is sent to the chat.
	
	private static Logger logger = LoggerFactory.getLogger(BotCommand.class);
	
	public BotCommand(String id, String description)
	{
		this.id = id;
		this.subcommand = new SubcommandData(this.id, description);
		this.action = (event, guild, channel, author) ->
				event.reply("Ops, qualcuno si è scordato di settare una callback per questo comando!").queue();
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
	
	public SubcommandData getSubcommand()
	{
		return this.subcommand;
	}
}
