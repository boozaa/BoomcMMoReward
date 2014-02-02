package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Messages extends AbstractReward {
	

	
	public Messages() {
		super();
	}


	public void proceedRewards(cReward reward, ConfigurationSection confSection){
		
		if( confSection.get(Const.MESSAGE) != null ) { 
			
			Log.debug("---Messages node found on reward file ... processing" );
			
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
	public boolean isValid(cReward reward, ConfigurationSection confSection) {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	public class RewardMessageException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardMessageException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}

}
