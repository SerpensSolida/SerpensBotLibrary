package com.serpenssolida.discordbot.webserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serpenssolida.discordbot.SerpensBot;
import com.serpenssolida.discordbot.webserver.data.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class SerpensBotWebServer
{
	private static String password = "";
	private static boolean running = false;
	private static HashSet<UUID> tokens = new HashSet<>();
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger logger = LoggerFactory.getLogger(SerpensBotWebServer.class);
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
	private static final String LOGIN_PATH = "/login";
	private static final String RESTART_PATH = "/restart";
	private static final String STOP_PATH = "/stop";
	private static final String STATUS_PATH = "/status";
	private static final String TOKEN_PATH = "/token";
	private static final String LOG_PATH = "/log";
	
	private static final String HOME_PATH = "/";
	private static final String ADMIN_PATH = "/admin";
	
	private enum TokenStatus
	{
		NOT_FOUND,
		AUTHORIZED,
		UNAUTHORIZED
	}
	
	public static void start(int port, String password)
	{
		if (SerpensBotWebServer.running)
			return;
		
		SerpensBotWebServer.password = password;
		SerpensBotWebServer.tokens.clear();
		
		Spark.port(port);
		
		//API paths.
		Spark.get(SerpensBotWebServer.TOKEN_PATH, SerpensBotWebServer::checkToken);
		Spark.get(SerpensBotWebServer.STATUS_PATH, SerpensBotWebServer::botStatus);
		Spark.get(SerpensBotWebServer.LOG_PATH, SerpensBotWebServer::getLog);
		Spark.post(SerpensBotWebServer.LOGIN_PATH, SerpensBotWebServer::login);
		Spark.post(SerpensBotWebServer.RESTART_PATH, SerpensBotWebServer::restartBot);
		Spark.post(SerpensBotWebServer.STOP_PATH, SerpensBotWebServer::stopBot);
		
		//Pages paths.
		Spark.get(SerpensBotWebServer.HOME_PATH, SerpensBotWebServer::loginPage);
		Spark.get(SerpensBotWebServer.ADMIN_PATH, SerpensBotWebServer::adminPage);
		
		SerpensBotWebServer.loadTokens();
		SerpensBotWebServer.running = true;
	}
	
	public static void loadTokens()
	{
		File tokensFile = new File(Paths.get("server", "tokens.json").toString());
		
		logger.info("Loading tokens.");
		
		try (BufferedReader reader = new BufferedReader(new FileReader(tokensFile)))
		{
			TokenData tokenData = gson.fromJson(reader, TokenData.class);
			
			//Check if the data was read correctly.
			if (tokenData == null)
				return;
			
			SerpensBotWebServer.tokens = tokenData.getTokens();
		}
		catch (FileNotFoundException e)
		{
			logger.info("No token file found.");
		}
		catch (IOException e)
		{
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void saveTokens()
	{
		File tokensFile = new File(Paths.get("server", "tokens.json").toString());
		
		logger.info("Saving tokens.");
		
		//Init data containers.
		TokenData tokenData = new TokenData(SerpensBotWebServer.tokens);
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(tokensFile)))
		{
			writer.println(gson.toJson(tokenData));
		}
		catch (FileNotFoundException e)
		{
			logger.info("File not found. Creating new file.");
			
			try
			{
				tokensFile.getParentFile().mkdirs();
				
				if (tokensFile.createNewFile())
					SerpensBotWebServer.saveTokens();
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
	
	private static Object checkToken(Request request, Response response)
	{
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		if (tokenStatus == TokenStatus.AUTHORIZED)
			return "{}";
		
		response.status(401);
		return gson.toJson(new ErrorResponseBody("You must be logged in!"));
	}
	
	static Object login(Request request, Response response)
	{
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check if the user is already logged in.
		if (tokenStatus == TokenStatus.AUTHORIZED)
			return gson.toJson(new ErrorResponseBody("Already logged in!"));
		
		//Get the response body.
		LoginRequestBody responseBody = gson.fromJson(request.body(), LoginRequestBody.class);
		String paramPassword = responseBody.getPassword();
		
		//Check the password.
		if (!paramPassword.equals(password))
		{
			response.status(401);
			return gson.toJson(new ErrorResponseBody("Wrong password!"));
		}
		
		//Generate token and cookie.
		UUID uuid = UUID.randomUUID();
		tokens.add(uuid);
		SerpensBotWebServer.saveTokens();
		
		return gson.toJson(new LoginResponseBody(uuid, "Login was succesful."));
	}
	
	private static Object restartBot(Request request, Response response)
	{
		//Get user's token status.
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token status.
		if (tokenStatus != TokenStatus.AUTHORIZED)
		{
			logger.info("Token non presente o non autorizzato. Richiesta di restart annullata.");
			response.status(401);
			response.removeCookie("token");
			
			return gson.toJson(new ErrorResponseBody("You must be logged in!"));
		}
		
		logger.info("Il bot verrà restartato.");
		List<ListenerAdapter> modules = SerpensBot.getApi()
				.getRegisteredListeners()
				.stream()
				.map(element -> (ListenerAdapter) element)
				.toList();
		modules.forEach(listenerAdapter -> SerpensBot.getApi().removeEventListener(listenerAdapter));
		SerpensBot.getApi().shutdown();
		SerpensBot.start();
		
		return "{}";
	}
	
	private static Object stopBot(Request request, Response response)
	{
		//Get user's token status.
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token status.
		if (tokenStatus != TokenStatus.AUTHORIZED)
		{
			logger.info("Token non presente o non autorizzato.  Richiesta di arresto annullata.");
			response.status(401);
			response.removeCookie("token");
			
			return gson.toJson(new ErrorResponseBody("You must be logged in!"));
		}
		
		logger.info("Il bot verrà arrestato.");
		SerpensBot.getApi().shutdown();
		
		return "{}";
	}
	
	private static Object getLog(Request request, Response response)
	{
		//Get user's token status.
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token status.
		if (tokenStatus != TokenStatus.AUTHORIZED)
		{
			logger.info("Token non presente o non autorizzato.  Richiesta di log annullata.");
			response.status(401);
			response.removeCookie("token");
			
			return gson.toJson(new ErrorResponseBody("You must be logged in!"));
		}
		
		//Get last update param.
		String strLastDate = request.queryParams("lastUpdate");
		
		ArrayList<String> logLines = new ArrayList<>();
		File logFile = Paths.get("logs", "bot.log").toFile();
		
		LocalDateTime lastDate = LocalDateTime.MIN;
		
		//Read lines from the logs and buffer them.
		try (BufferedReader reader = new BufferedReader(new FileReader(logFile)))
		{
			final LocalDateTime lastUpdate = (strLastDate != null ? LocalDateTime.parse(strLastDate, SerpensBotWebServer.dateTimeFormatter) : null);
			
			//Collect all logs beofore the last update.
			for (String line : reader.lines().toList())
			{
				try
				{
					String strDate = line.substring(1, 20);
					lastDate = LocalDateTime.parse(strDate, dateTimeFormatter);
				}
				catch (DateTimeParseException | StringIndexOutOfBoundsException ignored){}
				
				if (lastUpdate == null || lastUpdate.isBefore(lastDate))
					logLines.add(line);
			}
		}
		catch (IOException e)
		{
			logger.error("", e);
			response.status(500);
			
			return gson.toJson(new ErrorResponseBody(e.getMessage()));
		}
		
		//Send the data.
		LogResponseBody logData = new LogResponseBody(logLines, lastDate.format(SerpensBotWebServer.dateTimeFormatter));
		return gson.toJson(logData);
	}
	
	private static Object botStatus(Request request, Response response)
	{
		//Get user's token status.
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token status.
		if (tokenStatus != TokenStatus.AUTHORIZED)
		{
			response.status(401);
			
			return gson.toJson(new ErrorResponseBody("You must be logged in!"));
		}
		
		JDA.Status botStatus = SerpensBot.getApi().getStatus();
		
		return gson.toJson(new BotStatusResponseBody(botStatus));
	}
	
	private static Object adminPage(Request request, Response response)
	{
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token.
		if (tokenStatus != TokenStatus.AUTHORIZED)
			response.redirect(SerpensBotWebServer.HOME_PATH);
		
		Map<String, Object> model = new HashMap<>();
		return new VelocityTemplateEngine().render(new ModelAndView(model, "webserver/admin_page.vm"));
	}
	
	private static Object loginPage(Request request, Response response)
	{
		TokenStatus tokenStatus = SerpensBotWebServer.getTokenStatus(request);
		
		//Check user's token.
		if (tokenStatus == TokenStatus.AUTHORIZED)
			response.redirect(SerpensBotWebServer.ADMIN_PATH);
		
		Map<String, Object> model = new HashMap<>();
		return new VelocityTemplateEngine().render(new ModelAndView(model, "webserver/login_page.vm"));
	}
	
	public static TokenStatus getTokenStatus(Request request)
	{
		String tokenString = request.cookie("token");
		
		try
		{
			UUID token = UUID.fromString(tokenString);
			
			if (!SerpensBotWebServer.tokens.contains(token))
				return TokenStatus.NOT_FOUND;
			
			return TokenStatus.AUTHORIZED;
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			return TokenStatus.NOT_FOUND;
		}
	}
}
