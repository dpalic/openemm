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
# include	<string.h>
# include	"xmlback.h"

typedef struct { /*{{{*/
	char	*path;
	/*}}}*/
}	preview_t;
static preview_t *
preview_alloc (void) /*{{{*/
{
	preview_t	*p;
	
	if (p = (preview_t *) malloc (sizeof (preview_t))) {
		p -> path = NULL;
	}
	return p;
}/*}}}*/
static preview_t *
preview_free (preview_t *p) /*{{{*/
{
	if (p) {
		if (p -> path)
			free (p -> path);
		free (p);
	}
	return NULL;
}/*}}}*/
static bool_t
preview_set_output_path (preview_t *p, const char *path) /*{{{*/
{
	if (p -> path)
		free (p -> path);
	p -> path = path ? strdup (path) : NULL;
	return ((! path) || p -> path) ? true : false;
}/*}}}*/

void *
preview_oinit (blockmail_t *blockmail, var_t *opts) /*{{{*/
{
	preview_t	*pv;

	if (pv = preview_alloc ()) {
		var_t		*tmp;
		const char	*path;
		
		for (tmp = opts; tmp; tmp = tmp -> next)
			if ((! tmp -> var) || var_partial_imatch (tmp, "path"))
				path = tmp -> val;
		if ((! path) || (! preview_set_output_path (pv, path)))
			pv = preview_free (pv);
	}
	return pv;
}/*}}}*/
bool_t
preview_odeinit (void *data, blockmail_t *blockmail, bool_t success) /*{{{*/
{
	preview_t	*pv = (preview_t *) data;
	
	preview_free (pv);
	return true;
}/*}}}*/

static bool_t
make_pure_header (buffer_t *dest, const byte_t *content, long length, bool_t usecrlf) /*{{{*/
{
	bool_t	st = true;
	long	n = 0;
	long	start, end;
	
	while (st && (n < length)) {
		start = n;
		while ((n + 1 < length) && (content[n] != '\r') && (content[n + 1] != '\n'))
			++n;
		end = n;
		n += 2;
		if ((start < end) && (content[start] == 'H')) {
			++start;
			if ((start + 2 < end) && (content[start] == '?')) {
				++start;
				while ((start < end) && (content[start] != '?'))
					++start;
				if ((start < end) && (content[start] == '?'))
					++start;
			}
			if (start < end)
				if ((! buffer_stiff (dest, content + start, end - start)) ||
				    (! (usecrlf ? buffer_stiff (dest, "\r\n", 2) : buffer_stiff (dest, "\n", 1))))
					st = false;
		}
	}
	return st;
}/*}}}*/
static bool_t
replace_crnl_by_nl (buffer_t *dest, const byte_t *content, long length) /*{{{*/
{
	bool_t	st = true;
	long	n = 0;
	long	start = 0;
					
	while (st && (n <= length))
		if ((n == length) || ((n + 1 < length) && (content[n] == '\r') && (content[n + 1] == '\n'))) {
			if (start < n)
				if (! buffer_stiff (dest, content + start, n - start))
					st = false;
			++n;
			start = n;
		} else
			++n;
	return st;
}/*}}}*/
bool_t
preview_owrite (void *data, blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	preview_t	*pv = (preview_t *) data;
	bool_t		st = false;
	xmlDocPtr	doc;
	
	if (doc = xmlNewDoc ("1.0")) {
		xmlNodePtr	root;
		rblock_t	*run;
		buffer_t	*scratch;
		FILE		*fp;
		
		if (root = xmlNewNode (NULL, "preview")) {
			xmlDocSetRootElement (doc, root);
			st = true;
			scratch = NULL;
			for (run = blockmail -> rblocks; st && run; run = run -> next)
				if (run -> bname && run -> content) {
					const byte_t	*content = xmlBufferContent (run -> content);
					long		length = xmlBufferLength (run -> content);

					if ((run -> tid == TID_EMail_Head) || (run -> tid != TID_Unspec && (! blockmail -> usecrlf))) {
						if (scratch || (scratch = buffer_alloc (65536))) {
							scratch -> length = 0;
							if (run -> tid == TID_EMail_Head)
								st = make_pure_header (scratch, content, length, blockmail -> usecrlf);
							else
								st = replace_crnl_by_nl (scratch, content, length);
							content = scratch -> buffer;
							length = scratch -> length;
						} else
							st = false;
					}
					if (st) {
						xmlNodePtr	node, text;
						
						if ((node = xmlNewNode (NULL, "content")) && xmlNewProp (node, "name", run -> bname)) {
							xmlAddChild (root, node);
							if (text = xmlNewTextLen (content, length)) {
								xmlAddChild (node, text);
							} else
								st = false;
						} else
							st = false;
					}
				}
		}
		if (st && pv -> path && (fp = fopen (pv -> path, "w"))) {
			xmlDocDump (fp, doc);
			if (fclose (fp) == EOF)
				st = false;
		} else
			st = false;
		xmlFreeDoc (doc);
	}
	return st;
}/*}}}*/
