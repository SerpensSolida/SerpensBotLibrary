package com.serpenssolida.discordbot.interaction;

import java.util.HashMap;

/**
 * This class represent the group of interaction callback linked to components of a message.
 *
 * <p>InteractionGroup usually are registered to a {@link com.serpenssolida.discordbot.module.Task} or a {@link com.serpenssolida.discordbot.module.BotListener}
 * and when registered they are linked to a message. The InteractionGroup when registered must contain <u>all</u> {@link InteractionCallback}
 * of the component of the message, if not the interaction can fail in unexpected ways.</p>
 */
public class InteractionGroup
{
	private HashMap<String, InteractionCallback> components = new HashMap<>();
	
	private void addComponentCallback(InteractionCallback interaction)
	{
		this.components.put(interaction.getId(), interaction);
	}
	
	/**
	 * Link the given action to the button component with the given id. <br>
	 * When the button is pressed the given action will be performed.
	 *
	 * @param id
	 * 		id of the button componet to link the action to.
	 * @param action
	 * 		action that will be perfomerd when the button is pressed.
	 */
	public void addButtonCallback(String id, ButtonAction action)
	{
		InteractionCallback interactionCallback = new InteractionCallback(id, action);
		this.addComponentCallback(interactionCallback);
	}
	
	/**
	 * Link the given action to the selection menu component with the given id. <br>
	 * When a user select an option from the component the given action will be performed.
	 *
	 * @param id
	 * 		id of the selection menu component to link the action to.
	 * @param action
	 * 		action that will be perfomerd when the button is pressed.
	 */
	public void addSelectionMenuCallback(String id, SelectionMenuAction action)
	{
		InteractionCallback interactionCallback = new InteractionCallback(id, action);
		this.addComponentCallback(interactionCallback);
	}
	
	/**
	 * @param id
	 * 		Id of the component the callback is linked to.
	 *
	 * @return The interaction callback linked to the given id.
	 */
	public InteractionCallback getComponentCallback(String id)
	{
		return this.components.get(id);
	}
	
	/**
	 * Removes the component linked to the given id.
	 *
	 * @param id
	 * 		Id of the component the callback is linked to.
	 */
	public void removeComponentCallback(String id)
	{
		this.components.remove(id);
	}
}
