#	-*- python -*-
"""**********************************************************************************
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
**********************************************************************************
"""
#
import	sys, os, time
#
def show (s):
	sys.stderr.write (s)
	sys.stderr.flush ()
def prompt (prmt):
	if prmt:
		show (prmt)
	return sys.stdin.readline ().strip ()
def error (msg):
	show (msg + '\n')
	prompt ('[press return]')
	sys.exit (1)
def addpath (path):
	parts = os.environ['PATH'].split (os.path.pathsep)
	if not path in parts:
		parts.insert (0, path)
		os.environ['PATH'] = os.path.pathsep.join (parts)
def checkprop (homedir):
	replaces = [
		'jdbc.url=jdbc:mysql://127.0.0.1/openemm',
		'system.script_logdir=var\\log',
		'system.upload_archive=var\\tmp',
		'system.attachment_archive=var\\tmp',
		'log4j.appender.LOGFILE.File=var\\log\\emm_axis.log',
		'log4j.appender.STRUTSLOG.File=var\\log\\emm_struts.log',
		'mailgun.ini.maildir=var\\\\spool\\\\ADMIN',
		'mailgun.ini.metadir=var\\\\spool\\\\META',
		'mailgun.ini.xmlback=bin\\xmlback.exe',
		'mailgun.ini.account_logfile=var\\\\spool\\\\log\\\\account.log'
	]
	ignores = [
		'system.url',
		'system.updateserver'
	]
	rplc = {}
	for replace in replaces:
		parts = replace.split ('=', 1)
		rplc[parts[0].strip ()] = replace.replace ('\\', '\\\\') + '\n'
	prop = os.path.sep.join ([homedir, 'webapps', 'core', 'WEB-INF', 'classes', 'emm.properties'])
	save = prop + '.orig'
	fd = open (prop)
	content = fd.readlines ()
	fd.close ()
	ncontent = []
	changed = False
	for line in content:
		if line[0] != '#':
			parts = line.split ('=', 1)
			if len (parts) == 2:
				if rplc.has_key (parts[0]):
					nline = rplc[parts[0]]
					if nline != line:
						line = nline
						changed = True
				elif not parts[0] in ignores:
					if '/' in line:
						error ('Found possible invalid entry in %s: %s' % (prop, line))
		ncontent.append (line)
	if changed:
		try:
			os.rename (prop, save)
		except (WindowsError, OSError):
			pass
		fd = open (prop, 'w')
		fd.write (''.join (ncontent))
		fd.close ()
#
show ('Starting up .. ')
try:
	homedrive = os.environ['HOMEDRIVE']
except KeyError:
	homedrive = 'C:'
home = homedrive + os.path.sep + 'OpenEMM'
if not os.path.isdir (home):
	guess = None
	for disk in 'CDEFGHIJKLMNOPQRSTUVWXYZ':
		temp = disk + ':' + os.path.sep + 'OpenEMM'
		if os.path.isdir (temp):
			guess = temp
			break
	if guess is None:
		error ('Failed to find homedir "%s"' % home)
	home = guess
show ('home is %s .. ' % home)
checkprop (home)
#
os.environ['HOME'] = home
binhome = home + os.path.sep + 'bin'
addpath (binhome)
schome = binhome + os.path.sep + 'scripts'
os.environ['PYTHONPATH'] = schome
if not schome in sys.path:
	sys.path.append (schome)
os.environ['LC_ALL'] = 'C'
os.environ['LANG'] = 'en_US.ISO8859_1'
os.environ['NLS_LANG'] = 'american_america.UTF8'
resin = binhome + os.path.sep + 'httpd.exe'

import	agn
agn.require ('2.0.0')
show ('found codebase .. ')
#
# Check for working database
if not 'DBase' in dir (agn):
	error ('No database module found')
#
# add python to path
addpath (agn.pythonpath)
#
# find jdk
jdkkey = r'SOFTWARE\JavaSoft\Java Development Kit'
version = agn.winregFind (jdkkey, 'CurrentVersion')
if version is None:
	error ('JDK not found')
