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
# include	<string.h>
# include	"xmlback.h"

counter_t *
counter_alloc (const char *mediatype, const char *subtype) /*{{{*/
{
	counter_t	*c;
	
	if (c = (counter_t *) malloc (sizeof (counter_t))) {
		c -> mediatype = strdup (mediatype);
		c -> subtype = strdup (subtype);
		c -> unitcount = 0;
		c -> bytecount = 0;
		c -> next = NULL;
		if (! (c -> mediatype && c -> subtype))
			c = counter_free (c);
	}
	return c;
}/*}}}*/
counter_t *
counter_free (counter_t *c) /*{{{*/
{
	if (c) {
		if (c -> mediatype)
			free (c -> mediatype);
		if (c -> subtype)
			free (c -> subtype);
		free (c);
	}
	return NULL;
}/*}}}*/
counter_t *
counter_free_all (counter_t *c) /*{{{*/
{
	counter_t	*tmp;
	
	while (tmp = c) {
		c = c -> next;
		counter_free (tmp);
	}
	return NULL;
}/*}}}*/
