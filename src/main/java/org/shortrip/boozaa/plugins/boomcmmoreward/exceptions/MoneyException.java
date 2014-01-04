package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class MoneyException extends BooSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** 
	* 
	*/  
	public MoneyException(String raison, Throwable cause) {
		super("MoneyException", cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- You have a problem with your money's reward");
		BoomcMMoReward.log(Level.WARNING, "- " + raison);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	} 
}
