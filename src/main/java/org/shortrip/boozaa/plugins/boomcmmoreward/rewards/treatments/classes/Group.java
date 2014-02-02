package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
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

		this.reward = reward;
		
		if( confSection.get(Const.GROUP) != null ) {
			
			Log.debug("---Groups node found on reward file ... processing" );
			
			listGroups = new ArrayList<String>();
			
			if( !BoomcMMoReward.isVaultEnabled() ){
				Log.debug("---Vault is required to do that" );
				return listGroups;
			}
			
			try {
			
				List<String> newGroups = confSection.getStringList(Const.GROUP);    	
		    	for( String p : newGroups) {
		    		
		    		String group = p.substring(1);
		    		boolean isInGroup = isInGroup(group);
		    		
		    		// Vérification d'existence de ce groupe
		    		if( isGroupExists(group) ) {
		    			
		    			// On stocke en db
		    			listGroups.add(group);
		    			
		    			if( p.startsWith("+") ) {

			    			if( !isInGroup ){
			    				// On ajoute au groupe
			    				addToGroup(group);			    				
			    				Log.debug("-Player added to group " + group);
			    			}else{
			    				Log.debug("-Player is already on Group " + group + " -> no changes");
			    			}
			    			
			    			
			    		}else if( p.startsWith("-") ){
			    			
			    			if( isInGroup ){
			    				// On ajoute au groupe
			    				removeFromGroup(group);			    				
			    				Log.debug("-Player removed from group " + group);
			    			}else{
			    				Log.debug("-Player is not on Group " + group + " -> no changes");
			    			}
			    			
			    		}
		    			
		    		}else{
		    			Log.debug("-The group " + group + " doesn't exist -> no changes");
		    		}
		    		
		    	}

				// On donne les commandes lancées en variables de remplacement
				reward.addReplacement("%groups%", listGroups);
				
			} catch (Exception e) {
				throw new RewardGroupException("Error in the group section", e);
			}	
						
		}
		
		return listGroups;
		
	}
	


	@Override
	public boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardGroupException {

		if(confSection.get(Const.GROUP) != null) {
			
			Log.debug("---Checking Group conditions");
			
			// Vérification
			List<String> groupConditions = confSection.getStringList(Const.GROUP);    	
	    	for( String p : groupConditions) {
	    		
	    		try{
	    			
	    			String groupName = p.trim().substring(1).trim();
	    			boolean isInGroup = isInGroup(groupName);
	    			
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

	

	private boolean isGroupExists(String groupName){		
		for( String g : BoomcMMoReward.getPerms().getGroups() ){
			if( g.equalsIgnoreCase(groupName)){ return true; }
		}
		return false;
	}
	
	
	private boolean isInGroup(String groupName){
		return ( BoomcMMoReward.getPerms().playerInGroup(this.reward.getPlayer(), groupName));		
	}
	
	
	@SuppressWarnings("unused")
	private void addToGroupInWorld(String groupName, String worldName) {
		// Tentative d'ajout du player au groupe dans Monde
		BoomcMMoReward.getPerms().playerAddGroup(this.reward.getPlayer().getServer().getWorld(worldName), this.reward.getPlayer().getName(), groupName);	
	}
	
	private void addToGroup(String groupName) {
		// Tentative d'ajout du player au groupe
		BoomcMMoReward.getPerms().playerAddGroup(this.reward.getPlayer(), groupName);
	}
		
	@SuppressWarnings("unused")
	private void removeFromGroupInWorld(String groupName, String worldName){
		// Tentative de retrait de player au group dans Monde
		BoomcMMoReward.getPerms().playerRemoveGroup(this.reward.getPlayer().getServer().getWorld(worldName), this.reward.getPlayer().getName(), groupName);
	}
		
	private void removeFromGroup(String groupName){
		// Tentative de retrait de player au group
		BoomcMMoReward.getPerms().playerRemoveGroup(this.reward.getPlayer(), groupName);
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
