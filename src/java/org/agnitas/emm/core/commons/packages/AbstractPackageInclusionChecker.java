package org.agnitas.emm.core.commons.packages;

public abstract class AbstractPackageInclusionChecker implements PackageInclusionChecker {

	@Override
	public boolean includePackage(Package p) {
		return includePackage( p.getName());
	}

}
