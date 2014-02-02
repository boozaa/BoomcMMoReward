package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Power extends AbstractReward {
	
	private cReward reward;	
	
	public Power() {
		super();				
	}
		

	@Override
	public boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardPowerException {
		
		if(confSection.get(Const.POWER) != null) {
			
			Log.debug("---Checking Power conditions");
			
			this.reward = reward;
			
			try{

				// Vérification
				@SuppressWarnings("unchecked")
				List<String> powerConditions = (List<String>)confSection.getList(Const.POWER);    	
		    	for( String p : powerConditions) {
		    		
	    			int limit = Integer.parseInt( p.trim().substring(1).trim() );
		    		
		    		if( p.trim().startsWith("-")) {	    				
		    			Log.debug("-Testing if user's power < " + limit);	    					    				  
		    			if( !isPowerMinorLimit(limit) ) {		    				
	    					return false;
		    			}
		    			Log.debug("-Ok");
	    					    				
	    			}else if( p.trim().startsWith("+")) {	    				
	    				Log.debug("-Testing if user's power > " + limit);
	    				if( !isPowerMajorLimit(limit) ) {		    				
	    					return false;
		    			}
	    				Log.debug("-Ok");
	    					    				
	    			}else{
	    				Log.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
		    		
		    	}
		    	
			}catch(Exception ex){
    			throw new RewardPowerException("", ex);
			}
			
		}
		// Pas ou plus de conditions donc toutes remplies
		return true;
	}

	
	private boolean isPowerMinorLimit(int limit){		
		return (this.reward.getPlayerPower() < limit);		
	}
	
	private boolean isPowerMajorLimit(int limit) {		
		return (this.reward.getPlayerPower() > limit);		
	}



	public class RewardPowerException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardPowerException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	
	
}
