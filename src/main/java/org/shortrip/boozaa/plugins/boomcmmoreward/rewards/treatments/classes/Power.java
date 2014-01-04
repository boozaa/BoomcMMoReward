package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Power extends Parent implements iConditions {
	
	public Power() {
		super(TreatmentEnum.POWER);				
	}
		

	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
		
		if(confSection.get(Const.POWER) != null) {
			
			BoomcMMoReward.debug("---Checking Power conditions");
			
			try{

				// Vérification
				@SuppressWarnings("unchecked")
				List<String> powerConditions = (List<String>)confSection.getList(Const.POWER);    	
		    	for( String p : powerConditions) {
		    		
	    			int limit = Integer.parseInt( p.trim().substring(1).trim() );
		    		
		    		if( p.trim().startsWith("-")) {	    				
		    			BoomcMMoReward.debug("-Testing if user's power < " + limit);	    					    				  
		    			if( !reward.isPowerMinorLimit(limit) ) {		    				
	    					return false;
		    			}
		    			BoomcMMoReward.debug("-Ok");
		    			/*
	    				if( reward.getPlayerPower() >= limit ) {		    				
	    					return false;
		    			}
		    			BoomcMMoReward.debug("-Ok");
		    			*/
	    					    				
	    			}else if( p.trim().startsWith("+")) {	    				
	    				BoomcMMoReward.debug("-Testing if user's power > " + limit);
	    				if( !reward.isPowerMajorLimit(limit) ) {		    				
	    					return false;
		    			}
		    			BoomcMMoReward.debug("-Ok");
	    				/*
	    				if( reward.getPlayerPower() <= limit ) {
	    					return false;
		    			}
	    				BoomcMMoReward.debug("-Ok");
	    				*/	    				
	    			}else{
	    				BoomcMMoReward.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
		    		
		    	}
		    	
			}catch(Exception ex){
    			return false;
			}
			
		}
		// Pas ou plus de conditions donc toutes remplies
		return true;
	}

}
