package com.serpenssolida.discordbot.module.settings;

import java.util.HashMap;
import java.util.Map;

public class SettingsData
{
	private Map<String, String> modulePrefixes = new HashMap<>();
	private Map<String, Boolean> moduleStates = new HashMap<>();
	
	public Map<String, String> getModulePrefixes()
	{
		return this.modulePrefixes;
	}
	
	public void setModulePrefixes(Map<String, String> modulePrefixes)
	{
		this.modulePrefixes = modulePrefixes;
	}
	
	public Map<String, Boolean> getModuleStates()
	{
		return this.moduleStates;
	}
	
	public void setModuleStates(Map<String, Boolean> moduleStates)
	{
		this.moduleStates = moduleStates;
	}

}
