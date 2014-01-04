package org.shortrip.boozaa.plugins.boomcmmoreward.listeners;

import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
//import org.perf4j.StopWatch;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.RewardQueue;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;

import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;


public class McMMOListener implements Listener {

	// Le plugin
	private Plugin plugin;
	// Le fichier reward en cours de traitement
	private Configuration conf;
	// La file d'attente
	private RewardQueue queue;
	

	    
	
    public McMMOListener(Plugin plugin) {
    	
    	this.plugin = plugin;    	
    	// On instancie la queue
    	this.queue = new RewardQueue();
    	    	
    }


	@EventHandler
	public void onPlayerLevelUp(final McMMOPlayerLevelUpEvent event) {		
		
		try{
			
			final Player player = event.getPlayer();
			final int playerPower = com.gmail.nossr50.api.ExperienceAPI.getPowerLevel(event.getPlayer());
			final int skillLevel = event.getSkillLevel();
			final SkillType skill = event.getSkill();

			// TRAITEMENT
			// POWER/ONE/1.yml
			final String powerReward = plugin.getDataFolder() + File.separator + "POWER" + File.separator;
			final File rewardOneFile = new File(powerReward + "ONE" + File.separator + playerPower + ".yml" );
			
			if( rewardOneFile.exists() ){

				BoomcMMoReward.debug("-----New player level : " + playerPower);
				BoomcMMoReward.debug("-----POWER ONE reward file to process : " + rewardOneFile);
				
				conf = new Configuration(rewardOneFile);
				if (conf.exists()) {					
					conf.load();
					// On ajoute ce cReward à la Queue
					queue.enqueue(new cReward(rewardOneFile.toString(),conf,player,skill,playerPower,skillLevel) );				
				}

			}		
			
			
			// SKILLS/SKILL/ONE/1.yml
			String skillReward = plugin.getDataFolder() + File.separator + "SKILLS" + File.separator + skill + File.separator;
			File rewardskillOneFile = new File(skillReward + "ONE" + File.separator + skillLevel + ".yml" );
			
			if( rewardskillOneFile.exists() ){
				
				BoomcMMoReward.debug("-----New player level : " + playerPower);
				BoomcMMoReward.debug("-----" + skill + " ONE reward file to process : " + rewardskillOneFile);
				
				conf = new Configuration(rewardskillOneFile);
				if (conf.exists()) {					
					conf.load();
					// On ajoute ce cReward à la Queue
					queue.enqueue(new cReward(rewardskillOneFile.toString(),conf,player,skill,playerPower,skillLevel) );
				}
				
			}
			
			
			// POWER/EVERY
			File powerEveryFolder = new File(powerReward + "EVERY" + File.separator );
			String [] listeFichiers;
			listeFichiers = powerEveryFolder.list();

			for( String fichier : listeFichiers){
				
				int level = Integer.parseInt(fichier.replace(".yml", ""));
				// Si le modulo renvoit 0 alors multiple
				if( (playerPower%level)==0 ){
					// On traite ce fichier
					File everyPowerFile = new File(powerReward + "EVERY" + File.separator + fichier );				
					
					BoomcMMoReward.debug("-----New player level : " + playerPower);
					BoomcMMoReward.debug("-----EVERY POWER reward file to process : " + everyPowerFile);				
					
					conf = new Configuration(everyPowerFile);
					if (conf.exists()) {					
						conf.load();
						// On ajoute ce cReward à la Queue
						queue.enqueue(new cReward(everyPowerFile.toString(),conf,player,skill,playerPower,skillLevel) );
					}
					
				}
			}
			
			
			// SKILLS/SKILL/EVERY
			File skillEveryFolder = new File(skillReward + "EVERY" + File.separator );
			listeFichiers = skillEveryFolder.list();

			for( String fichier : listeFichiers){
				
				int level = Integer.parseInt(fichier.replace(".yml", ""));
				// Si le modulo renvoit 0 alors multiple
				if( (skillLevel%level)==0 ){
					// On traite ce fichier
					File everySkillFile = new File(skillReward + "EVERY" + File.separator + fichier );				
					
					BoomcMMoReward.debug("-----New player skill level : " + skillLevel);
					BoomcMMoReward.debug("-----EVERY SKILLS reward file to process : " + everySkillFile);				
					
					conf = new Configuration(everySkillFile);
					if (conf.exists()) {					
						conf.load();
						// On ajoute ce cReward à la Queue
						queue.enqueue(new cReward(everySkillFile.toString(),conf,player,skill,playerPower,skillLevel) );
					}
				}
			}
			
			
			// On lance la queue
			queue.sendNextReward();
			
		} catch (Exception e) {}
		
				
	}
	
	

	
}