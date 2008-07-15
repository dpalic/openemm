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
# include	<stdio.h>
# include	<stdlib.h>
# include	<ctype.h>
# include	<string.h>
# include	<signal.h>
# include	<dirent.h>
# include	"agn.h"

int
main (int argc, char **argv) /*{{{*/
{
	int	rc;
	
	if ((argc == 1) || ((argc == 2) && (! strcasecmp (argv[1], "help")))) {
		fprintf (stderr, "Usage: %s [stop | <sendmail-options>]\n", argv[0]);
		return 0;
	}
	if ((argc == 2) && (! strcasecmp (argv[1], "stop"))) {
		int	n;
		
		rc = 0;
		for (n = 0; (n < 2) && (! rc); ++n) {
			int	sig;
			int	count;
			DIR	*dir;

			sig = n == 0 ? SIGTERM : SIGKILL;
			count = 0;
			if (dir = opendir ("/proc")) {
				struct dirent	*ent;

				while (ent = readdir (dir)) {
					char	*ptr;

					for (ptr = ent -> d_name; isdigit (*ptr); ++ptr)
						;
					if (! *ptr) {
						char	path[128];
						char	buf[512];
						FILE	*fp;
						
						sprintf (path, "/proc/%s/status", ent -> d_name);
						if (fp = fopen (path, "r")) {
							if ((fgets (buf, sizeof (buf) - 1, fp)) &&
							    (ptr = strchr (buf, '\n'))) {
								*ptr = '\0';
								if (! strcmp (skip (buf), "sendmail")) {
									int	pid;
									
									pid = atoi (ent -> d_name);
									printf (" -%d:%d", sig, pid);
									fflush (stdout);
									if (kill (pid, sig) == -1) {
										printf ("[failed %m]");
										fflush (stdout);
									} else
										++count;
								}
							}
							fclose (fp);
						}
					}
				}
				closedir (dir);
			}
			if ((n == 0) && count)
				sleep (2);
		}
	} else {
		setuid (0);
		setgid (0);
		setsid ();
		argv[0] = (char *) "/usr/sbin/sendmail";
		execv (argv[0], argv);
		fprintf (stderr, "Failed to start %s (%m)\n", argv[0]);
		rc = 1;
	}
	return rc;
}/*}}}*/
