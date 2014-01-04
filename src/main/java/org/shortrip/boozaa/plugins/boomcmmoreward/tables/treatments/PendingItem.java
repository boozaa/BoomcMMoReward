package org.shortrip.boozaa.plugins.boomcmmoreward.tables.treatments;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class PendingItem {

	private Player player;
	private List<ItemStack> itemstacks;
	
	
	public PendingItem( Player p ){
		this.player = p;
		this.itemstacks = new ArrayList<ItemStack>();		
	}
	
	public void addItemStack( ItemStack stack ){
		this.itemstacks.add(stack);
		// Stockage metadata
		if( !player.getMetadata("BooPendingItems").isEmpty() ){
			player.setMetadata("BooPendingItems", new FixedMetadataValue(BoomcMMoReward.getInstance(), this));
		}
		// Stockage database
		storeInDatabase(stack);
	}
	
	public void removeItemStack( ItemStack stack ){
		this.itemstacks.remove(stack);
		// Stockage metadata
		if( !player.getMetadata("BooPendingItems").isEmpty() ){
			player.setMetadata("BooPendingItems", new FixedMetadataValue(BoomcMMoReward.getInstance(), this));
		}
		
		// Stockage database
		removeFromDatabase(stack);
	}
	
	private void storeInDatabase(ItemStack stack){
		
	}
	
	private void removeFromDatabase(ItemStack stack){
		
	}
	
}
