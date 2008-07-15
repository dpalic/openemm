#!/usr/bin/env python
#	-*- mode: python; mode: fold -*-
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
import	os, time, socket, re, types
import	subprocess
import	threading
import	smtplib
import	smtpd, asyncore
import	agn, bavd
agn.require ('1.5.7')
#
agn.loglevel = agn.LV_DEBUG
#
ADMIN_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'ADMIN')
QUEUE_SPOOL = agn.mkpath (agn.base, 'var', 'spool', 'QUEUE')
BAV_CONF = agn.mkpath (agn.base, 'var', 'spool', 'bav', 'bav.conf')
BOUNCE_LOG = agn.mkpath (agn.base, 'var', 'spool', 'log', 'extbounce.log')
#
fqdn = socket.getfqdn ()
#
running = True
class Threadpool: #{{{
	def __init__ (self, maxthreads):
		self.maxthreads = maxthreads
		self.threads = [None] * maxthreads
	
	def findFreeSlot (self):
		global	running
		
		rc = None
		while running and rc is None:
			n = 0
			while n < self.maxthreads:
				if self.threads[n] is None:
					if rc is None:
						rc = n
				else:
					self.threads[n].join (0)
					if not self.threads[n].isAlive ():
						self.threads[n] = None
						if rc is None:
							rc = n
				n += 1
			if rc is None:
				time.sleep (1)
		return rc
	
	def setThread (self, thrID, thr):
		self.threads[thrID] = thr
#}}}
class Mail: #{{{
	lock = threading.Lock ()
	count = 1
	
	def __init__ (self, sender, receiver):
		self.sender = sender
		self.receiver = receiver
		self.queue = None
		self.header = {}
		self.body = None
	
	def __setitem__ (self, var, val):
		self.header[var.lower ()] = [var, val]
	
	def setQueue (self, queue):
		self.queue = queue

	def setBody (self, body):
		self.body = body
	
	def createMail (self):
		self.lock.acquire ()
		nr = self.count
		self.count += 1
		self.lock.release ()
		now = time.time ()
		mid = '%.04f.%d' % (now, nr)
		qf = 'T%d\n' % now
		qf += 'K%d\n' % (now + 5 * 24 * 60 * 60)
		qf += 'N1\n'
		qf += 'Menqueued\n'
		qf += 'S<%s>\n' % self.sender
		qf += 'R<%s>\n' % self.receiver
		hids = self.header.keys ()
		for use in [['Return-Path', '<%s>' % self.sender],
			    ['Message-ID', '<%s@%s>' % (mid, fqdn)],
			    ['Date', time.strftime ('%a, %d %b %Y %H:%M:%S GMT', time.gmtime (now))],
			    ['From', self.sender],
			    ['To', self.receiver],
			   ]:
			try:
				hid = use[0].lower ()
				qf += 'H%s: %s\n' % (use[0], self.header[hid][1])
				hids.remove (hid)
			except KeyError:
				qf += 'H%s: %s\n' % (use[0], use[1])
		for hid in hids:
			qf += 'H%s: %s\n' % (self.header[hid][0], self.header[hid][1])
		qf += '.\n'
		tfname = self.queue + os.sep + 'tfb%s' % mid
		qfname = self.queue + os.sep + 'qfb%s' % mid
		dfname = self.queue + os.sep + 'dfb%s' % mid
		try:
			fd = open (tfname, 'w')
			fd.write (qf)
			fd.close ()
			fd = open (dfname, 'w')
			fd.write (self.body)
			fd.close ()
			os.rename (tfname, qfname)
			agn.log (agn.LV_DEBUG, 'mail', 'Mail from <%s> to <%s> created as %s/%s' % (self.sender, self.receiver, qfname, dfname))
		except (IOError, OSError), e:
			agn.log (agn.LV_ERROR, 'mail', 'Failed to create mail from <%s> to <%s>: %s' % (self.sender, self.receiver, `e.args`))
			try:
				os.unlink (tfname)
			except OSError, e:
				agn.log (agn.LV_ERROR, 'mail', 'Failed to remove temp.file %s %s' % (tfname, `e.args`))
			try:
				os.unlink (dfname)
			except OSError, e:
				agn.log (agn.LV_ERROR, 'mail', 'Failed to remove data file %s %s' % (dfname, `e.args`))
