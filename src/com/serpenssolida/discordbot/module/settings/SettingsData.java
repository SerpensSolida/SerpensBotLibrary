package com.serpenssolida.discordbot.module.settings;

import java.util.HashMap;

public class SettingsData
{
	private HashMap<String, String> modulePrefixes = new HashMap<>();
	private HashMap<String, Boolean> moduleStates = new HashMap<>();
	
	public HashMap<String, String> getModulePrefixes()
	{
		return this.modulePrefixes;
	}
	
	public void setModulePrefixes(HashMap<String, String> modulePrefixes)
	{
		this.modulePrefixes = modulePrefixes;
	}
	
	public HashMap<String, Boolean> getModuleStates()
	{
		return this.moduleStates;
	}
	
	public void setModuleStates(HashMap<String, Boolean> moduleStates)
	{
		this.moduleStates = moduleStates;
	}

}
