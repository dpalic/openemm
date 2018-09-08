package org.agnitas.emm.core.hashtag;

import org.agnitas.emm.core.hashtag.exception.HashTagException;

/**
 * Interface for any kind of hash tag.
 * 
 * @author md
 */
public interface HashTag {

	/**
	 * Checks, if the implementation can handle the given tag string.
	 * 
	 * @param context {@link HashTagContext}
	 * @param tagString tag string
	 * 
	 * @return true if tag string can be handled, otherwise false
	 */
	public boolean canHandle(HashTagContext context, String tagString);
	
	/**
	 * Handles the tag string.
	 * 
	 * @param context {@link HashTagContext} 
	 * @param tagString tag string
	 * 
	 * @return replacement text for hash tag
	 * 
	 * @throws HashTagException on errors during processing the hash tag
	 */
	public String handle(HashTagContext context, String tagString) throws HashTagException;
}
