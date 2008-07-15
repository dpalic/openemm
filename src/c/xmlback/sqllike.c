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

#define incr(xxx,lll)	do {								\
				if ((n = xmlValidPosition ((xxx), (lll))) == -1)	\
					return false;					\
				else {							\
					(xxx) += n;					\
					(lll) -= n;					\
				}							\
			} while (0)

bool_t
xmlSQLlike (const xmlChar *pattern, int plen,
	    const xmlChar *string, int slen,
	    bool_t icase) /*{{{*/
{
	const xmlChar	*cur;
	int		n, sn;
	
	while ((plen > 0) && (slen > 0)) {
		cur = pattern;
		incr (pattern, plen);
		if (*cur == '_')
			incr (string, slen);
		else if (*cur == '%') {
			while ((plen > 0) && (*pattern == '%')) {
				cur = pattern;
				incr (pattern, plen);
			}
			if (! plen)
				return true;
			while (slen > 0) {
				if (xmlSQLlike (pattern, plen, string, slen, icase))
					return true;
				incr (string, slen);
			}
		} else {
			if ((*cur == '\\') && (plen > 0)) {
				cur = pattern;
				incr (pattern, plen);
			}
			if (((sn = xmlStrictCharLength (*string)) > slen) || (sn != n) ||
			    (icase ? xmlStrncasecmp (cur, string, n) : xmlStrncmp (cur, string, n)))
				return false;
			incr (string, slen);
		}
	}
	if ((slen == 0) && (plen > 0))
		while ((plen > 0) && (*pattern == '%'))
			incr (pattern, plen);
	return plen == slen ? true : false;
}/*}}}*/
