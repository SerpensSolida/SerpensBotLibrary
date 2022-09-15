package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;

/**
 * The action performed by a Discord button when the user presses it.
 */
public interface ButtonAction extends InteractionAction
{
	boolean doAction(ButtonInteractionEvent event, Guild guild, MessageChannel channel, Message message, User author);
	
	@Override
	default boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException
	{
		if (event instanceof ButtonInteractionEvent)
			return this.doAction((ButtonInteractionEvent) event, guild, channel, message, author);
		
		throw new WrongInteractionEventException(event.getComponentId(), event.getClass(), ButtonInteractionEvent.class);
	}
}
