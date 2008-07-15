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
/* # define	DATE_HACK */
# include	<string.h>
# include	"xmlback.h"

static bool_t
use_block (block_t *block, mailtype_t *mtyp, links_t *links) /*{{{*/
{
	bool_t	rc;

	rc = false;
	if (block -> attachment) {
			rc = true;
	} else {
		if (links && block -> cid) {
			int	n;
		
			for (n = 0; n < links -> lcnt; ++n)
				if (((! links -> seen) || (! links -> seen[n])) &&
				    (! strcmp (block -> cid, links -> l[n]))) {
					if (links -> seen)
						links -> seen[n] = true;
					rc = true;
					break;
				}
		}
	}
	return rc;
}/*}}}*/
static bool_t
create_mail (blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	int		n, m;
	bool_t		st;
	int		attcount;
	links_t		*links;
	postfix_t	*postfixes;
	buffer_t	*dest;
	mailtype_t	*mtyp;
	blockspec_t	*bspec;
	block_t		*block;
	bool_t		changed;
	
	mtyp = NULL;
	for (n = 0; n < blockmail -> mailtype_count; ++n)
		if (! strcmp (rec -> mailtype, blockmail -> mailtype[n] -> mailtype)) {
			mtyp = blockmail -> mailtype[n];
			break;
		}
	if (! mtyp) {
		log_out (blockmail -> lg, LV_ERROR, "No mailtype (%s) for receiver %d found", rec -> mailtype, rec -> customer_id);
		return false;
	}
	st = true;
	attcount = 0;

	/*
	 * 1. Stage: check for usful blocks, count attachments and
	 *           create the content part */
	links = mtyp -> offline ? links_alloc () : NULL;
	for (n = 0; st && (n < mtyp -> blockspec_count); ++n) {
		bspec = mtyp -> blockspec[n];
		block = bspec -> block;
		changed = false;
		if (blockmail -> eval && (blockmail -> mailtype_index != -1)) {
			int	idx = -1;
			
			if (block -> tid == TID_EMail_Text) {
				idx = 0;
				changed = true;
			} else if (block -> tid == TID_EMail_HTML) {
				idx = 1;
				changed = true;
			}
			if (changed)
				eval_change_data (blockmail -> eval, blockmail -> mtbuf[idx], false, blockmail -> mailtype_index);
		}
			block -> inuse = block_match (block, blockmail -> eval);
		if (block -> inuse &&
		    (block -> tid != TID_EMail_Head) &&
		    (block -> tid != TID_EMail_Text) &&
		    (block -> tid != TID_EMail_HTML))
			block -> inuse = use_block (block, mtyp, links);
		if (block -> inuse) {
			if (block -> attachment)
				attcount++;
			if (! block -> binary) {
				if (st) {
					log_idpush (blockmail -> lg, "replace_tags", "->");
					st = replace_tags (blockmail, rec, block, (block -> tid != TID_EMail_Text ? true : false));
					log_idpop (blockmail -> lg);
					if (! st)
						log_out (blockmail -> lg, LV_ERROR, "Unable to replace tags in block %d for %d", block -> nr, rec -> customer_id);
				}
				if (st) {
					log_idpush (blockmail -> lg, "modify_output", "->");
					st = modify_output (blockmail, rec, block, bspec, links);
					log_idpop (blockmail -> lg);
					if (! st)
						log_out (blockmail -> lg, LV_ERROR, "Unable to modify output in block %d for %d", block -> nr, rec -> customer_id);
				}
				if (st) {
						log_idpush (blockmail -> lg, "convert_charset", "->");
						st = convert_charset (blockmail, block, (block -> tid == TID_EMail_Head ? true : false));
						log_idpop (blockmail -> lg);
						if (! st)
							log_out (blockmail -> lg, LV_ERROR, "Unable to convert chararcter set in block %d for %d", block -> nr, rec -> customer_id);
				}
			}
		}
		if (changed)
			eval_change_data (blockmail -> eval, rec -> data[blockmail -> mailtype_index], rec -> dnull[blockmail -> mailtype_index], blockmail -> mailtype_index);
	}
	if (links)
		links_free (links);

	/*
	 * 2. Stage: determinate the required postfixes */
	postfixes = NULL;
	for (n = 0; st && (n < mtyp -> blockspec_count); ++n) {
		bspec = mtyp -> blockspec[n];
		block = bspec -> block;
		if (block -> inuse) {
			postfix_t	*cur, *tmp, *prv;
				
			for (m = bspec -> postfix_count - 1; m >= 0; --m) {
				cur = bspec -> postfix[m];
				if (cur -> pid) {
					for (tmp = postfixes, prv = NULL; tmp; tmp = tmp -> stack)
						if (tmp -> pid && (! strcmp (tmp -> pid, cur -> pid)))
							break;
						else
							prv = tmp;
					if (tmp) {
						cur -> stack = tmp -> stack;
						if (prv)
							prv -> stack = cur;
						else
							postfixes = cur;
						cur = NULL;
					}
				}
				if (cur) {
					cur -> stack = postfixes;
					postfixes = cur;
				}
			}
		}
	}

	/*
	 * 3. Stage: create the output */
	for (n = 0; st && (n <= mtyp -> blockspec_count); ++n) {
		if (n < mtyp -> blockspec_count) {
			bspec = mtyp -> blockspec[n];
			block = bspec -> block;
		} else {
			bspec = NULL;
			block = NULL;
		}
		if (postfixes) {
			postfix_t	*run, *prv;
			
			for (run = postfixes, prv = NULL; st && run; run = run -> stack)
				if ((! block) || (run -> after < block -> nr)) {
					dest = (run -> ref -> block -> tid == TID_EMail_Head ? blockmail -> head : blockmail -> body);
					if (! append_cooked (dest, blockmail -> usecrlf, (attcount ? run -> c -> acont : run -> c -> cont), run -> ref -> block -> charset, Enc8bit)) {
						log_out (blockmail -> lg, LV_ERROR, "Unable to append postfix for block %d for %d", run -> ref -> block -> nr, rec -> customer_id);
						st = false;
					}
					if (prv)
						prv -> stack = run -> stack;
					else
						postfixes = run -> stack;
				} else
					prv = run;
		}
		if (st && block && block -> inuse) {
			dest = (block -> tid == TID_EMail_Head ? blockmail -> head : blockmail -> body);
			if (! append_cooked (dest, blockmail -> usecrlf, (attcount ? bspec -> prefix -> acont : bspec -> prefix -> cont), block -> charset, Enc8bit)) {
				log_out (blockmail -> lg, LV_ERROR, "Unable to append prefix for block %d for %d", block -> nr, rec -> customer_id);
				st = false;
			}
			if (st) {
				if (! block -> binary)
				{
					if (! append_cooked (dest, blockmail -> usecrlf, block -> out, block -> charset, block -> method))
						st = false;
# ifdef         DATE_HACK
					else if (block -> nr == 0) {
						xmlBufferPtr    temp = xmlBufferCreate ();
						
						if (temp) {
							time_t          now;
							struct tm       *tt;
							char            dbuf[128];

							time (& now);
							if ((tt = gmtime (& now)) &&
							    (strftime (dbuf, sizeof (dbuf) - 1, "HDate: %a, %e %b %Y %H:%M:%S GMT\n", tt) > 0)) {
								xmlBufferCCat (temp, dbuf);
								if (! append_cooked (dest, blockmail -> usecrlf, temp, block -> charset, block -> method))
									st = false;
							}
							xmlBufferFree (temp);
						}
					}
# endif
					
				} else {
					if (! append_raw (dest, blockmail -> usecrlf, block -> bout))
						st = false;
				}
				if (! st)
					log_out (blockmail -> lg, LV_ERROR, "Unable to append content of block %d for %d", block -> nr, rec -> customer_id);
			}
		}
	}
	return st;
}/*}}}*/
bool_t
create_output (blockmail_t *blockmail, receiver_t *rec) /*{{{*/
{
	bool_t	st;
	bool_t	(*docreate) (blockmail_t *, receiver_t *);
	media_t	*m;
	
	st = true;
	m = NULL;
	blockmail -> active = true;
	blockmail -> head -> length = 0;
	blockmail -> body -> length = 0;
	if (rec -> mediatypes) {
		char	*copy, *cur, *ptr;
		mtype_t	type;
		
		docreate = NULL;
		if (copy = strdup (rec -> mediatypes)) {
			for (cur = copy; st && cur && (! m); ) {
				if (ptr = strchr (cur, ','))
					*ptr++ = '\0';
				if (media_parse_type (cur, & type)) {
					int	n;

					for (n = 0; n < blockmail -> media_count; ++n)
						if (blockmail -> media[n] -> type == type) {
							m = blockmail -> media[n];
							if (m -> stat == MS_Active) {
								switch (type) {
								case MT_EMail:
									docreate = create_mail;
									break;
								}
							} else
								blockmail -> active = false;
							break;
						}
				} else
					st = false;
				cur = ptr;
			}
			free (copy);
		} else
			st = false;
	} else
		docreate = create_mail;
	if (st) {
		rec -> media = m;
		strcpy (rec -> mid, media_typeid (m ? m -> type : MT_EMail));
		if (blockmail -> active && docreate)
			st = (*docreate) (blockmail, rec);
	}
	return st;
}/*}}}*/
