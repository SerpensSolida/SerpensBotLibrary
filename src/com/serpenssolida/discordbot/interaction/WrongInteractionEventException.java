package com.serpenssolida.discordbot.interaction;

/**
 * Exception thrown when an {@link InteractionAction} is linked to a compomponent of the wrong type.
 *
 * <h2>Example</h2>
 * <pre>{@code MessageBuilder builder = new MessageBuilder();
 * builder.setActionRows(ActionRow.of(Button.primary("button-test", "Test")));
 * ...
 * interactionGroup.addSelectionMenuCallback("button-test", buttonCallBack); //We are linking a selection menu callback to a button.
 *}</pre>
 * When the button will be pressed the exception will be thrown because the wrong interaction type was linked to the component.
 */
public class WrongInteractionEventException extends Exception
{
	private static final String MESSAGE = "Wrong type of interaction event found for interaction component with id \"%s\". Expected \"%s\", found \"%s\".";
	private final String interactionId;
	private final Class<?> expected;
	private final Class<?> found;
	
	public WrongInteractionEventException(String interactionId, Class<?> expected, Class<?> found)
	{
	    super(String.format(WrongInteractionEventException.MESSAGE, interactionId, expected.getName(), found.getName()));
		this.interactionId = interactionId;
		this.expected = expected;
		this.found = found;
	}
	
	public Class<?> getExpected()
	{
		return this.expected;
	}
	
	public String getInteractionId()
	{
		return this.interactionId;
	}
	
	public Class<?> getFound()
	{
		return this.found;
	}
}
