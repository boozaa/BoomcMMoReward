package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class GroupException extends BooSystemException {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public GroupException(String type, Throwable cause) {
		super(type, cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- You have a problem with your group's reward");
		BoomcMMoReward.log(Level.WARNING, "- " + cause.getMessage());
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	}

	
	
	
}
