#	-*- mode: python; mode: fold -*-
#
"""

**********************************************************************************
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
Support routines for general and company specific purposes:
	class struct:     general empty class for temp. structured data
	class AgnError:   general exception thrown by this module (deprectated)
	class error:	  new version for general execption
	def chop:         removes trailing newlines
	def atob:         converts a string to a boolean value  
	def filecount:    counts files matching a pattern in a directory
	def which:        finds program in path
	def mkpath:       creates a path from path components
	def fingerprint:  calculates a fingerprint from a file
	def toutf8:       converts input string to UTF-8 encoding
	def fromutf8:     converts UTF-8 encoded strings to unicode
	def msgn:         output a message on stdout, if verbose ist set
	def msgcnt:       output a number for progress
	def msg:          output a message with trailing newline on stdout,
	                  if verbose is set
	def err:          output a message on stderr

	class Backlog:    support class for enabling backlogging
	def level_name:   returns a string representation of a log level
	def logfilename:  creates the filename to write logfiles to
	def logappend:    copies directly to logfile
	def log:          writes an entry to the logfile
	def mark:         writes a mark to the logfile, if nothing had been
	                  written for a descent time
	def backlogEnable: switch backlogging on
	def backlogDisable: switch backlogging off
	def backlogRestart: flush all recorded entries and restart with
	                    a clean buffer
	def backlogSuspend: suspend storing entries to backlog
	def backlogResume: resume storing entries to backlog
	def backlogSave:  write current backlog to logfile

	def lock:         creates a lock for this running process
	def unlock:       removes the lock
	def signallock:   send signal to process owing a lockfile

	class Filepos:    line by line file reading with remembering th
	                  the file position
	
	def die:          terminate the program removing aquired lock, if
	                  neccessary
	rip = die         alias for die
	
	def mailsend:     send a mail using SMTP
	class Filesystem: retreives informations about a filesystem
	
	class Process:    support class for Processtable
	
	class Processtable:    retreive informations about running
	                  processes
	
	class Memory:      retreive informations about memory usage
	
	class UID:         handles parsing and validation of UIDs

	class DBInstance:  a cursor instance for database access
	class DBase:       an interface to the database
"""
#
# Imports, Constants and global Variables
#{{{
import	sys, os, types, errno, stat, signal
import	string, time, re, socket, md5, sha
import	platform, traceback, codecs
import	smtplib
try:

	import	MySQLdb
	database = MySQLdb
except ImportError:
	database = None
try:
	True, False
except NameError:
	True = 1
	False = 0
#
version = ('1.5.7', '2007-09-11 16:30:05 CEST', 'ud')
#
verbose = 1
system = platform.system ().lower ()
host = platform.node ()
if host.find ('.') != -1:
	host = host.split ('.')[0]

if system == 'windows':
	import	_winreg
	
	def winregFind (key, qkey):
		try:
			value = None
			rkey = _winreg.OpenKey (_winreg.HKEY_LOCAL_MACHINE, key)
			found = True
			n = 0
			while value is None:
				temp = _winreg.EnumValue (rkey, n)
				if qkey is None or qkey == temp[0]:
					value = temp[1]
				n += 1
			rkey.Close ()
		except WindowsError:
			value = None
		return value

	pythonbin = winregFind (r'SOFTWARE\Classes\Python.File\shell\open\command', None)
	if pythonbin is None:
		pythonbin = r'C:\Python25\python.exe'
	else:
		pythonbin = pythonbin.split ()[0]
		if len (pythonbin) > 1 and pythonbin[0] == '"' and pythonbin[-1] == '"':
			pythonbin = pythonbin[1:-1]
	pythonpath = os.path.dirname (pythonbin)
	try:
		home = os.environ['HOMEDRIVE'] + '\\OpenEMM'
	except KeyError:
		home = 'C:\\OpenEMM'
	os.environ['HOME'] = home
	iswin = True
	
	winstopfile = home + os.path.sep + 'var' + os.path.sep + 'run' + os.path.sep + 'openemm.stop'
	def winstop ():
		return os.path.isfile (winstopfile)
else:
	iswin = False
#
try:
	base = os.environ['HOME']
except KeyError, e:
	base = '.'

scripts = base + os.path.sep + 'bin' + os.path.sep + 'scripts'
if not scripts in sys.path:
	sys.path.insert (0, scripts)
#}}}
#
# Support routines
#{{{
class struct:
	"""class struct:

General empty class as placeholder for temp. structured data"""
	pass

class error (Exception):
	"""class error (Exception):

This is a general exception thrown by this module."""
	def __init__ (self, msg = None):
		Exception.__init__ (self, msg)
		self.msg = msg
AgnError = error

def require (checkversion):
	if cmp (checkversion, version[0]) > 0:
		raise error ('Version too low, require at least %s, found %s' % (checkversion, version[0]))
		
def chop (s):
	"""def chop (s):

removes any trailing LFs and CRs."""
	while len (s) > 0 and s[-1] in '\r\n':
		s = s[:-1]
	return s

