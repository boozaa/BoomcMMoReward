package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments;

import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;

public interface iConditions {

	
	/**
	 * isValid()
	 * @return Version sous forme de String.
	 * @throws Exception 
	 */
	public Boolean isValid(cReward reward, ConfigurationSection confSection) throws Exception;
	
	
}
