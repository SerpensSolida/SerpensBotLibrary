package com.serpenssolida.discordbot.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * Used by {@link BotCommand} as callback when the command is used in a chat.
 */
public interface BotCommandAction
{
	/**
	 * Called when the command is sent to the chat.
	 *
	 * @param event
	 * 		The event that called this action.
	 * @param guild
	 * 		The guild the commands was sent on.
	 * @param channel
	 * 		Channel where the command was sent.
	 * @param author
	 * 		Author that sent the command.
	 *
	 */
	void doAction(SlashCommandEvent event, Guild guild, MessageChannel channel, User author);
}
