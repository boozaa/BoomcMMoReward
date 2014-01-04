package org.shortrip.boozaa.plugins.boomcmmoreward.exceptions;

import org.shortrip.boozaa.plugins.boomcmmoreward.BoomcMMoReward;

public class BooSystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BooSystemException( String type, Throwable cause ){		
		BoomcMMoReward.getStoreErrors().writeError(type, cause);
	}
	

}
