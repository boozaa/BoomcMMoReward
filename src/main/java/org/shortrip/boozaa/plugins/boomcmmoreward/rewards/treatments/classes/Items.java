package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Items extends AbstractReward {


	private cReward reward;
	private ConfigurationSection confSection;
	private static List<String>listItems;
	
	
	
	
	public Items() {
		super();
	}

	
	public List<String> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardItemException{
		
		this.reward = reward;
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
    		Log.debug("-Parse item : " + p); 
    		item = processItem(p);    		
    		Log.debug("-Giving item : " + p); 
    		listItems.add(item.toString());
    		reward.giveItem(item);    		
    	}

		// On donne les commandes lancées en variables de remplacement
		reward.addReplacement("%items%", listItems);
		
	}
	
	
	
	private void giveLotteryItems() throws RewardItemException{
		
		ItemStack item;
		
		int max = 10;
		int proba = 1;
		
		// Tirage au hasard pour chaque item de la liste
		if( BoomcMMoReward.getYmlConf().contains(Const.PLUGIN_DICEFACES)  ) {
			max = BoomcMMoReward.getYmlConf().getInt(Const.PLUGIN_DICEFACES);
		}
		// On récupere la probabilité
		if( confSection.contains(Const.ITEM_LOTTERY_PROBABILITY)  ) {
			proba = confSection.getInt(Const.ITEM_LOTTERY_PROBABILITY);
			if( proba > max){ proba=max;}
		}

		// Pour chaque item de la liste on lance le dé
		List<String> newItems = confSection.getStringList(Const.ITEM_LOTTERY_ITEMS);    	
    	for( String p : newItems) {
    		
    		// Lance le dé
    		if( launchTheDice(max, proba) ){
    			
    			item = processItem(p);
    			reward.giveItem(item);
    			
        		Log.debug("-Lottery: lucky guy giving item : " + p);
        		// On stocke en db
        		listItems.add(item.toString());	
        			        		     		
    			
    		}else{
    			Log.debug("-Lottery: no luck missed item : " + p); 
    		}

    	}

		// On donne les commandes lancées en variables de remplacement
		reward.addReplacement("%items%", listItems);
		
	}
	
	
	private void giveLuckyItem() throws RewardItemException{

		ItemStack item;
		
		// Tirage au hasard d'un item de la liste
		List<String> newItems = confSection.getStringList(Const.ITEM_LUCKY_ITEMS);
		// Tirage au sort du gain
		item = processItem(newItems.get( (int)(Math.random()*newItems.size() ) ) );	
		
		reward.giveItem(item);		
		
		Log.debug("-luckyItem: the dice choose item : " + item.toString());	
		// On stocke en db
		listItems.add(item.toString());

		// On donne les commandes lancées en variables de remplacement
		reward.addReplacement("%items%", listItems);
				
	}
	
	
	private void giveLuckyKit() throws RewardItemException{
		
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
									
					Log.debug("- deal with : " + p);
					item = processItem(p);
					Log.debug("- given : " + p);					
					
					reward.giveItem(item);
					
					// On stocke en db
					listItems.add(item.toString());					
									
				}				
			}
			
		}

		// On donne les commandes lancées en variables de remplacement
		reward.addReplacement("%items%", listItems);
		
	}
	
	

	@SuppressWarnings("deprecation")
	private ItemStack processItem(String p) throws RewardItemException {
		
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
	

	
	private boolean launchTheDice(int max, int proba){

		return BoomcMMoReward.RANDOM.nextFloat() < (proba / (float)max);
		/*
		Random rand = new Random();
		int randomNumber = rand.nextInt(max);
		if(randomNumber <= proba){			
			return true;
		}
		return false;
		*/

	}

	
	
	
	@Override
	public boolean isValid(cReward reward, ConfigurationSection confSection) {
		return true;
	}
	

	

	public class RewardItemException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardItemException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	        Log.warning("RewardItemException occured " + t.getCause());
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}


}
