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
/** @file str.c
 * String utilities.
 */
# include	<stdlib.h>
# include	<string.h>
# include	"agn.h"

/** Reuse a string variable.
 * Create a copy of input string, free up old memory, if neccessary
 * @param buf the destination to copy to, freeing used memory
 * @param str the string to copy
 * @return true on success, false otherwise
 */
bool_t
struse (char **buf, const char *str) /*{{{*/
{
	if (*buf)
		free (*buf);
	*buf = str ? strdup (str) : NULL;
	return (! str) || *buf ? true : false;
}/*}}}*/
