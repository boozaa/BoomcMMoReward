package org.shortrip.boozaa.plugins.boomcmmoreward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.shortrip.boozaa.plugins.boomcmmoreward.persistence.Cache;


public class PendingItems {

	
	public static void addPendingItemToPlayer( Player player, ItemStack item ){		
		Cache pendingCache = BoomcMMoReward.getPendingCache();
		pendingCache.addItemStack( player.getName(), item);						 
	}
	
	
	public static void givePendingItemsToPlayer( Player player ){
		
		Cache pendingCache = BoomcMMoReward.getPendingCache();
		List<ItemStack> items = pendingCache.getItemStacks(player.getName());
		
		for( ItemStack itemToGive : items ){
			Map<Integer, ItemStack> rest = addItems( player, player.getInventory(), itemToGive );
			if( !rest.isEmpty() ){
				List<ItemStack> itemsToCache = new ArrayList<ItemStack>();
				for( Entry<Integer, ItemStack> entry : rest.entrySet() ){
					int amount = entry.getKey();
					ItemStack stack = entry.getValue();
					stack.setAmount(amount);
					itemsToCache.add(stack);
				}
				// Suppress old cache
				pendingCache.remove(player.getName());
				pendingCache.addItemStackList(player.getName(), itemsToCache);
			}
		}
		
	}


    public static boolean addAllItems(final Player player, final Inventory inventory, final ItemStack... items)
    {
            final Inventory fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
            fakeInventory.setContents(inventory.getContents());
            if (addItems(player, fakeInventory, items).isEmpty())
            {
                    addItems(player, inventory, items);
                    return true;
            }
            return false;
    }

    public static Map<Integer, ItemStack> addItems(final Player player, final Inventory inventory, final ItemStack... items)
    {
            return addOversizedItems(player, inventory, 0, items);
    }

    public static Map<Integer, ItemStack> addOversizedItems(final Player player, final Inventory inventory, final int oversizedStacks, final ItemStack... items)
    {
            
    	Cache pendingCache = BoomcMMoReward.getPendingCache();
    	String playername = player.getName();
    	
    	
    	final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

            /*
             * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' - Record the lastPartial per Material -
             * Cache firstEmpty result
             */

            // combine items

            ItemStack[] combined = new ItemStack[items.length];
            for (int i = 0; i < items.length; i++)
            {
                    if (items[i] == null || items[i].getAmount() < 1)
                    {
                            continue;
                    }
                    for (int j = 0; j < combined.length; j++)
                    {
                            if (combined[j] == null)
                            {
                                    combined[j] = items[i].clone();
                                    break;
                            }
                            if (combined[j].isSimilar(items[i]))
                            {
                                    combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
                                    break;
                            }
                    }
            }


            for (int i = 0; i < combined.length; i++)
            {
                    final ItemStack item = combined[i];
                    if (item == null)
                    {
                            continue;
                    }

                    while (true)
                    {
                            // Do we already have a stack of it?
                            final int maxAmount = oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks : item.getType().getMaxStackSize();
                            final int firstPartial = firstPartial(player, inventory, item, maxAmount);

                            // Drat! no partial stack
                            if (firstPartial == -1)
                            {
                                    // Find a free spot!
                                    final int firstFree = inventory.firstEmpty();

                                    if (firstFree == -1)
                                    {
                                            // No space at all!
                                            leftover.put(i, item);
                                            break;
                                    }
                                    else
                                    {
                                            // More than a single stack!
                                            if (item.getAmount() > maxAmount)
                                            {
                                                    final ItemStack stack = item.clone();
                                                    stack.setAmount(maxAmount);
                                                    inventory.setItem(firstFree, stack);
                                                    item.setAmount(item.getAmount() - maxAmount);
                                                    // Remove from pendingCache
                                                    pendingCache.removeItemStack(playername, stack);
                                            }
                                            else
                                            {
                                                    // Just store it
                                                    inventory.setItem(firstFree, item);
                                                    // Remove from pendingCache
                                                    pendingCache.removeItemStack(playername, item);
                                                    break;
                                            }
                                    }
                            }
                            else
                            {
                                    // So, apparently it might only partially fit, well lets do just that
                                    final ItemStack partialItem = inventory.getItem(firstPartial);

                                    final int amount = item.getAmount();
                                    final int partialAmount = partialItem.getAmount();

                                    // Check if it fully fits
                                    if (amount + partialAmount <= maxAmount)
                                    {
                                            partialItem.setAmount(amount + partialAmount);
                                            break;
                                    }

                                    // It fits partially
                                    partialItem.setAmount(maxAmount);
                                    item.setAmount(amount + partialAmount - maxAmount);
                            }
                    }
            }
            return leftover;
    }
	
	private static int firstPartial(final Player player, final Inventory inventory, final ItemStack item, final int maxAmount)
    {
            if (item == null)
            {
                    return -1;
            }
            final ItemStack[] stacks = inventory.getContents();
            for (int i = 0; i < stacks.length; i++)
            {
                    final ItemStack cItem = stacks[i];
                    if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item))
                    {
                            return i;
                    }
            }
            return -1;
    }
	
	
	
}