def atob (s):
	"""def atob (s):

tries to interpret the incoming string as a boolean value"""
	if s and len (s) > 0 and s[0] in [ '1', 'T', 't', 'Y', 'y', '+' ]:
		return True
	return False

def filecount (directory, pattern):
	"""def filecount (directory, pattern):

counts the files in dir which are matching the regular expression
in pattern."""
	pat = re.compile (pattern)
	dirlist = os.listdir (directory)
	count = 0
	for fname in dirlist:
		if pat.search (fname):
			count += 1
	return count

def which (program):
	"""def which (program):

finds 'program' in the $PATH enviroment, returns None, if not available."""
	rc = None
	try:
		paths = os.environ['PATH'].split (':')
	except KeyError:
		paths = []
	for path in paths:
		if path:
			p = path + os.path.sep + program
		else:
			p = program
		if os.access (p, os.X_OK):
			rc = p
			break
	return rc

def mkpath (*parts, **opts):
	"""def mkpath (*parts, **opts):

create a valid pathname from the elements"""
	try:
		absolute = opts['absolute']
	except KeyError:
		absolute = False
	rc = os.path.sep.join (parts)
	if absolute and not rc.startswith (os.path.sep):
		rc = os.path.sep + rc
	if iswin:
		try:
			drive = opts['drive']
			if len (drive) == 1:
				rc += drive + ':' + rc
		except KeyError:
			pass
	return os.path.normpath (rc)

def fingerprint (fname):
	"""def fingerprint (fname):

calculates a MD5 hashvalue (a fingerprint) of a given file."""
	fp = md5.new ()
	fd = open (fname, 'r')
	while 1:
		chunk = fd.read (65536)
		if chunk == '':
			break
		fp.update (chunk)
	fd.close ()
	return fp.hexdigest ()

__encoder = codecs.getencoder ('UTF-8')
def toutf8 (s, charset = 'ISO-8859-1'):
	"""def toutf8 (s, [charset]):

convert unicode (or string with charset information) inputstring
to UTF-8 string."""
	if type (s) == types.StringType:
		s = unicode (s, charset)
	return __encoder (s)[0]
def fromutf8 (s):
	"""def fromutf8 (s):

converts an UTF-8 coded string to a unicode string."""
	return unicode (s, 'UTF-8')

def msgn (s):
	"""def msgn (s):

prints s to stdout, if the module variable verbose is not equal to 0."""
	global	verbose

	if verbose:
		sys.stdout.write (s)
		sys.stdout.flush ()
def msgcnt (cnt):
	"""def msgcnt (cnt):

prints a counter to stdout. If the number has more than eight digits, this
function will fail. msgn() is used for the output itself."""
	msgn ('%8d\b\b\b\b\b\b\b\b' % cnt)
def msg (s):
	"""def msg (s):

prints s with a newline appended to stdout. msgn() is used for the output
itself."""
	msgn (s + '\n')
def err (s):
	"""def err (s):

prints s with a newline appended to stderr."""
	sys.stderr.write (s + '\n')
	sys.stderr.flush ()
#}}}
#
# 1.) Logging
#
#{{{
class Backlog:
	def __init__ (self, maxcount, level):
		self.maxcount = maxcount
		self.level = level
		self.backlog = []
		self.count = 0
		self.isSuspended = False
		self.asave = None
	
	def add (self, s):
		if not self.isSuspended and self.maxcount:
			if self.maxcount > 0 and self.count >= self.maxcount:
				self.backlog.pop (0)
			else:
				self.count += 1
			self.backlog.append (s)
	
	def suspend (self):
		self.isSuspended = True
	
	def resume (self):
		self.isSuspended = False
	
	def restart (self):
		self.backlog = []
		self.count = 0
		self.isSuspended = False
	
	def save (self):
		if self.count > 0:
			self.backlog.insert (0, '-------------------- BEGIN BACKLOG --------------------\n')
			self.backlog.append ('--------------------  END BACKLOG  --------------------\n')
			logappend (self.backlog)
			self.backlog = []
			self.count = 0
	
	def autosave (self, level):
		if not self.asave is None and level in self.asave:
			return True
		return False
	
	def addLevelForAutosave (self, level):
		if self.asave is None:
			self.asave = [level]
		elif not level in self.asave:
			self.asave.append (level)
	
	def removeLevelForAutosave (self, level):
		if not self.asave is None and level in self.asave:
			self.asave.remove (level)
			if not self.asave:
				self.asave = None
	
	def clearLevelForAutosave (self):
		self.asave = None
	
	def setLevelForAutosave (self, levels):
		if levels:
			self.asave = levels
		else:
			self.asave = None

LV_NONE = 0
LV_FATAL = 1
LV_ERROR = 2
LV_WARNING = 3
LV_NOTICE = 4
LV_INFO = 5
LV_VERBOSE = 6
LV_DEBUG = 7
loglevel = LV_WARNING
loghost = host
logname = None
logpath = None
outlevel = LV_FATAL
outstream = None
backlog = None
try:
	logpath = os.environ['LOG_HOME']
