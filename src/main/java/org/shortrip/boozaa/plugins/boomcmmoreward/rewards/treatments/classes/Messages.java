package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Messages extends Parent implements iConditions {
	
	
	public Messages() {
		super(TreatmentEnum.MESSAGE);
	}





	public void proceedRewards(cReward reward, ConfigurationSection confSection){
		
		if( confSection.get(Const.MESSAGE) != null ) { 
			
			BoomcMMoReward.debug("---Messages node found on reward file ... processing" );
			
			if( confSection.getString(Const.MESSAGE_MP) != null ) {				
				List<String> m = confSection.getStringList(Const.MESSAGE_MP);
				// On traite tous les messages privés
	    		reward.sendMP(m);	
			}
			
			if( confSection.getString(Const.MESSAGE_BROADCAST) != null ) {				
				List<String> m = confSection.getStringList(Const.MESSAGE_BROADCAST);
				// On traite tous les messages broadcast
	    		reward.sendBroadcast(m);				
			}
			
			if( confSection.getString(Const.MESSAGE_LOG) != null ) {					
				List<String> m = confSection.getStringList(Const.MESSAGE_LOG);
				// On traite tous les messages console
	    		reward.sendLog(m);
			}
			
		}
		
	}

	
	
	
	
	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
		// TODO Auto-generated method stub
		return true;
	}

}
