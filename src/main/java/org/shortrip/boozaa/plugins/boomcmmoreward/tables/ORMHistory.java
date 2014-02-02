package org.shortrip.boozaa.plugins.boomcmmoreward.tables;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@ToString
@DatabaseTable(tableName = "history")
public class ORMHistory {

	public static final String REWARDNAME 	= "rewardName";
	public static final String PLAYERNAME 	= "playerName";
	public static final String AMOUNT 		= "amount";
	public static final String PERMS 		= "perms";
	public static final String GROUPS 		= "groups";
	public static final String ITEMS 		= "items";
	public static final String COMMANDS 	= "commands";
	public static final String TIMESPAN 	= "timespan";
	
	@DatabaseField(generatedId = true)
	@Getter private int id;
	
	@DatabaseField(columnName = REWARDNAME, dataType = DataType.LONG_STRING, canBeNull = false)
	@Getter @Setter private String rewardName;	
	@DatabaseField(columnName = PLAYERNAME, canBeNull = false)
	@Getter @Setter private String playerName;	
	@DatabaseField(columnName = AMOUNT, canBeNull = true)
	@Getter @Setter private Double amount;	
	@DatabaseField(columnName = PERMS, dataType = DataType.LONG_STRING, canBeNull = true)
	@Getter @Setter private String perms;	
	@DatabaseField(columnName = GROUPS, dataType = DataType.LONG_STRING, canBeNull = true)
	@Getter @Setter private String groups;	
	@DatabaseField(columnName = ITEMS, dataType = DataType.LONG_STRING, canBeNull = true)
	@Getter @Setter private String items;	
	@DatabaseField(columnName = COMMANDS, dataType = DataType.LONG_STRING, canBeNull = true)
	@Getter @Setter private String commands;	
	@DatabaseField(columnName = TIMESPAN, canBeNull = false)
	@Getter @Setter private long timespan;
	
	
	ORMHistory(){
		
	}
	

	public ORMHistory(String playerName){
		this.playerName = playerName;
	}
	
	
	public void setAmountFromList(List<Double> amount) {
		Double result = 0.0;
		for( Double d : amount) {
			result += d;
		}
		this.setAmount(result);
	}
	
	public void setPermsFromList(List<String> perms) {		
		String result = "";
		for( String s : perms) {
			result += "|" + s;
		}
		this.setPerms(result);
	}
	
	public void setGroupsFromList(List<String> groups) {
		String result = "";
		for( String s : groups) {
			result += "|" + s;
		}
		this.setGroups(result);
	}
	
	public void setItemsFromList(List<String> items) {
		String result = "";
		for( String s : items) {
			result += "|" + s;
		}
		this.setItems(result);
	}
	
	public void setCommandsFromList(List<String> commands) {
		String result = "";
		for( String s : commands) {
			result += "|" + s;
		}
		this.setCommands(result);
	}
	
	
	
	
	

}
