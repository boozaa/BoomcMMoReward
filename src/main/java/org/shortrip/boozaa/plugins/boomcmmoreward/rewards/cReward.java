package org.shortrip.boozaa.plugins.boomcmmoreward.rewards;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
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
	 * Boolean mcMMO
	 */
	public Boolean isSkillExists(String skill) {
		if( SkillType.valueOf(skill) != null ){
			return true;
		}
		return false;
	}
	
	public Boolean isPowerMinorLimit(int limit){		
		return (getPlayerPower() < limit);		
	}
	
	public Boolean isPowerMajorLimit(int limit) {		
		return (getPlayerPower() > limit);		
	}
	
	public Boolean isSkillLevelMinorLimit(String skill, int limit){		
		if( isSkillExists(skill) ){
			return ( getPlayerSkillLevel(skill) < limit );
		}
		return false;
	}
	
	public Boolean isSkillLevelMajorLimit(String skill, int limit){		
		if( isSkillExists(skill) ){
			return ( getPlayerSkillLevel(skill) > limit );
		}
		return false;
	}
	
	
	/*
	 * Boolean economy
	 */
	public Boolean isMoneyMinorLimit(Double limit) {
		return ( BoomcMMoReward.getEcon().getBalance(player.getName()) < limit );		
	}
	
	public Boolean isMoneyMajorLimit(Double limit) {
		return ( BoomcMMoReward.getEcon().getBalance(player.getName()) > limit );		
	}
	
	
	/*
	 * Boolean permission / group
	 */
	public Boolean isGroupExists(String groupName){		
		for( String g : BoomcMMoReward.getPerms().getGroups() ){
			if( g.equalsIgnoreCase(groupName)){ return true; }
		}
		return false;
	}
	
	
	public Boolean isInGroup(String groupName){
		return ( BoomcMMoReward.getPerms().playerInGroup(player, groupName));		
	}
	
	public Boolean hasPermission(String permission){
		return BoomcMMoReward.getPerms().playerHas(player, permission);
	}
	
	public Boolean hasPermissionInWorld(String permission, String worldName){
		if( player.getServer().getWorld(worldName) != null) {
			return BoomcMMoReward.getPerms().playerHas(player.getServer().getWorld(worldName), player.getName(), permission);
		}	
		return false;
	}
	
	
	/*
	 * Boolean World
	 */
	public Boolean isInWorld(String worldName){
		return player.getWorld().equals( player.getServer().getWorld(worldName) );
	}
	
	public Boolean isInWorld(World world){
		return player.getWorld().equals(world);
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
	
	
	public void giveMoney(final String sender, final Double amount){
		
		// On prends le montant chez le sender
		BoomcMMoReward.getEcon().withdrawPlayer(sender, amount);
		// Que l'on donne au méritant
		BoomcMMoReward.getEcon().depositPlayer(player.getName(), amount);
			
	}
	
	
	/*
	 * Permission gestion
	 */
	public void givePermissionInWorld(String permission, String worldName){		
		// Tentative d'ajout de permission spécifique au Monde
		BoomcMMoReward.getPerms().playerAdd(player.getServer().getWorld(worldName), player.getName(), permission);							
	}
	
	public void givePermission(String permission){			
		// Tentative d'ajout de permission 
		BoomcMMoReward.getPerms().playerAdd(player, permission);
	}
	
	public void removePermissionInWorld(String permission, String worldName){
		// Tentative de retrait de permission dans World
		BoomcMMoReward.getPerms().playerRemove(player.getServer().getWorld(worldName), player.getName(), permission);					
	}
		
	public void removePermission(String permission){
		// Tentative de retrait de permission 
		BoomcMMoReward.getPerms().playerRemove(player, permission);
	}
	
	
	/*
	 * Group gestion
	 */
	public void addToGroupInWorld(String groupName, String worldName) {
		// Tentative d'ajout du player au groupe dans Monde
		BoomcMMoReward.getPerms().playerAddGroup(player.getServer().getWorld(worldName), player.getName(), groupName);	
	}
	
	public void addToGroup(String groupName) {
		// Tentative d'ajout du player au groupe
		BoomcMMoReward.getPerms().playerAddGroup(player, groupName);
	}
		
	public void removeFromGroupInWorld(String groupName, String worldName){
		// Tentative de retrait de player au group dans Monde
		BoomcMMoReward.getPerms().playerRemoveGroup(player.getServer().getWorld(worldName), player.getName(), groupName);
	}
		
	public void removeFromGroup(String groupName){
		// Tentative de retrait de player au group
		BoomcMMoReward.getPerms().playerRemoveGroup(player, groupName);
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
		
		BukkitTasklauncher.launchTask( new Runnable() {
			@Override
			public void run() {
				for( String cmd : commands) {			
					if( Bukkit.dispatchCommand(Bukkit.getConsoleSender(), variableReplace(cmd) ) ) {
						Log.info( "-Command sent : " + variableReplace(cmd) );
		    		}			
				}
			}			
		});	
				
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
