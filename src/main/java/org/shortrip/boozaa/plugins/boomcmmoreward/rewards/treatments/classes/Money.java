package org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.configuration.ConfigurationSection;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.MoneyException;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.cReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.Parent;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.TreatmentEnum;
import org.shortrip.boozaa.plugins.boomcmmoreward.rewards.treatments.iConditions;
import org.shortrip.boozaa.plugins.boomcmmoreward.utils.Const;


public class Money extends Parent implements iConditions {

	
	
	public Money() {
		super(TreatmentEnum.MONEY);
	}


	private cReward reward;
	private static List<Double>listMoney;
	
	
	
	public List<Double> proceedRewards(cReward reward, ConfigurationSection confSection, Messages cmess) throws MoneyException{
		
		this.reward = reward;
		
		listMoney = new ArrayList<Double>();
		
		
		// money
		if( confSection.get(Const.MONEY) != null &&  confSection.get(Const.MONEY_AMOUNT) != null ) {
			BoomcMMoReward.debug("---Money node found on reward file ... processing" );
			sendMoney(confSection.getConfigurationSection(Const.MONEY));
			// Si il y a section message elle est traitée dans le sendMoney
		}
		
		// lotteryMoney
		if( confSection.get(Const.MONEY_LOTTERY) != null &&  confSection.get(Const.MONEY_LOTTERY_AMOUNT) != null ) {
			BoomcMMoReward.debug("---lotteryMoney node found on reward file ... processing" );
			giveLotteryMoney(confSection.getConfigurationSection(Const.MONEY_LOTTERY));
			// Si il y a section message elle est traitée dans le giveLotteryMoney
		}
				
		return listMoney;		
		
	}
	
	
	private void giveLotteryMoney(ConfigurationSection confSection){
		
		int max = 10;
		int proba = 1;
		String amount = confSection.getString(Const.AMOUNT);
		
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
			
			// On traite la partie message
			if( confSection.get(Const.MESSAGE) != null ) {
				
				ConfigurationSection msgConf = confSection.getConfigurationSection(Const.MESSAGE);
				
				
				
				if( msgConf.get(Const.MP) != null ) {
					
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.MP);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", amount);
    					nouveaux.add(s);
    				}
    				reward.sendMP(nouveaux); 					
					
				}
				if( msgConf.get(Const.BROADCAST) != null ) {
					
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.BROADCAST);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", amount);
    					nouveaux.add(s);
    				}
    				reward.sendBroadcast(nouveaux); 
					
				}
				if( msgConf.get(Const.LOG) != null ) {
	
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.LOG);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", amount);
    					nouveaux.add(s);
    				}
    				reward.sendLog(nouveaux); 
	
				}
				
			}
			
		}else{
			BoomcMMoReward.debug("-No luck" );
		}
		
		
		
		
	}
	
	
	
	
	private void sendMoney(ConfigurationSection confSection){
		
		try {
			String sender = "server";			
			// Vérification des noeuds
			if( confSection.getString("sender") != null ) {
				sender = confSection.getString(Const.SENDER);
			}				
			// On demande le paiement 
			reward.giveMoney(sender, confSection.getDouble(Const.AMOUNT));
			// On stocke en db
			listMoney.add(confSection.getDouble(Const.AMOUNT));
			BoomcMMoReward.debug("-Give " + confSection.getDouble(Const.AMOUNT) + " from " + sender);
			
			// On traite la partie message
			if( confSection.get(Const.MESSAGE) != null ) {
				
				ConfigurationSection msgConf = confSection.getConfigurationSection(Const.MESSAGE);
				
				
				
				if( msgConf.get(Const.MP) != null ) {
					
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.MP);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", confSection.getString(Const.AMOUNT));
    					s = s.replace("%sender%", confSection.getString(Const.SENDER));
    					nouveaux.add(s);
    				}
    				reward.sendMP(nouveaux); 					
					
				}
				if( msgConf.get(Const.BROADCAST) != null ) {
					
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.BROADCAST);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", confSection.getString(Const.AMOUNT));
    					s = s.replace("%sender%", confSection.getString(Const.SENDER));
    					nouveaux.add(s);
    				}
    				reward.sendBroadcast(nouveaux); 
					
				}
				if( msgConf.get(Const.LOG) != null ) {
	
					List<String> nouveaux = new ArrayList<String>();
					List<String> m = msgConf.getStringList(Const.LOG);
    				for( String s : m ){    					
    					// On remplace %amount% par le montant gagné
    					s = s.replace("%amount%", confSection.getString(Const.AMOUNT));
    					s = s.replace("%sender%", confSection.getString(Const.SENDER));
    					nouveaux.add(s);
    				}
    				reward.sendLog(nouveaux); 
	
				}
				
			}
						
			
		} catch (MoneyException e) { }
		
	}
	
	
	private Boolean launchTheDice(int max, int proba){
		Random rand = new Random();
		int randomNumber = rand.nextInt(max);
		if(randomNumber <= proba){			
			return true;
		}
		return false;
	}
	
	
	
	
	
	@Override
	public Boolean isValid(cReward reward, ConfigurationSection confSection) {

		if(confSection.get(Const.MONEY) != null) {
			
			BoomcMMoReward.debug("---Checking Money conditions"); 
			
			// Vérification
			List<String> moneyConditions = confSection.getStringList(Const.MONEY);    	
	    	for( String p : moneyConditions) {
	    		
	    		try{
	    			
	    			//double account = this.econ.getBalance(player.getName());
	    			Double limit = Double.parseDouble( p.trim().substring(1).trim() );
	    			
	    			if( p.trim().startsWith("-")) {
	    				
	    				BoomcMMoReward.debug("-Testing : < " + limit);	    				
	    				if( !reward.isMoneyMinorLimit(limit) ) {		    				
	    					return false;
		    			}
	    				BoomcMMoReward.debug("-Ok");
	    				
	    			}else if( p.trim().startsWith("+")) {
	    				
	    				BoomcMMoReward.debug("-Testing : > " + limit);	    				
	    				if( !reward.isMoneyMajorLimit(limit) ) {	
	    					return false;
		    			}
	    				BoomcMMoReward.debug("-Ok");
	    				
	    			}else{
	    				BoomcMMoReward.debug("-Not found operator '+' or '-' ... aborting");
	    				return false;
	    			}
	    			
	    			
	    		} catch (Exception e) { }
	    		
	    		return false;
							    		
	    			    		
	    	}
			
		}
		
		// Pas ou plus de conditions donc toutes remplies
		return true;
	}
	

	
	
}