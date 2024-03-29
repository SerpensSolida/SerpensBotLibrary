package com.serpenssolida.discordbot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class MessageUtils
{
	private MessageUtils() {}
	
	/**
	 * Method used to build simple embed error messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param author
	 * 		Author of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 *
	 * @return The message containing the generated embed.
	 */
	public static MessageCreateData buildErrorMessage(String title, User author, String description)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title, author)
			.setDescription(description)
			.setThumbnail("https://i.imgur.com/N5QySPm.png[/img]")
			.setColor(Color.RED);
		
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
		
		return messageBuilder.build();
	}
	
	/**
	 * Method used to build simple embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param author
	 * 		Author of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 *
	 * @return The message containing the generated embed.
	 */
	public static MessageCreateData buildSimpleMessage(String title, User author, String description)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title, author)
				.setDescription(description);
		
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
		
		return messageBuilder.build();
	}
	
	/**
	 * Method used to build simple embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param author
	 * 		Author of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 * @param color Color of the embed.
	 *
	 * @return
	 * 		The message containing the generated embed.
	 */
	public static MessageCreateData buildSimpleMessage(String title, User author, String description, Color color)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title, author)
				.setDescription(description)
				.setColor(color);
		
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
		
		return messageBuilder.build();
	}
	
	/**
	 * Method used to build simple embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 * @param color Color of the embed.
	 *
	 * @return
	 * 		The message containing the generated embed.
	 */
	public static MessageCreateData buildSimpleMessage(String title, String description, Color color)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title)
				.setDescription(description)
				.setColor(color);
		
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
		
		return messageBuilder.build();
	}
	
	/**
	 * Method used to build simple embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 *
	 * @return
	 * 		The message containing the generated embed.
	 */
	public static MessageCreateData buildSimpleMessage(String title, String description)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title)
				.setDescription(description);
		
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
		
		return messageBuilder.build();
	}
	
	/**
	 * Method used to generate editable embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param author
	 * 		Author of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 *
	 * @return The message containing the generated embed.
	 */
	public static MessageCreateBuilder createSimpleMessage(String title, User author, String description)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title, author)
				.setDescription(description);
        
        return new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
	}
	
	/**
	 * Method used to generate editable embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param author
	 * 		Author of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 * @param color Color of the embed.
	 *
	 * @return
	 * 		The message containing the generated embed.
	 */
	public static MessageCreateBuilder createSimpleMessage(String title, User author, String description, Color color)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title, author)
				.setDescription(description)
				.setColor(color);
        
        return new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
	}
	
	/**
	 * Method used to generate editable embed messages.
	 *
	 * @param title
	 * 		Title of the embed.
	 * @param description
	 * 		String showed as description of the embed.
	 * @param color Color of the embed.
	 *
	 * @return
	 * 		The message containing the generated embed.
	 */
	public static MessageCreateBuilder createSimpleMessage(String title, String description, Color color)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title)
				.setDescription(description)
				.setColor(color);
        
        return new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
	}
	
	/**
	 * Method used to generate editable embed messages.
	 *
	 * @param title Title of the embed.
	 * @param description String showed as description of the embed.
	 *
	 * @return The message containing the generated embed.
	 */
	public static MessageCreateBuilder createSimpleMessage(String title, String description)
	{
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(title)
				.setDescription(description);
        
        return new MessageCreateBuilder()
				.setEmbeds(embedBuilder.build());
	}
	
	/**
	 * Create an {@link EmbedBuilder} with standard content for ease of use.
	 *
	 * @param title
	 * 		The title to give to the embed.
	 *
	 * @return
	 * 		A {@link EmbedBuilder} with standard content.
	 */
	public static EmbedBuilder getDefaultEmbed(String title)
	{
		EmbedBuilder embedBuilder = new EmbedBuilder();
		
		//Add footer
		embedBuilder.setTitle(title);
		embedBuilder.setAuthor(SerpensBot.getApi().getSelfUser().getName(), "https://github.com/SerpensSolida/SerpensBot", SerpensBot.getApi().getSelfUser().getAvatarUrl());
		
		return embedBuilder;
	}
	
	/**
	 * Create an {@link EmbedBuilder} with standard content and author footer for ease of use.
	 *
	 * @param title
	 * 		The title to give to the embed.
	 * @param author
	 * 		The user that will be shown in the footer.
	 *
	 * @return
	 * 		A {@link EmbedBuilder} with standard content.
	 */
	public static EmbedBuilder getDefaultEmbed(String title, User author)
	{
		return MessageUtils.getDefaultEmbed(title)
				.setFooter(SerpensBot.getMessage("requested", author.getName()), author.getAvatarUrl());
	}
}
