package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

/**
 * The action performed by a Discord button.
 */
public interface SelectionMenuAction extends InteractionAction
{
	boolean doAction(SelectMenuInteractionEvent event, Guild guild, MessageChannel channel, Message message, User author);
	
	@Override
	default boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException
	{
		if (event instanceof SelectMenuInteractionEvent)
			return this.doAction((SelectMenuInteractionEvent) event, guild, channel, message, author);
		
		throw new WrongInteractionEventException(event.getComponentId(), event.getClass(), SelectMenuInteractionEvent.class);
	}
}
