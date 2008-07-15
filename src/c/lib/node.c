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
/** @file node.c
 * Handle nodes in hash collection.
 */
# include	<stdlib.h>
# include	<string.h>
# include	"agn.h"

/** Allocate node.
 * Allocate memory for a new node
 * @param mkey the key for the hashing
 * @param hash the hash code
 * @param okey the original key
 * @param data the value for this node
 * @return the new node on success, NULL otherwise
 */
node_t *
node_alloc (const char *mkey, hash_t hash,
	    const char *okey, const char *data) /*{{{*/
{
	node_t	*n;
	
	if (n = (node_t *) malloc (sizeof (node_t))) {
		n -> mkey = NULL;
		n -> hash = hash;
		n -> okey = NULL;
		n -> data = NULL;
		n -> next = NULL;
		if ((mkey && (! (n -> mkey = strdup (mkey)))) ||
		    (okey && (! (n -> okey = strdup (okey)))) ||
		    (data && (! (n -> data = strdup (data)))))
			n = node_free (n);
	}
	return n;
}/*}}}*/
/** Frees node.
 * Return the memory allocated to the system
 * @param n the node to free
 * @return NULL
 */
node_t *
node_free (node_t *n) /*{{{*/
{
	if (n) {
		if (n -> mkey)
			free (n -> mkey);
		if (n -> okey)
			free (n -> okey);
		if (n -> data)
			free (n -> data);
		free (n);
	}
	return NULL;
}/*}}}*/
/** Free nodes.
 * Return the resources of the node and all siblings to the system
 * @param n the node to start
 * @return NULL
 */
node_t *
node_free_all (node_t *n) /*{{{*/
{
	node_t	*tmp;
	
	while (tmp = n) {
		n = n -> next;
		node_free (tmp);
	}
	return NULL;
}/*}}}*/
/** Set node data.
 * Set/change the value for this node
 * @param n the node to change
 * @param data the new content
 * @return true on success, false otherwise
 */
bool_t
node_setdata (node_t *n, const char *data) /*{{{*/
{
	if (n -> data)
		free (n -> data);
	n -> data = data ? strdup (data) : NULL;
	return (data && (! n -> data)) ? false : true;
}/*}}}*/
