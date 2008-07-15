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
# include	<unistd.h>
# include	<fcntl.h>
# include	<string.h>
# include	<sys/types.h>
# include	<sys/stat.h>
# include	"bav.h"

static map_t *
parse_config (char *buf, int len) /*{{{*/
{
	map_t	*temp;
	
	if (temp = map_alloc (true, len > 5000 ? len / 100 : 47)) {
		char	*cur, *ptr, *val;
		bool_t	st;
		
		st = true;
		for (ptr = buf; ptr && st; ) {
			cur = ptr;
			if (ptr = strchr (ptr, '\n'))
				*ptr++ = '\0';
			while (isspace (*cur))
				++cur;
			if (*cur && (*cur != '#')) {
				val = skip (cur);
				if (*val)
					if (! map_add (temp, cur, val))
						st = false;
			}
		}
		if (! st)
			temp = map_free (temp);
	}
	return temp;
}/*}}}*/
static map_t *
read_config (const char *fname) /*{{{*/
{
	map_t		*rc;
	struct stat	st;
	char		*buf;
	int		fd;
		
	rc = NULL;
	if ((stat (fname, & st) != -1) && (buf = malloc (st.st_size + 1))) {
		if ((fd = open (fname, O_RDONLY)) != -1) {
			int	n, count;
			char	*ptr;
					
			for (ptr = buf, count = 0; count < st.st_size; )
				if ((n = read (fd, ptr, st.st_size - count)) > 0) {
					ptr += n;
					count += n;
				} else
					break;
			close (fd);
			if (count == st.st_size) {
				buf[count] = '\0';
				rc = parse_config (buf, count);
			}
		}
		free (buf);
	}
	return rc;
}/*}}}*/
cfg_t *
cfg_alloc (const char *fname) /*{{{*/
{
	cfg_t	*c;
	
	if (c = (cfg_t *) malloc (sizeof (cfg_t))) {
		if (! (c -> amap = read_config (fname)))
			c = cfg_free (c);
	}
	return c;
}/*}}}*/
cfg_t *
cfg_free (cfg_t *c) /*{{{*/
{
	if (c) {
		if (c -> amap)
			map_free (c -> amap);
		free (c);
	}
	return NULL;
}/*}}}*/
char *
cfg_valid_address (cfg_t *c, const char *addr) /*{{{*/
{
	char	*rc;
	char	*copy;
	
	if (copy = strdup (addr)) {
		node_t	*found;
		char	*ptr;

		if (ptr = strchr (copy, '>'))
			*ptr = '\0';
		if (*copy == '<')
			ptr = copy + 1;
		else
			ptr = copy;
		if (found = map_find (c -> amap, ptr))
			if (! strncmp (found -> data, "alias:", 6)) {
				ptr = found -> data + 6;
				found = map_find (c -> amap, ptr);
			}
		if ((! found) && (ptr = strchr (ptr, '@')))
			found = map_find (c -> amap, ptr);
		if (found)
			rc = strdup (found -> data);
		else
			rc = strdup (ID_REJECT);
		free (copy);
	} else
		rc = NULL;
	return rc;
}/*}}}*/
