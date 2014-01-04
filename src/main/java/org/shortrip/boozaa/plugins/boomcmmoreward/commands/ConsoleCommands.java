package org.shortrip.boozaa.plugins.boomcmmoreward.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.exceptions.CommandException;
import org.shortrip.boozaa.plugins.boomcmmoreward.tables.HistoryTable;

public class ConsoleCommands {
	
	public ConsoleCommands(String[] args) throws CommandException {
		// Pas d'arguments suffisants on sort
		if( args.length < 1 ){return;}
		
		String cmd = args[0];
		String toggleDebug = "debug";
		String database = "db";
		
		
		
		// ToggleDebug
		if(cmd.equalsIgnoreCase(toggleDebug)){
			if(BoomcMMoReward.getYmlConf().getBoolean("config.debugMode")){				
				BoomcMMoReward.getYmlConf().set("config.debugMode", false);				
				BoomcMMoReward.log(Level.INFO, "Debug mode deactivated");				
			}else{
				BoomcMMoReward.getYmlConf().set("config.debugMode", true);
				BoomcMMoReward.log(Level.INFO, "Debug mode activated");	
			}		
			// On sauvegarde la modification dans le fichier yml
			BoomcMMoReward.getYmlConf().save();
		}	
		
		// database
		if(cmd.equalsIgnoreCase(database)){
			
			if( args.length < 2 ){
				
				if(BoomcMMoReward.getYmlConf().getBoolean("config.logInDatabase")){				
					BoomcMMoReward.getYmlConf().set("config.logInDatabase", false);				
					BoomcMMoReward.log(Level.INFO, "Stop storing history in database");				
				}else{
					BoomcMMoReward.getYmlConf().set("config.logInDatabase", true);
					BoomcMMoReward.log(Level.INFO, "Store history in database");	
				}		
				// On sauvegarde la modification dans le fichier yml
				BoomcMMoReward.getYmlConf().save();
				
			}else{
						
				
				String subCommand = args[1];
				String get = "get";
				
				if(subCommand.equalsIgnoreCase(get)){
					
					String player = args[2];
					List<HistoryTable> histoire;
					histoire = BoomcMMoReward.getDB().findhistory(player);
					
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					Date dt;
					for( HistoryTable h : histoire ){
						dt = new Date(h.getTimespan());
						BoomcMMoReward.log(Level.INFO, "----- " +sdf.format(dt) + " - [" + player + "]" );
						BoomcMMoReward.log(Level.INFO, "Reward file : " + h.getRewardName() );
						if( h.getAmount() != 0.0 ){
							BoomcMMoReward.log(Level.INFO, "Money" );
							BoomcMMoReward.log(Level.INFO, "  " + h.getAmount().toString() );
						}
						if( h.getItems() != null ){
							
							BoomcMMoReward.log(Level.INFO, "Items" );
							String[] items = h.getItems().split("\\|");
							for(String item : items){
								if( !item.isEmpty()){
									BoomcMMoReward.log(Level.INFO, "  " + item.replace("ItemStack{", "").replace("}", "") );
								}
								
							}
							
							
						}
						if( h.getPerms() != null ){
							BoomcMMoReward.log(Level.INFO, "Perms" );
							String[] items = h.getPerms().split("\\|");
							for(String item : items){
								if( !item.isEmpty()){
									BoomcMMoReward.log(Level.INFO, "  " + item );
								}							
							}
							
						}
						if( h.getGroups() != null ){
							BoomcMMoReward.log(Level.INFO, "Groups" );
							String[] items = h.getGroups().split("\\|");
							for(String item : items){
								if( !item.isEmpty()){
									BoomcMMoReward.log(Level.INFO, "  " + item );
								}
							}
						}
						if( h.getCommands() != null ){
							BoomcMMoReward.log(Level.INFO, "Commands" );
							String[] items = h.getCommands().split("\\|");
							for(String item : items){
								if( !item.isEmpty()){
									BoomcMMoReward.log(Level.INFO, "  " + item );
								}
							}
							
						}
						BoomcMMoReward.log(Level.INFO, "");
					}
					
				}
				
			}
			
			
			
		}
		
		
		
	}
	

	
	
}