#}}}
class Spool: #{{{
	class Relays: #{{{
		class Relay: #{{{
			def __init__ (self, expire, resolv):
				self.expire = expire
				self.resolv = resolv
		#}}}
		class Smartrelay: #{{{
			def __init__ (self, sexpr):
				self.smartrelay = None
				self.username = None
				self.password = None
				parts = sexpr.split ('@')
				if len (parts) >= 2:
					self.smartrelay = parts[-1]
					auth = '@'.join (parts[:-1]).split (':', 1)
					if len (auth) == 2:
						self.username = auth[0]
						self.password = auth[1]
				else:
					self.smartrelay = sexpr
		#}}}

		def __init__ (self):
			self.r = {}
			self.checkForSmartRelay = True
			self.smartRelay = None

		def findRelay (self, domain, now, loopcheck):
			try:
				data = ''
				error = ''
				for typ in ['mx', 'any']:
					pp = subprocess.Popen (['nslookup', '-type=%s' % typ, domain], stdout = subprocess.PIPE, stderr = subprocess.PIPE, shell = False, universal_newlines = True)
					(tempdata, temperror) = pp.communicate ()
					pp.wait ()
					data += tempdata
					error += temperror
			except OSError, e:
				data = None
				error = None
				agn.log (agn.LV_ERROR, 'relay', 'Failed to start external program: %s' % `e.args`)
			resolv = None
			if data:
				pat = re.compile ('^%s[ \t]+' % domain)
				is_a = False
				mx = []
				cname = None
				seen = []
				for line in [l for l in data.split ('\n') if not l in seen and not pat.match (l) is None]:
					seen.append (line)
					parm = line.split (None, 1)
					if len (parm) == 2:
						opts = {}
						for opt in parm[1].split (','):
							popt = opt.split ('=', 1)
							if len (popt) == 2:
								opts[popt[0].strip ().lower ()] = popt[1].strip ()
						if opts.has_key ('internet address'):
							is_a = True
						elif opts.has_key ('mail exchanger'):
							try:
								pref = int (opts['mx preference'])
							except (KeyError, ValueError):
								pref = 0
							mx.append ('%04d:%s' % (pref, opts['mail exchanger']))
						elif opts.has_key ('canonical name'):
							cname = opts['canonical name']
				if mx:
					mx.sort ()
					resolv = ','.join ([x.split (':')[1] for x in mx])
					agn.log (agn.LV_DEBUG, 'relay', 'Found "%s" as relay for "%s"' % (resolv, domain))
				elif is_a:
					resolv = domain
					agn.log (agn.LV_DEBUG, 'relay', 'Use A record for "%s" as relay' % domain)
				elif cname and not cname in loopcheck:
					loopcheck.append (cname)
					relay = self.findRelay (cname, now, loopcheck)
					if not relay is None:
						resolv = relay.resolv
			if resolv is None and error:
				for line in error.split ('\n'):
					if line.startswith ('***'):
						parts = line.split (':')
						if len (parts) > 1:
							reason = parts[-1].strip ().lower ()
							if reason == 'non-existent domain':
								resolv = ''
								agn.log (agn.LV_DEBUG, 'relay', 'Domain "%s" not found' % domain)
			if not resolv is None:
				r = Spool.Relays.Relay (now + 3600, resolv)
				self.r[domain] = r
				return r
			return None

		def getRelay (self, domain):
			if self.checkForSmartRelay:
				self.checkForSmartRelay = False
				try:
					fd = open ('conf' + os.path.sep + 'smart-relay')
					data = fd.read ().strip ()
					fd.close ()
					if data:
						self.smartRelay = Spool.Relays.Smartrelay (data)
				except IOError:
					pass
			#
			if not self.smartRelay is None:
				return self.smartRelay
			#
			now = time.time ()
			try:
				r = self.r[domain]
				if r.expire < now:
					del self.r[domain]
					r = None
			except KeyError:
				r = None
			if r is None:
				r = self.findRelay (domain, now, [domain])
				if not r is None:
					self.r[domain] = r
			if not r is None:
				return r.resolv
			return None
	#}}}

	relays = Relays ()

	class Entry (threading.Thread): #{{{
		parseFID = re.compile ('^qf([0-9A-F]{6})[0-9A-Z]{3}([0-9A-F]{8})$', re.IGNORECASE)
		parseDSN = re.compile ('^(#?([0-9]\\.[0-9]\\.[0-9])|.*\\(#?([0-9]\\.[0-9]\\.[0-9])\\))')
		noSuchUser = re.compile ('(no such|invalid|unknown) (user|address|mailbox)', re.IGNORECASE)
		noSuchHost = re.compile ('unknown (host|domain)', re.IGNORECASE)

		def __init__ (self, **kws):
			threading.Thread.__init__ (self, **kws)
			self.qpath = None
			self.dpath = None
			self.mid = None
			self.qdata = None
			self.qlines = None
			self.qmap = None
			self.qfmod = None
			self.sendtime = None
			self.mailingID = None
			self.customerID = None
			self.mail = None

		def setup (self, path, qfname, dfname):
			self.qpath = path + os.sep + qfname
			self.dpath = path + os.sep + dfname
			if len (qfname) > 2:
				self.mid = qfname[2:]
			else:
				self.mid = qfname
			try:
				fd = open (self.qpath)
				self.qdata = fd.read ()
				fd.close ()
			except IOError, e:
				self.qdata = None
				agn.log (agn.LV_ERROR, self.mid, 'Failed to read control file %s: %s' % (self.qpath, `e.args`))
			self.qmap = {}
			if self.qdata:
				self.qlines = self.qdata.split ('\n')
				for line in self.qlines:
					if len (line) and line[0] in 'TKNMSRX':
						self.qmap[line[0]] = line
			else:
				self.qlines = None
			self.qfmod = False
			self.sendtime = 0
			mtch = self.parseFID.match (qfname)
			if not mtch is None:
				(mid, cid) = mtch.groups ()
				self.mailingID = int (mid, 16)
				self.customerID = int (cid, 16)
				agn.log (agn.LV_DEBUG, self.mid, 'New entry %s for Mailing %d and Customer %d' % (self.qpath, self.mailingID, self.customerID))
			else:
				agn.log (agn.LV_DEBUG, self.mid, 'New entry %s from external source' % self.qpath)
		
		def updateQF (self):
			if self.qfmod:
				try:
					qnew = ''
					for key in self.qmap.keys ():
						found = False
						for line in self.qlines:
							if len (line) and line[0] == key:
								found = True
								break
						if not found:
							qnew += self.qmap[key] + '\n'
					fd = open (self.qpath, 'w')
					if qnew:
						fd.write (qnew)
					for line in [l for l in self.qlines if l]:
						if self.qmap.has_key (line[0]):
							line = self.qmap[line[0]]
						fd.write (line + '\n')
					fd.close ()
					agn.log (agn.LV_DEBUG, self.mid, 'Updated qfile %s' % self.qpath)
				except IOError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to update qfile "%s": %s' % (self.qpath, `e.args`))
		
		def removeSpoolfiles (self):
			for path in [self.qpath, self.dpath]:
				try:
					os.unlink (path)
				except OSError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to remove spoolfile %s: %s' % (path, `e.args`))

		def __getset (self, qid, nval):
			try:
				val = int (self.qmap[qid][1:])
			except (KeyError, ValueError):
				val = nval
				self.qmap[qid] = '%s%d' % (qid, val)
				self.qfmod = True
			return val
		
		def __getaddr (self, s):
			start = s.find ('<')
			end = s.find ('>')
			if start != -1 and end != -1 and start < end:
				s = s[start + 1:end]
			return s
		
		def __mergeDSN (self, s, dflt):
			try:
				return int (s[0]) * 100 + int (s[2]) * 10 + int (s[4])
			except:
				return dflt

		def validate (self, now, increase):
			valid = False
			expire = self.__getset ('K', now + 60 * 60 * 24)
			if expire < now or not self.qmap.has_key ('R') or not self.qmap.has_key ('S'):
				if expire < now:
					agn.log (agn.LV_INFO, self.mid, 'Removed expired entry')
				else:
					agn.log (agn.LV_WARNING, self.mid, 'Removed incomplete entry')
				self.removeSpoolfiles ()
			else:
				start = self.__getset ('T', now)
				tries = self.__getset ('N', 1)
				self.sendtime = start + (tries - 1) * increase
				if self.sendtime < now:
					agn.log (agn.LV_DEBUG, self.mid, 'Entry is ready to send, current trycount is %d' % tries)
					self.qmap['N'] = 'N%d' % (tries + 1, )
					self.qfmod = True
					self.mail = ''
					for line in self.qlines:
						if len (line):
							if line[0] == 'H':
								if line[:4] == 'H?P?':
									line = line[4:]
								else:
									line = line[1:]
								self.mail += line + '\n'
							elif line[0] in ' \t':
								self.mail += line + '\n'
					self.mail += '\n'
					try:
						fd = open (self.dpath)
						self.mail += fd.read ()
						fd.close ()
						if self.mail[-1] != '\n':
							self.mail += '\n'
						valid = True
					except IOError, e:
						agn.log (agn.LV_ERROR, self.mid, 'Failed to read body from %s: %s' % (self.dpath, `e.args`))
				else:
					agn.log (agn.LV_DEBUG, self.mid, 'Skip %s as it is not ready to send' % self.mid)
			return valid

		def writeBounce (self, dsn, message):
			if not self.mailingID is None and not self.customerID is None and not message is None:
				s = '%d.%d.%d;1;%d;0;%d;stat=%s\n' % (dsn / 100, (dsn / 10) % 10, dsn % 10, self.mailingID, self.customerID, message)
				try:
					fd = open (BOUNCE_LOG, 'a')
					fd.write (s)
					fd.close ()
				except IOError, e:
					agn.log (agn.LV_ERROR, self.mid, 'Failed to write bounce log to %s: %s' % (BOUNCE_LOG, `e.args`))
			else:
				agn.log (agn.LV_DEBUG, self.mid, 'Skip incomplete bounce %s/%s/%s' % (`self.mailingID`, `self.customerID`, `message`))
		
		def report (self, dsn, message):
			did = dsn / 100
			if did in (2, 5):
				if did == 5:
					self.writeBounce (dsn, message)
					agn.log (agn.LV_INFO, self.mid, 'Hardbounce %d: %s' % (dsn, message))
				else:
					agn.log (agn.LV_INFO, self.mid, 'Successful %d: %s' % (dsn, message))
				self.removeSpoolfiles ()
			elif did == 4:
				self.writeBounce (dsn, message)
				self.qmap['M'] = 'M[%d] %s' % (dsn, message)
				self.qfmod = True
				self.updateQF ()
				agn.log (agn.LV_INFO, self.mid, 'Softbounce %d: %s' % (dsn, message))
			else:
				agn.log (agn.LV_ERROR, self.mid, 'Strange report %d: %s' % (dsn, `message`))

		def run (self):
			sender = self.__getaddr (self.qmap['S'][1:])
			receiver = self.__getaddr (self.qmap['R'][1:])
			parts = receiver.split ('@')
			if len (parts) != 2:
				self.report (511, 'Invalid receiver')
				return
			domain = parts[1].lower ()
			auth = None
			try:
				relay = self.qmap['X'][1:]
			except KeyError:
				qrelay = Spool.relays.getRelay (domain)
				if type (qrelay) == types.StringType:
					relay = qrelay
					self.qmap['X'] = 'X%s' % relay
					self.qfmod = True
				else:
					if qrelay.username and qrelay.password:
						auth = [qrelay.username, qrelay.password]
					relay = qrelay.smartrelay
			if relay is None:
				self.report (412, 'Failed to resolve domain %s' % domain)
				return
			if relay == '':
				self.report (512, 'Domain %s not existing' % domain)
				return
			dsn = 0
			msg = ''
			for r in relay.split (','):
				retry = False
				try:
					smtp = smtplib.SMTP (r)
					if auth:
						smtp.starttls ()
						smtp.login (auth[0], auth[1])
					smtp.sendmail (sender, [receiver], self.mail)
					smtp.quit ()
					dsn = 250
					msg = 'Message send via %s' % r
				except smtplib.SMTPSenderRefused, e:
					dsn = e.smtp_code
					msg = e.sender + ': ' + e.smtp_error
					if dsn / 100 != 5:
						retry = True
				except smtplib.SMTPRecipientsRefused, e:
					(dsn, msg) = e.recipients.values ()[0]
					mtch = self.parseDSN.match (msg)
					if not mtch is None:
						grp = mtch.groups ()
						if grp[1]:
							dsn = self.__mergeDSN (grp[1], dsn)
						elif grp[2]:
							dsn = self.__mergeDSN (grp[2], dsn)
					elif not self.noSuchUser.search (msg) is None:
						dsn = 511
					elif not self.noSuchHost.search (msg) is None:
						dsn = 512
					if dsn in (511, 571):
						msg += ': user unknown'
				except smtplib.SMTPResponseException, e:
					(dsn, msg) = e.args
					if dsn / 100 != 5:
						retry = True
				except smtplib.SMTPException, e:
					dsn = 400
					msg = `e.args`
					retry = True
				except socket.error, e:
					dsn = 400
					msg = e.args[1]
					retry = True
				if not retry:
					break
				agn.log (agn.LV_WARNING, self.mid, 'Retry as sent to %s failed %d: %s' % (r, dsn, `msg`))
			self.report (dsn, msg)
	#}}}

	def __init__ (self, path, interval, threads):
		self.path = path
		self.sid = os.path.basename (path)
		self.interval = interval
		self.pool = Threadpool (threads)
		agn.log (agn.LV_INFO, self.sid, 'Initial setup for %d threads completed' % threads)

	def delay (self):
		global	running

		n = self.interval
		while running and n > 0:
			time.sleep (1)
			n -= 1
	
	def execute (self):
		global	running

		flist = os.listdir (self.path)
		agn.log (agn.LV_DEBUG, self.sid, 'Found %d files in queue' % len (flist))
		now = time.time ()
		for qfname in [fn for fn in flist if fn.startswith ('qf')]:
			dfname = 'd' + qfname[1:]
			if dfname in flist:
				e = Spool.Entry ()
				e.setup (self.path, qfname, dfname)
				if e.validate (now, self.interval - 1):
					thr = self.pool.findFreeSlot ()
					if not thr is None:
						e.start ()
						self.pool.setThread (thr, e)
			else:
				agn.log (agn.LV_WARNING, self.sid, 'Stale control file %s found' % qfname)
			if not running:
				break
