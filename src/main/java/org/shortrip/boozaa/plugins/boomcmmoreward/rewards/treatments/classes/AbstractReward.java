package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;

public abstract class AbstractReward {

	
	public AbstractReward(){
		
	}
	
	
	
	
	
	public abstract Boolean isValid(cReward reward, ConfigurationSection confSection) throws Exception;
	protected abstract String variableReplace(String msg);
	
}
