package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.CommandException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;



public class Commands extends Parent {

	
	public Commands() {
		super(TreatmentEnum.COMMAND);
	}


	private static List<String> listCommands;
	

	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws CommandException{
		
		if( confSection.get(Const.COMMAND) != null ) {
			
			BoomcMMoReward.debug("---Commands node found on reward file ... processing" );
			
			listCommands = new ArrayList<String>();
			
			try {
				
				List<String> commands = confSection.getStringList(Const.COMMAND);				
				// On stocke en db
				listCommands = commands;				
				// On fait traiter ces commandes par le cReward
				reward.sendCommands(commands);
				
				// Si il y a section message on la traite
				if( confSection.get(Const.COMMAND + "." + Const.MESSAGE) != null ) {
					cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.COMMAND));					
				}
				
			} catch (Exception e) {
				throw new CommandException("Error in your command section", e);
			}
			
		}
		
		return listCommands;
		
	}
	
	

}
