package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class PermissionException extends BooSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** 
	* 
	*/  
	public PermissionException(String raison, Throwable cause) {
		super("PermissionException", cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- You have a problem with your permissions's reward");
		BoomcMMoReward.log(Level.WARNING, "- " + raison);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	} 
}
