package org.shortrip.boozaa.plugins.boomcmmoreward;

import org.bukkit.Bukkit;


public class BukkitSyncTaskLauncher {

	
	
	public static void launch( Runnable task ){
		
		// Synchrone task with bukkit
		Bukkit.getServer().getScheduler().runTask( BoomcMMoReward.getInstance(), task );
		
	}
	
}
