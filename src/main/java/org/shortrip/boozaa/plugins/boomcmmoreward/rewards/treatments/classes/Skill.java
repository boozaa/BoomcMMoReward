package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;

import com.gmail.nossr50.datatypes.skills.SkillType;





public class Skill extends Parent implements iConditions {

	
	public Skill() {
		super(TreatmentEnum.SKILL);
	}

	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
		
		if( confSection.get(Const.SKILL) != null ) {
			
			BoomcMMoReward.debug("---Checking Skill conditions");
			
			try{
			
				for( SkillType s : SkillType.values()){
					
					if( confSection.get( Const.SKILL+"."+s.name().toLowerCase() ) != null ) {
						
						// Pour ce skill on vérifie les conditions de level
						@SuppressWarnings("unchecked")
						List<String> levelConditions = (List<String>)confSection.getList(Const.SKILL+"."+s.name().toLowerCase()+".level");    	
				    	for( String p : levelConditions) {
				    		
				    		int level = Integer.parseInt( p.trim().substring(1).trim() );
				    		
				    		if( p.trim().startsWith("-")) {			    				
				    			BoomcMMoReward.debug("-Testing if user's " + s.name() + " level is < " + level);	    				
			    				if( !reward.isSkillLevelMinorLimit(s.name(), level) ){
			    					return false;
			    				}
			    				BoomcMMoReward.debug("-Ok");
				    			/*
				    			if( reward.getPlayerSkillLevel(s) >= level ) {		    				
			    					return false;
				    			}
			    				BoomcMMoReward.debug("-Ok");
			    				*/
			    			}else if( p.trim().startsWith("+")) {
			    				
			    				BoomcMMoReward.debug("-Testing if user's " + s.name() + " level is > " + level);	    				
			    				if( reward.getPlayerSkillLevel(s) <= level ) {
			    					return false;
				    			}
			    				BoomcMMoReward.debug("-Ok");			    				
			    			}else{
			    				BoomcMMoReward.debug("-Not found operator '+' or '-' ... aborting");
			    				return false;
			    			}
				    		
				    	}
						
					}
					
				}
			
			}catch(Exception ex){
    			return false;
			}
			
			
		}
		
		
		
		
		return true;
	}
	

	
}
