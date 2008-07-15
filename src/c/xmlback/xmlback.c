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
# include	<stdio.h>
# include	<stdlib.h>
# include	<unistd.h>
# include	<string.h>
# include	<locale.h>
# include	"xmlback.h"


static output_t	output_table[] = { /*{{{*/
	{	"none",		false,
		none_oinit, none_odeinit, none_owrite
	}, {	"generate",	true,
		generate_oinit,	generate_odeinit, generate_owrite
	}, {	"count",	false,
		count_oinit, count_odeinit, count_owrite
	}
	/*}}}*/
};

static var_t *
parse_parm (const char *str) /*{{{*/
{
	var_t	*base, *prev, *tmp;
	char	*copy, *ptr, *sav, *equal, *var, *val, *nxt;
	int	n, m;

	base = NULL;
	prev = NULL;
	if (copy = strdup (str)) {
		for (ptr = copy; *ptr; ) {
			sav = ptr;
			equal = NULL;
			for (n = 0, m = 0; ptr[n] && (ptr[n] != ';'); ++n) {
				if ((ptr[n] == '\\') && ptr[n + 1])
					++n;
				else if ((! equal) && (ptr[n] == '='))
					equal = ptr + m;
				if (m != n)
					ptr[m++] = ptr[n];
				else
					++m;
			}
			if (n != m)
				ptr[m] = '\0';
			ptr += n;
			if (*ptr)
				*ptr++ = '\0';
			if (equal) {
				var = sav;
				val = equal;
				*val++ = '\0';
				if (! *var)
					var = NULL;
			} else {
				var = NULL;
				val = sav;
			}
			do {
				nxt = NULL;
				if (var && (nxt = strchr (var, ',')))
					*nxt++ = '\0';
				if (tmp = var_alloc (var, val)) {
					if (prev)
						prev -> next = tmp;
					else
						base = tmp;
					prev = tmp;
				} else {
					base = var_free_all (base);
					break;
				}
				var = nxt;
			}	while (var);
			if (! base)
				break;
		}
		free (copy);
	}
	return base;
}/*}}}*/

# ifdef		WIN32
static int		optind = 0;
static int		optpos = 0;
static const char	*optarg = NULL;

static int
getopt (int argc, char **argv, const char *opts) /*{{{*/
{
	if (optpos == 0) {
		++optind;
		if ((optind < argc) && (argv[optind][0] == '-') && argv[optind][1])
			optpos = 1;
	}
	if (optpos > 0) {
		while (*opts)
			if (argv[optind][optpos] == *opts)
				break;
			else
				++opts;
		if (! *opts)
			return '?';
		++optpos;
		if (*(opts + 1) == ':') {
			optarg = argv[optind] + optpos;
			optpos = 0;
			if (! *optarg)
				if (optind < argc)
					optarg = argv[++optind];
				else
					return '?';
		} else if (! argv[optind][optpos])
			optpos = 0;
		return *opts;
	}
	return -1;
}/*}}}*/
# endif		/* WIN32 */

/* this is a clear candidate for more trouble :-( */
int __libc_enable_secure = 0;