javahome = agn.winregFind (jdkkey + '\\' + version, 'JavaHome')
addpath (javahome + os.path.sep + 'bin')
#
# find mysql
mskey = r'SOFTWARE\MySQL AB\MySQL Server 5.0'
mysqlhome = agn.winregFind (mskey, 'Location')
if not mysqlhome is None:
	addpath (mysqlhome + os.path.sep + 'bin')
#
# build additional CLASSPATH
cp = []
#
# Optional commands
if len (sys.argv) > 1:
	os.chdir (home)
	versionTable = '__version'
	curversion = '5.5.1'
	if sys.argv[1] == 'setup':
		show ('setup:\n')
		show ('Setup database, please enter the super user password defined during MySQL instllation:\n')
		if os.system ('mysqladmin -u root -p create openemm'):
			error ('Failed to create database')
		show ('Database created, now setting up initial data, please enter again your databae super user password:\n')
		if os.system ('mysql -u root -p -e "source USR_SHARE\\openemm.sql" openemm'):
			error ('Failed to setup database')
		show ('Database setup completed.\n')
		db = agn.DBase ()
		if not db is None:
			cursor = db.cursor ()
			if not cursor is None:
				cursor.execute ('CREATE TABLE %s (version varchar(50))' % versionTable)
				cursor.execute ('INSERT INTO %s VALUES (:version)' % versionTable, {'version': curversion})
				cursor.sync ()
				cursor.close ()
			db.close ()
	if sys.argv[1] in ('setup', 'config'):
		db = agn.DBase ()
		if not db:
			error ('Failed to setup database connection')
		i = db.cursor ()
		if not i:
			error ('Failed to connect to database')
		rdir = None
		mailloop = None
		for r in i.query ('SELECT rdir_domain, mailloop_domain FROM company_tbl WHERE company_id = 1'):
			rdir = r[0]
			mailloop = r[1]
		if sys.argv[1] == 'config':
			show ('config:\n')
		if rdir is None: rdir = ''
		nrdir = prompt ('Enter redirection domain [%s]: ' % rdir)
		if not nrdir: nrdir = rdir
		if mailloop is None: mailloop = ''
		nmailloop = prompt ('Enter mailloop domain [%s]: ' % mailloop)
		if not nmailloop: nmailloop = mailloop
		if nrdir != rdir or nmailloop != mailloop:
			i.update ('UPDATE company_tbl SET rdir_domain = :rdir, mailloop_domain = :mailloop WHERE company_id = 1',
				  { 'rdir': nrdir, 'mailloop': nmailloop })
			db.commit ()
		i.close ()
		db.close ()
		sfname = 'conf' + os.path.sep + 'smart-relay'
		try:
			fd = open (sfname)
			sr = fd.read ().strip ()
			fd.close ()
		except IOError:
			sr = ''
		show ('Smart mail relay - optional parameter. Specifiy this, if you want to send\n')
		show ('all your outgoing mail via one deticated server (e.g. your ISP mail server.)\n')
		show ('You may add login information in the form <username>:<password>@<relay> if\n')
		show ('the smart relay requires authentication.\n')
		nsr = prompt ('Enter smart relay (or just - to remove existing one) [%s]: ' % sr)
		if nsr:
			if nsr == '-':
				try:
					os.unlink (sfname)
				except (WindowsError, OSError):
					pass
			elif nsr != sr:
				fd = open (sfname, 'w')
				fd.write ('%s\n' % nsr)
				fd.close ()
		prompt ('Congratulations, %s completed! [return] ' % sys.argv[1])
	elif sys.argv[1] == 'update':
		show ('update:\n')
		db = agn.DBase ()
		if not db:
			error ('Failed to setup database connection')
		i = db.cursor ()
		if not i:
			error ('Failed to connect to database')
		found = False
		for r in i.query ('SHOW TABLES'):
			if r[0] == versionTable:
				found = True
				break
		if not found:
			version = '5.1.0'
			tempfile = 'version.sql'
			fd = open (tempfile, 'w')
			fd.write ('CREATE TABLE %s (version varchar(50));\n' % versionTable)
			fd.close ()
			show ('Database update, please enter your database super user password now\n')
			st = os.system ('mysql -u root -p -e "source %s" openemm' % tempfile)
			try:
				os.unlink (tempfile)
			except (WindowsError, OSError):
				pass
			if st:
				error ('Failed to setup database')
			i.update ('INSERT INTO %s VALUES (:version)' % versionTable, {'version': version })
			db.commit ()
		else:
			version = None
			for r in i.query ('SELECT version FROM %s' % versionTable):
				version = r[0]
			if version is None:
				error ('Found version table, but no content in table')
			elif version == '5.1.0':
				version = '5.1.1'
			elif version == '5.1.1':
				version = '5.3.0'
		ans = prompt ('It looks like your previous version is "%s", is this corrent? [no] ' % version)
		if not ans or not ans[0] in 'Yy':
			error ('Version conflict!')
		updates = []
		for fname in os.listdir ('USR_SHARE'):
			if fname.endswith ('.usql'):
				base = fname[:-5]
				parts = base.split ('-')
				if len (parts) == 2:
					updates.append ([parts[0], parts[1], 'USR_SHARE\\%s' % fname])
		seen = []
		while version != curversion:
			found = False
			oldversion = version
			for upd in updates:
				if upd[0] == version and not upd[2] in seen:
					try:
						fd = open (upd[2])
						cont = fd.read ()
						fd.close ()
						isEmpty = (len (cont) == 0)
					except IOError:
						isEmpty = False
					if not isEmpty:
						show ('Database upgrade from %s to %s, please enter your super user password now\n' % (version, upd[1]))
						if os.system ('mysql -u root -p -e "source %s" openemm' % upd[2]):
							error ('Failed to update')
					else:
						show ('No database update from %s to %s required\n' % (version, upd[1]))
					version = upd[1]
					seen.append (upd[2])
					i.update ('UPDATE %s SET version = :version' % versionTable, {'version': version})
					db.commit ()
					found = True
					break
			if not found:
				error ('No update from %s to %s found' % (version, curversion))
			if oldversion == '5.3.2':
				i.close ()
				db.close ()
				dbfile = os.path.sep.join (['var', 'tmp', 'openemm.dump'])
				dbconv = os.path.sep.join (['var', 'tmp', 'openemm.conv'])
				show ('===========================================================================\n')
				show ('!! Please read and follow the next steps carefully to avoid loss of data !!\n')
				show ('Now forcing cleanup of the database, please enter your super user password now\n')
				if os.system ('mysqldump -aCceQ --lock-all-tables -u root -p -r %s openemm' % dbfile):
					error ('Failed to dump current database')
				try:
					fdi = open (dbfile, 'r')
					fdo = open (dbconv, 'w')
				except IOError, e:
					error ('Failed to open database convertion file %s %s' % (dbconv, `e.args`))
				fdo.write ('ALTER DATABASE openemm DEFAULT CHARACTER SET utf8;\n')
				for line in fdi.readlines ():
					line = line.replace (' character set utf8 collate utf8_unicode_ci', '')
					line = line.replace (' collate utf8_unicode_ci', '')
					line = line.replace ('DEFAULT CHARSET=latin1', 'DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci')
					fdo.write (line)
				fdo.close ()
				fdi.close ()
				show ('Now we remove and recreate the database and import the converted content.\n')
				show ('Please enter your super user database password each time, if asked for:\n')
				state = 0
				while state < 3:
					if state == 0:
						action = 'Drop database'
						command = 'mysqladmin -u root -p drop openemm'
					elif state == 1:
						action = 'Create database'
						command = 'mysqladmin -u root -p create openemm'
					elif state == 2:
						action = 'Import database'
						command = 'mysql -u root -p openemm < %s' % dbconv
					show ('--> %s:\n' % action)
					if os.system (command):
						show ('Command failed! If you have just mistyped your password, just try\n')
						show ('again, otherwise abort the update and fix the problem by hand.\n')
					else:
						state += 1
				try:
					os.unlink (dbconv)
				except (WindowsError, OSError):
					pass
				db = agn.DBase ()
				i = db.cursor ()
				show ('===========================================================================\n')
		i.close ()
		db.close ()
		show ('Update to version %s finished! You may start config.bat now to see\n' % version)
		prompt ('if there are some new things to setup [return] ')
	else:
		error ('Unknown option %s' % sys.argv[1])
	sys.exit (0)

