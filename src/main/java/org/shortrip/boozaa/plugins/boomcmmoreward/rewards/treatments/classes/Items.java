package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.ItemException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Items extends Parent implements iConditions {

	@SuppressWarnings("unused")
	private Boolean isPending = false;
	
	
	public Items() {
		super(TreatmentEnum.ITEM);
	}


			/*
			# Enchantment IDs:
			# Enchantments cannot be paired with enchantments in [] brackets
			# ID - MaxLevel - Name
			# Armor
			# 0 - 4 - Protection [1/3/4]
			# 1 - 4 - Fire protection [0/3/4]
			# 3 - 4 - Blast protection [0/1/4]
			# 4 - 4 - Projectile protection [0/1/3]
			## Boots
			# 2 - 4 - Feather falling
			## Helms
			# 5 - 3 - Respiration
			# 6 - 1 - Aqua affinity
			# Tools
			## Swords
			# 16 - 5 - Sharpness [18/17]
			# 17 - 5 - Smite [16/18]
			# 18 - 5 - Bane of arthropods [16/17]
			# 19 - 2 - Knockback
			# 20 - 2 - Fire aspect
			# 21 - 3 - Looting
			## Pickaxes, Axes, Spades - No hoes/rods
			# 32 - 5 - Efficiency
			# 33 - 1 - Silk touch
			# 34 - 3 - Unbreaking
			# 35 - 3 - Fortune
			## Bows
			# 48 - 5 - Power
			# 49 - 2 - Punch
			# 50 - 1 - Flame
			# 51 - 1 - Infinity
			 */

	private cReward reward;
	private ConfigurationSection confSection;
	private static List<String>listItems;
	
	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws ItemException{
		
		this.reward = reward;
		this.confSection = confSection;
				
		listItems = new ArrayList<String>();
		
		
		//Items
		if( confSection.get(Const.ITEM) != null ) {
			BoomcMMoReward.debug("---Items node found on reward file ... processing" );
			giveItems();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM));					
			}
		}
		
		// LotteryItems
		if( confSection.get(Const.ITEM_LOTTERY) != null ) {
			BoomcMMoReward.debug("---lotteryItems node found on reward file ... processing" );
			giveLotteryItems();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LOTTERY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LOTTERY));					
			}
		}
		
		//LuckyItem
		if( confSection.get(Const.ITEM_LUCKY) != null ) {
			BoomcMMoReward.debug("---luckyItem node found on reward file ... processing" );
			giveLuckyItem();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LUCKY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LUCKY));					
			}
		}
		
		//LuckyKit
		if( confSection.get(Const.ITEM_LUCKYKIT) != null ) {
			BoomcMMoReward.debug("---luckyKit node found on reward file ... processing" );
			giveLuckyKit();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LUCKYKIT + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LUCKYKIT));					
			}
		}
		
		
		
		return listItems;
	}

	
	private void giveItems() throws ItemException{
		
		ItemStack item;
		
		@SuppressWarnings("unchecked")
		List<String> newItems = (List<String>)confSection.get(Const.ITEM);    	
    	for( String p : newItems) {
    		item = processItem(p);
    		
    		
    		Boolean success = reward.giveItem(item);
    		if( !success ){
    			// On place en pending
    			this.isPending = true;
    			reward.addPendingItem(this);
    			return;
    		}
    		
    		
    		BoomcMMoReward.debug("-Giving item : " + p); 
    		// On stocke en db
    		listItems.add(item.toString());
    	}
    	
		
	}
	
	
	
	private void giveLotteryItems() throws ItemException{
		
		ItemStack item;
		
		int max = 10;
		int proba = 1;
		
		// Tirage au hasard pour chaque item de la liste
		if( BoomcMMoReward.getYmlConf().get(Const.PLUGIN_DICEFACES) != null ) {
			max = BoomcMMoReward.getYmlConf().getInt(Const.PLUGIN_DICEFACES);
		}
		// On récupere la probabilité
		if( confSection.get(Const.ITEM_LOTTERY_PROBABILITY) != null ) {
			proba = confSection.getInt(Const.ITEM_LOTTERY_PROBABILITY);
			if( proba > max){ proba=max;}
		}

		// Pour chaque item de la liste on lance le dé
		List<String> newItems = confSection.getStringList(Const.ITEM_LOTTERY_ITEMS);    	
    	for( String p : newItems) {
    		
    		// Lance le dé
    		if( launchTheDice(max, proba) ){
    			// Ok gagnant
    			try {
	    			item = processItem(p);
	    			
	    			
	    			Boolean success = reward.giveItem(item);
	    			if( !success ){
	        			// On place en pending
	        			this.isPending = true;
	        			reward.addPendingItem(this);
	        			return;
	        		}
	    			
	        		BoomcMMoReward.debug("-Lottery: lucky guy giving item : " + p);
	        		// On stocke en db
	        		listItems.add(item.toString());	        		
	        		int amount = item.getAmount();
	        		String itemName = item.getType().name() + " x " + amount;	 
	        		
	        		// Si il y a une section messages on la traite
	        		if( confSection.get(Const.ITEM_LOTTERY_MESSAGES) != null ){        			       			
	        			
	        			if( confSection.get(Const.ITEM_LOTTERY_MESSAGES_MP) != null ) {        				
	        				List<String> nouveaux = new ArrayList<String>();
	        				List<String> m = confSection.getStringList(Const.ITEM_LOTTERY_MESSAGES_MP);
	        				for( String s : m ){
	        					// On remplace %item% par l'item gagné
	        					s = s.replace("%item%", itemName);  
	        					nouveaux.add(s);
	        				}
	        				reward.sendMP(nouveaux);        				
	        			}
	        			if( confSection.get(Const.ITEM_LOTTERY_MESSAGES_BROADCAST) != null ) {        				
	        				List<String> nouveaux = new ArrayList<String>();
	        				List<String> m = confSection.getStringList(Const.ITEM_LOTTERY_MESSAGES_BROADCAST);
	        				for( String s : m ){
	        					// On remplace %item% par l'item gagné
	        					s = s.replace("%item%", itemName);
	        					nouveaux.add(s);
	        				}
	        				reward.sendBroadcast(nouveaux);        				
	        			}
	        			if( confSection.get(Const.ITEM_LOTTERY_MESSAGES_LOG) != null ) {        				
	        				List<String> nouveaux = new ArrayList<String>();
	        				List<String> m = confSection.getStringList(Const.ITEM_LOTTERY_MESSAGES_LOG);
	        				for( String s : m ){
	        					// On remplace %item% par l'item gagné
	        					s = s.replace("%item%", itemName );
	        					nouveaux.add(s);
	        				}
	        				reward.sendLog(nouveaux);        				
	        			}
	        		}
	        		
    			} catch (ItemException e) {}
        		
    			
    		}else{
    			BoomcMMoReward.debug("-Lottery: no luck missed item : " + p); 
    		}

    	}
    	
		
	}
	
	
	private void giveLuckyItem() throws ItemException{

		ItemStack item;
		
		// Tirage au hasard d'un item de la liste
		List<String> newItems = confSection.getStringList(Const.ITEM_LUCKY_ITEMS);
		// Tirage au sort du gain
		item = processItem(newItems.get( (int)(Math.random()*newItems.size() ) ) );		
		
		
		Boolean success = reward.giveItem(item);
		if( !success ){
			// On place en pending
			this.isPending = true;
			reward.addPendingItem(this);
			return;
		}
		
		
		BoomcMMoReward.debug("-luckyItem: the dice choose item : " + item.toString());	
		// On stocke en db
		listItems.add(item.toString());
		// On gere les messages
		Messages cmess = new Messages();		
		cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LUCKY));
				
	}
	
	
	private void giveLuckyKit(){
		
		ItemStack item;
		
		// Tirage au hasard d'un item de la liste
		List<String> newItems = confSection.getStringList(Const.ITEM_LUCKYKIT_ITEMS);
		// Tirage au sort du gain		
		String kit = newItems.get( (int)(Math.random()*newItems.size() ) );	
		BoomcMMoReward.debug("-luckyKit: the dice choose item : " + kit);	
		// On décompose le kit
		if( kit.contains("|")){
			
			String[] items = kit.split("\\|");
			for( String p : items){
				if( !p.isEmpty()){
					try {						
						BoomcMMoReward.debug("- deal with : " + p);
						item = processItem(p);
						BoomcMMoReward.debug("- given : " + p);
						
						
						Boolean success = reward.giveItem(item);
						if( !success ){
			    			// On place en pending
			    			this.isPending = true;
			    			reward.addPendingItem(this);
			    			return;
			    		}
						
						// On stocke en db
						listItems.add(item.toString());					
					} catch (ItemException e) {}
				}				
			}
			
		}
		
		// Si il y a une section messages on la traite
		if( confSection.get(Const.ITEM_LUCKYKIT_MESSAGES) != null ){			
			
			if( confSection.get(Const.ITEM_LUCKYKIT_MESSAGES_MP) != null ) {        				
				List<String> nouveaux = new ArrayList<String>();
				List<String> m = confSection.getStringList(Const.ITEM_LUCKYKIT_MESSAGES_MP);
				for( String s : m ){
					// On remplace %item% par l'item gagné
					s = s.replace("%item%", kit);  
					nouveaux.add(s);
				}
				reward.sendMP(nouveaux);        				
			}
			if( confSection.get(Const.ITEM_LUCKYKIT_MESSAGES_BROADCAST) != null ) {        				
				List<String> nouveaux = new ArrayList<String>();
				List<String> m = confSection.getStringList(Const.ITEM_LUCKYKIT_MESSAGES_BROADCAST);
				for( String s : m ){
					// On remplace %item% par l'item gagné
					s = s.replace("%item%", kit);
					nouveaux.add(s);
				}
				reward.sendBroadcast(nouveaux);        				
			}
			if( confSection.get(Const.ITEM_LUCKYKIT_MESSAGES_LOG) != null ) {        				
				List<String> nouveaux = new ArrayList<String>();
				List<String> m = confSection.getStringList(Const.ITEM_LUCKYKIT_MESSAGES_LOG);
				for( String s : m ){
					// On remplace %item% par l'item gagné
					s = s.replace("%item%", kit );
					nouveaux.add(s);
				}
				reward.sendLog(nouveaux);        				
			}
			
			
		}		
		
	}
	
	

	@SuppressWarnings("deprecation")
	private ItemStack processItem(String p) throws ItemException{
		
		ItemStack item;
		
		
		String[] arr = p.split(":");
		
		// Construction itemId/damage:qty:enchantId:multiplier
		if( p.contains(":")) {
			
			if( arr.length >= 2 ){ // Au moins itemId/damage:qty
				
				// Damage
				if( arr[0].contains("/")){					
					
					String[] itemWithDamage = arr[0].split("/");
					
					int itemId = Integer.parseInt(itemWithDamage[0]);
					int damage = Integer.parseInt(itemWithDamage[1]);
					int qty = Integer.parseInt(arr[1]);			
					
					// On prépare l'item tel quel au player en stack
	    			item = new ItemStack(itemId, qty, (short) damage);	    								
				}else{
					// Pas de damage
					int itemId = Integer.parseInt(arr[0]); 
	    			int qty = Integer.parseInt(arr[1]);
	    			
	    			// On prépare l'item tel quel au player en stack
	    			item = new ItemStack(itemId,qty);
	    			
				}
				
				try{
    				// Avec enchant
	    			if( arr.length == 4) {
	    				
	    				int enchantId = Integer.parseInt(arr[2]); 
			    		int multiplier = Integer.parseInt(arr[3]);
	    				try{	    				
		    				item.addEnchantment(Enchantment.getById(enchantId), multiplier);
		    				// On donne l'item avec enchantement
		    				return item;
		    			}catch(Exception ex){	    				
		    				throw new ItemException("-Enchant not valid for this item so cancel it : " +	p,ex);
		    			}	
		    			
	    			}
					
	    			// On donne l'item
	    			return item;
	    			
				}catch(Exception ex){	    				
					throw new ItemException("-Is this item exists ? : " +	p, ex);  
    			}
    			
    						
    			
			}
				
		
		}
		
		// item unique avec Damage ?
		if( p.contains("/")){					
			
			String[] itemWithDamage = p.split("/");
			int itemId = Integer.parseInt(itemWithDamage[0]);
			int damage = Integer.parseInt(itemWithDamage[1]);
			
			// On prépare l'item tel quel au player en stack
			item = new ItemStack(itemId, 1, (short) damage);
			
		}else{
			
			// On prépare l'item tel quel au player en stack
			item = new ItemStack( Integer.parseInt(p) );
			
		}
				
		// On donne l'item tel quel au player
		return item;
		
	}
	

	
	private Boolean launchTheDice(int max, int proba){

		Random rand = new Random();
		int randomNumber = rand.nextInt(max); // un random  dans la limite de max
		if(randomNumber <= proba){			
			return true;
		}
		return false;

	}

	
	
	
		
	
	
	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {
		return true;
	}
	

}
