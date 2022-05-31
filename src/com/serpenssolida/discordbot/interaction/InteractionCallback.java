package com.serpenssolida.discordbot.interaction;

import com.serpenssolida.discordbot.SerpensBot;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to map a Discord button to an action.
 */
public class InteractionCallback
{
	private String id;
	private InteractionAction action;
	
	public static final boolean DELETE_MESSAGE = true;
	public static final boolean LEAVE_MESSAGE = false;
	
	private static final Logger logger = LoggerFactory.getLogger(InteractionCallback.class);
	
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
