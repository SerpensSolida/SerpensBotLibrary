package com.serpenssolida.discordbot.interaction;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;

/**
 * The action performed by a generic Discord interaction when the user interacts with it.
 */
public interface InteractionAction
{
	public boolean doAction(GenericComponentInteractionCreateEvent event, Guild guild, MessageChannel channel, Message message, User author) throws WrongInteractionEventException;
}
