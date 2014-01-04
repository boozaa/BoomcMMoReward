package org.shortrip.boozaa.plugins.boomcmmoreward.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class StoreErrors {

	private final Plugin plugin;
	private final File errorFile;
	
	public StoreErrors( Plugin plugin ){
		this.plugin = plugin;
		errorFile = new File(plugin.getDataFolder() + File.separator + "errors.txt");
		if( !errorFile.exists() ){
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(errorFile, true)));
			    out.println("If you have errors here please reports them on http://dev.bukkit.org/server-mods/boomcmmolvlup/");
			    out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeError(String error, Throwable message){
		String nl = System.getProperty("line.separator");
		// Date d'aujourd'hui
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String mcMMOVersion = "";
        if( Bukkit.getPluginManager().getPlugin("mcMMO") != null){
        	mcMMOVersion = Bukkit.getPluginManager().getPlugin("mcMMO").getDescription().getVersion();
        }else{
        	mcMMOVersion = "There is no mcMMO here !!";
        }
        String vaultVersion = "";
        if( Bukkit.getPluginManager().getPlugin("Vault") != null){
        	vaultVersion = Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion();
        }else{
        	vaultVersion = "Vault is not installed";
        }
        
        
		try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(errorFile, true)));
		    out.println("------------------------------------------------------------------");
		    out.println(sdf.format(today));
		    out.println("Server bukkit Version: " + plugin.getServer().getBukkitVersion());
		    out.println("BoomcMMoReward version: " + plugin.getDescription().getVersion());
		    out.println("mcMMO Version: " + mcMMOVersion);
		    out.println("Vault Version: " + vaultVersion);
		    out.println(nl);
		    out.println("Error occured on " + error);
		    out.println(nl);
		    out.println(message); 
		    out.close();
		} catch (IOException e) {
		    //oh noes!
		}
	}
	
	
}
