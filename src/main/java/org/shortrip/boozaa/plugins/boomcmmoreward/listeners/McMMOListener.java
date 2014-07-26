package org.shortrip.boozaa.plugins.boomcmmoreward.listeners;

import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.RewardQueue;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.events.skills.abilities.McMMOPlayerAbilityActivateEvent;


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

				Log.debug("-----New player level : " + playerPower);
				Log.debug("-----POWER ONE reward file to process : " + rewardOneFile);
				
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
				
				Log.debug("-----New player level : " + playerPower);
				Log.debug("-----" + skill + " ONE reward file to process : " + rewardskillOneFile);
				
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
					
					Log.debug("-----New player level : " + playerPower);
					Log.debug("-----EVERY POWER reward file to process : " + everyPowerFile);				
					
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
					
					Log.debug("-----New player skill level : " + skillLevel);
					Log.debug("-----EVERY SKILLS reward file to process : " + everySkillFile);				
					
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
	
	
	@EventHandler( priority = EventPriority.LOW )
	public void onPlayerAbilityActivated(final McMMOPlayerAbilityActivateEvent event) {	
		
		try {
			
			final String abilityType = event.getAbility().name();
			final Player player = event.getPlayer();
			final int playerPower = com.gmail.nossr50.api.ExperienceAPI.getPowerLevel(event.getPlayer());
			final SkillType skilltype = event.getSkill();
			final int skillLevel = event.getSkillLevel();
			
			Log.debug( "Ability event available in your mcMMO's version" );
			Log.debug( "Ability launched by " + player.getName() + " -> " + abilityType );
			
			// TRAITEMENT si plugins/BoomcMMoReward/ABILITIES/abilityType/POWER/EVERY/power.yml exists -> deal with it
			String abilityFolder 	= plugin.getDataFolder() + File.separator + "ABILITIES" + File.separator + abilityType + File.separator;
			String power 			= abilityFolder + "POWER" + File.separator;
			String skills 			= abilityFolder + "SKILLS" + File.separator;
			
			// TRAITEMENT plugins/BoomcMMoReward/ABILITIES/BERSERK/POWER/ONE
			final File powerOne = new File( power + "ONE" + File.separator +  playerPower + ".yml");
			//Log.debug( "powerOne -> " + powerOne );
			if( powerOne.exists() ){
				conf = new Configuration(powerOne);
				if (conf.exists()) {	
					Log.debug("-----New player power : " + playerPower);
					Log.debug("-----Ability reward file to process : " + powerOne);
					conf.load();
					// On ajoute ce cReward à la Queue
					queue.enqueue(new cReward(powerOne.toString(),conf,player,skilltype,playerPower,skillLevel) );				
				}
			}
			
			// TRAITEMENT plugins/BoomcMMoReward/ABILITIES/BERSERK/POWER/EVERY
			final File powerEVERY = new File( power + "EVERY" + File.separator );
			//Log.debug( "powerEVERY -> " + powerEVERY );
			String [] listeFichiers;
			listeFichiers = powerEVERY.list();

			for( String fichier : listeFichiers){
				Log.debug( "Find file in EVERY -> " + fichier );
				int level = Integer.parseInt(fichier.replace(".yml", ""));
				// Si le modulo renvoit 0 alors multiple
				if( (playerPower%level)==0 ){
					// On traite ce fichier
					File everyPowerFile = new File( powerEVERY + File.separator + fichier );	
					//Log.debug( "everyPowerFile -> " + everyPowerFile );			
											
					conf = new Configuration(everyPowerFile);
					if (conf.exists()) {
						Log.debug("-----New player power : " + playerPower);
						Log.debug("-----EVERY POWER reward file to process : " + everyPowerFile);			
						conf.load();
						// On ajoute ce cReward à la Queue
						queue.enqueue(new cReward(everyPowerFile.toString(),conf,player,skilltype,playerPower,skillLevel) );
					}
					
				}
				
			}
			
			// TRAITEMENT plugins/BoomcMMoReward/ABILITIES/BERSERK/SKILLS/skillType/ONE
			final File skillsONE = new File( skills + "ONE" + File.separator +  skillLevel + ".yml");
			//Log.debug( "skillsONE -> " + skillsONE );
			if( skillsONE.exists() ){
				conf = new Configuration(skillsONE);
				if (conf.exists()) {	
					Log.debug("-----New player skill level : " + skillLevel);
					Log.debug("-----Ability reward file to process : " + skillsONE);
					conf.load();
					// On ajoute ce cReward à la Queue
					queue.enqueue(new cReward(skillsONE.toString(),conf,player,skilltype,playerPower,skillLevel) );				
				}
			}
			
			
			
			

			// TRAITEMENT plugins/BoomcMMoReward/ABILITIES/BERSERK/SKILLS/skillType/ONE
			final File skillsEVERY = new File( skills + "EVERY" + File.separator);
			//Log.debug( "skillsEVERY -> " + skillsEVERY );
			listeFichiers = skillsEVERY.list();

			for( String fichier : listeFichiers){

				//Log.debug( "Find file in EVERY -> " + fichier );
				int level = Integer.parseInt(fichier.replace(".yml", ""));
				// Si le modulo renvoit 0 alors multiple
				if( (skillLevel%level)==0 ){
					// On traite ce fichier
					File everySkillFile = new File( skillsEVERY + File.separator + fichier );
					//Log.debug( "everySkillFile -> " + everySkillFile );	
					
					conf = new Configuration(everySkillFile);
					if (conf.exists()) {
						Log.debug("-----New player skill level : " + skillLevel);
						Log.debug("-----EVERY POWER reward file to process : " + everySkillFile);			
						conf.load();
						// On ajoute ce cReward à la Queue
						queue.enqueue(new cReward(everySkillFile.toString(),conf,player,skilltype,playerPower,skillLevel) );
					}
					
				}
				
			}
			

			// On lance la queue
			queue.sendNextReward();
			
		} catch (NoSuchMethodError e) {
			Log.debug( "Your version of mcMMO doesn't support Ability events, NoSuchMethodError -> " + e.getLocalizedMessage() );
		}
	}
	
}