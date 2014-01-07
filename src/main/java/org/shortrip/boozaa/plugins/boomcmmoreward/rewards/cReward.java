package org.shortrip.boozaa.plugins.boomcmmoreward.rewards;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.BukkitTasklauncher;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Configuration;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;
import com.gmail.nossr50.datatypes.skills.SkillType;



public class cReward {

	private String name;
	private Configuration conf = null;	
	private Player player = null;
	private SkillType skill;
	private int playerPower;
	private int skillLevelNow;	
	private Boolean hasConditions 	= false;
	private Boolean hasRewards 		= false;

	private Map<String, List<String> > replacementMap = new HashMap<String, List<String> >();
	
	
	public cReward(String name, Configuration conf, Player player, SkillType skill, int playerPower, int skillLevelNow) {
		
		this.name = name;
		this.skill = skill;
		this.playerPower = playerPower;
		this.skillLevelNow = skillLevelNow;
		this.conf = conf;
		this.player = player;
		// On vérifie les conditions			
		if( conf.getString(Const.CONDITIONS) != null ) {
			this.hasConditions = true;
		}
		// On vérifie les rewards			
		if( conf.getString(Const.REWARDS) != null ) {
			this.hasRewards = true;
		}
		
	}
	
	
	public void addReplacement( String variable, List<String> replacements ){
		this.replacementMap.put(variable,  replacements);
	}
	
	
	public Configuration getConf(){
		return this.conf;
	}
	
	public Player getPlayer(){
		return this.player;
	}


	public String getName(){
		return this.name;
	}
	
	/*
	 * Booleans
	 */
	public Boolean hasConditions(){
		return this.hasConditions;
	}	
	
	public Boolean hasRewards() {
		return hasRewards;		
	}

	
	/*
	 * mcMMO related
	 */
	
	public int getPlayerPower(){
		return com.gmail.nossr50.api.ExperienceAPI.getPowerLevel(this.player);
				
	}
	
	public int getPlayerSkillLevel(SkillType skilltype){
		return com.gmail.nossr50.api.ExperienceAPI.getLevel(this.player, skilltype.name());
		
	}
	
	public int getPlayerSkillLevel(String skilltype){
		if( SkillType.valueOf(skilltype) != null ){
			return com.gmail.nossr50.api.ExperienceAPI.getLevel(this.player, skilltype);
		}		
		return 0;
	}
	
	
	
	/*
	 * Giving rewards
	 * If inventory is full store them on metadata
	 * and recall them on inventory's event
	 */
	public Boolean giveItem(final ItemStack item){
		
		if( item != null ){
			
			int count = 0;
			 for (ItemStack i : this.player.getInventory().getContents()) {
				 if (i == null) {
					 count++;
				 } else if (i.getType() == Material.AIR) {
					 count++;
				 }
			 }
			 if (count == 0) {							 
				 this.player.getWorld().dropItemNaturally(this.player.getEyeLocation(), item);							 
			 } else {
				 this.player.getInventory().addItem(item);
			 }

			return true;
			
		}
		return false;
		
	}
	
	
	public void addPendingItem( ItemStack citems ){		
		// Ici on recoit un item qui ne peut pas être placé dans l'inventaire du joueur
		BoomcMMoReward.getPendingCache().addItemStack(player.getName(), citems);		
	}
	
	
	/*
	 * Messages gestion
	 */
	public void sendMP(final List<String> messages){
		
		BukkitTasklauncher.launchTask( new Runnable() {

			@Override
			public void run() {
				for( String msg : messages) {  	    			
					player.sendMessage( variableReplace(msg) );
				}	
			}
			
		});
		
	}
	
	public void sendBroadcast(final List<String> messages){
		
		BukkitTasklauncher.launchTask( new Runnable() {
			@Override
			public void run() {
				for( String msg : messages) {  	    			
					player.getServer().broadcastMessage( variableReplace( msg ));
				}		
			}			
		});
			
	}

	public void sendLog(final List<String> messages){
		
		BukkitTasklauncher.launchTask( new Runnable() {
			@Override
			public void run() {
				for( String msg : messages) {  	    			
					Log.info( variableReplace( msg ) );
				}	
			}			
		});	
		
	}
	
	
	public void sendCommands(final List<String> commands){
		
		for( String cmd : commands) {
			Log.debug( "-Trying to launch command" + "\n" +  variableReplace(cmd) );
			if( Bukkit.dispatchCommand(Bukkit.getConsoleSender(), variableReplace(cmd) ) ) {
				Log.debug( "-Command sent : " + variableReplace(cmd) );
    		}			
		}
				
	}
	

	public void processMessage( ConfigurationSection section, String parentNode ) {
		if( section.contains(parentNode + "." + Const.MESSAGE_MP) )
			sendMP( variableReplace( section.getStringList(parentNode + "." + Const.MESSAGE_MP) ) );
		if( section.contains(parentNode + "." + Const.MESSAGE_LOG) )
			sendMP( variableReplace( section.getStringList(parentNode + "." + Const.MESSAGE_LOG) ));
		if( section.contains(parentNode + "." + Const.MESSAGE_BROADCAST) )
			sendMP( variableReplace( section.getStringList(parentNode + "." + Const.MESSAGE_BROADCAST) ));
	}
	
	private List<String> variableReplace(List<String> msg){
		
		for( String str : msg ){
			String message = "";		
			// Replace pour les codes couleurs
			message = str.replace("&", "§");
			
			for (Entry<String, List<String> > entry : this.replacementMap.entrySet() ){
				String variable = entry.getKey();
				List<String> replaces = entry.getValue();
				message = message.replace(variable, Arrays.toString(replaces.toArray()));			
			}
			
			
			// Replace des pseudo variables
			message = message.replace("%player%", player.getName());		
			message = message.replace("%power%", Integer.toString(playerPower) );
			message = message.replace("%skillName%", skill.name() );
			message = message.replace("%skillLevel%", Integer.toString(skillLevelNow) );		
			message = message.replace("%XLoc%", Integer.toString(player.getLocation().getBlockX()) );
			message = message.replace("%YLoc%", Integer.toString(player.getLocation().getBlockY()) );
			message = message.replace("%ZLoc%", Integer.toString(player.getLocation().getBlockZ()) );		
			message = message.replace("%worldName%", player.getWorld().getName());
			str = message;
		}
		return msg;
		
	}
	

	
	private String variableReplace(String msg){
		
		String message = "";		
		// Replace pour les codes couleurs
		message = msg.replace("&", "§");

		for (Entry<String, List<String> > entry : this.replacementMap.entrySet() ){
			String variable = entry.getKey();
			List<String> replaces = entry.getValue();
			message = message.replace(variable, Arrays.toString(replaces.toArray()));			
		}
		
		// Variables de ce cReward
		message = message.replace("%player%", player.getName());		
		message = message.replace("%power%", Integer.toString(playerPower) );
		message = message.replace("%skillName%", skill.name() );
		message = message.replace("%skillLevel%", Integer.toString(skillLevelNow) );		
		message = message.replace("%XLoc%", Integer.toString(player.getLocation().getBlockX()) );
		message = message.replace("%YLoc%", Integer.toString(player.getLocation().getBlockY()) );
		message = message.replace("%ZLoc%", Integer.toString(player.getLocation().getBlockZ()) );		
		message = message.replace("%worldName%", player.getWorld().getName());
		
		return message;
		
	}

	
}
