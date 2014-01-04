package org.shortrip.boozaa.plugins.boomcmmoreward;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;


public class Log {

	private final static String prefix = "[BoomcMMoReward] ";
	// Logger
	private static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private static File errorFile;
	
	
	public static void info(String message) {
		console.sendMessage(prefix + "- " + message);		
	}
	
	public static void warning(String message) {
		console.sendMessage(prefix + "- WARNING - " +  message);
	}
	
	public static void error(String message) {
		console.sendMessage(prefix + "- ERROR - " + message);
	}
	
	public static void severe(String error, Throwable message) {
		console.sendMessage(prefix + ChatColor.RED + "- SEVERE - Fatal error, the plugin must be disabled: " + message.getMessage());
		writeError(error, message);
		Bukkit.getPluginManager().disablePlugin(BoomcMMoReward.getInstance());
	}
	
	// Debug si activé
	public static void debug(String message) {
		
		if( BoomcMMoReward.getYmlConf().getBoolean("config.debugMode") ) {		
			console.sendMessage(prefix + "- DEBUG - " + ChatColor.GREEN + message);
		}
		
	}


	private static void writeError(String error, Throwable message){
		try {
		
			errorFile = new File( "plugins"  + File.separator + "BooTreasure" + File.separator + "errors.txt");
			
			if( !errorFile.exists() )
				errorFile.createNewFile();
			
			PrintStream ps = new PrintStream( new FileOutputStream(errorFile, true) );
			
			Date today = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        	        
	        String vaultVersion = "";
	        if( Bukkit.getPluginManager().getPlugin("Vault") != null){
	        	vaultVersion = Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion();
	        }else{
	        	vaultVersion = "Vault is not installed";
	        }
	        
	        ps.print( "\n" );
	        ps.print( "------------------------------------------------------------------\n" );
	        ps.print( sdf.format(today) + "\n" );
	        ps.print( "Server bukkit Version: " + Bukkit.getServer().getBukkitVersion() + "\n" );
	        ps.print( BoomcMMoReward.getInstance().getName() + " version: " + BoomcMMoReward.getInstance().getDescription().getVersion() + "\n" );
	        ps.print( "Vault Version: " + vaultVersion + "\n" + "\n" + "\n" );
	        ps.print( "Error occured on " + error + "\n" + "\n" );
	        message.printStackTrace(ps);
	        ps.print( "\n" );
	        
	        ps.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
