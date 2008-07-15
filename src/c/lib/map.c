/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/
/** @file map.c
 * Handles hash collections.
 */
# include	<stdlib.h>
# include	<ctype.h>
# include	"agn.h"

/** Calculates hash value.
 * @param str the string to calculate the hash for
 * @return the hash code
 */
static hash_t
hasher (const char *str) /*{{{*/
{
	hash_t	hash = 0;
	
	while (*str) {
		hash *= 119;
		hash |= (unsigned char) *str++;
	}
	return hash;
}/*}}}*/
/** Find useful hashsize.
 * Taken the amount of nodes, a "good" value for the size
 * of the hash array is searched
 * @param size the number of nodes in the collection
 * @return the proposed size of the hash array
 */
static int
find_hash_size (int size) /*{{{*/
{
	int	htab[] = {
		113,
		311,
		733,
		1601,
		3313,
		5113,
		8677,
		13121,
		25457,
		50021,
		99607
	};
	int	n;
	int	hsize;
	
	size >>= 2;
	hsize = htab[0];
	for (n = 0; n < sizeof (htab) / sizeof (htab[0]); ++n)
		if (htab[n] >= size) {
			hsize = htab[n];
			break;
		}
	return hsize;
}/*}}}*/
/** Creates the node key.
 * According to flag on howto compare keys, the main key
 * is generated here
 * @param m the map
 * @param key the key to modify
 * @return the main key on success, NULL otherwise
 */
static char *
mkmkey (map_t *m, const char *key) /*{{{*/
{
	char	*mkey;
	
	if (m -> icase) {
		if (mkey = malloc (strlen (key) + 1)) {
			int	n;
			
			for (n = 0; key[n]; ++n)
				if (isupper ((int) ((unsigned char) key[n])))
					mkey[n] = tolower (key[n]);
				else
					mkey[n] = key[n];
			mkey[n] = '\0';
		}
	} else
		mkey = (char *) key;
	return mkey;
}/*}}}*/
/** Find node in map.
 * @param m the map
 * @param key the key to search for
 * @param hash its hashvalue
 * @param prv store the previous node in sibling chain here, if *prv not NULL
 * @return the node on success, NULL otherwise
 */
static node_t *
locate (map_t *m, const char *key, hash_t hash, node_t **prv) /*{{{*/
{
	node_t	*n;
	
	if (prv)
		*prv = NULL;
	for (n = m -> cont[hash % m -> hsize]; n; n = n -> next)
		if ((n -> hash == hash) && (n -> mkey[0] == key[0]) && (! strcmp (n -> mkey, key)))
			break;
		else if (prv)
			*prv = n;
	return n;
}/*}}}*/
/** Allocate a map.
 * @param icase if true, keys are treated ignoring case
 * @param aproxsize the aprox. number of nodes in this map
 * @return the new map on success, NULL otherwise
 */
map_t *
map_alloc (bool_t icase, int aproxsize) /*{{{*/
{
	map_t	*m;
	
	if (m = (map_t *) malloc (sizeof (map_t))) {
		m -> icase = icase;
		m -> hsize = find_hash_size (aproxsize);
		if (m -> cont = (node_t **) malloc (m -> hsize * sizeof (node_t *))) {
			int	n;
			
			for (n = 0; n < m -> hsize; ++n)
				m -> cont[n] = NULL;
		} else {
			free (m);
			m = NULL;
		}
	}
	return m;
}/*}}}*/
/** Free map.
 * Returns all allocated memory used by this map an its nodes
 * @param m the map to free
 * @return NULL
 */
map_t *
map_free (map_t *m) /*{{{*/
{
	if (m) {
		if (m -> cont) {
			int	n;
			
			for (n = 0; n < m -> hsize; ++n)
				if (m -> cont[n])
					node_free_all (m -> cont[n]);
			free (m -> cont);
		}
		free (m);
	}
	return NULL;
}/*}}}*/
/** Add a node to the map.
 * @param m the map
 * @param key the key of the node
 * @param data the value of the node
 * @return true on success, false otherwise
 */
bool_t
map_add (map_t *m, const char *key, const char *data) /*{{{*/
{
	bool_t	rc;
	char	*mkey;
	
	rc = false;
	if (mkey = mkmkey (m, key)) {
		hash_t	hash;
		node_t	*n;

		hash = hasher (mkey);
		if (n = locate (m, mkey, hash, NULL))
			rc = node_setdata (n, data);
		else if (n = node_alloc (mkey, hash, key, data)) {
			int	hpos = hash % m -> hsize;
			
			n -> next = m -> cont[hpos];
			m -> cont[hpos] = n;
			rc = true;
		}
		if (mkey != key)
			free (mkey);
	}
	return rc;
}/*}}}*/
/** Remove node from map.
 * @param m the map
 * @param n the node to remove
 * @return true on success, false otherwise
 */
bool_t
map_delete_node (map_t *m, node_t *n) /*{{{*/
{
	bool_t	rc;
	int	hpos = n -> hash % m -> hsize;
	node_t	*run, *prv;
	
	rc = false;
	for (run = m -> cont[hpos], prv = NULL; run; run = run -> next)
		if (run == n)
			break;
		else
			prv = run;
	if (run) {
		if (prv)
			prv -> next = run -> next;
		else
			m -> cont[hpos] = run -> next;
		node_free (run);
		rc = true;
	}
	return rc;
}/*}}}*/
/** Delete node using key from map.
 * @param m the map
 * @param key the key to look for
 * @return true on success, false otherwise
 */
bool_t
map_delete (map_t *m, const char *key) /*{{{*/
{
	bool_t	rc;
	char	*mkey;
	
	rc = false;
	if (mkey = mkmkey (m, key)) {
		node_t	*n, *prv;

		if (n = locate (m, mkey, hasher (mkey), & prv)) {
			if (prv)
				prv -> next = n -> next;
			else
				m -> cont[n -> hash % m -> hsize] = n -> next;
			node_free (n);
			rc = true;
		}
		if (mkey != key)
			free (mkey);
	}
	return rc;
}/*}}}*/
/** Find a node in the map.
 * @param m the map
 * @param key the key
 * @return the node if found, NULL otherwise
 */
node_t *
map_find (map_t *m, const char *key) /*{{{*/
{
	node_t	*n;
	char	*mkey;
	
	if (mkey = mkmkey (m, key)) {
		n = locate (m, mkey, hasher (mkey), NULL);
		if (mkey != key)
			free (mkey);
	} else
		n = NULL;
	return n;
}/*}}}*/
