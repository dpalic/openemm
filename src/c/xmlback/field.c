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

field_t *
field_alloc (void) /*{{{*/
{
	field_t	*f;
	
	if (f = (field_t *) malloc (sizeof (field_t))) {
		f -> name = NULL;
		f -> type = '\0';
	}
	return f;
}/*}}}*/
field_t *
field_free (field_t *f) /*{{{*/
{
	if (f) {
		if (f -> name)
			free (f -> name);
		free (f);
	}
	return NULL;
}/*}}}*/
