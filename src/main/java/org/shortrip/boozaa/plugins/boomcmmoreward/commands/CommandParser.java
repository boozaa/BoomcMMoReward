package org.shortrip.boozaa.plugins.boomcmmoreward.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.Log;
import org.shortrip.boozaa.plugins.boomcmmoreward.commands.CommandFramework.Command;
import org.shortrip.boozaa.plugins.boomcmmoreward.commands.CommandFramework.CommandArgs;
import org.shortrip.boozaa.plugins.boomcmmoreward.commands.CommandFramework.Completer;


public class CommandParser {

	private Plugin plugin;
	
	public CommandParser( Plugin plugin ){
		this.plugin = plugin;
	}
	
	
	@Command( name = "boomcmmoreward", aliases = { "boomcmmoreward" } )
	public void bootreasure(CommandArgs args) {
		args.getSender().sendMessage("This is bootreasure command");
	}
	
	
	@Command( name = "boomcmmoreward.debug", aliases = { "boomcmmoreward.debug" } )
	public void consoleToggleDebug(CommandArgs args) {
		if( !( args.getSender() instanceof Player ) ) {
			if(BoomcMMoReward.getYmlConf().getBoolean("config.debugMode")){				
				BoomcMMoReward.getYmlConf().set("config.debugMode", false);				
				Log.info("Debug mode deactivated");
			}else{
				BoomcMMoReward.getYmlConf().set("config.debugMode", true);
				Log.info("Debug mode activated");
			}		
			// On sauvegarde la modification dans le fichier yml
			BoomcMMoReward.getYmlConf().save();
		}		
	}
	

	/*
	 * Completer for all sub commands of /boomcmmoreward
	 */
	@Completer(name = "boomcmmoreward", aliases = { "boomcmmoreward" })
	public List<String> bootreasureCompleter(CommandArgs args) {
		List<String> list = new ArrayList<String>();
		list.add("debug");
		return list;
	}
	
	
}