except KeyError:
	try:
		logpath = os.environ['HOME'] + os.path.sep + 'var' + os.path.sep + 'log'
	except KeyError:
		logpath = 'var' + os.path.sep + 'log'
if len (sys.argv) > 0:
	logname = os.path.basename (sys.argv[0])
	(basename, extension) = os.path.splitext (logname)
	if extension.lower () == '.py':
		logname = basename
if not logname:
	logname = 'unset'
loglast = 0
#
def level_name (lvl):
	"""def level_name (lvl):

returns a name for a numeric loglevel."""
	if lvl == LV_FATAL:
		name = 'FATAL'
	elif lvl == LV_ERROR:
		name = 'ERROR'
	elif lvl == LV_WARNING:
		name = 'WARNING'
	elif lvl == LV_NOTICE:
		name = 'NOTICE'
	elif lvl == LV_INFO:
		name = 'INFO'
	elif lvl == LV_VERBOSE:
		name = 'VERBOSE'
	elif lvl == LV_DEBUG:
		name = 'DEBUG'
	else:
		name = str (lvl)
	return name

def logfilename ():
	global	logpath, loghost, logname
	
	now = time.localtime (time.time ())
	return '%s%s%04d%02d%02d-%s-%s.log' % (logpath, os.path.sep, now[0], now[1], now[2], loghost, logname)

def logappend (s):
	global	loglast

	fname = logfilename ()
	try:
		fd = open (fname, 'a')
		if type (s) in types.StringTypes:
			fd.write (s)
		elif type (s) in (types.ListType, types.TupleType):
			for l in s:
				fd.write (l)
		else:
			fd.write (str (s) + '\n')
		fd.close ()
		loglast = int (time.time ())
	except Exception, e:
		err ('LOGFILE write failed[%s]: %s' % (`e.args`, `s`))

def log (lvl, ident, s):
	global	loglevel, logname, backlog

	if not backlog is None and backlog.autosave (lvl):
		backlog.save ()
		backlogIgnore = True
	else:
		backlogIgnore = False
	if lvl <= loglevel or \
	   (lvl <= outlevel and not outstream is None) or \
	   (not backlog is None and lvl <= backlog.level):
		if not ident:
			ident = logname
		now = time.localtime (time.time ())
		lstr = '[%02d.%02d.%04d  %02d:%02d:%02d] %d %s/%s: %s\n' % (now[2], now[1], now[0], now[3], now[4], now[5], os.getpid (), level_name (lvl), ident, s)
		if lvl <= loglevel:
			logappend (lstr)
		else:
			backlogIgnore = False
		if lvl <= outlevel and not outstream is None:
			outstream.write (lstr)
			outstream.flush ()
		if not backlogIgnore and not backlog is None and lvl <= backlog.level:
			backlog.add (lstr)

def mark (lvl, ident, dur = 60):
	global	loglast
	
	now = int (time.time ())
	if loglast + dur * 60 < now:
		log (lvl, ident, '-- MARK --')

def backlogEnable (maxcount = 100, level = LV_DEBUG):
	global	backlog
	
	if maxcount == 0:
		backlog = None
	else:
		backlog = Backlog (maxcount, level)

def backlogDisable ():
	global	backlog
	
	backlog = None

def backlogRestart ():
	global	backlog
	
	if not backlog is None:
		backlog.restart ()

def backlogSave ():
	global	backlog
	
	if not backlog is None:
		backlog.save ()

def backlogSuspend ():
	global	backlog
	
	if not backlog is None:
		backlog.suspend ()

def backlogResume ():
	global	backlog
	
	if not backlog is None:
		backlog.resume ()

def logExcept (typ, value, tb):
	ep = traceback.format_exception (typ, value, tb)
	rc = 'CAUGHT EXCEPTION:\n'
	for p in ep:
		rc += p
	backlogSave ()
	log (LV_FATAL, 'except', rc)
	err (rc)
sys.excepthook = logExcept
#}}}
#
# 2.) Locking
#
#{{{

if iswin:
	lockfd = None
lockname = None
try:
	lockpath = os.environ['LOCK_HOME']
except:
	try:
		lockpath = os.environ['HOME'] + os.path.sep + 'var' + os.path.sep + 'lock'
	except KeyError:
		lockpath = 'var' + os.path.sep + 'lock'

def _mklockpath (pgmname):
	global	lockpath
	
	return lockpath + os.path.sep + pgmname + '.lock'