int
main (int argc, char **argv) /*{{{*/
{
	int		n;
	const char	*ptr;
	int		len;
	bool_t		quiet;
	const char	*error_file;
	bool_t		usecrlf;
	output_t	*out;
	const char	*outparm;
	const char	*level;
	var_t		*pparm;
	log_t		*lg;
	FILE		*errfp;
	FILE		*devnull;
	bool_t		st, dst;

	quiet = false;
	error_file = NULL;
	usecrlf = true;
	out = & output_table[1];
	outparm = NULL;
	level = NULL;
	setlocale (LC_ALL, "");
	xmlInitParser ();
	xmlInitializePredefinedEntities ();
	xmlInitCharEncodingHandlers ();
	while ((n = getopt (argc, argv, "VDvpqE:lo:L:")) != -1)
		switch (n) {
		case 'V':
			printf ("%s\n", XML_VERSION);
			return 0;
		case 'D':
			printf ("%s\n", dtd);
			return 0;
		case 'v':
			xmlDoValidityCheckingDefaultValue = 1;
			break;
		case 'p':
			xmlPedanticParserDefault (1);
			break;
		case 'q':
			quiet = true;
			break;
		case 'E':
			error_file = optarg;
			break;
		case 'l':
			usecrlf = false;
			break;
		case 'o':
			if (ptr = strchr (optarg, ':')) {
				len = ptr - optarg;
				++ptr;
			} else
				len = strlen (optarg);
			out = NULL;
			outparm = NULL;
			for (n = 0; n < sizeof (output_table) / sizeof (output_table[0]); ++n)
				if ((len == strlen (output_table[n].name)) && (! strncmp (optarg, output_table[n].name, len))) {
					out = & output_table[n];
					break;
				}
			if (! out)
				return fprintf (stderr, "Invalid output method %s specified, aborted.\n", optarg), 1;
			outparm = ptr;
			break;
		case 'L':
			level = optarg;
			break;
		default:
			fprintf (stderr, "Usage: %s [-V] [-D] [-v] [-p] [-q] [-E <file>] [-l] [-o <output>[:<parm>] [-L <loglevel>] <file(s)>\n", argv[0]);
			return 1;
		}
	pparm = NULL;
	if (outparm && outparm[0] && (! (pparm = parse_parm (outparm))))
		return fprintf (stderr, "Unable to parse output paramter %s, aborted.\n", outparm), 1;
	if (! (lg = log_alloc (NULL, argv[0], level)))
		return fprintf (stderr, "Unable to setup logging interface, aborted.\n"), 1;
	errfp = NULL;
	if (error_file) {
		if (! strcmp (error_file, "-"))
			errfp = stdout;
		else if (! (errfp = fopen (error_file, "a")))
			return fprintf (stderr, "Unable to open error file %s, aborted.\n", error_file), 1;
		xmlSetGenericErrorFunc (errfp, NULL);
		log_collect (lg);
	}
	devnull = NULL;
	if (! quiet) {
		if ((! level) && (lg -> level < LV_INFO))
			lg -> level = LV_INFO;
		log_tofd (lg, 2);
	} else {
		if ((! level) && (lg -> level < LV_NOTICE))
			lg -> level = LV_NOTICE;
		if (! errfp) {
			devnull = fopen (_PATH_DEVNULL, "r+");
			xmlSetGenericErrorFunc (devnull, NULL);
		}
	}
	st = true;
	for (n = optind; st && (n < argc); ++n) {
		blockmail_t	*blockmail;
		xmlDocPtr	doc;
		xmlNodePtr	base;
	
		st = false;
		if (! (blockmail = blockmail_alloc (argv[n], out -> syncfile, lg)))
			log_out (lg, LV_ERROR, "Unable to setup blockmail");
		else {
			blockmail -> usecrlf = usecrlf;
			blockmail -> output = out;
			blockmail -> outputdata = NULL;
			log_idset (lg, "init");
			blockmail -> outputdata = (*out -> oinit) (blockmail, pparm);
			log_idclr (lg);
			if (! blockmail -> outputdata)
				log_out (lg, LV_ERROR, "Unable to initialize output method %s for %s", out -> name, argv[n]);
			else {
				if (doc = xmlParseFile (argv[n])) {
					if (doc -> encoding) {
						blockmail -> translate = xmlFindCharEncodingHandler (doc -> encoding);
						if (! (blockmail -> translate -> input || blockmail -> translate -> iconv_in ||
						       blockmail -> translate -> output || blockmail -> translate -> iconv_out)) {
							xmlCharEncCloseFunc (blockmail -> translate);
							blockmail -> translate = NULL;
						}
					}
					if (base = xmlDocGetRootElement (doc))
						st = parse_file (blockmail, doc, base);
					xmlFreeDoc (doc);
				} else
					log_out (lg, LV_ERROR, "Unable to open/parse file %s", argv[n]);
				if (st)
					blockmail_count_sort (blockmail);
				log_idset (lg, "deinit");
				dst = (*out -> odeinit) (blockmail -> outputdata, blockmail, st);
				log_idclr (lg);
				if (! dst) {
					log_out (lg, LV_ERROR, "Unable to deinitialize output method %s for %s", out -> name, argv[n]);
					st = false;
				}
			}
			if (st)
				blockmail_unsync (blockmail);
			blockmail_free (blockmail);
		}
	}
	if (errfp && lg -> collect && (lg -> collect -> length > 0))
		fwrite (lg -> collect -> buffer, sizeof (lg -> collect -> buffer[0]), lg -> collect -> length, errfp);
	log_free (lg);
	if (pparm)
		var_free_all (pparm);
	xmlCleanupCharEncodingHandlers ();
	xmlCleanupPredefinedEntities ();
	xmlCleanupParser ();
	if (devnull)
		fclose (devnull);
	if (errfp)
		if (errfp == stdout)
			fflush (stdout);
		else
			fclose (errfp);
	return (! st) || (n < argc) ? 1 : 0;
}/*}}}*/
