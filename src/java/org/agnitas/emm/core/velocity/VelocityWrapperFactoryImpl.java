package org.agnitas.emm.core.velocity;

import org.agnitas.util.TimeoutLRUMap;

/**
 * Implementation of {@link VelocityWrapperFactory} that caches existing {@link VelocityWrapper}
 * for companies.
 * 
 * @author md
 *
 */
public class VelocityWrapperFactoryImpl implements VelocityWrapperFactory {
	@Override
	public VelocityWrapper getWrapper(int companyId) throws Exception {
		VelocityWrapper wrapper = cache.get( companyId);
		
		if( wrapper == null) {
			wrapper = createVelocityWrapper( companyId);
			
			cache.put( companyId, wrapper);
		}
		
		return wrapper;
	}

	/**
	 * Creates a new {@link VelocityWrapper} instance running in the context of the
	 * given comapny ID.
	 * 
	 * @param companyId company ID
	 * 
	 * @return new {@link VelocityWrapper} instance
	 * 
	 * @throws Exception on errors creating new instance
	 */
	protected VelocityWrapper createVelocityWrapper( int companyId) throws Exception {
		return new VelocityWrapperImpl( companyId, factory);
	}
	
	/**
	 * Returns the factory for Uberspect delegate targets used in this
	 * Velocity wrapper factory.
	 * 
	 * @return factory for Uberspect delegate targets
	 */
	public UberspectDelegateTargetFactory getUberspectDelegateTargetFactory() {
		return this.factory;
	}

	// --------------------------------------------------------------------------- Dependency Injection
	
	/** LRU map caching the VelocityWrapper instances. */
	private TimeoutLRUMap<Integer, VelocityWrapper> cache;
	
	/** Factory for Uberspect delegate targets. */
	private UberspectDelegateTargetFactory factory;

	/**
	 * Sets the LRU map for caching.
	 *  
	 * @param cache LRU map
	 */
	public void setVelocityWrapperCache( TimeoutLRUMap<Integer, VelocityWrapper> cache) {
		this.cache = cache;
	}

	/** 
	 * Set factory for Uberspect delegate targets. 
	 * 
	 * @param factory factory for Uberspect delegate targets
	 */
	public void setUberspectDelegateTargetFactory( UberspectDelegateTargetFactory factory) {
		this.factory = factory;
	}
}
