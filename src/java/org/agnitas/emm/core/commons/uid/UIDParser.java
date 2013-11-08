package org.agnitas.emm.core.commons.uid;


public interface UIDParser {

	public abstract UID parseUID(String uidString) throws DeprecatedUIDVersionException;

}