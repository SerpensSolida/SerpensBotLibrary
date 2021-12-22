package com.serpenssolida.discordbot.module.logger;

import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.module.BotListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class LoggerListener extends BotListener
{
	private static final Logger logger = LoggerFactory.getLogger(LoggerListener.class);
	
	public LoggerListener()
	{
		super("logger");
		this.setModuleName("Logger");
		
		//Clear all commands.
		this.getBotCommands().clear();
	}
	
	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event)
	{
		//Don't accept messages from private channels.
		if (!event.isFromGuild())
			return;
		
		String message = event.getMessage().getContentDisplay().replaceAll(" +", " "); //Received message.
		Guild guild = event.getGuild();
		User author = event.getAuthor(); //Author of the message.
		MessageChannel channel = event.getChannel(); //Channel where the message was sent.
		
		//If the author of the message is the bot, ignore the message.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		//Log the event.
		logger.info("[MESSAGE RECEIVED][{}][#{}][{}] {}", guild.getName(), channel.getName(), author.getName(), message.substring(0, Math.min(25, message.length())));
	}
	
	@Override
	public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event)
	{
		//Don't accept reaction from private channels.
		if (!event.isFromGuild())
			return;
		
		MessageReaction messageReaction = event.getReaction(); //Reaction added to the message.
		User author = event.getUser(); //The user that added the reaction.
		Guild guild = event.getGuild(); //The user that added the reaction.
		MessageChannel channel = event.getChannel(); //The user that added the reaction.
		
		if (author == null)
			return;
		
		//Ignore bot reaction.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		//Log the event.
		logger.info("[REACTION ADDED][{}][#{}][{}][{}] {}", guild.getName(), channel.getName(), author.getName(), event.getMessageId(), messageReaction.getReactionEmote().getName());
	}
	
	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event)
	{
		//Don't accept messages from private channels.
		if (!event.isFromGuild())
			return;
		
		Guild guild = event.getGuild();
		User author = event.getUser(); //Author of the message.
		MessageChannel channel = event.getChannel(); //Channel where the message was sent.
		
		//If the author of the message is the bot, ignore the message.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		if (guild == null)
			return;
		
		//Log the event.
		logger.info("[SLASH COMMAND][{}][#{}][{}] {}", guild.getName(), channel.getName(), author.getName(), "/"+ event.getName());
	}
	
	@Override
	public void onGenericComponentInteractionCreate(@Nonnull GenericComponentInteractionCreateEvent event)
	{
		String componendId = event.getComponentId();
		User author = event.getUser(); //The user that added the reaction.
		Guild guild = event.getGuild(); //The user that added the reaction.
		MessageChannel channel = event.getChannel(); //Channel where the event occurred.
		
		//If this event is not from a guild ignore it.
		if (guild == null)
			return;
		
		//Ignore bot reaction.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		//Log the event.
		String messageId = event.getMessage().getId();
		logger.info("[INTERACTION][{}][#{}][{}][{}][{}] {}", guild.getName(), channel.getName(), author.getName(), event.getComponentType(), messageId, componendId);
	}
	
	@Override
	public String getModulePrefix(String guildID)
	{
		return "";
	}
}
