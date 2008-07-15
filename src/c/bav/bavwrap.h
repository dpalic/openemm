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
# ifndef	__BAVWRAP_H
# define	__BAVWRAP_H		1
# include	"agn.h"

typedef struct { /*{{{*/
	int		msize;	/* max size in memory			*/
	buffer_t	*buf;	/* input buffer				*/
	int		fd;	/* optional file to write temp. buffer	*/
	int		size;	/* commulated size of this store	*/
	int		pos;	/* current position			*/
	/*}}}*/
}	store_t;

extern int	store_add (store_t *st, byte_t *buf, int len);
extern int	store_get (store_t *st, byte_t *buf, int room);
extern void	store_rewind (store_t *st);
extern store_t	*store_alloc (int inmemsize);
extern store_t	*store_free (store_t *st);
# endif		/* __BAVWRAP_H */