db = agn.DBase ()
if not db:
	error ('Failed to setup database connection')
i = db.cursor ()
if not i:
	error ('Failed to connect to database')
i.close ()
db.close ()
show ('found database.\n')
#
# remove potential stale files
sessions = os.path.sep.join ([home, 'webapps', 'core', 'WEB-INF', 'sessions'])
fnames = [agn.winstopfile]
if os.path.isdir (sessions):
	for fname in os.listdir (sessions):
		fnames.append (sessions + os.path.sep + fname)
	for fname in fnames:
		try:
			os.unlink (fname)
#			show ('Removed stale file %s.\n' % fname)
		except (WindowsError, OSError):
			pass
#
# change to home directory
os.chdir (home)
def pystart (cmd):
	args = cmd.split ()
	args.insert (0, agn.pythonbin)
	return os.spawnv (os.P_NOWAIT, args[0], args)

def resinexec (module, what):
	lpath = home + os.path.sep + 'var' + os.path.sep + 'log' + os.path.sep
	lout = lpath + module + '_stdout.log'
	lerr = lpath + module + '_stderr.log'
	cmd = '%s -conf %s%sconf%s%s.conf' % (resin, home, os.path.sep, os.path.sep, module)
	cmd += ' -verbose'
	cmd += ' -jvm-log %s%s.log' % (lpath, module)
	cmd += ' -resin-home %s' % home
	cmd += ' -server %s' % module
