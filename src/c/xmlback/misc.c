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
# include	"xmlback.h"

# ifndef	I
# define	I	/* */
# endif		/* I */

# ifndef	__MISC_C
# define	__MISC_C		1
I bool_t
xmlEqual (xmlBufferPtr p1, xmlBufferPtr p2) /*{{{*/
{
	int		len;
	const xmlChar	*c1, *c2;
	
	len = xmlBufferLength (p1);
	if ((len == xmlBufferLength (p2)) &&
	    (c1 = xmlBufferContent (p1)) &&
	    (c2 = xmlBufferContent (p2)) &&
	    ((! len) || (! memcmp (c1, c2, len * sizeof (xmlChar)))))
		return true;
	return false;
}/*}}}*/
I int
xmlCharLength (xmlChar ch) /*{{{*/
{
	extern int	xmlLengthtab[256];
	
	return xmlLengthtab[ch];
}/*}}}*/
I int
xmlStrictCharLength (xmlChar ch) /*{{{*/
{
	extern int	xmlStrictLengthtab[256];
	
	return xmlStrictLengthtab[ch];
}/*}}}*/
I int
xmlValidPosition (const xmlChar *str, int length) /*{{{*/
{
# define	VALID(ccc)	(((ccc) & 0xc0) == 0x80)
	int	len, n;
	
	if (((len = xmlStrictCharLength (*str)) > 0) && (length >= len))
		for (n = len; n > 1; ) {
			--n;
			if (! VALID (*(str + n))) {
				len = -1;
				break;
			}
		}
	else
		len = -1;
	return len;
# undef		VALID	
}/*}}}*/
I bool_t
xmlValid (const xmlChar *str, int length) /*{{{*/
{
	int	n;
	
	while (length > 0)
		if ((n = xmlValidPosition (str, length)) > 0) {
			str += n;
			length -= n;
		} else
			break;
	return length == 0 ? true : false;
}/*}}}*/
# endif		/* __MISC_C */
