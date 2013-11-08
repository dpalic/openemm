IMPORTANT INFORMATION
=====================
Since version 6.0 Java 5 is no longer supported because SUN stopped free
support in October 2009.

Since version 5.5 the login on port 8081 is no longer supported, please use
port 8080 instead.

If you used the online update of OpenEMM to upgrade to this version, please
read file UPDATE.txt in this directory to finish the update process manually.

If you want to upgrade OpenEMM manually, please read the section below.

If you want to install OpenEMM from scratch we strongly recommend to download
and consult the extensive OpenEMM Install Guide (PDF format) to avoid problems.

To use the online update feature of OpenEMM (LInux only) you have to open
TCP/IP port 8044 in the same way you opened port 8080 as described in the
OpenEMM Install Guide.


OpenEMM QUICK UPDATE GUIDE for Red Hat and compatible Linux distributions
=========================================================================
1. Download binary tarball to directory /tmp from web address
   http://sourceforge.net/projects/openemm/files/

2. Stop the old OpenEMM and rename the old OpenEMM directory
$> su - openemm
$> OpenEMM.sh stop
$> exit
$> cd /home
$> mv openemm openemm_backup

3. Create the new OpenEMM directory
$> mkdir openemm
$> cd openemm

4. Untar OpenEMM tarball as root
$> tar -xvzpf /tmp/OpenEMM-6.2-bin.tar.gz
   (please do not forget option "p"!)

5. Copy content for /usr/share/doc
$> mkdir -p /usr/share/doc/OpenEMM-6.2
$> mv USR_SHARE/* /usr/share/doc/OpenEMM-6.2
$> rm -rf USR_SHARE

6. Replace the generic string "http://localhost:8080" with the domain name of
   your server (like "http://www.domain.com:8080") in these files:
   - /usr/share/doc/OpenEMM-6.2/openemm-6.2.sql (once)
   - /home/openemm/webapps/core/WEB-INF/classes/emm.properties (twice)
   - /home/openemm/webapps/core/WEB-INF/classes/cms.properties (once)

7. Copy the modifications you made to the "old" files emm.properties and
   cms.properties (found in /home/openemm_backup/webapps/core/WEB-INF/classes/)
   to the new files.

8. Start MySQL DBMS and update the OpenEMM DB and CMS DB
   (depending on the version which you update from you have to update the
    databases step by step through executing the corresponding SQL files
    in the right order - please see OpenEMM Install Guide for details)
$> /etc/init.d/mysqld start
$> mysql -u root -p openemm < /usr/share/doc/OpenEMM-6.2/update_openemm-...
$> mysql -u root -p openemm < /usr/share/doc/OpenEMM-6.2/update_openemm-...

9. Launch OpenEMM
$> su - openemm
$> OpenEMM.sh start
$> exit


OPENEMM FILES
=============
MySQL OpenEMM CMS database dump:
/usr/share/doc/OpenEMM-6.2/openemm_cms.sql

MySQL OpenEMM CMS demo database dump:
/usr/share/doc/OpenEMM-6.2/openemm_demo-cms.sql


OpenEMM Change Log:
/usr/share/doc/OpenEMM-6.2/CHANGELOG.txt

OpenEMM License:
/usr/share/doc/OpenEMM-6.2/LICENSE.txt

ThirdPartyLicenses:
/usr/share/doc/OpenEMM-6.2/ThirdPartyLicenses/


MySQL OpenEMM database dump:
/usr/share/doc/OpenEMM-6.2/openemm-6.2.sql

MySQL database update (for OpenEMM 5.0.3 and earlier)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.0.3-5.1.0.sql

MySQL database update (for OpenEMM 5.1.0)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.1.0-5.3.0.sql

MySQL database update (for OpenEMM 5.3.0)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.3.0-5.3.1.sql

MySQL database update (for OpenEMM 5.3.1)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.3.1-5.3.2.sql

MySQL database update (for OpenEMM 5.3.2)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.3.2-5.4.0.sql

MySQL database update (for OpenEMM 5.4.0)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.4.0-5.5.0.sql

MySQL database update (for OpenEMM 5.5.0)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.5.0-5.5.1.sql

MySQL database update (for OpenEMM 5.5.1)
/usr/share/doc/OpenEMM-6.2/update_openemm-5.5.1-6.0.sql

MySQL database update (for OpenEMM 6.0)
/usr/share/doc/OpenEMM-6.2/update_openemm-6.0-6.0.1.sql

MySQL database update (for OpenEMM 6.0.1)
/usr/share/doc/OpenEMM-6.2/update_openemm-6.0.1-6.1.sql

MySQL database update (for OpenEMM 6.1)
/usr/share/doc/OpenEMM-6.2/update_openemm-6.1-6.2.sql

System info for OpenEMM Update feature:
/usr/share/doc/OpenEMM-6.2/empty-updates.txt

Script which is executed by OpenEMM's online update
after the upgrade to the current version:
/usr/share/doc/OpenEMM-6.2/upgrade-postproc.sh


MySQL Database Conversion Script to convert the whole database
to UTF-8 character set (if you want to use OpenEMM beyond the
5.4.0 release, you must once convert your database):
/home/openemm/bin/openemm-charset-convert.sh


MISCELLANEOUS
=============
Website:    http://www.openemm.org
Twitter:    http://www.twitter.com/openemm
Support:    http://www.openemm.org/support.html
Facebook:   http://www.facebook.com/openemm
Newsletter: http://www.openemm.org/newsletter.html
Maintainer: Martin Aschoff (ma AT os-inside DOT org)

OpenEMM uses the Open Source Initiative Approved License "Common Public
Attribution License 1.0 (CPAL)". Open Source Initiative Approved is a
trademark of the Open Source Initiative.

Copyright (c) 2006-2010 AGNITAS AG, Munich, Germany
