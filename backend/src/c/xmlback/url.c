/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
# include	<stdlib.h>
# include	<ctype.h>
# include	"xmlback.h"

static urllink_t *
urllink_alloc (void) /*{{{*/
{
	urllink_t	*l;
	
	if (l = (urllink_t *) malloc (sizeof (urllink_t))) {
		l -> url = NULL;
		l -> ptr = NULL;
		l -> len = 0;
	}
	return l;
}/*}}}*/
static urllink_t *
urllink_free (urllink_t *l) /*{{{*/
{
	if (l) {
		if (l -> url) {
			xmlBufferFree (l -> url);
		}
		free (l);
	}
	return NULL;
}/*}}}*/
static bool_t
urllink_match (urllink_t *l, const xmlChar *check, int clen) /*{{{*/
{
	return l && (l -> len == clen) && (! xmlStrncmp (l -> ptr, check, clen)) ? true : false;
}/*}}}*/
static void
urllink_set (urllink_t *l, xmlBufferPtr url) /*{{{*/
{
	l -> url = url;
	if (l -> url) {
		l -> ptr = xmlBufferContent (l -> url);
		l -> len = xmlBufferLength (l -> url);
	} else {
		l -> ptr = NULL;
		l -> len = 0;
	}
}/*}}}*/
url_t *
url_alloc (void) /*{{{*/
{
	url_t	*u;
	
	if (u = (url_t *) malloc (sizeof (url_t))) {
		u -> uid = 0;
		u -> dest = NULL;
		u -> usage = 0;
	}
	return u;
}/*}}}*/
url_t *
url_free (url_t *u) /*{{{*/
{
	if (u) {
		if (u -> dest) {
			urllink_free (u -> dest);
		}
		free (u);
	}
	return NULL;
}/*}}}*/
bool_t
url_match (url_t *u, const xmlChar *check, int clen) /*{{{*/
{
	return urllink_match (u -> dest, check, clen);
}/*}}}*/
static void
set_link (urllink_t **lnk, xmlBufferPtr url) /*{{{*/
{
	if (! url) {
		if (*lnk) {
			*lnk = urllink_free (*lnk);
		}
	} else if (*lnk || (*lnk = urllink_alloc ())) {
		urllink_set (*lnk, url);
	}
}/*}}}*/
void
url_set_destination (url_t *u, xmlBufferPtr dest) /*{{{*/
{
	set_link (& u -> dest, dest);
}/*}}}*/
