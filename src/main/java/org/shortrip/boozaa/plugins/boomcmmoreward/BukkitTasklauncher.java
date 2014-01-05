package org.shortrip.boozaa.plugins.boomcmmoreward;

import org.bukkit.Bukkit;

public class BukkitTasklauncher {

	
	public static void launchTask( Runnable task ){
		Bukkit.getScheduler().runTask(BoomcMMoReward.getInstance(), task );
	}
	
	public static void launchDelayedTask( Runnable task, long delay ){
		Bukkit.getScheduler().runTaskLater(BoomcMMoReward.getInstance(), task , delay);
	}
	
}
