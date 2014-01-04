package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class World extends Parent implements iConditions {
	
	public World() {
		super(TreatmentEnum.WORLD);
	}
	

	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
	
		if(confSection.get(Const.WORLD) != null) {
			BoomcMMoReward.debug("---Checking World conditions");
			BoomcMMoReward.debug("-Testing if in World -> " + confSection.get(Const.WORLD) );
			// On testes si dans le bon Monde
			if( reward.isInWorld(confSection.getString(Const.WORLD) )){
				BoomcMMoReward.debug("-Ok");
				return true;
			}else{
				return false;
			}
		}	
		return true;
		
	}

	
}
