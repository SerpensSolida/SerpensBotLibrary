package com.serpenssolida.discordbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serpenssolida.discordbot.module.BotListener;
import com.serpenssolida.discordbot.module.base.BaseListener;
import com.serpenssolida.discordbot.module.logger.LoggerListener;
import com.serpenssolida.discordbot.module.settings.SettingsData;
import com.serpenssolida.discordbot.module.settings.SettingsListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SerpensBot
{
	public static final String SETTINGS_FOLDER = "settings";
	public static final ResourceBundle defaultLanguage = ResourceBundle.getBundle("SerpensBot");
	
	private static JDA api;
	private static ResourceBundle language;
	private static String ownerId;
	
	private static final Logger logger = LoggerFactory.getLogger(SerpensBot.class);
	
	private SerpensBot() {}
	
	public static void start()
	{
		//Load language
		SerpensBot.language = loadLanguage();
		
		//Load data from file.
		BotData data = SerpensBot.loadBotData();
		
		if (data == null)
		{
			logger.error(SerpensBot.getMessage("missing_json"));
			return;
		}
		
		//Setting headless mode. We are using some drawing function without the gui.
		System.setProperty("java.awt.headless", "true");
		
		try
		{
			SerpensBot.api = JDABuilder
					.createDefault(data.getToken())
					.setChunkingFilter(ChunkingFilter.ALL)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.enableIntents(GatewayIntent.GUILD_MEMBERS)
					.build();
			SerpensBot.api.awaitReady();
		}
		catch (LoginException e)
		{
			logger.error(SerpensBot.getMessage("login_error"), e);
			return;
		}
		catch (InterruptedException e)
		{
			logger.error(e.getLocalizedMessage(), e);
			Thread.currentThread().interrupt();
			return;
		}
		
		api.addEventListener(new SettingsListener());
		api.addEventListener(new BaseListener());
		api.addEventListener(new LoggerListener());
		
		if (data.getOwner() == null || data.getOwner().isBlank())
		{
			logger.error(SerpensBot.getMessage("owner_not_set"));
			return;
		}
		
		//Set the owner of the bot.
		SerpensBot.ownerId = data.getOwner();
		
		logger.info(SerpensBot.getMessage("bot_ready"));
	}
	
	/**
	 * @return The JDA api.
	 */
	public static JDA getApi()
	{
		return api;
	}
	
	/**
	 * Register a {@link BotListener}.
	 * @param listener The {@link BotListener} to register.
	 */
	public static void addModule(BotListener listener)
	{
		SerpensBot.api.addEventListener(listener);
	}
	
	/**
	 * @return The user id of the owner of the bot.
	 */
	public static String getOwnerId()
	{
		return ownerId;
	}
	
	/**
	 * Update command list for all guilds.
	 */
	public static void updateAllGuildsCommands()
	{
		for (Guild guild : api.getGuilds())
			SerpensBot.updateGuildCommands(guild);
	}
	
	/**
	 * Update the command list of the given guild.
	 *
	 * @param guild
	 * 		The guild that will receive the update command list.
	 */
	public static void updateGuildCommands(Guild guild)
	{
		CommandListUpdateAction commands = guild.updateCommands();
		for (Object registeredListener : SerpensBot.api.getRegisteredListeners())
		{
			if (registeredListener instanceof BotListener)
			{
				BotListener listener = (BotListener) registeredListener;
				
				if (!listener.isEnabledOrDefault(guild.getId()))
					return;
				
				for (CommandData commandData : listener.generateCommands(guild))
					commands.addCommands(commandData);
			}
		}
		
		commands.queue(a -> logger.info(SerpensBot.getMessage("guild_commands_updated", guild.getName())));
	}
	
	/**
	 * Load the bot data from file.
	 *
	 * @return
	 * 		The data read from file.
	 */
	private static BotData loadBotData()
	{
		File tokenFile = new File("bot.json");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		logger.info(SerpensBot.getMessage("reading_main_settings"));
		
		try (BufferedReader reader = new BufferedReader(new FileReader(tokenFile)))
		{
			BotData botData = gson.fromJson(reader, BotData.class);
			
			logger.info(SerpensBot.getMessage("token", botData.getToken()));
			logger.info(SerpensBot.getMessage("owner", botData.getOwner()));
			
			return botData;
		}
		catch (IOException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		
		return null;
	}
	
	/**
	 * Load the {@link ResourceBundle} from the root directory. If there is no language file a default one will be used.
	 *
	 * @return
	 * 		The resource bundle that has been loaded.
	 */
	public static ResourceBundle loadLanguage()
	{
		try
		{
			ResourceBundle language = new PropertyResourceBundle(Files.newInputStream(Paths.get("SerpensBot.properties")));
			
			logger.info(language.getString("loaded_resource_bundle_external"));
			
			return language;
		}
		catch (IOException e)
		{
			logger.info(language.getString("loaded_resource_bundle_default"));
			
			return SerpensBot.defaultLanguage;
		}
	}
	
	public static String getMessage(String key)
	{
		//Check if the property file has the given key.
		if (SerpensBot.language.containsKey(key))
			return SerpensBot.language.getString(key);
		
		logger.warn(SerpensBot.defaultLanguage.getString("language_key_not_found"), key);
		return SerpensBot.defaultLanguage.getString(key);
	}
	
	public static String getMessage(String key, Object... args)
	{
		return String.format(SerpensBot.getMessage(key), args);
	}
	
	/**
	 * @return The list of modules of the bot.
	 */
	public static ArrayList<BotListener> getModules()
	{
		ArrayList<BotListener> modules = new ArrayList<>();
		
		for (Object registeredListener : SerpensBot.api.getRegisteredListeners())
		{
			if (registeredListener instanceof BotListener)
			{
				modules.add((BotListener) registeredListener);
			}
		}
		
		return modules;
	}
	
	/**
	 * Get the module with the given id.
	 *
	 * @param moduleID
	 * 		The id of the module to get.
	 *
	 * @return The module with the passed id.
	 */
	public static BotListener getModuleById(String moduleID)
	{
		//Get the module with the correct id and get its module prefix.
		for (BotListener listener : SerpensBot.getModules())
		{
			if (listener.getInternalID().equals(moduleID))
			{
				return listener;
			}
		}
		
		return null;
	}
	
	/**
	 * Load the setting of the bot for the given guild.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 *
	 * @return
	 * 		-True if the settings were loaded, false otherwise.
	 */
	public static boolean loadSettings(String guildID)
	{
		File settingsFile = new File(Paths.get("server_data", guildID, SerpensBot.SETTINGS_FOLDER, "settings.json").toString());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Guild guild = SerpensBot.api.getGuildById(guildID);
		
		if (guild == null)
			return false;
		
		logger.info(SerpensBot.getMessage("loading_guild_settings", guild.getName()));
		
		try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile)))
		{
			SettingsData settingsData = gson.fromJson(reader, SettingsData.class);
			
			//Check if the data was read correctly.
			if (settingsData == null)
				return false;
			
			HashMap<String, String> modulePrefixes = settingsData.getModulePrefixes();
			HashMap<String, Boolean> moduleStates = settingsData.getModuleStates();
			
			for (BotListener listener : SerpensBot.getModules())
			{
				//Set listener prefix, if the key is not found set it to the default value.
				listener.setModulePrefix(guildID, modulePrefixes.getOrDefault(listener.getInternalID(), listener.getInternalID()));
				
				//Set listener state, if the key is not found set it to the default value.
				listener.setEnabled(guildID, moduleStates.getOrDefault(listener.getInternalID(), true));
			}
			
			SerpensBot.updateGuildCommands(guild);
		}
		catch (FileNotFoundException e)
		{
			logger.info(SerpensBot.getMessage("no_guild_settings_file", guild.getName()));
			logger.info(SerpensBot.getMessage("guild_settings_file_creation", guild.getName()));
			
			//Initialize default values.
			for (BotListener module : getModules())
				module.setModulePrefix(guildID, module.getInternalID());
			
			SerpensBot.saveSettings(guildID);
		}
		catch (IOException e)
		{
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Save the guild setting.
	 *
	 * @param guildID
	 * 		The id of the guild.
	 */
	public static void saveSettings(String guildID)
	{
		File settingsFile = new File(Paths.get("server_data", guildID, SerpensBot.SETTINGS_FOLDER, "settings.json").toString());
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Guild guild = SerpensBot.api.getGuildById(guildID);
		
		if (guild == null)
			return;
		
		logger.info(SerpensBot.getMessage("saving_guild_settings", guild.getName()));
		
		//Init data containers.
		SettingsData settingsData = new SettingsData();
		HashMap<String, String> modulePrefixes = new HashMap<>();
		HashMap<String, Boolean> moduleStates = new HashMap<>();
		
		//Add list of prefixes and states.
		for (BotListener listener : SerpensBot.getModules())
		{
			modulePrefixes.put(listener.getInternalID(), listener.getModulePrefixOrDefault(guildID));
			moduleStates.put(listener.getInternalID(), listener.isEnabledOrDefault(guildID));
		}
		
		settingsData.setModulePrefixes(modulePrefixes);
		settingsData.setModuleStates(moduleStates);
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(settingsFile)))
		{
			writer.println(gson.toJson(settingsData));
		}
		catch (FileNotFoundException e)
		{
			logger.info(SerpensBot.getMessage("guild_settings_file_missing", guild.getName()));
			
			try
			{
				settingsFile.getParentFile().mkdirs();
				
				if (settingsFile.createNewFile())
					SerpensBot.saveSettings(guildID);
			}
			catch (IOException ex)
			{
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		catch (IOException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
		
	}
}
