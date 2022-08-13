package com.serpenssolida.discordbot.module;

import com.serpenssolida.discordbot.MessageUtils;
import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.command.BotCommand;
import com.serpenssolida.discordbot.interaction.InteractionCallback;
import com.serpenssolida.discordbot.interaction.InteractionGroup;
import com.serpenssolida.discordbot.interaction.WrongInteractionEventException;
import com.serpenssolida.discordbot.modal.ModalCallback;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BotListener extends ListenerAdapter
{
	private String internalID = ""; //Internal id used for retrieving listeners from the list.
	private String moduleName = ""; //Readable name of the module.
	private final HashMap<String, String> modulePrefix = new HashMap<>(); //Prefix of the module, used for commands.
	private final HashMap<String, Boolean> enabled = new HashMap<>(); //Whether the module is enabled or not.
	private final LinkedHashMap<String, BotCommand> botCommands = new LinkedHashMap<>(); //List of commands of the module that are displayed in the client command list.
	private final HashMap<String, HashMap<String, InteractionGroup>> activeGlobalInteractions = new HashMap<>();
	private final HashMap<String, HashMap<String, ModalCallback>> activeModalCallbacks = new HashMap<>();
	
	private static Logger logger = LoggerFactory.getLogger(BotListener.class);
	
	public BotListener(String modulePrefix)
	{
		this.internalID = modulePrefix;
		
		BotCommand command = new BotCommand("help", SerpensBot.getMessage("botlistener_command_help_desc"));
		command.setAction(this::sendHelp);
		command.getSubcommand()
				.addOption(OptionType.STRING, "command-name", SerpensBot.getMessage("botlistener_command_help_param1"), false);
		this.addBotCommand(command);
	}

	@Override
	public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event)
	{
		Guild guild = event.getGuild();
		
		if (guild == null)
			return;
		
		if (!event.getName().equals(this.getModulePrefix(guild.getId())))
			return;
		
		try
		{
			//Get the command from the list using the event command name and run it.
			BotCommand command = this.getBotCommand(event.getSubcommandName());
			command.doAction(event);
		}
		catch (PermissionException e)
		{
			//Send error message.
			Message message = MessageUtils.buildErrorMessage(SerpensBot.getMessage("botlistener_command_error"), event.getUser(), SerpensBot.getMessage("botlistener_missing_permmision_error", e.getPermission()));
			event.reply(message).setEphemeral(true).queue();
			
			//Log the error.
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void onGenericComponentInteractionCreate(@Nonnull GenericComponentInteractionCreateEvent event)
	{
		String componendId = event.getComponentId();
		User author = event.getUser(); //The user that added the reaction.
		Guild guild = event.getGuild(); //The user that added the reaction.
		
		//If this event is not from a guild ignore it.
		if (guild == null)
			return;
		
		//Ignore bot reaction.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		//Get the interacction that the user can interact with.
		InteractionGroup interactionGroup = this.getInteractionGroup(guild.getId(), event.getMessageId());
		
		//If no button group is found and the user hasn't got any task the user cannot press a button.
		if (interactionGroup == null)
			return;
		
		try
		{
			InteractionCallback interactionCallback = interactionGroup.getComponentCallback(componendId);
			
			//Do interaction action.
			boolean deleteMessage = interactionCallback.doAction(event);
			
			//Delete message that has the clicked button if it should be deleted.
			if (deleteMessage)
			{
				this.removeInteractionGroup(guild.getId(), event.getMessageId());
				event.getHook().deleteOriginal().queue();
			}
		}
		catch (WrongInteractionEventException e)
		{
			//Send error message.
			String embedTitle = SerpensBot.getMessage("botlistener_button_action_error");
			String embedDescription = SerpensBot.getMessage("botlistener_interaction_event_type_error", e.getInteractionId(), e.getExpected(), e.getFound());
			Message message = MessageUtils.buildErrorMessage(embedTitle, event.getUser(), embedDescription);
			event.reply(message).setEphemeral(true).queue();
			
			//Log the error.
			logger.error(e.getLocalizedMessage(), e);
		}
		catch (PermissionException e)
		{
			//Send error message.
			String embedTitle = SerpensBot.getMessage("botlistener_button_action_error");
			String embedDescription = SerpensBot.getMessage("botlistener_missing_permmision_error", e.getPermission());
			Message message = MessageUtils.buildErrorMessage(embedTitle, event.getUser(), embedDescription);
			event.reply(message).setEphemeral(true).queue();
			
			//Log the error.
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event)
	{
		User author = event.getUser(); //The user that added the reaction.
		Guild guild = event.getGuild(); //The user that added the reaction.
		MessageChannel channel = event.getMessageChannel();
		
		//If this event is not from a guild ignore it.
		if (guild == null)
			return;
		
		//Ignore bot reaction.
		if (SerpensBot.getApi().getSelfUser().getId().equals(author.getId()))
			return;
		
		ModalCallback modalCallback = this.getModalCallback(guild.getId(), author.getId());

		if (modalCallback == null)
			return;
		
		try
		{
			modalCallback.doAction(event, guild, channel, author);
			this.removeModalCallback(guild.getId(), author.getId());
		}
		catch (PermissionException e)
		{
			//Send error message.
			String embedTitle = SerpensBot.getMessage("botlistener_button_action_error");
			String embedDescription = SerpensBot.getMessage("botlistener_missing_permmision_error", e.getPermission());
			Message message = MessageUtils.buildErrorMessage(embedTitle, event.getUser(), embedDescription);
			event.reply(message).setEphemeral(true).queue();
			
			//Log the error.
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * Generate a list of commands for the given guild.
	 *
	 * @param guild
	 * 		The guild.
	 *
	 * @return
	 * 		An ArrayList of commands.
	 */
	public ArrayList<CommandData> generateCommands(Guild guild)
	{
		ArrayList<CommandData> commandList = new ArrayList<>();
		
		if (this.getModulePrefix(guild.getId()).isEmpty())
			return commandList;
		
		CommandDataImpl mainCommand = new CommandDataImpl(this.getModulePrefix(guild.getId()), "Main module command");
		for (BotCommand botCommand : this.botCommands.values())
		{
			mainCommand.addSubcommands(botCommand.getSubcommand());
		}
		
		commandList.add(mainCommand);
		return commandList;
	}
	
	/**
	 * Method for the "help" command of a module. Sends the module listed and unlisted command list.
	 */
	private void sendHelp(SlashCommandInteractionEvent event, Guild guild, MessageChannel channel, User author)
	{
		OptionMapping commandName = event.getOption("command-name");
		
		EmbedBuilder embedBuilder = MessageUtils.getDefaultEmbed("No title", author);
		
		if (commandName == null)
		{
			//Send list of commands.
			embedBuilder.setTitle(SerpensBot.getMessage("botlistener_command_help_list_title", this.getModuleName()));
			
			if (!this.botCommands.isEmpty())
			{
				StringBuilder commandField = new StringBuilder();
				for (BotCommand command : this.botCommands.values())
				{
					commandField
							.append("`/" + this.getModulePrefix(guild.getId()) + " " + command.getSubcommand().getName() + "`")
							.append(" " + command.getSubcommand().getDescription() + "\n");
				}
				
				embedBuilder.addField(SerpensBot.getMessage("botlistener_command_help_listed_commands"), commandField.toString(), false);
			}
		}
		
		event.reply(new MessageBuilder().setEmbeds(embedBuilder.build()).build()).setEphemeral(false).queue();
	}
	
	/**
	 * Whether or not the module can be disabled.
	 * @return True if the module can be deactivated, false otherwise.
	 */
	public boolean canBeDisabled()
	{
		return true;
	}
	
	/**
	 * Sets the prefix of the module for the given guild.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 * @param modulePrefix
	 * 		The new prefix of the module for the given guild.
	 */
	public void setModulePrefix(String guildID, String modulePrefix)
	{
		this.modulePrefix.put(guildID, modulePrefix);
	}
	
	/**
	 * Get the module prefix of the module for the given guild.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 *
	 * @return
	 * 		The prefix of the module for the given guild.
	 */
	public String getModulePrefix(String guildID)
	{
		if (!this.modulePrefix.containsKey(guildID))
		{
			SerpensBot.loadSettings(guildID); //Try loading the settings.
			
			//Put the default value if no key was loaded from file.
			this.modulePrefix.putIfAbsent(guildID, this.internalID);
		}
		
		return this.modulePrefix.get(guildID);
	}
	
	/**
	 * Get the module prefix of the module for the given guild, if not entry is found it will initialize it to
	 * the default value.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 *
	 * @return
	 * 		The prefix of the module for the given guild.
	 */
	public String getModulePrefixOrDefault(String guildID)
	{
		//Put the default value if no key was loaded from file.
		this.modulePrefix.putIfAbsent(guildID, this.internalID);
		
		return this.modulePrefix.get(guildID);
	}
	
	/**
	 * Enable or disable the module for the guild with the given id.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 * @param enabled
	 * 		True to enable the module, false to disable it.
	 */
	public void setEnabled(String guildID, boolean enabled)
	{
		if (!this.modulePrefix.containsKey(guildID))
		{
			SerpensBot.loadSettings(guildID); //Try loading the settings.
			
			//Put the default value if no key was loaded from file.
			this.enabled.putIfAbsent(guildID, true);
		}
		
		//Enable/disable the module.
		this.enabled.put(guildID, enabled);
	}
	
	/**
	 * Get the state of the module for the given guild.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 *
	 * @return
	 * 		True if the module is enabled, false otherwise.
	 */
	public boolean isEnabled(String guildID)
	{
		if (!this.enabled.containsKey(guildID))
		{
			SerpensBot.loadSettings(guildID); //Try loading the settings.
			
			//Put the default value if no key was loaded from file.
			this.enabled.putIfAbsent(guildID, true);
		}
		
		return this.enabled.get(guildID);
	}
	
	/**
	 * Get the state of the module for the given guild, if no entry is found it will
	 * initialize it to the default value.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 *
	 * @return
	 * 		True if the module is enabled, false otherwise.
	 */
	public boolean isEnabledOrDefault(String guildID)
	{
		//Put the default value if no key was loaded from file.
		this.enabled.putIfAbsent(guildID, true);
		
		return this.enabled.get(guildID);
	}
	
	public String getInternalID()
	{
		return this.internalID;
	}
	
	public void addBotCommand(BotCommand command)
	{
		if (command != null)
			this.botCommands.put(command.getId(), command);
	}
	
	public void removeBotCommand(String id)
	{
		this.botCommands.remove(id);
	}
	
	public BotCommand getBotCommand(String id)
	{
		return this.botCommands.get(id);
	}
	
	public void addInteractionGroup(String guildID, String messageID, InteractionGroup interactionGroup)
	{
		HashMap<String, InteractionGroup> guildInteractionGroup = this.activeGlobalInteractions.computeIfAbsent(guildID, k -> new HashMap<>());
		guildInteractionGroup.put(messageID, interactionGroup);
	}
	
	public InteractionGroup getInteractionGroup(String guildID, String messageID)
	{
		HashMap<String, InteractionGroup> guildInteractionGroup = this.activeGlobalInteractions.computeIfAbsent(guildID, k -> new HashMap<>());
		return guildInteractionGroup.get(messageID);
	}
	
	public void removeInteractionGroup(String guildID, String messageID)
	{
		HashMap<String, InteractionGroup> guildInteractionGroup = this.activeGlobalInteractions.computeIfAbsent(guildID, k -> new HashMap<>());
		guildInteractionGroup.remove(messageID);
	}
	
	public void switchInteractionGroupMessage(String guildID, String messageID, String newMessageID)
	{
		HashMap<String, InteractionGroup> guildInteractionGroup = this.activeGlobalInteractions.computeIfAbsent(guildID, k -> new HashMap<>());
		InteractionGroup interactionGroup = guildInteractionGroup.remove(messageID);
		
		this.addInteractionGroup(guildID, newMessageID, interactionGroup);
	}
	
	public String getModuleName()
	{
		return this.moduleName;
	}
	
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	public HashMap<String, BotCommand> getBotCommands()
	{
		return this.botCommands;
	}
	
	public void addModalCallback(String guildID, String userID, ModalCallback modalCallback)
	{
		HashMap<String, ModalCallback> guildActiveModals = this.activeModalCallbacks.computeIfAbsent(guildID, k -> new HashMap<>());
		guildActiveModals.put(userID, modalCallback);
	}
	
	public ModalCallback getModalCallback(String guildID, String userID)
	{
		HashMap<String, ModalCallback> guildActiveModals = this.activeModalCallbacks.computeIfAbsent(guildID, k -> new HashMap<>());
		return guildActiveModals.get(userID);
	}
	
	public void removeModalCallback(String guildID, String userID)
	{
		HashMap<String, ModalCallback> guildActiveModals = this.activeModalCallbacks.computeIfAbsent(guildID, k -> new HashMap<>());
		guildActiveModals.remove(userID);
	}
}
