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
/** @file net.c
 * Network related routines.
 */
# include	<string.h>
# include	<netdb.h>
# include	<sys/utsname.h>
# include	"agn.h"

/** Get full qualified domain name.
 * Retreive the full qualified domain name for a given hostname
 * @param name the hostname
 * @return the fqdn on success, NULL otherwise
 */
char *
get_fqdn (const char *name) /*{{{*/
{
	char		*fqdn;
	struct hostent	*hent;
	
	fqdn = NULL;
	sethostent (0);
	if ((hent = gethostbyname (name)) && hent -> h_name)
		fqdn = strdup (hent -> h_name);
	endhostent ();
	return fqdn;
}/*}}}*/
/** Get full qualified domain name for current system.
 * Retreive the fqdn for the local machine
 * @return the fqdn on success, NULL otherwise
 */
char *
get_local_fqdn (void) /*{{{*/
{
	char		*fqdn;
	struct utsname	un;
	
	fqdn = NULL;
	if (uname (& un) != -1)
		fqdn = get_fqdn (un.nodename);
	return fqdn;
}/*}}}*/
