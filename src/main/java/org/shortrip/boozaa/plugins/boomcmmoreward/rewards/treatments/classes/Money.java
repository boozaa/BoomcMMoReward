package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Money extends AbstractReward {


	private cReward reward;
	private List<Double>listMoney;
	
	
	public Money() {
		super();
	}
	
	
	public List<Double> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws RewardMoneyException{
		
		listMoney = new ArrayList<Double>();

		this.reward = reward;
		
		if( !BoomcMMoReward.isVaultEnabled() ){
			Log.debug("---Vault is required to do that" );
			return listMoney;
		}
		
		// money
		if( confSection.contains(Const.MONEY) &&  confSection.contains(Const.MONEY_AMOUNT) ) {
			Log.debug("---Money node found on reward file ... processing" );
			sendMoney(confSection.getConfigurationSection(Const.MONEY));
			// Si il y a section message on la traite
			if( confSection.get(Const.MONEY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.MONEY));					
			}
		}
		
		// lotteryMoney
		if( confSection.contains(Const.MONEY_LOTTERY)  &&  confSection.contains(Const.MONEY_LOTTERY_AMOUNT) ) {
			Log.debug("---lotteryMoney node found on reward file ... processing" );
			giveLotteryMoney(confSection.getConfigurationSection(Const.MONEY_LOTTERY));
			// Si il y a section message on la traite
			if( confSection.get(Const.MONEY_LOTTERY + "." + Const.MESSAGE) != null ) {
				cmess.proceedRewards(reward, confSection.getConfigurationSection(Const.MONEY_LOTTERY));					
			}
		}
				
		return listMoney;		
		
	}
	
	
	private void giveLotteryMoney(ConfigurationSection confSection) throws RewardMoneyException{
		
		int max = 10;
		int proba = 1;
		
		// Tirage au hasard pour chaque item de la liste
		if( BoomcMMoReward.getYmlConf().get(Const.PLUGIN_DICEFACES) != null ) {
			max = BoomcMMoReward.getYmlConf().getInt(Const.PLUGIN_DICEFACES);
		}
		// On récupere la probabilité
		if( confSection.get(Const.PROBABILITY) != null ) {
			proba = confSection.getInt(Const.PROBABILITY);
			if( proba > max){ proba=max;}
		}
		
		// Lance le dé
		if( launchTheDice(max, proba) ){	
			// Gagnant on envoit les ronds
			sendMoney(confSection);		
		}else{
			Log.debug("-No luck" );
		}
				
	}
	
	
	
	
	private void sendMoney(ConfigurationSection confSection) throws RewardMoneyException {
		
		try {
			String sender = "server";			
			// Vérification des noeuds
			if( confSection.getString("sender") != null ) {
				sender = confSection.getString(Const.SENDER);
				List<String> s = new ArrayList<String>();
				s.add(sender);
				reward.addReplacement("%sender%", s);
			}				
			// On demande le paiement 
			giveMoney(sender, confSection.getDouble(Const.AMOUNT));
			// On stocke en db
			listMoney.add(confSection.getDouble(Const.AMOUNT));
			Log.debug("-Give " + confSection.getDouble(Const.AMOUNT) + " from " + sender);

			// On donne les commandes lancées en variables de remplacement
			List<String> a = new ArrayList<String>();
			a.add(confSection.getString(Const.AMOUNT));
			reward.addReplacement("%amount%", a);			
			
		} catch (Exception e) { 
			throw new RewardMoneyException("Money transfer  exception", e);
		}
		
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
	public boolean isValid(cReward reward, ConfigurationSection confSection) throws RewardMoneyException {

		if(confSection.get(Const.MONEY) != null) {
			
			try {
				Log.debug("---Checking Money conditions"); 
				
				// Vérification
				List<String> moneyConditions = confSection.getStringList(Const.MONEY);    	
		    	for( String p : moneyConditions) {
		    		
	    			//double account = this.econ.getBalance(player.getName());
	    			Double limit = Double.parseDouble( p.trim().substring(1).trim() );
	    			
	    			if( p.trim().startsWith("-")) {
	    				
	    				Log.debug("-Testing : < " + limit);	    				
	    				if( !isMoneyMinorLimit(limit) ) {		    				
	    					return false;
		    			}
	    				Log.debug("-Ok");
	    				
	    			}else if( p.trim().startsWith("+")) {
	    				
	    				Log.debug("-Testing : > " + limit);	    				
	    				if( !isMoneyMajorLimit(limit) ) {	
	    					return false;
		    			}
	    				Log.debug("-Ok");
	    				
	    			}else{
	    				Log.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
		    		
		    		return false;
							   		    			    		
		    	}
			} catch (Exception e) { 
				throw new RewardMoneyException("Money transfer  exception", e);
			}
		}
		
		// Pas ou plus de conditions donc toutes remplies
		return true;
	}

	
	private boolean isMoneyMinorLimit(Double limit) {
		return ( BoomcMMoReward.getEcon().getBalance( this.reward.getPlayer().getName() ) < limit );		
	}
	
	private boolean isMoneyMajorLimit(Double limit) {
		return ( BoomcMMoReward.getEcon().getBalance( this.reward.getPlayer().getName() ) > limit );		
	}
	
	
	private void giveMoney(final String sender, final Double amount){		
		// On prends le montant chez le sender
		BoomcMMoReward.getEcon().withdrawPlayer(sender, amount);
		// Que l'on donne au méritant
		BoomcMMoReward.getEcon().depositPlayer(this.reward.getPlayer().getName(), amount);			
	}


	public class RewardMoneyException extends Exception {
		private static final long serialVersionUID = 1L;
		private Throwable throwable;
		public RewardMoneyException(String message, Throwable t) {
	        super(message);
	        this.throwable = t;
	    }	
		public Throwable get_Throwable(){
			return this.throwable;
		}
	}
	
	
}
