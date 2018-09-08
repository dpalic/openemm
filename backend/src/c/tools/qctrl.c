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
# include	<ctype.h>
# include	<unistd.h>
# include	<string.h>
# include	<errno.h>
# include	"qctrl.h"

# ifdef		sun
# define	USE_SLEEP
# endif		/* sun */

# define	DEF_DELAY	300

static const struct { /*{{{*/
	const char	*cmd;		/* the command to execute	*/
	int		minarg;		/* minimum number of arguments	*/
	int		maxarg;		/* maximum number of arguments	*/
	const char	*desc;		/* description for usage	*/
	/* callbacks for this command					*/
	void		*(*finit) (log_t *, bool_t, char **, int);
	bool_t		(*fdeinit) (void *);
	bool_t		(*fexec) (void *);
	/*}}}*/
}	cmdtab[] = { /*{{{*/
	{	"move",		2,	3,	"<source-path> <destination-path> [<limit-desc>]",
		move_init,	move_deinit,	move_exec
	},
	{	"stat",		1,	-1,	"<queue-path>+",
		stat_init,	stat_deinit,	stat_exec
	}
	/*}}}*/
};

static volatile bool_t	running;
static void
handler (int sig) /*{{{*/
{
	switch (sig) {
	case SIGINT:
	case SIGTERM:
		running = false;
		break;
	}
}/*}}}*/
static int
usage (const char *argv0) /*{{{*/
{
	int	n;
	
	fprintf (stderr, "Usage: %s [-f] [-a] [-o] [-v] [-d <delay>] [-L <logdesc>] [<force>] <command> [<commandparms>]\n", argv0);
	fprintf (stderr, "Function: Controls and modifies sendmail queues\n");
	fprintf (stderr, "Options:\n");
	fprintf (stderr, "\t-f          run in foreground\n");
	fprintf (stderr, "\t-a          do not detach from terminal\n");
	fprintf (stderr, "\t-o          oneshot mode, execute command only once\n");
	fprintf (stderr, "\t-v          verbose mode, print log entries to stderr\n");
	fprintf (stderr, "\t-d <sec>    delay the execution for <sec> seconds\n");
	fprintf (stderr, "\t-L <log>    defines the loglevel\n");
	fprintf (stderr, "Available commands are:\n");
	for (n = 0; n < sizeof (cmdtab) / sizeof (cmdtab[0]); ++n)
		fprintf (stderr, "\t%s %s\n", cmdtab[n].cmd, cmdtab[n].desc);
	return 1;
}/*}}}*/
int
main (int argc, char **argv) /*{{{*/
{
	int		n;
	bool_t		background, detach, oneshot, verbose;
	int		delay;
	const char	*loglevel;
	bool_t		force;
	char		*cmd;
	char		**args;
	int		alen;
	char		*lname;
	log_t		*lg;
	daemon_t	*dm;
	int		st;
	
	background = true;
	detach = true;
	oneshot = false;
	verbose = false;
	delay = DEF_DELAY;
	loglevel = log_level_name (LV_ERROR);
	while ((n = getopt (argc, argv, "faovd:L:")) != -1)
		switch (n) {
		case 'f':
			background = false;
			break;
		case 'a':
			detach = false;
			break;
		case 'o':
			oneshot = true;
			break;
		case 'v':
			verbose = true;
			break;
		case 'd':
			if ((delay = atoi (optarg)) <= 0)
				delay = DEF_DELAY;
			break;
		case 'L':
			loglevel = optarg;
			break;
		default:
			return usage (argv[0]);
		}
	if ((optind < argc) && (! strcmp (argv[optind], "force"))) {
		++optind;
		force = true;
	} else
		force = false;
	if (argc == optind)
		return usage (argv[0]);
	cmd = argv[optind++];
	args = argv + optind;
	alen = argc - optind;
	for (n = 0; n < sizeof (cmdtab) / sizeof (cmdtab[0]); ++n)
		if (! strcasecmp (cmd, cmdtab[n].cmd))
			break;
	if (n == sizeof (cmdtab) / sizeof (cmdtab[0]))
		return fprintf (stderr, "Unknown command %s.\n", cmd), 1;
	if (((cmdtab[n].minarg != -1) && (alen < cmdtab[n].minarg)) ||
	    ((cmdtab[n].maxarg != -1) && (alen > cmdtab[n].maxarg)))
		return fprintf (stderr, "Invalid number of arguments, usage: %s %s.\n", cmdtab[n].cmd, cmdtab[n].desc), 1;
	if (! (lname = malloc (strlen (argv[0]) + strlen (cmdtab[n].cmd) + 2)))
		return fprintf (stderr, "Out of memory!\n"), 1;
	sprintf (lname, "%s-%s", argv[0], cmdtab[n].cmd);
	lg = log_alloc (NULL, lname, loglevel);
	free (lname);
	if (! lg)
		return fprintf (stderr, "Unable to setup logging.\n"), 1;
	if (verbose)
		log_tofd (lg, 2);
	st = 1;
	log_out (lg, LV_DEBUG, "Setting up daemon mode");
	if (dm = daemon_alloc (NULL, background, detach)) {
		if (daemon_start (dm, lg)) {
			void		*data;
			csig_t		*csig;
# ifndef	USE_SLEEP
			struct timespec	tdelay, tcur, ttmp;
# endif		/* USE_SLEEP */
			bool_t		fst;

			log_out (lg, LV_DEBUG, "Initializing command %s", cmdtab[n].cmd);
			log_idpush (lg, cmdtab[n].cmd, NULL);
			data = (*cmdtab[n].finit) (lg, force, args, alen);
			log_idpop (lg);
			if (data) {
				running = true;
				if (csig = csig_alloc (SIGINT, handler,
						       SIGTERM, handler,
						       SIGPIPE, SIG_IGN, -1)) {
					st = 0;
# ifndef	USE_SLEEP					
					tdelay.tv_sec = delay;
					tdelay.tv_nsec = 0;
# endif		/* USE_SLEEP */
					log_out (lg, LV_INFO, "Starting up");
					while (running) {
						csig_block (csig);
						log_mark (lg, LV_INFO, 180);
						log_out (lg, LV_DEBUG, "Executing command %s", cmdtab[n].cmd);
						log_idpush (lg, cmdtab[n].cmd, NULL);
						fst = (*cmdtab[n].fexec) (data);
						log_idpop (lg);
						if (! fst) {
							running = false;
							st = 1;
						}
						csig_unblock (csig);
						if (oneshot)
							running = false;
						else
# ifdef		USE_SLEEP
							sleep (delay);
# else		/* USE_SLEEP */
							for (tcur = tdelay; running && (tcur.tv_sec || tcur.tv_nsec); ) {
								ttmp = tcur;
								log_out (lg, LV_DEBUG, "Delaying execution for %ld,%09ld seconds", (long) ttmp.tv_sec, ttmp.tv_nsec);
								if (nanosleep (& ttmp, & tcur) != -1) {
									tcur.tv_sec = 0;
									tcur.tv_nsec = 0;
								} else if (errno != EINTR) {
									log_out (lg, LV_FATAL, "nanosleep returns due to unexpected errno (%d, %m)", errno);
									running = false;
									st = 1;
								}
							}
# endif		/* USE_SLEEP */
					}
					log_out (lg, LV_INFO, "Going down");
					csig_free (csig);
				}
				log_out (lg, LV_DEBUG, "Deinitialize command %s", cmdtab[n].cmd);
				log_idpush (lg, cmdtab[n].cmd, NULL);
				fst = (*cmdtab[n].fdeinit) (data);
				log_idpop (lg);
				if (! fst)
					st = 1;
			} else
				log_out (lg, LV_FATAL, "Unable to setup command %s.", cmdtab[n].cmd);
			daemon_done (dm);
		} else
			log_out (lg, LV_FATAL, "Unable to start daemon mode.");
		daemon_free (dm);
	} else
		log_out (lg, LV_FATAL, "Unable to setup daemon mode.");
	log_free (lg);
	return st;
}/*}}}*/
