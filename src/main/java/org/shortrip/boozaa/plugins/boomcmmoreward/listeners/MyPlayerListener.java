package org.shortrip.boozaa.plugins.boomcmmoreward.listeners;

import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;
import org.shortrip.boozaa.plugins.boomcmmoreward.PendingItems;


public class MyPlayerListener implements Listener {

	// Le plugin
	@SuppressWarnings("unused")
	private Plugin plugin;
	
	
	public MyPlayerListener(Plugin plugin) {    	
    	this.plugin = plugin; 
    	BoomcMMoReward.debug("MyPlayerListener registered");
    }
	
	@EventHandler
	public void onPlayerInventoryClose(final InventoryCloseEvent event) {
		/*		
		if (event.getInventory().getHolder() instanceof Player){
			// On a le player en question
			Player player = (Player) event.getPlayer();
			player.sendMessage("Check for pending items");
			PendingItems.givePendingItemsToPlayer(player);
		}
		*/
	}
	
	
	
}
