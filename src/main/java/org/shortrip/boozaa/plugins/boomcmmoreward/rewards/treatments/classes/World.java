package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class World extends AbstractReward {
	
	public World() {
		super();
	}
	

	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
	
		if(confSection.get(Const.WORLD) != null) {
			Log.debug("---Checking World conditions");
			Log.debug("-Testing if in World -> " + confSection.get(Const.WORLD) );
			// On testes si dans le bon Monde
			if( reward.isInWorld(confSection.getString(Const.WORLD) )){
				Log.debug("-Ok");
				return true;
			}else{
				return false;
			}
		}	
		return true;
		
	}


	@Override
	protected String variableReplace(String msg) {
		return msg;
	}

	
}
