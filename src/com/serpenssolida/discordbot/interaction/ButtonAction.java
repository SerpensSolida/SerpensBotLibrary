package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;

/**
 * The action performed by a Discord button when the user presses it.
 */
public interface ButtonAction extends InteractionAction
{
	boolean doAction(ButtonClickEvent event, Guild guild, MessageChannel channel, Message message, User author);
	
	@Override
	default boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException
	{
		if (event instanceof ButtonClickEvent)
			return this.doAction(((ButtonClickEvent) event), guild, channel, message, author);
		
		throw new WrongInteractionEventException(event.getComponentId(), event.getClass(), ButtonClickEvent.class);
	}
}
