package org.shortrip.boozaa.plugins.boomcmmoreward.tables;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity()
@Table(name = "historyTable")
public class HistoryTable {

	@Id
	private int id;
	private String rewardName;
	private String playerName;
	private Double amount;
	private String perms;
	private String groups;
	private String items;
	private String commands;
	private long timespan;
	
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerName() {
		return playerName;
	}
	
	public void setAmount(Double amount){
		this.amount = amount;
	}
	public void setAmountFromList(List<Double> amount) {
		Double result = 0.0;
		for( Double d : amount) {
			result += d;
		}
		this.setAmount(result);
	}
	public Double getAmount() {
		return amount;
	}
	
	
	public void setPerms(String perms){
		this.perms = perms;
	}
	public void setPermsFromList(List<String> perms) {		
		String result = "";
		for( String s : perms) {
			result += "|" + s;
		}
		this.setPerms(result);
	}
	public String getPerms() {
		return perms;
	}
	
	
	public void setGroups(String groups){
		this.groups = groups;
	}
	public void setGroupsFromList(List<String> groups) {
		String result = "";
		for( String s : groups) {
			result += "|" + s;
		}
		this.setGroups(result);
	}
	public String getGroups() {
		return groups;
	}
	
	
	public void setItems(String items){
		this.items = items;
	}
	public void setItemsFromList(List<String> items) {
		String result = "";
		for( String s : items) {
			result += "|" + s;
		}
		this.setItems(result);
	}
	public String getItems() {
		return items;
	}
	
	
	public void setCommands(String commands){
		this.commands = commands;
	}
	public void setCommandsFromList(List<String> commands) {
		String result = "";
		for( String s : commands) {
			result += "|" + s;
		}
		this.setCommands(result);
	}
	public String getCommands() {
		return commands;
	}
	
	
	
	public void setTimespan(long timespan) {
		this.timespan = timespan;
	}
	public long getTimespan() {
		return timespan;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	public String getRewardName() {
		return rewardName;
	}
	
	
	public void save(){
		
		
		
	}
	
}
