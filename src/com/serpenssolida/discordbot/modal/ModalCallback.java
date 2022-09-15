package com.serpenssolida.discordbot.modal;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface ModalCallback
{
	void doAction(ModalInteractionEvent event, Guild guild, MessageChannel channel, User author);
}
