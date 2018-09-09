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
# include	<fcntl.h>
# include	<ctype.h>
# include	<string.h>
# include	<signal.h>
# include	<dirent.h>
# include	<errno.h>
# include	<sys/ioctl.h>
# include	"agn.h"

# define	MTA_SENDMAIL	0
# define	MTA_POSTFIX	1

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
static void
detach (void) /*{{{*/
{
	int	master, slave;

	setuid (geteuid ());
	setgid (getegid ());
	setsid ();
	if ((tcgetpgrp (0) == -1) && (errno == ENOTTY)) {
		if (openpty (& master, & slave, NULL, NULL, NULL) != -1) {
			ioctl (slave, TIOCSCTTY, 0);
		}
	}
	csig_alloc (SIGINT, SIG_IGN, SIGHUP, SIG_IGN, SIGCHLD, SIG_DFL, NULL);
}/*}}}*/
int
main (int argc, char **argv) /*{{{*/
{
	int		rc;
	int		mta;
	const char	*temp;
	
	if ((temp = getenv ("MTA")) && (! strcmp (temp, "postfix"))) {
		mta = MTA_POSTFIX;
	} else {
		mta = MTA_SENDMAIL;
	}
	if ((argc <= 1) || ((argc == 2) && (! strcasecmp (argv[1], "help")))) {
		const char	*mta_name;
		
		fprintf (stderr, "Usage: %s [stop [<pattern>] | service <option> | <mta-options>]\n", argv[0]);
		switch (mta) {
		case MTA_SENDMAIL:
			mta_name = "sendmail";
			break;
		case MTA_POSTFIX:
			mta_name = "postfix";
			break;
		default:
			mta_name = "*unknown*";
			break;
		}
		fprintf (stderr, "\tusing %s as mta\n", mta_name);
		return 0;
	}
	if (! strcasecmp (argv[1], "stop")) {
		if (mta == MTA_SENDMAIL) {
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
		}
	} else {
		detach ();
		if ((argc == 3) && (! strcmp (argv[1], "service"))) {
			const char	*args[4];
			int		n;
			
# define	CMD_SERVICE		"/sbin/service"
# define	CMD_INIT		(mta == MTA_SENDMAIL ? "/etc/init.d/sendmail" : "/etc/init.d/postfix")
# define	CMD_SH			"/bin/sh"			
			if (access (CMD_SERVICE, X_OK) != -1) {
				args[0] = CMD_SERVICE;
				args[1] = (mta == MTA_SENDMAIL ? "sendmail" : "postfix");
				n = 2;
			} else if (access (CMD_INIT, X_OK) != -1) {
				args[0] = CMD_INIT;
				n = 1;
			} else if ((access (CMD_INIT, F_OK) != 1) && (access (CMD_SH, X_OK) != -1)) {
				args[0] = CMD_SH;
				args[1] = CMD_INIT;
				n = 2;
			} else
				n = -1;
			if (strcmp (argv[2], "stop") && strcmp (argv[2], "start") && strcmp (argv[2], "restart") && strcmp (argv[2], "reload") && strcmp (argv[2], "status"))
				fprintf (stderr, "Unknown service command \"%s\"\n", argv[2]);
			else if (n < 0)
				fprintf (stderr, "Unknown system process starting.\n");
			else {
				args[n++] = argv[2];
				args[n] = NULL;
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
