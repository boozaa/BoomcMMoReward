package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.GroupException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Group extends Parent implements iConditions {

	
	public Group() {
		super(TreatmentEnum.GROUP);
	}



	private static List<String>listGroups;
	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws GroupException{
		
		if( confSection.get(Const.GROUP) != null ) {
			
			BoomcMMoReward.debug("---Groups node found on reward file ... processing" );
			
			listGroups = new ArrayList<String>();
			
			
			try {
			
				List<String> newGroups = confSection.getStringList(Const.GROUP);    	
		    	for( String p : newGroups) {
		    		
		    		String group = p.substring(1);
		    		Boolean isInGroup = reward.isInGroup(group);
		    		
		    		// Vérification d'existence de ce groupe
		    		if( reward.isGroupExists(group) ) {
		    			
		    			// On stocke en db
		    			listGroups.add(group);
		    			
		    			if( p.startsWith("+") ) {

			    			if( !isInGroup ){
			    				// On ajoute au groupe
			    				reward.addToGroup(group);			    				
			    				BoomcMMoReward.debug("-Player added to group " + group);
			    			}else{
			    				BoomcMMoReward.debug("-Player is already on Group " + group + " -> no changes");
			    			}
			    			
			    			
			    		}else if( p.startsWith("-") ){
			    			
			    			if( isInGroup ){
			    				// On ajoute au groupe
			    				reward.removeFromGroup(group);			    				
			    				BoomcMMoReward.debug("-Player removed from group " + group);
			    			}else{
			    				BoomcMMoReward.debug("-Player is not on Group " + group + " -> no changes");
			    			}
			    			
			    		}
		    			
		    		}else{
		    			BoomcMMoReward.debug("-The group " + group + " doesn't exist -> no changes");
		    		}
		    		
		    	}
				
				
			} catch (Exception e) {
				throw new GroupException("Error in the group section", e);
			}	
			
			// Si il y a section message on la traite
			if( confSection.get(Const.GROUP + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.GROUP));					
			}
			
		}
		
		return listGroups;
		
	}
	


	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) throws GroupException {

		if(confSection.get(Const.GROUP) != null) {
			
			BoomcMMoReward.debug("---Checking Group conditions");
			
			// Vérification
			List<String> groupConditions = confSection.getStringList(Const.GROUP);    	
	    	for( String p : groupConditions) {
	    		
	    		try{
	    			
	    			String groupName = p.trim().substring(1).trim();
	    			Boolean isInGroup = reward.isInGroup(groupName);
	    			
	    			if( p.trim().startsWith("-")) {
	    					    				
	    				BoomcMMoReward.debug("-Testing if user isn't on group " + groupName);	    				
	    				if( isInGroup ) {		    				
	    					return false;
		    			}
	    				BoomcMMoReward.debug("-Ok");
	    				
	    			}else if( p.trim().startsWith("+")) {
	    				
	    				BoomcMMoReward.debug("-Testing if user is on group " + groupName);	    				
	    				if( !isInGroup ) {	
	    					return false;
		    			}
	    				BoomcMMoReward.debug("-Ok");
	    				
	    			}else{
	    				BoomcMMoReward.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
	    			
	    			
	    		}catch(Exception ex){
	    			throw new GroupException("Error in the group section", ex);
				}			
	    		
	    	}
			
			
		}
		
		// Pas ou plus de conditions donc toutes remplies
		return true;
		
	}
	
}
