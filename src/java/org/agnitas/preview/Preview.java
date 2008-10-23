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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package	org.agnitas.preview;

import	java.util.Hashtable;
import	java.util.Enumeration;
import	java.util.ResourceBundle;
import	org.agnitas.backend.MailgunImpl;
import	org.agnitas.util.Log;

public class Preview {
	class PCache {
		class PEntry {
			protected long		timestamp;
			protected Hashtable	cont;
			
			protected PEntry (long nTimestamp, Hashtable nCont) {
				timestamp = nTimestamp;
				cont = nCont;
			}
		}
		private int		maxAge;
		private int		maxEntries;
		private int		size;
		private Hashtable	cache;

		protected PCache (int nMaxAge, int nMaxEntries) {
			maxAge = nMaxAge;
			maxEntries = nMaxEntries;
			size = 0;
			cache = new Hashtable ();
		}
		
		protected void done () {
			cache.clear ();
			size = 0;
		}
		
		protected Hashtable find (long mailingID, long customerID, long now) {
			String		key = mkKey (mailingID, customerID);
			PEntry		ent = (PEntry) cache.get (key);
			Hashtable	rc = null;
			
			if (ent != null) {
				if (ent.timestamp + maxAge < now) {
					cache.remove (ent);
					--size;
				} else {
					rc = ent.cont;
				}
			}
			return rc;
		}
		
		protected void store (long mailingID, long customerID, long now, Hashtable cont) {
			String		key = mkKey (mailingID, customerID);
			PEntry		ent;
			
			while (size + 1 >= maxEntries) {
				PEntry	cur = null;
				
				for (Enumeration e = cache.elements (); e.hasMoreElements (); ) {
					PEntry	chk = (PEntry) e.nextElement ();
					
					if ((cur == null) || (cur.timestamp > chk.timestamp))
						cur = chk;
				}
				if (cur != null) {
					cache.remove (cur);
					--size;
				} else
					break;
			}
			ent = new PEntry (now, cont);
			cache.put (key, ent);
			++size;
		}
		
		private String mkKey (long mailingID, long customerID) {
			return "[" + mailingID + "/" + customerID + "]";
		}
	}
	/** limited list for caching mailings */
	private Cache	mhead, mtail;
	/** max age in seconds for an entry in the cache */
	private int	maxAge;
	/** max number of entries in the cache */
	private int	maxEntries;
	/** current number of entries */
	private int	msize;
	/** cache for generated pages */
	private PCache	pcache;
	/** logger */
	protected Log	log;

	private int atoi (String s, int dflt) {
		int	rc;
		
		if (s == null)
			rc = dflt;
		else
			try {
				rc = Integer.parseInt (s);
			} catch (NumberFormatException e) {
				rc = dflt;
			}
		return rc;
	}

	public Preview () {
		String	age = null;
		String	size = null;
		String	pcage = null;
		String	pcsize = null;
		String	logname = null;
		String	loglevel = null;
		try {
			ResourceBundle  rsc;

			rsc = ResourceBundle.getBundle ("emm");
			if (rsc != null) {
				age = rsc.getString ("preview.mailgun.cache.age");
				size = rsc.getString ("preview.mailgun.cache.size");
				pcage = rsc.getString ("preview.page.cache.age");
				pcsize = rsc.getString ("preview.page.cache.size");
				logname = rsc.getString ("preview.logname");
				loglevel = rsc.getString ("preview.loglevel");
			}
		} catch (Exception e) {
			System.out.println (e.toString ());
		}
		mhead = null;
		mtail = null;
		maxAge = atoi (age, 300);
		maxEntries = atoi (size, 20);
		msize = 0;
		pcache = new PCache (atoi (pcage, 120), atoi (pcsize, 50));

		if (logname == null) {
			logname = "preview";
		}
		int	level;
		if (loglevel == null)
			level = Log.INFO;
		else
			try {
				level = Log.matchLevel (loglevel);
			} catch (NumberFormatException e) {
				level = Log.INFO;
			}
		log = new Log (logname, level);
	}
	
	public void done () {
		Cache	temp;
		
		while (mhead != null) {
			temp = mhead;
			mhead = mhead.next;
			try {
				temp.release ();
			} catch (Exception e) {
				log.out (Log.ERROR, "done", "Failed releasing cache: " + e.toString ());
			}
		}
		mhead = null;
		mtail = null;
		msize = 0;
		pcache.done ();
	}
	
	public int getMaxAge () {
		return maxAge;
	}
	
