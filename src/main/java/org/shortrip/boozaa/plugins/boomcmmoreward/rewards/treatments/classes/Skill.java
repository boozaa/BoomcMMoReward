package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;
import com.gmail.nossr50.datatypes.skills.SkillType;





public class Skill extends AbstractReward {

	
	public Skill() {
		super();
	}

	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardSkillException {
		
		if( confSection.get(Const.SKILL) != null ) {
			
			Log.debug("---Checking Skill conditions");
			
			try{
			
				for( SkillType s : SkillType.values()){
					
					if( confSection.get( Const.SKILL+"."+s.name().toLowerCase() ) != null ) {
						
						// Pour ce skill on vérifie les conditions de level
						@SuppressWarnings("unchecked")
						List<String> levelConditions = (List<String>)confSection.getList(Const.SKILL+"."+s.name().toLowerCase()+".level");    	
				    	for( String p : levelConditions) {
				    		
				    		int level = Integer.parseInt( p.trim().substring(1).trim() );
				    		
				    		if( p.trim().startsWith("-")) {			    				
				    			Log.debug("-Testing if user's " + s.name() + " level is < " + level);	    				
			    				if( !reward.isSkillLevelMinorLimit(s.name(), level) ){
			    					return false;
			    				}
			    				Log.debug("-Ok");
				    			/*
				    			if( reward.getPlayerSkillLevel(s) >= level ) {		    				
			    					return false;
				    			}
			    				BoomcMMoReward.debug("-Ok");
			    				*/
			    			}else if( p.trim().startsWith("+")) {
			    				
			    				Log.debug("-Testing if user's " + s.name() + " level is > " + level);	    				
			    				if( reward.getPlayerSkillLevel(s) <= level ) {
			    					return false;
				    			}
			    				Log.debug("-Ok");			    				
			    			}else{
			    				Log.debug("-Not found operator '+' or '-' ... aborting");
			    				return false;
			    			}
				    		
				    	}
						
					}
					
				}
			
			}catch(Exception ex){
    			throw new RewardSkillException("", ex);
			}
			
			
		}
		
		
		
		
		return true;
	}


	

	public class RewardSkillException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardSkillException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	

	
}
