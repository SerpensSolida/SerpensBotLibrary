package com.serpenssolida.discordbot.contextmenu;

import com.serpenssolida.discordbot.SerpensBot;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserContextMenuOption
{
	private final CommandData contextMenu;
	private UserContextMenuAction action;
	
	private static final Logger logger = LoggerFactory.getLogger(UserContextMenuOption.class);
	
	public UserContextMenuOption(String id)
	{
		this.contextMenu = Commands.user(id);
	}
	
	/**
	 * Calls the callback of the contextmenu, if no collback is set the function return false.
	 *
	 * @param event
	 * 		The event being performed.
	 *
	 */
	public void doAction(UserContextInteractionEvent event)
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
	
	public UserContextMenuAction getAction()
	{
		return this.action;
	}
	
	public void setAction(UserContextMenuAction action)
	{
		this.action = action;
	}
	
	public CommandData getContextMenu()
	{
		return this.contextMenu;
	}
}
