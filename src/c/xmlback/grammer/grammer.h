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
# ifndef	__GRAMMER_H
# define	__GRAMMER_H		1
# include	"xmlback.h"

typedef struct { /*{{{*/
	int		tid;
	char		*token;
	/*}}}*/
}	token_t;

typedef struct { /*{{{*/
	buffer_t	*buf;
	unsigned long	errcnt;
	buffer_t	*parse_error;
	/*}}}*/
}	private_t;

extern token_t		*token_alloc (int tid, const char *token);
extern token_t		*token_free (token_t *t);

extern bool_t		transform (buffer_t *buf, const xmlChar *input, int input_length, buffer_t *parse_error);
# ifndef	NDEBUG
extern bool_t		transformtable_check (buffer_t *out);
# endif		/* NDEBUG */

extern void		ParseTrace (FILE *, char *);
extern const char	*ParseTokenName (int);
extern void		*ParseAlloc (void *(*) (size_t));
extern void		ParseFree (void *, void (*) (void *));
extern void		Parse (void *, int, token_t *, private_t *);
#endif		/* __GRAMMER_H */
