package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Commands extends AbstractReward {

	private List<String> list = new ArrayList<String>();
	private cReward reward;
	
	
	public Commands() {
		super();
	}


	private static List<String> listCommands;
	

	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardCommandException{

		if( confSection.get(Const.COMMAND) != null ) {
			
			Log.debug("---Commands node found on reward file ... processing" );
			
			listCommands = new ArrayList<String>();
			list = new ArrayList<String>();

			
			try {
				
				List<String> commands = confSection.getStringList(Const.COMMAND);				
				// On stocke en db
				Collections.copy(listCommands, commands);
				
				// On fait traiter ces commandes par le cReward
				reward.sendCommands(commands);
				
				// On donne les commandes lancées en variables de remplacement
				reward.addReplacement("%commands%", listCommands);
				
				// Si il y a section message on la traite
				if( confSection.get(Const.COMMAND + "." + Const.MESSAGE) != null ) {
					cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.COMMAND));					
				}
				
			} catch (Exception e) {
				throw new RewardCommandException("Error in your command section", e);
			}
			
		}
		
		return listCommands;
		
	}


	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection)
			throws Exception {
		return true;
	}




	public class RewardCommandException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardCommandException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}

	
}
