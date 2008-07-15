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

url_t *
url_alloc (void) /*{{{*/
{
	url_t	*u;
	
	if (u = (url_t *) malloc (sizeof (url_t))) {
		u -> uid = 0;
		u -> dest = NULL;
		u -> dptr = NULL;
		u -> dlen = 0;
		u -> usage = 0;
	}
	return u;
}/*}}}*/
url_t *
url_free (url_t *u) /*{{{*/
{
	if (u) {
		if (u -> dest)
			xmlBufferFree (u -> dest);
		free (u);
	}
	return NULL;
}/*}}}*/
void
url_set_destination (url_t *u, xmlBufferPtr dest) /*{{{*/
{
	u -> dest = dest;
	if (u -> dest) {
		u -> dptr = xmlBufferContent (u -> dest);
		u -> dlen = xmlBufferLength (u -> dest);
	} else {
		u -> dptr = NULL;
		u -> dlen = 0;
	}
}/*}}}*/
bool_t
url_copy_destination (url_t *u, xmlBufferPtr dest) /*{{{*/
{
	xmlBufferPtr	ndest;
	
	if (! dest) {
		url_set_destination (u, dest);
		return true;
	}
	if (ndest = xmlBufferCreate ()) {
		xmlBufferAdd (ndest, xmlBufferContent (dest), xmlBufferLength (dest));
		url_set_destination (u, ndest);
		return true;
	}
	return false;
}/*}}}*/
