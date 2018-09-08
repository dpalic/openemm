/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * String Set that ignores the String case
 * 
 * @author Andreas
 */
public class CaseInsensitiveSet extends HashSet<String> {
	private static final long serialVersionUID = -7971851798927626414L;

	public CaseInsensitiveSet() {
	}
	
	public CaseInsensitiveSet(Collection<? extends String> collection) {
		addAll(collection);
	}
	
	@Override
	public boolean contains(Object object) {
		if (object == null) {
			return super.contains(null);
		} else {
			return super.contains(object.toString().toLowerCase());
		}
	}

	@Override
	public String[] toArray() {
		return (String[]) super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}

	@Override
	public boolean add(String value) {
		if (value == null) {
			return super.add(null);
		} else {
			return super.add(value.toLowerCase());
		}
	}

	@Override
	public boolean remove(Object object) {
		if (object == null) {
			return super.remove(null);
		} else {
			return super.remove(object.toString().toLowerCase());
		}
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if (collection == null) {
			return false;
		} else {
			for (Object object : collection) {
				if (object == null) {
					if (!super.contains(null)) {
						return false;
					}
				} else {
					if (!super.contains(object.toString().toLowerCase())) {
						return false;
					}
				}
			}
			return true;
		}
	}

	@Override
	public boolean addAll(Collection<? extends String> collection) {
		if (collection == null) {
			return false;
		} else {
			boolean returnValue = true;
			for (String value : collection) {
				if (value == null) {
					if (!super.add(null)) {
						returnValue = false;
					}
				} else {
					if (!super.add(value.toString().toLowerCase())) {
						returnValue = false;
					}
				}
			}
			return returnValue;
		}
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		CaseInsensitiveSet removeSet = new CaseInsensitiveSet();
		for (String value : this) {
			boolean contained = false;
			for (Object object : collection) {
				if (value == null && object == null) {
					contained = true;
					break;
				} else if (value != null && value.equals(object.toString().toLowerCase())) {
					contained = true;
					break;
				}
			}
			
			if (!contained) {
				removeSet.add(value);
			}
		}
		return removeAll(removeSet);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		if (collection == null) {
			return false;
		} else {
			for (Object object : collection) {
				if (object == null) {
					if (!super.remove(null)) {
						return false;
					}
				} else {
					if (!super.remove(object.toString().toLowerCase())) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