def lock (isFatal = True):
	global	lockname, logname

	if lockname:
		return lockname
	name = _mklockpath (logname)
	s = '%10d\n' % (os.getpid ())
	report = 'Try locking using file "' + name + '"\n'
	n = 0
	while n < 2:
		n += 1
		try:
			if not lockname:
				fd = os.open (name, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0444)
				os.write (fd, s)
				os.close (fd)
				lockname = name

				if iswin:
					global	lockfd

					lockfd = open (lockname)
					os.chmod (lockname, 0777)
				report += 'Lock aquired\n'
		except OSError, e:
			if e.errno == errno.EEXIST:
				report += 'File exists, try to read it\n'
				try:
					fd = os.open (name, os.O_RDONLY)
					inp = os.read (fd, 32)
					os.close (fd)
					idx = inp.find ('\n')
					if idx != -1:
						inp = inp[:idx]
					inp = chop (inp)
					pid = int (inp)
					if pid > 0:
						report += 'Locked by process %d, look if it is still running\n' % (pid)
						try:

							if iswin:
								try:
									os.unlink (name)
									n -= 1
								except WindowsError:
									pass
							else:
								os.kill (pid, 0)
							report += 'Process is still running\n'
							n += 1
						except OSError, e:
							if e.errno == errno.ESRCH:
								report += 'Remove stale lockfile\n'
								try:
									os.unlink (name)
								except OSError, e:
									report += 'Unable to remove lockfile: ' + e.strerror + '\n'
							elif e.errno == errno.EPERM:
								report += 'Process is running and we cannot access it\n'
							else:
								report += 'Unable to check: ' + e.strerror + '\n'
				except OSError, e:
					report += 'Unable to read file: ' + e.strerror + '\n'
			else:
				report += 'Unable to create file: ' + e.strerror + '\n'
	if not lockname and isFatal:
		raise error (report)
	return lockname

def unlock ():
	global	lockname

	if lockname:
		try:

			if iswin:
				global	lockfd

				if not lockfd is None:
					lockfd.close ()
					lockfd = None
				os.chmod (lockname, 0777)
			os.unlink (lockname)
			lockname = None
		except OSError, e:
			if e.errno != errno.ENOENT:
				raise error ('Unable to remove lock: ' + e.strerror + '\n')

def signallock (program, signr = signal.SIGTERM):
	rc = False
	report = ''
	fname = _mklockpath (program)
	try:
		fd = open (fname, 'r')
		pline = fd.readline ()
		fd.close ()
		try:
			pid = int (pline.strip ())
			if pid > 0:
				try:
					os.kill (pid, signr)
					rc = True
					report = None
				except OSError, e:
					if e.errno == errno.ESRCH:
						report += 'Process %d does not exist\n' % pid
						try:
							os.unlink (fname)
						except OSError, e:
							report += 'Unable to remove stale lockfile %s %s\n' % (fname, `e.args`)
					elif e.errno == errno.EPERM:
						report += 'No permission to signal process %d\n' % pid
					else:
						report += 'Failed to signal process %d %s' % (pid, `e.args`)
			else:
				report += 'PIDFile contains invalid PID: %d\n' % pid
		except ValueError:
			report += 'Content of PIDfile is not valid: "%s"\n' % chop (pline)
	except IOError, e:
		if e.args[0] == errno.ENOENT:
			report += 'Lockfile %s does not exist\n' % fname
		else:
			report += 'Lockfile %s cannot be opened: %s\n' % (fname, `e.args`)
	return (rc, report)
#}}}
#
# 3.) file I/O
#
#{{{
archtab = {}
def mkArchiveDirectory (path, mode = 0777):
	global	archtab

	tt = time.localtime (time.time ())
	ts = '%04d%02d%02d' % (tt[0], tt[1], tt[2])
	arch = path + os.path.sep + ts
	if not archtab.has_key (arch):
		try:
			st = os.stat (arch)
			if not stat.S_ISDIR (st[stat.ST_MODE]):
				raise error ('%s is not a directory' % arch)
		except OSError, e:
			if e.args[0] != errno.ENOENT:
				raise error ('Unable to stat %s: %s' % (arch, e.args[1]))
			try:
				os.mkdir (arch, mode)
			except OSError, e:
				raise error ('Unable to create %s: %s' % (arch, e.args[1]))
		archtab[arch] = True
	return arch
	
seektab = []
class Filepos:
	def __stat (self, stat_file):
		try:
			if stat_file:
				st = os.stat (self.fname)
			else:
				st = os.fstat (self.fd.fileno ())
			rc = (st[stat.ST_INO], st[stat.ST_CTIME], st[stat.ST_SIZE])
		except:
			rc = None
		return rc

	def __open (self):
		global	seektab

		err = None
		if os.access (self.info, os.F_OK):
			try:
				fd = open (self.info, 'r')
				line = fd.readline ()
				fd.close ()
				parts = chop (line).split (':')
				if len (parts) == 3:
					self.inode = int (parts[0])
					self.ctime = int (parts[1])
					self.pos = int (parts[2])
				else:
					err = 'Invalid input for %s: %s' % (self.fname, line)
			except:
				err = 'Unable to read info file ' + self.info
		if not err:
			try:
				self.fd = open (self.fname, 'r')
			except:
				err = 'Unable to open %s' % self.fname
			if self.fd:
				st = self.__stat (False)
				if st:
					ninode = st[0]
					nctime = st[1]
					if ninode == self.inode:
						if st[2] >= self.pos:
							self.fd.seek (self.pos)
						else:
							self.pos = 0
					self.inode = ninode
					self.ctime = nctime
				else:
					err = 'Failed to stat %s' % self.fname
				if err:
					self.fd.close ()
					self.fd = None
		if err:
			raise error (err)
		if not self in seektab:
			seektab.append (self)

	def __init__ (self, fname, info, checkpoint = 64):
		self.fname = fname
		self.info = info
		self.checkpoint = checkpoint
		self.fd = None
		self.inode = -1
		self.ctime = 0
		self.pos = 0
		self.count = 0
		self.__open ()
	
	def __save (self):
		fd = open (self.info, 'w')
		fd.write ('%d:%d:%d' % (self.inode, self.ctime, self.fd.tell ()))
		fd.close ()
		self.count = 0
	
	def close (self):
		if self.fd:
			self.__save ()
			self.fd.close ()
			self.fd = None
		if self in seektab:
			seektab.remove (self)

	def __check (self):
		rc = True
		st = self.__stat (True)
		if st:
			if st[0] == self.inode and st[1] == self.ctime and st[2] > self.fd.tell ():
				rc = False
		return rc

	def __readline (self):
		line = self.fd.readline ()
		if line != '':
			self.count += 1
			if self.count >= self.checkpoint:
				self.__save ()
			return chop (line)
		else:
			return None
	
	def readline (self):
		line = self.__readline ()
		if line is None and not self.__check ():
			self.close ()
			self.__open ()
			line = self.__readline ()
		return line
