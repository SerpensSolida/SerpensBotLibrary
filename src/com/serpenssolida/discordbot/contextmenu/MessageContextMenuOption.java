package com.serpenssolida.discordbot.contextmenu;

import com.serpenssolida.discordbot.SerpensBot;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageContextMenuOption
{
	private final CommandData contextMenu;
	private MessageContextMenuAction action;
	
	private static final Logger logger = LoggerFactory.getLogger(MessageContextMenuOption.class);
	
	public MessageContextMenuOption(String id)
	{
		this.contextMenu = Commands.message(id);
	}
	
	/**
	 * Calls the callback of the contextmenu, if no collback is set the function return false.
	 *
	 * @param event
	 * 		The event being performed.
	 *
	 */
	public void doAction(MessageContextInteractionEvent event)
	{
		if (this.action != null)
		{
			this.action.doAction(event);
			return;
		}
		
		logger.error(SerpensBot.getMessage("contextmenuoption_action_not_set_log", this.getId()));
	}
	
	public String getId()
	{
		return this.contextMenu.getName();
	}
	
	public MessageContextMenuAction getAction()
	{
		return this.action;
	}
	
	public void setAction(MessageContextMenuAction action)
	{
		this.action = action;
	}
	
	public CommandData getContextMenu()
	{
		return contextMenu;
	}
}
