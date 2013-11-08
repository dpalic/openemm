package org.agnitas.target.impl;

import org.agnitas.target.Target;
import org.agnitas.target.TargetFactory;

public class TargetFactoryImpl implements TargetFactory {

	@Override
	public Target newTarget() {
		return new TargetImpl();
	}
}
