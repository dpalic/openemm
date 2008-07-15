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

postfix_t *
postfix_alloc (void) /*{{{*/
{
	postfix_t	*p;
	
	if (p = (postfix_t *) malloc (sizeof (postfix_t)))
		if (p -> c = fix_alloc ()) {
			p -> pid = NULL;
			p -> after = -1;
			p -> ref = NULL;
			p -> stack = NULL;
		} else {
			free (p);
			p = NULL;
		}
	return p;
}/*}}}*/
postfix_t *
postfix_free (postfix_t *p) /*{{{*/
{
	if (p) {
		if (p -> c)
			fix_free (p -> c);
		if (p -> pid)
			free (p -> pid);
		free (p);
	}
	return NULL;
}/*}}}*/
