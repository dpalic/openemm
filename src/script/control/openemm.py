#	-*- python -*-
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
libdir = home + os.path.sep + 'lib'
olibdir = libdir + os.path.sep + 'openemm'
cp = [olibdir]
for fname in os.listdir (olibdir):
	if fname.endswith ('.jar'):
		cp.append (olibdir + os.path.sep + fname)
#
# Optional commands
if len (sys.argv) > 1:
	os.chdir (home)
	if sys.argv[1] == 'setup':
		show ('setup:\n')
		show ('Setup database, please enter the super user password defined during MySQL instllation:\n')
		if os.system ('mysqladmin -u root -p create openemm'):
			error ('Failed to create database')
		show ('Database created, now setting up initial data, please type in again your super user password:\n')
		if os.system ('mysql -u root -p -e "source USR_SHARE\\openemm.sql" openemm'):
			error ('Failed to setup database')
	if sys.argv[1] in ('setup', 'config'):
		db = agn.DBase ()
		if not db:
			error ('Failed to setup database connection')
		i = db.newInstance ()
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
		prompt ('Congratulations, %s completed! [return] ' % sys.argv[1])
	elif sys.argv[1] == 'update':
		show ('update:\n')
		prompt ('As there is no previous version available, you are done! [return] ')
	else:
		error ('Unknown option %s' % sys.argv[1])
	sys.exit (0)

db = agn.DBase ()
if not db:
	error ('Failed to setup database connection')
i = db.newInstance ()
if not i:
	error ('Failed to connect to database')
i.close ()
db.close ()
show ('found database.\n')
#
# remove potential stale files
sessions = os.path.sep.join ([home, 'webapps', 'openemm', 'htdocs', 'WEB-INF', 'sessions'])
fnames = [agn.winstopfile]
for fname in os.listdir (sessions):
	fnames.append (sessions + os.path.sep + fname)
for fname in fnames:
	try:
		os.unlink (fname)
		show ('Removed stale file %s.\n' % fname)
	except WindowsError:
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
p_con = resinstart ('console')
if p_con == -1:
	error ('Failed to start console')
time.sleep (2)
p_rdir = resinstart ('redirection')
if p_rdir == -1:
	error ('Failed to start redirection')
#time.sleep (2)
prompt ('Running, press return for termination: ')
show ('Please press the Resin QUIT buttons to terminate java processes.\n')
#resinstop ('redirection')
#time.sleep (2)
#resinstop ('console')
show ('Signal termination to enviroment\n')
open (agn.winstopfile, 'w').close ()
time.sleep (3)
prompt ('Finished, press [return] ')
show ('(window closes on final termination of all processes) ')
time.sleep (2)
