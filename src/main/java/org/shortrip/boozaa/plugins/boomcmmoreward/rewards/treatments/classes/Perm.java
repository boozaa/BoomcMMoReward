package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Perm extends AbstractReward {


	private List<String>listPerms;
	private cReward reward;
	
	
	public Perm() {
		super();
	}

	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardPermException{

		this.reward = reward;
		
		
		
		if( confSection.get(Const.PERM) != null ) {
			
			Log.debug("---Permissions node found on reward file ... processing" );
			
			listPerms = new ArrayList<String>();
			
			if( !BoomcMMoReward.isVaultEnabled() ){
				Log.debug("---Vault is required to do that" );
				return listPerms;
			}
			
			List<String> newPerms = confSection.getStringList(Const.PERM);    	
	    	for( String p : newPerms) {
	    		
	    		// On stocke en db
	    		listPerms.add(p);
	    		
	    		// Si un Monde est précisé [Base]
	    		if( p.contains("[") ) {
	    			
	    			int start = p.indexOf("[");
	    			int end = p.indexOf("]");
	    			String worldName = p.substring(start+1, end);
	    			String perm = p.substring(end+1);
	    			boolean hasThisPerm = hasPermissionInWorld(perm, worldName);
	    			
	    			// On demande d'ajouter cette perm
	    			if( p.startsWith("+") ) {
	    				if( !hasThisPerm ) {
		    				// Ajout de permission pour un Monde spécifique
		    				givePermissionInWorld(perm, worldName);
		    				Log.debug("-Added permission " + perm + " on World " + worldName);
		    			}else{
		    				Log.debug("-Player already has this permission :" + perm + " for World " + worldName + " -> no changes");
	    				}
	    			}else if( p.startsWith("-") ) {
	    				if( hasThisPerm ) {
	    					// Suppression de cette permission
	    					removePermissionInWorld(perm, worldName);
	    					Log.debug("-Removed permission " + perm + " on World " + worldName);
	    				}else{
	    					Log.debug("-Player don't have this permission :" + perm + " for World " + worldName + " -> no changes");
	    				}
	    			}else{
	    				Log.debug("-Can't determine if the permission node is for added or removed. Please adjust your config file");
	    			}
	    			
	    		}else{
	    			String perm = p.substring(1);
	    			boolean hasThisPerm = hasPermission(perm);
	    			
	    			if( p.startsWith("+") ) {
	    				if( !hasThisPerm ) {
		    				// Ajout de permission pour un Monde spécifique
		    				givePermission(perm);
		    				Log.debug("-Added permission " + perm);
		    			}else{
		    				Log.debug("-Player already has this permission :" + perm + " -> no changes");
	    				}
	    			}else if( p.startsWith("-") ) {
	    				if( hasThisPerm ) {
	    					// Suppression de cette permission
	    					removePermission(perm);
	    					Log.debug("-Removed permission " + perm );
	    				}else{
	    					Log.debug("-Player don't have this permission :" + perm + " -> no changes");
	    				}
	    			}else{
	    				Log.debug("-Do nothing. Can't determine if the permission node is for added or removed. Please adjust your config file");
	    			}		    			
	    		}
	    		
	    	}
	    	

			// On donne les commandes lancées en variables de remplacement
			reward.addReplacement("%perms%", listPerms);
			
			// Si il y a section message on la traite
			if( confSection.get(Const.PERM + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.PERM));					
			}
			
		}
		
		
		return listPerms;
		
		
	}
	

	
	@Override
	public boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardPermException {
		
		if(confSection.get(Const.PERM) != null) {
			
			Log.debug("---Checking Perm conditions");
			
			try{
				
				// Vérification
				List<String> permConditions = confSection.getStringList(Const.PERM);    	
		    	for( String p : permConditions) {
		    		
		    		// Ensuite vient le Monde avec [Base]
		    		if( p.contains("[") ) {
		    			int start = p.indexOf("[");
		    			int end = p.indexOf("]");
		    			String worldName = p.substring(start+1, end);
		    			String perm = p.substring(end+1);
		    			
		    			if( p.startsWith("+") ) {		    				
		    				if(!hasPermissionInWorld(perm, worldName) ) {
			    				// On informe et on sort
		    					Log.debug("-Player don't have the permission " + perm + " based on World " + worldName);
			    				return false;
			    			}else{
			    				Log.debug("-Ok");
			    			}
		    			}else if( p.startsWith("-") ) {
		    				if( hasPermissionInWorld(perm, worldName) ) {
		    					Log.debug("-Player has the permission " + perm + " for World " + worldName);
		    					return false;
		    				}else{
		    					Log.debug("-Ok");
			    			}
		    			}else{
		    				Log.debug("-Can't determine the permission condition node. Please adjust your config file");
		    			}		    			
		    			
		    		}else{
		    			String perm = p.substring(1);
		    			
		    			if( p.startsWith("+") ) {
		    				if( !hasPermission(perm) ) {
			    				// Ajout de permission pour un Monde spécifique
		    					Log.debug("-Player don't have the permission " + perm);
				    			return false;
		    				}else{
		    					Log.debug("-Ok");
			    			}
		    			}else if( p.startsWith("-") ) {
		    				if( hasPermission(perm) ) {
		    					// Suppression de cette permission
		    					Log.debug("-Player has the permission " + perm);
		    					return false;
		    				}else{
		    					Log.debug("-Ok");
			    			}
		    			}else{
		    				Log.debug("-Do nothing. Can't determine the permission condition node. Please adjust your reward file");
		    			}		    			
		    			
		    		}
		    		
		    	}
				
			}catch(Exception ex){
				throw new RewardPermException("Perm condition exception", ex);
			}
			
		}
		// Pas de conditions de permission donc true
		return true;
	}

	
	private boolean hasPermission(String permission){
		return BoomcMMoReward.getPerms().playerHas(this.reward.getPlayer(), permission);
	}
	
	private boolean hasPermissionInWorld(String permission, String worldName){
		if( this.reward.getPlayer().getServer().getWorld(worldName) != null) {
			return BoomcMMoReward.getPerms().playerHas(this.reward.getPlayer().getServer().getWorld(worldName), this.reward.getPlayer().getName(), permission);
		}	
		return false;
	}
	
	private void givePermissionInWorld(String permission, String worldName){		
		// Tentative d'ajout de permission spécifique au Monde
		BoomcMMoReward.getPerms().playerAdd(this.reward.getPlayer().getServer().getWorld(worldName), this.reward.getPlayer().getName(), permission);							
	}
	
	private void givePermission(String permission){			
		// Tentative d'ajout de permission 
		BoomcMMoReward.getPerms().playerAdd(this.reward.getPlayer(), permission);
	}
	
	private void removePermissionInWorld(String permission, String worldName){
		// Tentative de retrait de permission dans World
		BoomcMMoReward.getPerms().playerRemove(this.reward.getPlayer().getServer().getWorld(worldName), this.reward.getPlayer().getName(), permission);					
	}
		
	private void removePermission(String permission){
		// Tentative de retrait de permission 
		BoomcMMoReward.getPerms().playerRemove(this.reward.getPlayer(), permission);
	}



	public class RewardPermException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardPermException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	

}
