package com.serpenssolida.discordbot.module.logger;

import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.module.BotListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void onMessageReceived(MessageReceivedEvent event)
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
		logger.info("[MESSAGE RECEIVED][{}][#{}][{}] {}", guild.getName(), channel.getName(), author.getName(), message.substring(0, Math.min(150, message.length())));
	}
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event)
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
		logger.info("[REACTION ADDED][{}][#{}][{}][{}] {}", guild.getName(), channel.getName(), event.getMessageId(), author.getName(), messageReaction.getEmoji().getName());
	}
	
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event)
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
		logger.info("[SLASH COMMAND][{}][#{}][{}] {}", guild.getName(), channel.getName(), author.getName(), "/"+ event.getCommandIdLong());
	}
	
	@Override
	public void onGenericComponentInteractionCreate(GenericComponentInteractionCreateEvent event)
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
		String messageID = event.getMessage().getId();
		logger.info("[INTERACTION][{}][#{}][{}][{}][{}] {}", guild.getName(), channel.getName(), event.getComponentType(), messageID, author.getName(), componendId);
	}
	
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event)
	{
		//Don't accept messages from private channels.
		if (!event.isFromGuild())
			return;
		
		Guild guild = event.getGuild();
		User author = event.getUser(); //Author of the message.
		
		//If the author of the message is the bot, ignore the message.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		if (guild == null)
			return;
		
		//Log the event.
		logger.info("[MODAL INTERACTION][{}][{}] {}", guild.getName(), author.getName(), "/"+ event.getModalId());
	}
	
	@Override
	public String getModulePrefix(String guildID)
	{
		return "";
	}
}
