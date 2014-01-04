package org.shortrip.boozaa.plugins.boomcmmoreward;

import org.bukkit.Bukkit;


public class BukkitTasksLauncher {

	
	
	public static void launch( Runnable task ){		
		// Synchrone task with bukkit
		Bukkit.getServer().getScheduler().runTask( BoomcMMoReward.getInstance(), task );		
	}
	
	public static void launchDelayed( Runnable task, long delay ){
		Bukkit.getServer().getScheduler().runTaskLater(BoomcMMoReward.getInstance(), task, delay);
	}
	
}
