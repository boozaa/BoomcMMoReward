package org.shortrip.boozaa.plugins.boomcmmoreward.tables.treatments;

import java.util.List;

public class PlayerHistory {

	
	private String playerName;
	private Double amount;
	private List<String> perms;
	private List<String> groups;
	private List<String> items;
	private List<String> commands;
	private long timespan;
	
	
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Double getAmount() {
		return amount;
	}
	public void setPerms(List<String> perms) {
		this.perms = perms;
	}
	public List<String> getPerms() {
		return perms;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public List<String> getItems() {
		return items;
	}
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	public List<String> getCommands() {
		return commands;
	}
	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}
	public long getTimespan() {
		return timespan;
	}
	
	
	
	
	
	
	
}
