package org.agnitas.emm.core.commons.packages;

public interface PackageInclusionChecker {
	
	public boolean includePackage( Package p);
	public boolean includePackage( String packageName);
	
}
