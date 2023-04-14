package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * The action performed by a Discord button.
 */
public interface SelectionMenuAction extends InteractionAction
{
	boolean doAction(StringSelectInteractionEvent event, Guild guild, MessageChannel channel, Message message, User author);
	
	@Override
	default boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException
	{
		if (event instanceof StringSelectInteractionEvent selectMenuInteractionEvent)
			return this.doAction(selectMenuInteractionEvent, guild, channel, message, author);
		
		throw new WrongInteractionEventException(event.getComponentId(), event.getClass(), StringSelectInteractionEvent.class);
	}
}