#
def die (lvl = LV_FATAL, ident = None, s = None):
	global	seektab

	if s:
		err (s)
		log (lvl, ident, s)
	for st in seektab[:]:
		st.close ()
	unlock ()
	sys.exit (1)
rip = die
#}}}
#
# 4.) mailing/httpclient
#
#{{{
def mailsend (relay, sender, receivers, headers, body,
	      myself = host):
	def codetype (code):
		return code / 100
	rc = False
	if not relay:
		return (rc, 'Missing relay\n')
	if not sender:
		return (rc, 'Missing sender\n')
	if len (receivers) == 0:
		return (rc, 'Missing receivers\n')
	if not body:
		return (rc, 'Empty body\n')
	report = ''
	try:
		s = smtplib.SMTP (relay)
		(code, detail) = s.helo (myself)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'HELO ' + myself + ': ' + detail)
		else:
			report = report + 'HELO ' + myself + ' sent\n'
		(code, detail) = s.mail (sender)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'MAIL FROM:<' + sender + '>: ' + detail)
		else:
			report = report + 'MAIL FROM:<' + sender + '> sent\n'
		for r in receivers:
			(code, detail) = s.rcpt (r)
			if codetype (code) != 2:
				raise smtplib.SMTPResponseException (code, 'RCPT TO:<' + r + '>: ' + detail)
			else:
				report = report + 'RCPT TO:<' + r + '> sent\n'
		mail = ''
		hsend = False
		hrecv = False
		for h in headers:
			if len (h) > 0 and h[-1] != '\n':
				h += '\n'
			if not hsend and len (h) > 5 and h[:5].lower () == 'from:':
				hsend = True
			elif not hrecv and len (h) > 3 and h[:3].lower () == 'to:':
				hrecv = True
			mail = mail + h
		if not hsend:
			mail += 'From: ' + sender + '\n'
		if not hrecv:
			recvs = ''
			for r in receivers:
				if recvs:
					recvs += ', '
				recvs += r
			mail += 'To: ' + recvs + '\n'
		mail += '\n' + body
		(code, detail) = s.data (mail)
		if codetype (code) != 2:
			raise smtplib.SMTPResponseException (code, 'DATA: ' + detail)
		else:
			report = report + 'DATA sent\n'
		s.quit ()
		report = report + 'QUIT sent\n'
		rc = True
	except smtplib.SMTPConnectError, e:
		report = report + 'Unable to connect to %s, got %d %s response\n' % (relay, e.smtp_code, e.smtp_error)
	except smtplib.SMTPServerDisconnected:
		report = report + 'Server connection lost\n'
	except smtplib.SMTPResponseException, e:
		report = report + 'Invalid response: %d %s\n' % (e.smtp_code, e.smtp_error)
	except socket.error, e:
		report = report + 'General socket error: ' + `e.args` + '\n'
	except:
		report = report + 'General problems during mail sending'
	return (rc, report)
#}}}
#
# 5.) system interaction
#
#{{{
class Filesystem:
	def __init__ (self, path, device = None):
		self.path = path
		self.device = device
		self.stat = os.statvfs (path)
	
	def percentBlockFree (self):
		return int ((self.stat.f_bavail * 100) / self.stat.f_blocks)
	
	def percentBlockUsed (self):
		return 100 - self.percentBlockFree ()
		
	def percentInodeFree (self):
		return int ((self.stat.f_favail * 100) / self.stat.f_files)
		
	def percentInodeUsed (self):
		return 100 - self.percentInodeFree ()
		
	def usage (self):
		return (self.percentBlockFree (), self.percentBlockUsed (), self.percentInodeFree (), self.percentInodeUsed ())

