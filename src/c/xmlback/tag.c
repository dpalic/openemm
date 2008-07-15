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
# include	<ctype.h>
# include	"xmlback.h"

tag_t *
tag_alloc (void) /*{{{*/
{
	tag_t	*t;
	
	if (t = (tag_t *) malloc (sizeof (tag_t))) {
		t -> name = xmlBufferCreate ();
		t -> hash = 0;
		t -> value = xmlBufferCreate ();
		t -> used = false;
		t -> next = NULL;
		if (! (t -> name && t -> value))
			t = tag_free (t);
	}
	return t;
}/*}}}*/
tag_t *
tag_free (tag_t *t) /*{{{*/
{
	if (t) {
		if (t -> name)
			xmlBufferFree (t -> name);
		if (t -> value)
			xmlBufferFree (t -> value);
		free (t);
	}
	return NULL;
}/*}}}*/
tag_t *
tag_free_all (tag_t *t) /*{{{*/
{
	tag_t	*tmp;
	
	while (tmp = t) {
		t = t -> next;
		tag_free (tmp);
	}
	return NULL;
}/*}}}*/
static void
xmlSkip (xmlChar **ptr, int *len) /*{{{*/
{
	int	n;
	
	while (*len > 0) {
		n = xmlCharLength (**ptr);
		if ((n == 1) && isspace (**ptr)) {
			*(*ptr)++ = '\0';
			*len -= 1;
			while ((*len > 0) && (xmlCharLength (**ptr) == 1) && isspace (**ptr))
				++(*ptr);
			break;
		} else {
			*ptr += n;
			*len -= n;
		}
	}
}/*}}}*/
void
tag_parse (tag_t *t) /*{{{*/
{
	xmlBufferPtr	temp;
	
	if (t -> name && (xmlBufferLength (t -> name) > 0) && (temp = xmlBufferCreateSize (xmlBufferLength (t -> name) + 1))) {
		xmlChar	*ptr;
		xmlChar	*name;
		int	len;
		
		xmlBufferAdd (temp, xmlBufferContent (t -> name), xmlBufferLength (t -> name));
		ptr = (xmlChar *) xmlBufferContent (temp);
		len = xmlBufferLength (temp);
		if ((xmlCharLength (*ptr) == 1) && (*ptr == '[')) {
			++ptr;
			--len;
			if ((len > 0) && (xmlStrictCharLength (*(ptr + len - 1)) == 1) && (*(ptr + len - 1) == ']'))
				--len;
		}
		name = ptr;
		xmlSkip (& ptr, & len);
		if (! strcmp (name, "agnSYSINFO")) {
			xmlChar	*var, *val;
			int	n;
			
			while (len > 0) {
				var = ptr;
				while (len > 0) {
					n = xmlCharLength (*ptr);
					if ((n == 1) && (*ptr == '=')) {
						*ptr++ = '\0';
						len -= 1;
						break;
					} else {
						ptr += n;
						len -= n;
					}
				}
				if (len > 0) {
					if ((xmlCharLength (*ptr) == 1) && (*ptr == '"')) {
						++ptr;
						--len;
						val = ptr;
						while (len > 0) {
							n = xmlCharLength (*ptr);
							if ((n == 1) && (*ptr == '"')) {
								*ptr++ = '\0';
								len -= 1;
								xmlSkip (& ptr, & len);
							} else {
								ptr += n;
								len -= n;
							}
						}
					} else {
						val = ptr;
						xmlSkip (& ptr, & len);
					}
					if (! strcmp (var, "name")) {
						if (! strcmp (val, "FQDN")) {
							char		*fqdn;
						
							if (fqdn = get_local_fqdn ()) {
								xmlBufferEmpty (t -> value);
								xmlBufferCCat (t -> value, fqdn);
								free (fqdn);
							}
						} else if (! strcmp (val, "RFCDATE")) {
							time_t          now;
							struct tm       *tt;
							char            dbuf[128];

							time (& now);
							if ((tt = gmtime (& now)) &&
							    (strftime (dbuf, sizeof (dbuf) - 1, "%a, %e %b %Y %H:%M:%S GMT", tt) > 0)) {
								xmlBufferEmpty (t -> value);
								xmlBufferCCat (t -> value, dbuf);
							}
						} else if (! strcmp (val, "EPOCH")) {
							time_t		now;
							char		dbuf[64];
							
							time (& now);
							sprintf (dbuf, "%ld", (long) now);
							xmlBufferEmpty (t -> value);
							xmlBufferCCat (t -> value, dbuf);
						}
					}
				}
			}
		}
		xmlBufferFree (temp);
	}
}/*}}}*/
bool_t
tag_match (tag_t *t, const xmlChar *name, int nlen) /*{{{*/
{
	const xmlChar	*ptr;
	int		len;
	
	len = xmlBufferLength (t -> name);
	if (len == nlen) {
		ptr = xmlBufferContent (t -> name);
		if (! memcmp (name, ptr, len))
			return true;
	}
	return false;
}/*}}}*/