#	cmd += ' %s' % what
	args = cmd.split ()
	env = os.environ.copy ()
	env['LANG'] = 'en_US.ISO8859_1'
	env['CLASSPATH'] = os.path.pathsep.join (cp)
	saveout = os.dup (1)
	saveerr = os.dup (2)
	os.close (1)
	os.close (2)
	os.open (lout, os.O_WRONLY | os.O_APPEND | os.O_CREAT, 0666)
	os.open (lerr, os.O_WRONLY | os.O_APPEND | os.O_CREAT, 0666)
	pid = os.spawnve (os.P_NOWAIT, args[0], args, env)
	os.close (1)
	os.close (2)
	os.dup (saveout)
	os.dup (saveerr)
	os.close (saveout)
	os.close (saveerr)
	return pid
	
def resinstart (module):
	return resinexec (module, 'start')
def resinstop (module):
	return resinexec (module, 'stop')
os.system ('bin\\xmlback.exe -D > var\\spool\\META\\blockmail.dtd')
p_upd = pystart (schome + os.path.sep + 'update.py account bounce')
if p_upd == -1:
	error ('Failed to start update process')
p_dst = pystart (schome + os.path.sep + 'pickdist.py')
if p_dst == -1:
	error ('Failed to start pickdist process')
p_bav = pystart (schome + os.path.sep + 'bav-update.py')
if p_bav == -1:
	error ('Failed to start bav-update process')
p_sem = pystart (schome + os.path.sep + 'semu.py')
if p_sem == -1:
	error ('Failed to start semu process')
p_con = resinstart ('core')
if p_con == -1:
	error ('Failed to start core')
prompt ('Running, press return for termination: ')
show ('Please press the Resin QUIT button to terminate java process.\n')
#resinstop ('redirection')
#time.sleep (2)
#resinstop ('console')
show ('Signal termination to enviroment\n')
open (agn.winstopfile, 'w').close ()
time.sleep (3)
prompt ('Finished, press [return] ')
show ('(window closes on final termination of all processes) ')
time.sleep (2)
