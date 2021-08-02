package com.serpenssolida.discordbot.interaction;

import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.command.BotCommand;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to map a Discord button to an action.
 */
public class InteractionCallback
{
	private String id;
	private InteractionAction action;
	
	public static boolean DELETE_MESSAGE = true;
	public static boolean LEAVE_MESSAGE = false;
	
	private static Logger logger = LoggerFactory.getLogger(BotCommand.class);
	
	public InteractionCallback(String id, InteractionAction action)
	{
		this.id = id;
		this.action = action;
	}
	
	/**
	 * Calls the callback of the button, if no collback is set the function return false.
	 *
	 * @param event
	 * 		The event being performed.
	 *
	 * @return
	 * 		If the action has been set.
	 *
	 * @throws WrongInteractionEventException
	 * 		if the action set for the component is of the wrong type.
	 */
	public boolean doAction(GenericComponentInteractionCreateEvent event) throws WrongInteractionEventException
	{
		if (this.action != null)
		{
			return this.action.doAction(event, event.getGuild(), event.getChannel(), event.getMessage(), event.getUser());
		}
		
		logger.error(SerpensBot.getMessage("interactioncallback_action_not_set_log", this.getId()));
		
		return false;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public InteractionAction getAction()
	{
		return this.action;
	}
	
	public void setAction(InteractionAction action)
	{
		this.action = action;
	}
}
