package org.shortrip.boozaa.plugins.boomcmmoreward.utils;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class Const {

	public final static String PLUGIN_NAME = "[BoomcMMoReward] ";
	public final static String PLUGIN_VERSION = BoomcMMoReward.getYmlConf().getString("config.version");
	public static Boolean PLUGIN_DEBUG = BoomcMMoReward.getYmlConf().getBoolean("config.debugMode");
	public static Boolean PLUGIN_METRICS = BoomcMMoReward.getYmlConf().getBoolean("config.allowMetricsStats");
	public static Boolean PLUGIN_DATABASE = BoomcMMoReward.getYmlConf().getBoolean("config.logInDatabase");
	public static Boolean PLUGIN_INFORM_UPDATE = BoomcMMoReward.getYmlConf().getBoolean("config.informUpdate");
	public final static String PLUGIN_DICEFACES = BoomcMMoReward.getYmlConf().getString("config.diceFaces");
	
	public final static String CONDITIONS = "conditions";
	public final static String REWARDS = "rewards";
	public final static String PERM = "perm";
	public final static String GROUP = "group";
	public final static String WORLD = "world";
	public final static String PROBABILITY = "probability";
	
	public final static String MONEY = "money";
	public final static String AMOUNT = "amount";
	public final static String SENDER = "sender";
	public final static String MONEY_AMOUNT = "money.amount";
	public final static String MONEY_SENDER = "money.sender";
	public final static String MONEY_LOTTERY = "lotteryMoney";
	public final static String MONEY_LOTTERY_AMOUNT = "lotteryMoney.amount";
	public final static String MONEY_LOTTERY_SENDER = "lotteryMoney.sender";
	public final static String ITEM = "item";
	public final static String MESSAGE = "message";
	public final static String MP = "mp";
	public final static String BROADCAST = "broadcast";
	public final static String LOG = "log";
	
	public final static String MESSAGE_MP = "message.mp";
	public final static String MESSAGE_BROADCAST = "message.broadcast";
	public final static String MESSAGE_LOG = "message.log";
	
	public final static String POWER = "power";
	public final static String SKILL = "skill";
	public final static String COMMAND = "command";
	
	public final static String ITEM_LOTTERY = "lotteryItem";
	public final static String ITEM_LOTTERY_ITEMS = "lotteryItem.item";
	public final static String ITEM_LOTTERY_PROBABILITY = "lotteryItem.probability";
	public final static String ITEM_LOTTERY_MESSAGES = "lotteryItem.message";
	public final static String ITEM_LOTTERY_MESSAGES_MP = "lotteryItem.message.mp";
	public final static String ITEM_LOTTERY_MESSAGES_BROADCAST = "lotteryItem.message.broadcast";
	public final static String ITEM_LOTTERY_MESSAGES_LOG = "lotteryItem.message.log";
	
	public final static String ITEM_LUCKY = "luckyItem";
	public final static String ITEM_LUCKY_ITEMS = "luckyItem.item";
	
	public final static String ITEM_LUCKYKIT = "luckyKit";
	public final static String ITEM_LUCKYKIT_ITEMS = "luckyKit.item";
	public final static String ITEM_LUCKYKIT_MESSAGES = "luckyKit.message";
	public final static String ITEM_LUCKYKIT_MESSAGES_MP = "luckyKit.message.mp";
	public final static String ITEM_LUCKYKIT_MESSAGES_BROADCAST = "luckyKit.message.broadcast";
	public final static String ITEM_LUCKYKIT_MESSAGES_LOG = "luckyKit.message.log";
	
}
