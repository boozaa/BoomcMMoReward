package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class UnknownError extends BooSystemException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public UnknownError(Throwable cause) {
		super("UnknownError", cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- An unknown problem occured");
		BoomcMMoReward.log(Level.WARNING, "- Please send your errors.txt content on Boo mcMMO Reward dev.bukkit pages");
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	}

}
