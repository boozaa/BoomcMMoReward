package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.PermissionException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Perm extends Parent implements iConditions {

	
	public Perm() {
		super(TreatmentEnum.PERM);
	}



	private static List<String>listPerms;
	
	
	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws PermissionException{
		
		if( confSection.get(Const.PERM) != null ) {
			
			BoomcMMoReward.debug("---Permissions node found on reward file ... processing" );
			
			listPerms = new ArrayList<String>();
			
			
			try {
			
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
		    			Boolean hasThisPerm = reward.hasPermissionInWorld(perm, worldName);
		    			
		    			// On demande d'ajouter cette perm
		    			if( p.startsWith("+") ) {
		    				if( !hasThisPerm ) {
			    				// Ajout de permission pour un Monde spécifique
			    				reward.givePermissionInWorld(perm, worldName);
			    				BoomcMMoReward.debug("-Added permission " + perm + " on World " + worldName);
			    			}else{
			    				BoomcMMoReward.debug("-Player already has this permission :" + perm + " for World " + worldName + " -> no changes");
		    				}
		    			}else if( p.startsWith("-") ) {
		    				if( hasThisPerm ) {
		    					// Suppression de cette permission
		    					reward.removePermissionInWorld(perm, worldName);
		    					BoomcMMoReward.debug("-Removed permission " + perm + " on World " + worldName);
		    				}else{
		    					BoomcMMoReward.debug("-Player don't have this permission :" + perm + " for World " + worldName + " -> no changes");
		    				}
		    			}else{
		    				BoomcMMoReward.debug("-Can't determine if the permission node is for added or removed. Please adjust your config file");
		    			}
		    			
		    		}else{
		    			String perm = p.substring(1);
		    			Boolean hasThisPerm = reward.hasPermission(perm);
		    			
		    			if( p.startsWith("+") ) {
		    				if( !hasThisPerm ) {
			    				// Ajout de permission pour un Monde spécifique
			    				reward.givePermission(perm);
			    				BoomcMMoReward.debug("-Added permission " + perm);
			    			}else{
			    				BoomcMMoReward.debug("-Player already has this permission :" + perm + " -> no changes");
		    				}
		    			}else if( p.startsWith("-") ) {
		    				if( hasThisPerm ) {
		    					// Suppression de cette permission
		    					reward.removePermission(perm);
		    					BoomcMMoReward.debug("-Removed permission " + perm );
		    				}else{
		    					BoomcMMoReward.debug("-Player don't have this permission :" + perm + " -> no changes");
		    				}
		    			}else{
		    				BoomcMMoReward.debug("-Do nothing. Can't determine if the permission node is for added or removed. Please adjust your config file");
		    			}		    			
		    		}
		    		
		    	}
			} catch (PermissionException e) { }
			
			// Si il y a section message on la traite
			if( confSection.get(Const.PERM + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.PERM));					
			}
			
		}
		
		
		return listPerms;
		
		
	}
	

	
	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
		
		if(confSection.get(Const.PERM) != null) {
			
			BoomcMMoReward.debug("---Checking Perm conditions");
			
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
		    				if(!reward.hasPermissionInWorld(perm, worldName) ) {
			    				// On informe et on sort
		    					BoomcMMoReward.debug("-Player don't have the permission " + perm + " based on World " + worldName);
			    				return false;
			    			}else{
			    				BoomcMMoReward.debug("-Ok");
			    			}
		    			}else if( p.startsWith("-") ) {
		    				if( reward.hasPermissionInWorld(perm, worldName) ) {
		    					BoomcMMoReward.debug("-Player has the permission " + perm + " for World " + worldName);
		    					return false;
		    				}else{
		    					BoomcMMoReward.debug("-Ok");
			    			}
		    			}else{
		    				BoomcMMoReward.debug("-Can't determine the permission condition node. Please adjust your config file");
		    			}		    			
		    			
		    		}else{
		    			String perm = p.substring(1);
		    			
		    			if( p.startsWith("+") ) {
		    				if( !reward.hasPermission(perm) ) {
			    				// Ajout de permission pour un Monde spécifique
		    					BoomcMMoReward.debug("-Player don't have the permission " + perm);
				    			return false;
		    				}else{
		    					BoomcMMoReward.debug("-Ok");
			    			}
		    			}else if( p.startsWith("-") ) {
		    				if( reward.hasPermission(perm) ) {
		    					// Suppression de cette permission
		    					BoomcMMoReward.debug("-Player has the permission " + perm);
		    					return false;
		    				}else{
		    					BoomcMMoReward.debug("-Ok");
			    			}
		    			}else{
		    				BoomcMMoReward.debug("-Do nothing. Can't determine the permission condition node. Please adjust your reward file");
		    			}		    			
		    			
		    		}
		    		
		    	}
				
			}catch(Exception ex){}
			
		}
		// Pas de conditions de permission donc true
		return true;
	}

}
