package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class World extends AbstractReward {
	

	private cReward reward;
	
	
	public World() {
		super();
	}
	

	@Override
	public boolean isValid(cReward reward, ConfigurationSection confSection) {
	
		this.reward = reward;
		
		if(confSection.get(Const.WORLD) != null) {
			Log.debug("---Checking World conditions");
			Log.debug("-Testing if in World -> " + confSection.get(Const.WORLD) );
			// On testes si dans le bon Monde
			if( isInWorld(confSection.getString(Const.WORLD) )){
				Log.debug("-Ok");
				return true;
			}else{
				return false;
			}
		}	
		return true;
		
	}

	
	private boolean isInWorld(String worldName){
		return this.reward.getPlayer().getWorld().equals( this.reward.getPlayer().getServer().getWorld(worldName) );
	}
	
	@SuppressWarnings("unused")
	private boolean isInWorld(World world){
		return this.reward.getPlayer().getWorld().equals(world);
	}

	
}
