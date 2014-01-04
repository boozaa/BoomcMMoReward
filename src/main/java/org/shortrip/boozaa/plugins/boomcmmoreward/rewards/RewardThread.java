package org.shortrip.boozaa.plugins.boomcmmoreward.rewards;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Commands;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Group;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Items;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Messages;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Money;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Perm;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Power;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Skill;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.World;
import org.shortrip.boozaa.plugins.boomcmmoreward.tables.HistoryTable;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class RewardThread implements Runnable  {

	private volatile cReward reward;
	
	
	// Les instances utiles
	private final Money cmoney 		= new Money();
	private final Group cgroup 		= new Group();
	private final Power cpower 		= new Power();
	private final Skill cskill 		= new Skill();
	private final World cworld 		= new World();
	private final Perm cperm 		= new Perm();
	private final Items citem 		= new Items();
	private final Messages cmess 	= new Messages();
	private final Commands ccmds 	= new Commands();
	
	
	enum Message {
		
		MESSAGE("message"), MP("message.mp"), LOG("message.log"), BROADCAST("message.broadcast");
		public String value;

		Message(String value) {
			this.value = value;
		}
		
	}; 
	
	
	
	
	public RewardThread(cReward reward){		
		this.reward = reward;	
		this.run();		
	}
	
	
	@Override
	public void run() {
		
		BoomcMMoReward.debug("-----Reward file found -> " + reward.getName() + " ...processing");
		
		// On prends tous les premiers noeuds enfants de all:
		Configuration conf = reward.getConf();
		ConfigurationSection all = conf.getConfigurationSection("all");
		
		// Retour si stockage en db
		List<Double>historyMoney = null;
		List<String>historyPerms = null;
		List<String>historyGroups = null;
		List<String>historyCommands = null;
		List<String>historyItems = null;
		
		// Les premiers noeuds enfants pour obtenir liste des scenarios
		Set<String> enfants = all.getKeys(false);
		for( String enf : enfants ){
			BoomcMMoReward.debug("-----Deal with node: " + enf);
			// Noeud complet
			String node = "all." + enf;
			String conditions = node + "." + Const.CONDITIONS;
			String rewards = node + "." + Const.REWARDS;
			// La section conditions
			ConfigurationSection conditionsSection = conf.getConfigurationSection(conditions);
			// La section rewards
			ConfigurationSection rewardsSection = conf.getConfigurationSection(rewards);			
			
			// Vérification du remplissage des conditions
			if ( checkConditions(reward.getPlayer(), conditionsSection) ) {
				// Si il y a une section rewards dans le yml
				if( rewardsSection != null ){					
					BoomcMMoReward.debug("-----Giving Rewards");					
					
					try {
						
						// On donne money si demandé
						historyMoney = cmoney.proceedRewards( reward, rewardsSection, cmess );
						if( !historyMoney.isEmpty() ){
							reward.processMessage(rewardsSection, Const.MONEY);
						}
						
						// On donne perms si demandé
						historyPerms = cperm.proceedRewards( reward, rewardsSection, cmess );
						if( !historyPerms.isEmpty() ){
							reward.processMessage( rewardsSection, Const.PERM );
						}
						
						// On gere les groupes si demandé
						historyGroups = cgroup.proceedRewards( reward, rewardsSection, cmess );
						if( !historyGroups.isEmpty() ){
							reward.processMessage( rewardsSection, Const.GROUP );
						}
						
						// On gere les messages
						cmess.proceedRewards(reward, rewardsSection);
						
						// On gere les commandes
						historyCommands = ccmds.proceedRewards( reward, rewardsSection, cmess );
						if( !historyCommands.isEmpty() ){
							reward.processMessage( rewardsSection, Const.COMMAND );
						}
						
						// On donne items si demandé
						historyItems = citem.proceedRewards( reward, rewardsSection, cmess );
						if( !historyItems.isEmpty() ){
							reward.processMessage( rewardsSection, Const.ITEM );
						}						
						
					} catch (Exception e) { }

					BoomcMMoReward.debug("-----End Rewards");
					
					// On stocke cet historique en base de données si true
					if( BoomcMMoReward.getYmlConf().getBoolean("config.logInDatabase")) {					
						
						// Date d'aujourd'hui
				        Date today = new Date();																	        		       
				        HistoryTable history = new HistoryTable();
				        history.setPlayerName(reward.getPlayer().getName());
				        history.setRewardName(reward.getName());
				        history.setTimespan(today.getTime());
				        if( historyMoney != null ){history.setAmountFromList(historyMoney);}
				        if( historyPerms != null ){history.setPermsFromList(historyPerms);}
				        if( historyGroups != null ){history.setGroupsFromList(historyGroups);}
				        if( historyItems != null ){history.setItemsFromList(historyItems);}
				        if( historyCommands != null ){history.setCommandsFromList(historyCommands);}
						BoomcMMoReward.getDB().addHistory(history);
						BoomcMMoReward.debug("---History saved in database");
																	
					}
										
				}
	    	
			}
			
		}
			
	}
	
	
	
	private Boolean checkConditions(Player player, ConfigurationSection conf) {

    	// Si il n'y a pas de conditions on retourne true
    	if( conf == null ) { return true;}
    	
    	BoomcMMoReward.debug("-----Checking Conditions");
    	
    	try {

        	if( !this.cmoney.isValid(reward, conf ) ) { 
        		BoomcMMoReward.debug("-Condition Money not fulfill or bad formatted");
    			return false; 
    		}

        	
    		if( !this.cgroup.isValid(reward, conf ) ) { 
    			BoomcMMoReward.debug("-Condition Group not fulfill or bad formatted");
    			return false;
    		}

    		
    		if( !this.cpower.isValid(reward, conf ) ) { 
    			BoomcMMoReward.debug("-Condition Power not fulfill or bad formatted");
    			return false;				
    		}

    		
    		if( !this.cskill.isValid(reward, conf ) ) { 
    			BoomcMMoReward.debug("-Condition Skill not fulfill or bad formatted");
    			return false;
    		}

    		
    		if( !this.cworld.isValid(reward, conf ) ) { 
    			BoomcMMoReward.debug("-Condition World not fulfill or bad formatted");
    			return false;
    		}

    		
    		if( !this.cperm.isValid(reward, conf ) ) { 
    			BoomcMMoReward.debug("-Condition Permission not fulfill or bad formatted");
    			return false; 
    		} 
    		    		
    		
    	} catch (Exception e) {}
    					
		BoomcMMoReward.debug("-----End Conditions");
    	
    	// Tout est ok on renvoit true
		return true;
	
	}
	
	
	
	
	
}
