package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class CommandException extends BooSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public CommandException(String raison, Throwable cause) {
		super("CommandException", cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- You have an error with your command's reward");
		BoomcMMoReward.log(Level.WARNING, "- " + raison);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	}

	
}