class Filesystemtable:
	def __init__ (self, fstyp):
		self.table = []
		mtab = None
		if system == 'sunos':
			mtab = '/etc/mnttab'
		elif system == 'linux':
			mtab = '/etc/mtab'
		if mtab:
			try:
				fd = open (mtab, 'r')
				for line in fd:
					elem = line.rstrip ().split ()
					if len (elem) > 2 and elem[2] in fstyp:
						try:
							fs = Filesystem (elem[1], elem[0])
							self.table.append (fs)
						except:
							pass
				fd.close ()
			except:
				pass
			
class Process:
	def __init__ (self):
		self.pid = -1
		self.parent = None
		self.sibling = None
		self.child = None

	def __cmp__ (self, other):
		return self.pid - other.pid

	def relate (self, other):
		"""relate (self, other)
if other is parent (or pre-parent), this returns a number
greater than 0, counting the generations, if other is a
child (or even younger), than a number less than 0 is
returned. If the two processes are not related to each other
(they are not related, even if they are siblings), 0 is returned."""
		rc = 0
		n = 0
		cur = self
		while cur:
			if cur == other:
				rc = n
				break
			cur = cur.parent
			n += 1
		if not rc:
			n = 0
			cur = other
			while cur:
				if cur == self:
					rc = n
					break
				cur = cur.parent
				n -= 1
		return rc
	
class Processtable:
	def __timeparse (self, s):
		part = s.split ('-')
		if len (part) == 2:
			sec = int (part[0]) * 60 * 60 * 24
			sstr = part[1]
		else:
			sec = 0
			sstr = part[0]
		part = sstr.split (':')
		nsec = 0
		for p in part:
			nsec *= 60
			nsec += int (p)
		return sec + nsec

	def __init__ (self):
		try:
			oldcol = os.environ['COLUNMS']
		except KeyError:
			oldcol = None
		os.environ['COLUMNS'] = '4096'
		try:
			self.table = []
			pp = os.popen ('ps -e -o pid,ppid,user,group,etime,time,tty,vsz,comm,args', 'r')
			for line in pp.readlines ():
				elem = chop (line).split ()
				if len (elem) > 7:
					try:
						p = Process ()
						p.pid = int (elem[0])
						p.ppid = int (elem[1])
						p.user = elem[2]
						p.group = elem[3]
						p.etime = self.__timeparse (elem[4])
						p.time = self.__timeparse (elem[5])
						p.tty = elem[6]
						p.size = int (elem[7]) * 1024
						p.comm = elem[8]
						p.cmd = ' '.join (elem[9:])
						self.table.append (p)
					except:
						pass
			pp.close ()
			self.table.sort ()
			self.root = None
			for p in self.table:
				if p.ppid == 0:
					if p.pid == 1:
						self.root = p
				else:
					for pp in self.table:
						if p.ppid == pp.pid:
							p.parent = pp
							if pp.child:
								sib = pp.child
								while sib.sibling:
									sib = sib.sibling
								sib.sibling = p
							else:
								pp.child = p
		except:
			self.table = None
		if oldcol:
			os.environ['COLUMNS'] = oldcol
		else:
			del os.environ['COLUMNS']
		if not self.table:
			raise error ('Unable to read process table')
	
	def find (self, val):
		pid = None
		if type (val) == types.IntType:
			pid = val
		elif type (val) in types.StringTypes:
			if len (val) > 0 and val[0] == '/':
				try:
					fd = open (val, 'r')
					line = chop (fd.readline ())
					fd.close ()
					if line != '':
						pid = int (line)
				except:
					pass
		if pid == None:
			try:
				pid = int (val)
			except:
				pass
		if pid == None:
			raise error ('Given paramter "%s" cannot be mapped to a pid' % `val`)
		match = None
		for p in self.table:
			if p.pid == pid:
				match = p
				break
		if not match:
			raise error ('No process with pid %d (extracted from %s) found' % (pid, `val`))
		return match
	
	def select (self, user = None, group = None, comm = None, rcmd = None, ropt = 0):
		if rcmd:
			regcmd = re.compile (rcmd, ropt)
		else:
			regcmd = None
		rc = []
		for t in self.table:
			if user and t.user != user:
				continue
			if group and t.group != group:
				continue
			if comm and t.comm != comm:
				continue
			if regcmd and not regcmd.search (t.cmd):
				continue
			rc.append (t)
		return rc
	
	def tree (self, indent = 2):
		rc = ''
		up = []
		cur = self.root
		while cur:
			lu = len (up) * indent
			rc += '%*.*s%s\n' % (lu, lu, '', cur.cmd)
			if cur.child:
				up.append (cur.sibling)
				cur = cur.child
			else:
				cur = cur.sibling
			if not cur:
				while len (up) > 0:
					cur = up.pop ()
					if cur:
						break
		return rc

class Memory:
	def __init__ (self):
		if system != 'linux':
			raise error ('class Memory is only supported on linux systems')
		self.collect ()
	
	def collect (self):
		self.mem = {}
		try:
			fd = open ('/proc/meminfo', 'r')
		except:
			fd = None
		if fd:
			lines = fd.readlines ()
			fd.close ()
			pat = re.compile ('^([A-Za-z]+):[ \t]+([0-9]+)[ \t]+kB')
			for line in lines:
				mtch = pat.match (line)
				if mtch:
					grps = mtch.groups ()
					if len (grps) == 2:
						self.mem[grps[0].lower ()] = int (grps[1]) * 1024
