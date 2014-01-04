package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Items extends AbstractReward {

	@SuppressWarnings("unused")
	private Boolean isPending = false;
	private cReward reward;
	private ConfigurationSection confSection;
	private static List<String>listItems;
	
	
	
	public Items() {
		super();
	}

	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardItemException{
		
		
		this.confSection = confSection;
				
		listItems = new ArrayList<String>();

		
		//Items
		if( confSection.contains(Const.ITEM) ) {
			Log.debug("---Items node found on reward file ... processing" );
			giveItems();
		}
		
		// LotteryItems
		if( confSection.contains(Const.ITEM_LOTTERY) ) {
			Log.debug("---lotteryItems node found on reward file ... processing" );
			giveLotteryItems();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LOTTERY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LOTTERY));					
			}
		}
		
		//LuckyItem
		if( confSection.contains(Const.ITEM_LUCKY) ) {
			Log.debug("---luckyItem node found on reward file ... processing" );
			giveLuckyItem();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LUCKY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LUCKY));					
			}
		}
		
		//LuckyKit
		if( confSection.contains(Const.ITEM_LUCKYKIT) ) {
			Log.debug("---luckyKit node found on reward file ... processing" );
			giveLuckyKit();
			// Si il y a section message on la traite
			if( confSection.get(Const.ITEM_LUCKYKIT + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.ITEM_LUCKYKIT));					
			}
		}
		
		return listItems;
		
	}

	
	private void giveItems() throws RewardItemException{
		
		ItemStack item;
		
		List<String> newItems = confSection.getStringList(Const.ITEM);    	
    	for( String p : newItems) {
    		
    		item = processItem(p);    		
    		Log.debug("-Giving item : " + p); 
    		reward.giveItem(item);
    		/*
    		Boolean success = reward.giveItem(item);
    		if( success == false ){
    			// On place en pending
    			this.isPending = true;
    			reward.addPendingItem(item);    			
    		}else{
    			// On stocke en db
    			
    		}
    		*/
    		listItems.add(item.toString());
    	}
    	
		
	}
	
	
	
	private void giveLotteryItems() throws RewardItemException{
		
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
	        			reward.addPendingItem(item);
	        			return;
	        		}
	    			
	        		Log.debug("-Lottery: lucky guy giving item : " + p);
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
	        		
    			} catch (RewardItemException e) {}
        		
    			
    		}else{
    			Log.debug("-Lottery: no luck missed item : " + p); 
    		}

    	}
    	
		
	}
	
	
	private void giveLuckyItem() throws RewardItemException{

		ItemStack item;
		
		// Tirage au hasard d'un item de la liste
		List<String> newItems = confSection.getStringList(Const.ITEM_LUCKY_ITEMS);
		// Tirage au sort du gain
		item = processItem(newItems.get( (int)(Math.random()*newItems.size() ) ) );		
		
		
		Boolean success = reward.giveItem(item);
		if( !success ){
			// On place en pending
			this.isPending = true;
			reward.addPendingItem(item);
			return;
		}
		
		
		Log.debug("-luckyItem: the dice choose item : " + item.toString());	
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
		Log.debug("-luckyKit: the dice choose item : " + kit);	
		// On décompose le kit
		if( kit.contains("|")){
			
			String[] items = kit.split("\\|");
			for( String p : items){
				if( !p.isEmpty()){
					try {						
						Log.debug("- deal with : " + p);
						item = processItem(p);
						Log.debug("- given : " + p);
						
						
						Boolean success = reward.giveItem(item);
						if( !success ){
			    			// On place en pending
			    			this.isPending = true;
			    			reward.addPendingItem(item);
			    			return;
			    		}
						
						// On stocke en db
						listItems.add(item.toString());					
					} catch (RewardItemException e) {
						
						// TODO; exception to catch
						
					}
					
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
	private ItemStack processItem(String p) throws RewardItemException{
		
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
				
				// Avec enchant
    			if( arr.length == 4) {
    				
    				int enchantId = Integer.parseInt(arr[2]); 
		    		int multiplier = Integer.parseInt(arr[3]);
    				try{	    				
	    				item.addEnchantment(Enchantment.getById(enchantId), multiplier);
	    				// On donne l'item avec enchantement
	    				return item;
	    			}catch(Exception ex){	    				
	    				throw new RewardItemException("-Enchant not valid for this item so cancel it : " +	p,ex);
	    			}	
	    			
    			}
				
    			// On donne l'item
    			return item;
    				
    			
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
	
	

	@Override
	protected String variableReplace(String msg) {
		String message = "";		
		// Replace pour les codes couleurs
		message = msg.replace("&", "§");
		// Replace des pseudo variables
		message = message.replace("%items%", Arrays.toString(Items.listItems.toArray()));	
		return message;
	}

	

	public class RewardItemException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardItemException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}


}
