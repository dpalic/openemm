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
# include	<stdlib.h>
# include	"xmlback.h"

dyn_t *
dyn_alloc (int did, int order) /*{{{*/
{
	dyn_t	*d;
	
	if (d = (dyn_t *) malloc (sizeof (dyn_t))) {
		d -> did = did;
		d -> name = NULL;
		d -> order = order;
		d -> condition = NULL;
		DO_ZERO (d, block);
		d -> sibling = NULL;
		d -> next = NULL;
	}
	return d;
}/*}}}*/
dyn_t *
dyn_free (dyn_t *d) /*{{{*/
{
	if (d) {
		if (d -> name)
			free (d -> name);
		if (d -> condition)
			xmlBufferFree (d -> condition);
		DO_FREE (d, block);
		if (d -> sibling)
			dyn_free_all (d -> sibling);
		free (d);
	}
	return NULL;
}/*}}}*/
dyn_t *
dyn_free_all (dyn_t *d) /*{{{*/
{
	dyn_t	*tmp;
	
	while (tmp = d) {
		d = d -> next;
		dyn_free (tmp);
	}
	return NULL;
}/*}}}*/
bool_t
dyn_match (const dyn_t *d, eval_t *eval) /*{{{*/
{
	/* trivial case */
	if (! d -> condition)
		return true;
	return eval_match (eval, SP_DYNAMIC, d -> did);
}/*}}}*/