#}}}
class Sender (threading.Thread): #{{{
	def __init__ (self, **kws):
		threading.Thread.__init__ (self, **kws)
		self.spool = None

	def setSpool (self, spool):
		self.spool = spool
	
	def run (self):
		global	running
		
		agn.log (agn.LV_INFO, 'sender', 'Starting for %s' % self.spool.sid)
		while running:
			self.spool.execute ()
			self.spool.delay ()
		agn.log (agn.LV_INFO, 'sender', 'Ending for %s' % self.spool.sid)
#}}}
class Server (threading.Thread): #{{{
	class Bavconf: #{{{
		def __init__ (self):
			self.aliases = {}
			self.rules = {}
			try:
				fd = open (BAV_CONF)
				for line in [lin for lin in fd.readlines () if len (lin) > 0 and not lin[0] in ('#', '\n')]:
					parts = line[:-1].split (None, 1)
					if len (parts) == 2:
						action = parts[1].split (':', 1)
						if len (action) == 2:
							if action[0] == 'alias':
								self.aliases[parts[0]] = action[1]
							else:
								self.rules[parts[0]] = action
				fd.close ()
			except IOError, e:
				agn.log (agn.LV_ERROR, 'bav', 'Failed to open %s: %s' % (BAV_CONF, `e.args`))
		
		def findRule (self, rcpt):
			try:
				alias = self.aliases[rcpt]
			except KeyError:
				alias = rcpt
			try:
				rc = self.rules[alias]
			except KeyError:
				rc = None
			return rc
	#}}}
	
	class Bavd (bavd.BAV): #{{{
		def __init__ (self, message, mode):
			msg = bavd.email.message_from_string (message)
			bavd.BAV.__init__ (self, msg, mode)
		
		def sendmail (self, msg, to):
			rmsg = msg.as_string (False)
			mail = Mail ('mailloop@%s' % fqdn, to)
			mail.setQueue (QUEUE_SPOOL)
			parts = rmsg.split ('\n\n', 1)
			if len (parts) == 2:
				header = []
				cur = None
				for line in parts[0].split ('\n'):
					if len (line) and line[0] in '\t ':
						if not cur is None:
							cur[1] += ' ' + line.lstrip ()
					else:
						token = line.split (':', 1)
						if len (token) == 2:
							cur = [token[0], token[1].lstrip ()]
							header.append (cur)
				for head in header:
					mail[head[0]] = head[1]
				mail.setBody (parts[1])
				mail.createMail ()
	#}}}

	class Process (threading.Thread): #{{{
		X_AGN = 'X-AGNMailloop'
		daemonSender = re.compile ('^$|MAILER-DAEMON', re.IGNORECASE)
		daemonHeader = [re.compile ('^(Resent-)?(From|Sender|Return-Path):.*(<>|<MAILER[_-]?DAEMON[^>]*>)', re.IGNORECASE),
				re.compile ('^Precedence:.*(junk|bulk|list)', re.IGNORECASE)]

		def __init__ (self, bav, peer, sender, receiver, header, body, message):
			threading.Thread.__init__ (self)
			self.bav = bav
			self.peer = peer
			self.sender = sender
			self.receiver = receiver
			self.header = header
			self.body = body
			self.message = message
			self.mail = None
		
		def createBounce (self, code, msg):
			mail = Mail ('', self.sender)
			mail.setQueue (QUEUE_SPOOL)
			mail['From'] = 'MAILER-DAEMON <>'
			mail['Subject'] = 'Mail failed: %d %s' % (code, msg)
			mail.setBody ('Mail failed due to %d:\n%s\n\n\nThe original message follows:\n%s\n%s\n' % (code, msg, '\n'.join (['>' + h[0] + ': ' + h[1] for h in self.header]), self.body))
			mail.createMail ()
			
		def isSystemMail (self):
			if not self.daemonSender.search (self.sender) is None:
				return True
			for head in [h[0] + ': ' + h[1] for h in self.header]:
				for rhead in self.daemonHeader:
					if not rhead.search (head) is None:
						return True
			baver = Server.Bavd (self.mail, 0)
			return not baver.execute ()
				
		def run (self):
			action = self.bav.findRule (self.receiver)
			if action is None:
				self.createBounce (510, 'Unknown user ' + self.receiver)
				return
			if action[0] == 'reject':
				self.createBounce (500, 'User rejected ' + self.receiver)
				return
			if action[0] == 'tempfail':
				self.createBounce (400, 'Unable to handle mail, try again later')
				return
			if action[0] != 'accept':
				self.createBounce (400, 'Internal error, invalid action ' + action[0])
				return
			info = action[1]
			info += ',from=%s,to=%s' % (self.sender, self.receiver)
			self.header.append ([self.X_AGN, info])
			self.mail = '\n'.join ([h[0] + ': ' + h[1] for h in self.header]) + '\n' + self.body
			if self.isSystemMail ():
				baver = Server.Bavd (self.mail, 2)
			else:
				baver = Server.Bavd (self.mail, 1)
			baver.execute ()
	#}}}

	class ServerLoop (smtpd.SMTPServer): #{{{
		X_LOOP = 'X-AGNLoop'

		def __init__ (self):
			smtpd.SMTPServer.__init__ (self, ('0.0.0.0', 25), None)
			self.pool = Threadpool (10)
	
		def process_message (self, peer, mailfrom, rcpttos, data):
			# 1.) Unifiy data
			data = data.replace ('\r\n', '\n')
			# 2.) Extract header
			header = [['Return-Path', '<%s>' % mailfrom]]
			missFrom = True
			n = data.find ('\n\n')
			if n != -1:
				heads = data[:n].split ('\n')
				body = data[n + 1:]
				cur = None
				for head in heads:
					if len (head) > 0 and head[0] in ' \t':
						if not cur is None:
							cur[1] += ' ' + head.lstrip ()
					else:
						parts = head.split (':', 1)
						if len (parts) == 2:
							cur = [parts[0], parts[1].lstrip ()]
							header.append (cur)
							if missFrom and cur[0].lower () == 'from':
								missFrom = False
			else:
				body = data
			if missFrom:
				header.append (['From', mailfrom])
			# 3.) Check for loops and silently ignore mail
			isLoop = False
			for head in header:
				if head[0] == self.X_LOOP:
					isLoop = True
					break
			if isLoop:
				return
			# 4.) Add loop marker to header
			header.append ([self.X_LOOP, 'set'])
			# 5.) Start off real processing
			bav = Server.Bavconf ()
			for rcpt in rcpttos:
				threadID = self.pool.findFreeSlot ()
				if not threadID is None:
					proc = Server.Process (bav, peer, mailfrom, rcpt, header, body, data)
					proc.start ()
					self.pool.setThread (threadID, proc)
	#}}}

	def run (self):
		Server.ServerLoop ()
		asyncore.loop (timeout = 1.0)
#}}}
class Watchdog (threading.Thread): #{{{
	def run (self):
		global	running

		while running:
			if agn.iswin:
				if agn.winstop ():
					running = False
					break
			time.sleep (1)
		asyncore.close_all ()
#}}}
s1 = Sender (name = 'Spool ADMIN')
s1.setSpool (Spool (ADMIN_SPOOL, 30, 5))
s1.start ()
s2 = Sender (name = 'Spool QUEUE')
s2.setSpool (Spool (QUEUE_SPOOL, 120, 50))
s2.start ()
serv = Server (name = 'SMTP Server')
serv.start ()
if agn.iswin:
	wd = Watchdog ()
	wd.start ()
