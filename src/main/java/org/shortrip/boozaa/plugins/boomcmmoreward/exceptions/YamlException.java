package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import java.util.logging.Level;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class YamlException extends BooSystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public YamlException(String raison, Throwable cause) {
		super("YamlException", cause);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
		BoomcMMoReward.log(Level.WARNING, "- You have a problem with your yaml syntax");
		BoomcMMoReward.log(Level.WARNING, "- " + raison);
		BoomcMMoReward.log(Level.WARNING, "-----------------------------------------------------------------------");
	} 
	
}
