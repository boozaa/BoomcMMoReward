package org.shortrip.boozaa.plugins.boomcmmoreward.rewards;

import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.BukkitTasksLauncher;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.GroupException;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.MoneyException;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.PermissionException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes.Items;
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
		
		// Synchrone task with bukkit
		BukkitTasksLauncher.launch(
				new Runnable() {					
					
					@Override
					public void run() {	
						
						PlayerInventory inv = player.getInventory();		
						
						 int count = 0;
						 for (ItemStack i : inv.getContents()) {
							 if (i == null) {
								 count++;
							 } else if (i.getType() == Material.AIR) {
								 count++;
							 }
						 }
						 if (count == 0) {							 
							 player.getWorld().dropItemNaturally(player.getEyeLocation(), item);							 
						 } else {
							 inv.addItem(item);
						 }
						
					}
					/*
					@Override
					public void run() {						
						PlayerInventory inv = player.getInventory();		
						
						 int count = 0;
						 for (ItemStack i : inv.getContents()) {
							 if (i == null) {
								 count++;
							 } else if (i.getType() == Material.AIR) {
								 count++;
							 }
						 }
						 if (count == 0) {
							 
							 player.sendMessage("Store item because your inventory is full " + item.toString());
							 PendingItems.addPendingItemToPlayer(player, item);							
							 //player.getWorld().dropItemNaturally(player.getEyeLocation(), item);
							 
						 } else {
							 inv.addItem(item);
						 }
											
					}	
					*/
				}
		);		

		return true;
		
		
		/*
		// Si inventory full
		
		// Si l'inventaire contient deja un itemstack de ce typeId
		if( inv.contains(item)){
			// On vérifie si y a de la place pour le stacker
			int maxsize = item.getMaxStackSize();
			int itemsize = item.getAmount();
			
			Iterator<ItemStack> itr = inv.iterator();
			while(itr.hasNext()) {
				
				ItemStack slot = itr.next();
				
				// Si on trouve un slot avec item de même type
				if( slot.getTypeId() == item.getTypeId() ){
									
					int slotsize = itr.next().getAmount();
					// Si le contenu du slot + les items à ajouter ne dépasse pas le max stack
					if( (slotsize + itemsize) <= maxsize ){
						// On ajoute ces items au stack deja présent
						slot.setAmount(slotsize + itemsize);
						return true;
					}
					// Autrement on doit ajouter et mettre le reste dans un autre slot
					int libredansslot = maxsize - slotsize;
					slot.setAmount(maxsize);
					// Là le slot est full on doit mettre le reste dans un autre slot
					int restant = itemsize - libredansslot;
					// On fixe la nouvelle quantité à stocker
					item.setAmount(restant);
					// Si il y a un slot de libre					
					if( inv.firstEmpty() != -1 ){
						// On ajoute les items restant ici
						inv.addItem(item);
						return true;
					}else{
						// Ici on a plus de slot disponible
						FixedMetadataValue metadata = new FixedMetadataValue(BoomcMMoReward.getInstance(), item);
						// On accroche la metadata au joueur pour lui donner lorsqu'un slot sera disponible
						player.setMetadata("BoomcMMoRewardPendingItem", metadata);
						return true;
					}
					
				}
				
			}
			
		}
		return true;
		*/
	}
	
	
	public void addPendingItem( Items citems ){
		
		// Ici on recoit un item qui ne peut pas être placé dans l'inventaire du joueur
		player.setMetadata("BoomcMMoRewardItem", new FixedMetadataValue(BoomcMMoReward.getInstance(), citems));
		
	}
	
	
	public void giveMoney(final String sender, final Double amount) throws MoneyException{
		try{			
			
			// Synchrone task with bukkit
			BukkitTasksLauncher.launch(
					new Runnable() {					
						@Override
						public void run() {						
							// On prends le montant chez le sender
							BoomcMMoReward.getEcon().withdrawPlayer(sender, amount);
							// Que l'on donne au méritant
							BoomcMMoReward.getEcon().depositPlayer(player.getName(), amount);							
						}					
					}
			);	
			
			
		}catch(Exception ex){
			throw new MoneyException("cReward Economy problem ... abort operation",ex);
		}		
	}
	
	
	/*
	 * Permission gestion
	 */
	public void givePermissionInWorld(String permission, String worldName) throws PermissionException{
		try{			
			// Tentative d'ajout de permission spécifique au Monde
			BoomcMMoReward.getPerms().playerAdd(player.getServer().getWorld(worldName), player.getName(), permission);			
		}catch(Exception ex){
			throw new PermissionException("cReward adding Permission in World problem ... abort operation",ex);
		}			
	}
	
	public void givePermission(String permission) throws PermissionException{
		try{			
			// Tentative d'ajout de permission 
			BoomcMMoReward.getPerms().playerAdd(player, permission);			
		}catch(Exception ex){
			throw new PermissionException("cReward adding Permission problem ... abort operation",ex);
		}			
	}
	
	public void removePermissionInWorld(String permission, String worldName) throws PermissionException{
		try{			
			// Tentative de retrait de permission dans World
			BoomcMMoReward.getPerms().playerRemove(player.getServer().getWorld(worldName), player.getName(), permission);			
		}catch(Exception ex){
			throw new PermissionException("cReward removing Permission in World problem ... abort operation",ex);
		}			
	}
		
	public void removePermission(String permission) throws PermissionException{
		try{			
			// Tentative de retrait de permission 
			BoomcMMoReward.getPerms().playerRemove(player, permission);			
		}catch(Exception ex){
			throw new PermissionException("cReward removing Permission problem ... abort operation",ex);
		}			
	}
	
	
	/*
	 * Group gestion
	 */
	public void addToGroupInWorld(String groupName, String worldName) throws GroupException {
		try{
			// Tentative d'ajout du player au groupe dans Monde
			BoomcMMoReward.getPerms().playerAddGroup(player.getServer().getWorld(worldName), player.getName(), groupName);
		}catch(Exception ex){
			throw new GroupException("cReward adding Group in World problem ... abort operation",ex);
		}	
	}
	
	public void addToGroup(String groupName) throws GroupException {
		try{
			// Tentative d'ajout du player au groupe
			BoomcMMoReward.getPerms().playerAddGroup(player, groupName);
		}catch(Exception ex){
			throw new GroupException("cReward adding Group problem ... abort operation",ex);
		}	
	}
		
	public void removeFromGroupInWorld(String groupName, String worldName) throws GroupException{
		try{
			// Tentative de retrait de player au group dans Monde
			BoomcMMoReward.getPerms().playerRemoveGroup(player.getServer().getWorld(worldName), player.getName(), groupName);
		}catch(Exception ex){
			throw new GroupException("cReward removing Group in World problem ... abort operation",ex);
		}
	}
		
	public void removeFromGroup(String groupName) throws GroupException{
		try{
			// Tentative de retrait de player au group
			BoomcMMoReward.getPerms().playerRemoveGroup(player, groupName);
		}catch(Exception ex){
			throw new GroupException("cReward removing Group problem ... abort operation",ex);
		}
	}
	
	
	/*
	 * Messages gestion
	 */
	public void sendMP(final List<String> messages){
		// Synchrone task with bukkit
		BukkitTasksLauncher.launch(
				new Runnable() {					
					@Override
					public void run() {						
						for( String msg : messages) {  	    			
							player.sendMessage( variableReplace(msg) );
						}									
					}					
				}
		);		
		
	}
	
	public void sendBroadcast(final List<String> messages){
		// Synchrone task with bukkit
		BukkitTasksLauncher.launch(
				new Runnable() {					
					@Override
					public void run() {						
						for( String msg : messages) {  	    			
							player.getServer().broadcastMessage( variableReplace( msg ));
						}									
					}					
				}
		);		
	}

	public void sendLog(final List<String> messages){
		// Synchrone task with bukkit
		BukkitTasksLauncher.launch(
				new Runnable() {					
					@Override
					public void run() {						
						for( String msg : messages) {  	    			
							BoomcMMoReward.log(Level.INFO, variableReplace( msg ) );
						}									
					}					
				}
		);		
	}
	
	
	public void sendCommands(final List<String> commands){
		// Synchrone task with bukkit
		BukkitTasksLauncher.launch(
				new Runnable() {					
					@Override
					public void run() {						
						for( String cmd : commands) {			
							if( Bukkit.dispatchCommand(Bukkit.getConsoleSender(), variableReplace(cmd) ) ) {
								BoomcMMoReward.log(Level.INFO, "-Command sent : " + variableReplace(cmd) );
				    		}			
						}										
					}					
				}
		);			
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
		// Replace des pseudo variables
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
