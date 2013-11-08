#!/bin/sh
#	-*- sh -*-

##################################################################################
# The contents of this file are subject to the Common Public Attribution
# License Version 1.0 (the "License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.openemm.org/cpal1.html. The License is based on the Mozilla
# Public License Version 1.1 but Sections 14 and 15 have been added to cover
# use of software over a computer network and provide for limited attribution
# for the Original Developer. In addition, Exhibit A has been modified to be
# consistent with Exhibit B.
# Software distributed under the License is distributed on an "AS IS" basis,
# WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
# the specific language governing rights and limitations under the License.
# 
# The Original Code is OpenEMM.
# The Original Developer is the Initial Developer.
# The Initial Developer of the Original Code is AGNITAS AG. All portions of
# the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
# Reserved.
# 
# Contributor(s): AGNITAS AG. 
##################################################################################
#
. $HOME/bin/scripts/config.sh
#
smenable="`$HOME/bin/scripts/smenable.py status`"
if [ ! "$smenable" ]; then
	echo "Unable to determinate status for sendmail usages, assuming sendmail is enabled"
	smenable="1"
fi
sm="$BASE/bin/smctrl"
case "$1" in
start)
	if [ "$smenable" = "1" ]; then
		mstart "Stopping obsolete sendmail processes: "
		$sm stop
		mend "done"
		#
		mstart "Starting sendmails: "
		mproceed "listener"
		run="/var/run"
		for i in sendmail mta; do
			if [ -d "$run/$i" ]; then
				run="$run/$i"
				break
			fi
		done
		$sm service start
		mproceed "admin queue"
		$sm -q1m -NNEVER -OQueueDirectory=$BASE/var/spool/ADMIN -OPidFile=$run/sendmail-openemm-admin.pid
		mproceed "mail queue"
		$sm -q1m -NNEVER -OQueueDirectory=$BASE/var/spool/QUEUE -OPidFile=$run/sendmail-openemm-queue.pid
		mend "done"
	else
	        starter $HOME/bin/scripts/semu.py
	fi
	;;
stop)
	if [ "$smenable" = "1" ]; then
		mstart "Stop all sendmail processes: "
		$sm service stop
		$sm stop
		mend "done"
	else
		softterm scripts/semu.py
	fi
	;;
*)
	echo "Usage: $0 [ start | stop ]"
	exit 1
	;;
esac
