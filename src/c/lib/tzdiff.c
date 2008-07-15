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
/** @file tzdiff.c
 * Timezone difference calculator.
 */
# include	<time.h>
# include	"agn.h"

/** Timezone diff to gm time.
 * Calculates the difference between localtime and gmrime in seconds
 * @param tim current time
 * @return the difference in seconds
 */
int
tzdiff (time_t tim) /*{{{*/
{
	int		diff;
	time_t		gm, loc;
	struct tm	*tp;
	struct tm	tt;

	diff = 0;
	if (tp = gmtime (& tim)) {
		tt = *tp;
		tt.tm_isdst = 0;
		if ((gm = mktime (& tt)) != (time_t) -1) {
			if (tp = localtime (& tim)) {
				tt = *tp;
				tt.tm_isdst = 0;
				if ((loc = mktime (& tt)) != (time_t) -1)
					diff = loc - gm;
			}
		}
	}
	return diff;
}/*}}}*/
		