#}}}
#
# 6.) Validate UIDs
#
#{{{
class UID:
	def __init__ (self):
		self.companyID = 0
		self.mailingID = 0
		self.customerID = 0
		self.URLID = 0
		self.signature = None
		self.prefix = None
		self.password = None
	
	def __decodeBase36 (self, s):
		return int (s, 36)
	
	def __codeBase36 (self, i):
		if i == 0:
			return '0'
		elif i < 0:
			i = -i
			sign = '-'
		else:
			sign = ''
		s = ''
		while i > 0:
			s = '0123456789abcdefghijklmnopqrstuvwxyz'[i % 36] + s
			i /= 36
		return sign + s
	
	def __makeSignature (self, s):
		hashval = sha.sha (s).digest ()
		sig = ''
		for ch in hashval[::2]:
			sig += self.__codeBase36 ((ord (ch) >> 2) % 36)
		return sig
	
	def __makeBaseUID (self):
		if self.prefix:
			s = self.prefix + '.'
		else:
			s = ''
		s += self.__codeBase36 (self.companyID) + '.' + \
		     self.__codeBase36 (self.mailingID) + '.' + \
		     self.__codeBase36 (self.customerID) + '.' + \
		     self.__codeBase36 (self.URLID)
		return s
	
	def createSignature (self):
		return self.__makeSignature (self.__makeBaseUID () + '.' + self.password)
	
	def createUID (self):
		base = self.__makeBaseUID ()
		return base + '.' + self.__makeSignature (base + '.' + self.password)
	
	def parseUID (self, uid):
		parts = uid.split ('.')
		plen = len (parts)
		if plen != 5 and plen != 6:
			raise error ('Invalid input format')
		start = plen - 5
		if plen == 6:
			self.prefix = parts[0]
		else:
			self.prefix = None
		try:
			self.companyID = self.__decodeBase36 (parts[start])
			self.mailingID = self.__decodeBase36 (parts[start + 1])
			self.customerID = self.__decodeBase36 (parts[start + 2])
			self.URLID = self.__decodeBase36 (parts[start + 3])
			self.signature = parts[start + 4]
		except ValueError:
			raise error ('Invalid input in data')
	
	def validateUID (self):
		lsig = self.createSignature ()
		return lsig == self.signature
