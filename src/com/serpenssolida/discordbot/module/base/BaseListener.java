package com.serpenssolida.discordbot.module.base;

import com.serpenssolida.discordbot.MessageUtils;
import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.UserUtils;
import com.serpenssolida.discordbot.module.BotListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class BaseListener extends BotListener
{
	public BaseListener()
	{
		super("base");
		this.setModuleName("Base");
		
		//Module has no tasks and cannot get help.
		this.getBotCommands().clear();
	}
	
	@Override
	public ArrayList<CommandData> generateCommands(Guild guild)
	{
		ArrayList<CommandData> commandList = new ArrayList<>();
		CommandData mainCommand = Commands.slash("help" , SerpensBot.getMessage("base_command_help_description"));
		
		commandList.add(mainCommand);
		return commandList;
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
		
		//Parse special commands.
		if ("!!reset prefixes".equals(message))
			this.resetPrefixes(guild, channel, author);
	}
	
	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
	{
		Guild guild = event.getGuild();
		
		if (guild == null)
			return;
		
		if (!"help".equals(event.getName()))
			return;
		
		this.sendModuleHelp(event, event.getGuild(), event.getUser());
	}
	
	@Override
	public void onGuildJoin(@NotNull GuildJoinEvent event)
	{
		SerpensBot.updateGuildCommands(event.getGuild());
	}
	
	@Override
	public boolean canBeDisabled()
	{
		return false;
	}
	
	/**
	 * Reset the command symbol, if something bad happens while setting command symbol this will reset it to default.
	 */
	private void resetPrefixes(Guild guild, MessageChannel channel, User author)
	{
		Member authorMember = guild.retrieveMember(author).complete();
		
		//Check in the user has permission to run this command.
		if (!UserUtils.hasMemberAdminPermissions(authorMember))
		{
			Message message = MessageUtils.buildErrorMessage(SerpensBot.getMessage("base_command_reset_prefix_title"), author, SerpensBot.getMessage("base_command_reset_prefix_permission_error"));
			channel.sendMessage(message).queue();
			return;
		}
		
		for (BotListener listener : SerpensBot.getModules())
			listener.setModulePrefix(guild.getId(), listener.getInternalID());
		
		SerpensBot.updateGuildCommands(guild);
		SerpensBot.saveSettings(guild.getId());
		
		Message message = MessageUtils.buildSimpleMessage(SerpensBot.getMessage("base_command_reset_prefix_title"), author, SerpensBot.getMessage("base_command_reset_prefix_info"));
		channel.sendMessage(message).queue();
	}
	
	/**
	 * Send a message containing all help commands of the modules.
	 */
	private void sendModuleHelp(SlashCommandInteractionEvent event, Guild guild, User author)
	{
		String guildID = guild.getId();
		MessageBuilder messageBuilder = new MessageBuilder();
		
		StringBuilder builderList = new StringBuilder();
		StringBuilder builderCommands = new StringBuilder();
		
		//Add footer
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed(SerpensBot.getMessage("base_command_help_title"), author);

		//Add module list to the embed.
		for (BotListener listener : SerpensBot.getModules())
		{
			String modulePrefix = listener.getModulePrefix(guildID);
			
			if (modulePrefix.isBlank() || !listener.isEnabled(guildID))
				continue;
			
			//Add listener to the list.
			builderList.append("`•`" + SerpensBot.getMessage("base_command_help_command_field_value", listener.getModuleName()) + "\n");
			builderCommands.append("`/" + modulePrefix + " help`\n");
		}
		
		embedBuilder.addField(SerpensBot.getMessage("base_command_help_command_field_title"), builderList.toString(), true);
		embedBuilder.addField(SerpensBot.getMessage("base_command_help_help_field_title"), builderCommands.toString(), true);
		
		messageBuilder.setEmbeds(embedBuilder.build());
		event.reply(messageBuilder.build()).setEphemeral(false).queue();
	}
	
	@Override
	public void setModulePrefix(String guildID, String modulePrefix)
	{
		super.setModulePrefix(guildID, "");
	}
	
	@Override
	public String getModulePrefix(String guildID)
	{
		return "";
	}
}
