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
# include	<unistd.h>
# include	<fcntl.h>
# include	<ctype.h>
# include	<string.h>
# include	<signal.h>
# include	<dirent.h>
# include	"agn.h"

static bool_t
match (const char *pidstr, char **pattern) /*{{{*/
{
	bool_t	rc;
	char	path[128];
	int	fd;
	
	rc = false;
	sprintf (path, "/proc/%s/cmdline", pidstr);
	if ((fd = open (path, O_RDONLY)) != -1) {
		int	n;
		char	buf[4096];
		
		if ((n = read (fd, buf, sizeof (buf) - 1)) > 0) {
			buf[n] = '\0';
			while ((n > 0) && (! buf[n - 1]))
				--n;
			--n;
			while (n >= 0) {
				if (! buf[n])
					buf[n] = ' ';
				--n;
			}
			for (n = 0; pattern[n]; ++n)
				if (! strstr (buf, pattern[n]))
					break;
			if (! pattern[n])
				rc = true;
		}
		close (fd);
	}
	return rc;
}/*}}}*/
int
main (int argc, char **argv) /*{{{*/
{
	int	rc;
	
	if ((argc <= 1) || ((argc == 2) && (! strcasecmp (argv[1], "help")))) {
		fprintf (stderr, "Usage: %s [stop [<pattern>] | service <option> | <sendmail-options>]\n", argv[0]);
		return 0;
	}
	if (! strcasecmp (argv[1], "stop")) {
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
									
									if ((argc == 2) || match (ent -> d_name, argv + 2)) {
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
		if ((argc == 3) && (! strcmp (argv[1], "service"))) {
			const char	*args[4];
			
			args[0] = "/sbin/service";
			args[1] = "sendmail";
			if (strcmp (argv[2], "stop") && strcmp (argv[2], "start") && strcmp (argv[2], "restart") && strcmp (argv[2], "status"))
				fprintf (stderr, "Unknown service command \"%s\"\n", argv[2]);
			else {
				args[2] = argv[2];
				args[3] = NULL;
				execv (args[0], (char *const *) args);
				fprintf (stderr, "Failed to start %s (%m)\n", args[0]);
			}
		} else {
			argv[0] = (char *) "/usr/sbin/sendmail";
			execv (argv[0], argv);
			fprintf (stderr, "Failed to start %s (%m)\n", argv[0]);
		}
		rc = 1;
	}
	return rc;
}/*}}}*/
