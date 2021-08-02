package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;

/**
 * The action performed by a Discord button.
 */
public interface SelectionMenuAction extends InteractionAction
{
	boolean doAction(SelectionMenuEvent event, Guild guild, MessageChannel channel, Message message, User author);
	
	@Override
	default boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException
	{
		if (event instanceof SelectionMenuEvent)
			return this.doAction(((SelectionMenuEvent) event), guild, channel, message, author);
		
		throw new WrongInteractionEventException(event.getComponentId(), event.getClass(), SelectionMenuEvent.class);
	}
}