	public void setMaxAge (int nMaxAge) {
		maxAge = nMaxAge;
	}
	
	public int getMaxEntries () {
		return maxEntries;
	}
	
	public void setMaxEntries (int nMaxEntries) {
		if (nMaxEntries >= 0) {
			maxEntries = nMaxEntries;
			while (msize > maxEntries) {
				Cache	c = pop ();
				
				try {
					c.release ();
				} catch (Exception e) {
					log.out (Log.ERROR, "max", "Failed releasing cache: " + e.toString ());
				}
			}
		}
	}
	
	public Object mkMailgun () throws Exception {
		return new MailgunImpl ();
	}
	
	public Hashtable createPreview (long mailingID, long customerID, boolean cachable) {
		long		now;
		String		error;
		Cache		c;
		Hashtable	rc;

		now = System.currentTimeMillis () / 1000;
		error = null;
		if (cachable) {
			rc = pcache.find (mailingID, customerID, now);
			if (rc == null) {
				for (c = mhead; c != null; c = c.next)
					if (c.mailingID == mailingID)
						break;
				if (c != null) {
					pop (c);
					if (c.ctime + maxAge < now) {
						log.out (Log.VERBOSE, "create", "Found entry for " + mailingID + "/" + customerID + " in cache, but it is expired");
						try {
							c.release ();
						} catch (Exception e) {							;
							log.out (Log.ERROR, "create", "Failed releasing cache: " + e.toString ());
						}
					} else {
						log.out (Log.VERBOSE, "create", "Found entry for " + mailingID + "/" + customerID + " in cache");
						push (c);
					}
				}
				if (c == null) {
					try {
						c = new Cache (mailingID, now, this);
						push (c);
					} catch (Exception e) {
						c = null;
						error = e.toString ();
						log.out (Log.ERROR, "create", "Failed to create new cache entry for " + mailingID + "/" + customerID + ": " + error);
					}
				}
				if (c != null) {
					try {
						rc = c.createPreview (customerID);
					} catch (Exception e) {
						error = e.toString ();
						log.out (Log.ERROR, "create", "Failed to create preview for " + mailingID + "/" + customerID + ": " + error);
					}
					if ((rc != null) && (error == null)) {
						pcache.store (mailingID, customerID, now, rc);
					}
				}
			}
		} else {
			rc = null;
			try {
				c = new Cache (mailingID, now, this);
				rc = c.createPreview (customerID);
				c.release ();
			} catch (Exception e) {
				error = e.toString ();
				log.out (Log.ERROR, "create", "Failed to create uncached preview for " + mailingID + "/" + customerID + ": " + error);
			}
		}
		if (error != null) {
			if (rc == null)
				rc = new Hashtable ();
			if (rc != null)
				rc.put ("__error__", error);
		}
		error = (String) rc.get ("__error__");
		if (error != null)
			log.out (Log.INFO, "create", "Found error for " + mailingID + "/" + customerID + ": " + error);
		return rc;
	}
					
	private Cache pop (Cache c) {
		if (c != null) {
			if (c.next != null) {
				c.next.prev = c.prev;
			} else {
				mtail = c.prev;
			}
			if (c.prev != null) {
				c.prev.next = c.next;
			} else {
				mhead = c.next;
			}
			c.next = null;
			c.prev = null;
			--msize;
		}
		return c;
	}

	private Cache pop () {
		Cache	rc;
		
		rc = mtail;
		if (rc != null) {
			mtail = mtail.prev;
			if (mtail != null) {
				mtail.next = null;
			} else {
				mhead = null;
			}
			--msize;
			rc.next = null;
			rc.prev = null;
		}
		return rc;
	}
	
	private void push (Cache c) {
		if (msize >= maxEntries) {
			Cache	tmp = pop ();
			
			if (tmp != null) {
				try {
					tmp.release ();
				} catch (Exception e) {
					log.out (Log.ERROR, "push", "Failed releasing cache: " + e.toString ());
				}
				--msize;
			}
		}
		c.next = mhead;
		c.prev = null;
		if (mhead != null) {
			mhead.prev = c;
		}
		mhead = c;
		++msize;
	}
	
	public static void main (String[] args) {
		Preview		p = new Preview ();
		Hashtable	h = p.createPreview (5, 4, true);
		
		for (java.util.Enumeration e = h.keys (); e.hasMoreElements (); ) {
			String	key = (String) e.nextElement ();
			String	cont = (String) h.get (key);
			
			System.out.println (key + ":\n" + cont + "\n\n");
		}
		p.done ();
	}
}
