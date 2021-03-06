package org.shortrip.boozaa.plugins.boomcmmoreward.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ItemStack;

/*
*   Generated by Boozaa Bukkit Plugin Generator
*   Use it as you wish but please leave this header
*   I wish you good time develop your plugins
*   contact: boozaa@shortrip.org
*/
public class Cache {

	private HashMap<String, List<ItemStack>> store;

	
	public Cache(){
		store = new HashMap<String, List<ItemStack>>();
	}
	
		
	public void addItemStack(String playername, ItemStack stack) {
		
		List<ItemStack> stackList = null;
		
		if( this.exists(playername) ){
			stackList = this.getItemStacks(playername);
			if( stackList == null ){
				stackList = new ArrayList<ItemStack>();				
			}
		}else{
			stackList = new ArrayList<ItemStack>();	
		}		
		stackList.add(stack);
		this.store.put(playername, stackList);
		
	}
	
	public void addItemStackList(String playername, List<ItemStack> stacks) {
		this.store.put(playername, stacks);		
	}

	/**
	 * Removes the Object with the corresponding Id from the cache.
	 * @param id Reference
	 */
	public void remove(String playername) {
		this.store.remove(playername);
	}
	
	public void removeItemStack(String playername, ItemStack stack){
		List<ItemStack> stackList = this.getItemStacks(playername);
		stackList.remove(stack);
	}
	

	/**
	 *
	 */
	public boolean exists(String id) {
		return this.store.containsKey(id);
	}

	public List<ItemStack> getItemStacks(String playername) {
		return this.store.get(playername);
	}


	public void erase(){
		this.store.clear();
	}
	
	
	
	
	
}