#}}}
#
# 7.) General database interface
#
#{{{
if database:
	if database.apilevel != '2.0':
		err ('WARNING: Database API level is not 2.0, but ' + database.apilevel)

	if database.paramstyle != 'format':
		err ('WARNING: Database parameter style is not format, but ' + database.paramstyle)
	dbhost = 'localhost'
	dbuser = 'agnitas'
	dbpass = 'openemm'
	dbdatabase = 'openemm'

	class DBCache:
		def __init__ (self, data):
			self.data = data
			self.count = len (data)
			self.pos = 0
		
		def __iter__ (self):
			return self

		def next (self):
			if self.pos >= self.count:
				raise StopIteration ()
			record = self.data[self.pos]
			self.pos += 1
			return record

	class DBInstance:
		def __init__ (self, db):
			self.db = db
			self.curs = None
			self.desc = False
			self.rfparse = re.compile (':[A-Za-z0-9_]+|%')
			self.cache = {}
		
		def lastError (self):
			if self.db:
				return self.db.lastError ()
			return 'no database interface active'
		
		def close (self):
			if self.curs:
				try:
					self.curs.close ()
				except database.Error, err:
					if self.db:
						self.db.lasterr = err
				self.curs = None
				self.desc = False
	
		def __error (self, err):
			if self.db:
				self.db.lasterr = err
			self.close ()

		def open (self):
			self.close ()
			if self.db and self.db.isOpen ():
				try:
					self.curs = self.db.getCursor ()
				except database.Error, err:
					self.__error (err)
			if self.curs:
				return True
			return False
		
		def description (self):
			if self.desc:
				return self.curs.description
			return None

		#
		# old, deprecated interface
		#
		def __execute (self, req):
			rc = False
			for n in [ 0, 1 ]:
				if n:
					time.sleep (1)
				if not self.curs:
					if not self.open ():
						break
				try:
					self.curs.execute (req)
					rc = True
				except database.Error, err:
					self.__error (err)
				if rc:
					break
			return rc
		
		def queryAll (self, req):
			data = None
			rc = False
			if self.__execute (req):
				try:
					data = self.curs.fetchall ()
					rc = True
				except database.Error, err:
					self.__error (err)
			self.desc = rc
			return (rc, data)
	
		def queryStart (self, req):
			rc = self.__execute (req)
			self.desc = rc
			return rc
	
		def queryNext (self):
			data = None
			rc = False
			try:
				data = self.curs.fetchone ()
				rc = True
			except database.Error, err:
				self.__error (err)
			return (rc, data)
		
		def change (self, req):
			rows = 0
			rc = self.__execute (req)
			if rc:
				rows = self.curs.rowcount
				if rows > 0:
					try:
						self.db.db.commit ()
					except database.Error, err:
						self.__error (err)
						rc = False
			self.desc = False
			return (rc, rows)
		#
		# new interface using iterators and support for named
		# parameter
		#

		def __reformat (self, req, parm):
			try:
				(nreq, vars) = self.cache[req]
			except KeyError:
				nreq = ''
				vars = []
				while 1:
					mtch = self.rfparse.search (req)
					if mtch is None:
						nreq += req
						break
					else:
						span = mtch.span ()
						nreq += req[:span[0]]
						if span[0] + 1 < span[1]:
							vars.append (req[span[0] + 1:span[1]])
							nreq += '%s'
						else:
							nreq += '%%'
						req = req[span[1]:]
				self.cache[req] = (nreq, vars)
			nparm = []
			for key in vars:
				nparm.append (parm[key])
			return (nreq, nparm)

		def __valid (self):
			if not self.curs:
				if not self.open ():
					raise error ('Unable to setup cursor: ' + self.lastError ())
			
		def __iter__ (self):
			return self
		
		def next (self):
			try:
				data = self.curs.fetchone ()
			except database.Error, err:
				self.__error (err)
				raise error ('query next failed: ' + self.lastError ())
			if data is None:
				raise StopIteration ()
			return data

		def query (self, req, parm = None, cleanup = False):
			self.__valid ()
			try:
				if parm is None:
					self.curs.execute (req)
				else:

					(req, parm) = self.__reformat (req, parm)
					self.curs.execute (req, parm)
			except database.Error, err:
				self.__error (err)
				raise error ('query start failed: ' + self.lastError ())
			self.desc = True
			return self
		
		def queryc (self, req, parm = None, cleanup = False):
			if self.query (req, parm, cleanup) == self:
				try:
					data = self.curs.fetchall ()
					return DBCache (data)
				except database.Error, err:
					self.__error (err)
					raise error ('query all failed: ' + self.lastError ())
			raise error ('unable to setup query: ' + self.lastError ())
		
		def simpleQuery (self, req, parm = None, cleanup = False):
			rc = None
			for rec in self.query (req, parm, cleanup):
				rc = rec
				break
			return rc
		
		def sync (self, commit = True):
			rc = False
			if not self.db is None:
				if not self.db.db is None:
					try:
						if commit:
							self.db.db.commit ()
						else:
							self.db.db.rollback ()
						rc = True
					except database.Error, err:
						self.__error (err)
			return rc
		
		def update (self, req, parm = None, commit = False, cleanup = False):
			self.__valid ()
			try:
				if parm is None:
					self.curs.execute (req)
				else:

					(req, parm) = self.__reformat (req, parm)
					self.curs.execute (req, parm)
			except database.Error, err:
				self.__error (err)
				raise error ('update failed: ' + self.lastError ())
			rows = self.curs.rowcount
			if rows > 0 and commit:
				if not self.sync ():
					raise error ('commit failed: ' + self.lastError ())
			self.desc = False
			return rows

	class DBase:

		def __init__ (self, host = dbhost, user = dbuser, passwd = dbpass, database = dbdatabase):
			self.host = host
			self.user = user
			self.passwd = passwd
			self.database = database
			self.db = None
			self.lasterr = None

		def __error (self, err):
			self.lasterr = err
			self.close ()

		def lastError (self):
			if self.lasterr:

				return 'MySQL-%d: %s' % (self.lasterr.args[0], self.lasterr.args[1].strip ())
			if self.db is None:
				return 'No active database'
			return 'success'
			
		def commit (self):
			if self.db:
				self.db.commit ()
		
		def rollback (self):
			if self.db:
				self.db.rollback ()
		
		def close (self):
			if self.db:
				try:
					self.db.close ()
				except database.Error, err:
					self.lasterr = err;
				self.db = None
	
		def open (self):
			self.close ()
			try:

				self.db = database.connect (self.host, self.user, self.passwd, self.database)
			except database.Error, err:
				self.__error (err)
			if self.db:
				return 1
			return 0
	
		def isOpen (self):
			if self.db:
				return 1
			return 0
	
		def getCursor (self):
			curs = None
			if not self.db:
				self.open ()
			if self.db:
				try:
					curs = self.db.cursor ()
				except database.Error, err:
					self.__error (err)
			return curs
	
		def newInstance (self):
			inst = None
			if not self.db:
				self.open ()
			if self.db:
				inst = DBInstance (self)
			return inst
		
		def query (self, req):
			inst = self.newInstance ()
			if inst:
				rc = None
				try:
					rc = [r for r in inst.query (req)]
				finally:
					inst.close ()
				return rc
			raise error ('Unable to get database cursor: ' + self.lastError ())
		
		def update (self, req):
			inst = self.newInstance ()
			if inst:
				rc = None
				try:
					rc = inst.update (req)
				finally:
					inst.close ()
				return rc
			raise error ('Unable to get database cursor: ' + self.lastError ())
#}}}
