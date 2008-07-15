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
# ifndef	WIN32
# include	<stdlib.h>
# include	<string.h>
# include	<dirent.h>
# include	<dlfcn.h>
# include	"xmlback.h"
# include	"version.h"

# define	PLUGIN_DIR	"xmlback"
# define	PLUGIN_PATH	"lib/xmlback"
# define	SO		".so." XML_VERSION

# define	FN_REGISTER	"xmlback_register"
# define	FN_CLEANUP	"xmlback_cleanup"

dlink_t *
dlink_alloc (void *dl) /*{{{*/
{
	dlink_t	*d;
	
	if (d = (dlink_t *) malloc (sizeof (dlink_t))) {
		d -> dl = dl;
		d -> next = NULL;
	}
	return d;
}/*}}}*/
dlink_t *
dlink_free (dlink_t *d, blockmail_t *b) /*{{{*/
{
	if (d) {
		if (d -> dl) {
			void	(*cleanup) (blockmail_t *);
			
			if (cleanup = (void (*) (blockmail_t *)) dlsym (d -> dl, FN_CLEANUP))
				(*cleanup) (b);
			dlclose (d -> dl);
		}
		free (d);
	}
	return NULL;
}/*}}}*/
dlink_t *
dlink_free_all (dlink_t *d, blockmail_t *b) /*{{{*/
{
	dlink_t	*tmp;
	
	while (tmp = d) {
		d = d -> next;
		dlink_free (tmp, b);
	}
	return NULL;
}/*}}}*/

callback_t *
callback_alloc (const char *name, void *func, void (*cleanup) (void *), void *ud) /*{{{*/
{
	callback_t	*cb;
	
	if (cb = (callback_t *) malloc (sizeof (callback_t))) {
		cb -> name = NULL;
		cb -> ud = ud;
		cb -> func = func;
		cb -> cleanup = cleanup;
		cb -> next = NULL;
		if (name && (! (cb -> name = strdup (name)))) {
			cb -> cleanup = NULL;
			cb = callback_free (cb);
		}
	}
	return cb;
}/*}}}*/
callback_t *
callback_free (callback_t *cb) /*{{{*/
{
	if (cb) {
		if (cb -> cleanup)
			(*cb -> cleanup) (cb -> ud);
		if (cb -> name)
			free (cb -> name);
		free (cb);
	}
	return NULL;
}/*}}}*/
callback_t *
callback_free_all (callback_t *cb) /*{{{*/
{
	callback_t	*tmp;
	
	while (tmp = cb) {
		cb = cb -> next;
		callback_free (tmp);
	}
	return NULL;
}/*}}}*/
bool_t
plugin_setup (blockmail_t *b) /*{{{*/
{
	bool_t		rc;
	char		*pidir;
	const char	*env;
	
	rc = true;
	pidir = NULL;
	if (env = getenv ("XMLBACK_callback_DIR"))
		pidir = strdup (env);
	else if (env = getenv ("EMM_callback_DIR")) {
		if (pidir = malloc (strlen (env) + sizeof (PLUGIN_DIR) + 2))
			sprintf (pidir, "%s/" PLUGIN_DIR, env);
	} else {
		if (! (env = getenv ("HOME")))
			env = ".";
		if (pidir = malloc (strlen (env) + sizeof (PLUGIN_PATH) + 2))
			sprintf (pidir, "%s/" PLUGIN_PATH, env);
	}
	if (pidir) {
		DIR		*dp;
		struct dirent	*ent;
		char		*scratch;
		char		*base;
		char		*ptr;
		void		*dl;
		bool_t		(*reg) (blockmail_t *);
		dlink_t		*prev, *temp;
		
		if (dp = opendir (pidir)) {
			if (scratch = malloc (strlen (pidir) + NAME_MAX + 2)) {
				base = scratch + sprintf (scratch, "%s/", pidir);
				if (prev = b -> dlink)
					for (; prev -> next; prev = prev -> next)
						;
				while (ent = readdir (dp)) {
					if (ent -> d_name[0] == '.')
						continue;
					if ((! (ptr = strstr (ent -> d_name, SO))) || strcmp (ptr, SO))
						continue;
					strcpy (base, ent -> d_name);
					if (dl = dlopen (scratch, RTLD_NOW)) {
						if (reg = (bool_t (*) (blockmail_t *)) dlsym (dl, FN_REGISTER)) {
							if ((*reg) (b)) {
								if (temp = dlink_alloc (dl)) {
									if (prev)
										prev -> next = temp;
									else
										b -> dlink = temp;
									prev = temp;
								} else
									rc = false;
							} else {
								dlclose (dl);
								rc = false;
							}
						} else {
							dlclose (dl);
							rc= false;
						}
					} else
						rc = false;
				}
				free (scratch);
			} else
				rc = false;
			
			closedir (dp);
		}
		free (pidir);
	} else
		rc = false;
	return rc;
}/*}}}*/

static struct {
	const char	*where;
	cbtype_t	what;
}	cbtab[] = {
	{	"create-block",		CB_CreateBlock		}
};
bool_t
plugin_register (blockmail_t *b, const char *name, const char *where,
		 void *func, void (*cleanup) (void *), void *ud) /*{{{*/
{
	int		n;
	callback_t	*cb, *tmp;

	for (n = 0; n < sizeof (cbtab) / sizeof (cbtab[0]); ++n)
		if (! strcmp (cbtab[n].where, where))
			break;
	if (n == sizeof (cbtab) / sizeof (cbtab[0]))
		return false;
	if (! (cb = callback_alloc (name, func, cleanup, ud)))
		return false;
	if (tmp = b -> cb[cbtab[n].what]) {
		for (; tmp -> next; tmp = tmp -> next)
			;
		tmp -> next = cb;
	} else
		b -> cb[cbtab[n].what] = cb;
	return true;
}/*}}}*/

bool_t
callback_create_block (callback_t *cb, receiver_t *rec, block_t *block) /*{{{*/
{
	return  ((bool_t (*) (void *, receiver_t *, block_t *)) cb -> func) (cb -> ud, rec, block);
}/*}}}*/

# else		/* WIN32 */
# include	"xmlback.h"

dlink_t		*dlink_alloc (void *dl) { return NULL; }
dlink_t		*dlink_free (dlink_t *dl, blockmail_t *b) { return NULL; }
dlink_t		*dlink_free_all (dlink_t *dl, blockmail_t *b) { return NULL; }

callback_t	*callback_alloc (const char *name, void *func, void (*cleanup) (void *), void *ud) { return NULL; }
callback_t	*callback_free (callback_t *cb) { return NULL; }
callback_t	*callback_free_all (callback_t *cb) { return NULL; }
bool_t		plugin_setup (blockmail_t *b) { return true; }
bool_t		plugin_register (blockmail_t *b, const char *name, const char *where,
					 void *func, void (*cleanup) (void *), void *ud) { return true; }
bool_t		callback_create_block (callback_t *cb, receiver_t *rec, block_t *block) { return false; }
# endif		/* WIN32 */
