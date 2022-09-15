package com.serpenssolida.discordbot.contextmenu;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface UserContextMenuAction
{
	/**
	 * Called when the message context menu is selected.
	 *
	 * @param event
	 * 		The event that called this action.
	 *
	 */
	public void doAction(UserContextInteractionEvent event);
}
