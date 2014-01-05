package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Group extends AbstractReward {

	private List<String> listGroups = new ArrayList<String>();
	private cReward reward;
	
	
	public Group() {
		super();
	}

	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardGroupException{
		
		
		if( confSection.get(Const.GROUP) != null ) {
			
			Log.debug("---Groups node found on reward file ... processing" );
			
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
			    				Log.debug("-Player added to group " + group);
			    			}else{
			    				Log.debug("-Player is already on Group " + group + " -> no changes");
			    			}
			    			
			    			
			    		}else if( p.startsWith("-") ){
			    			
			    			if( isInGroup ){
			    				// On ajoute au groupe
			    				reward.removeFromGroup(group);			    				
			    				Log.debug("-Player removed from group " + group);
			    			}else{
			    				Log.debug("-Player is not on Group " + group + " -> no changes");
			    			}
			    			
			    		}
		    			
		    		}else{
		    			Log.debug("-The group " + group + " doesn't exist -> no changes");
		    		}
		    		
		    	}
				
				
			} catch (Exception e) {
				throw new RewardGroupException("Error in the group section", e);
			}	
						
		}
		
		return listGroups;
		
	}
	


	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardGroupException {

		if(confSection.get(Const.GROUP) != null) {
			
			Log.debug("---Checking Group conditions");
			
			// Vérification
			List<String> groupConditions = confSection.getStringList(Const.GROUP);    	
	    	for( String p : groupConditions) {
	    		
	    		try{
	    			
	    			String groupName = p.trim().substring(1).trim();
	    			Boolean isInGroup = reward.isInGroup(groupName);
	    			
	    			if( p.trim().startsWith("-")) {
	    					    				
	    				Log.debug("-Testing if user isn't on group " + groupName);	    				
	    				if( isInGroup ) {		    				
	    					return false;
		    			}
	    				Log.debug("-Ok");
	    				
	    			}else if( p.trim().startsWith("+")) {
	    				
	    				Log.debug("-Testing if user is on group " + groupName);	    				
	    				if( !isInGroup ) {	
	    					return false;
		    			}
	    				Log.debug("-Ok");
	    				
	    			}else{
	    				Log.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
	    			
	    			
	    		}catch(Exception ex){
	    			throw new RewardGroupException("Error in the group section", ex);
				}			
	    		
	    	}
			
			
		}
		
		// Pas ou plus de conditions donc toutes remplies
		return true;
		
	}

	
	

	public class RewardGroupException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardGroupException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	
	
	
}
