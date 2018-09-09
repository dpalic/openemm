#!/usr/bin/env python
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
import	sys, os, getopt, signal, time, re, gdbm
try:
	import	cPickle as pickle
except ImportError:
	import	pickle
import	agn
agn.require ('2.0.0')
agn.loglevel = agn.LV_INFO
#
class Scanner (object):
	syslog = '/var/log/maillog'
	saveFile = agn.mkpath (agn.base, 'var', 'run', 'slrtscn.save')
	bounceLog = agn.mkpath (agn.base, 'var', 'spool', 'log', 'extbounce.log')
	def __init__ (self):
		pass
	def done (self):
		pass
	def writeBounce (self, dsn, mailingID, customerID, reason):
		try:
			fd = open (self.bounceLog, 'a')
			fd.write ('%s;0;%d;0;%d;%s\n' %	(dsn, mailingID, customerID, reason))
			fd.close ()
			agn.log (agn.LV_DEBUG, 'scan', 'Written dsn=%s, mid=%d, cid=%d, reason=%s to %s' % (dsn, mailingID, customerID, reason, self.bounceLog))
		except IOError, e:
			agn.log (agn.LV_ERROR, 'scan', 'Unable to write bounce %s: %s' % (self.bounceLog, str (e)))

	def scan (self):
		try:
			fp = agn.Filepos (self.syslog, self.saveFile)
		except agn.error, e:
			agn.log (agn.LV_ERROR, 'scan', 'Unable to open %s: %s' % (self.syslog, e.msg))
			try:
				st = os.stat (self.saveFile)
				if st.st_size == 0:
					agn.log (agn.LV_ERROR, 'scan', 'Remove corrupt empty file %s' % self.saveFile)
					os.unlink (self.saveFile)
			except OSError:
				pass
			return
		try:
			count = 0
			while True:
				line = fp.readline ()
				if line is None:
					break
				count += 1
				if not self.parse (line):
					agn.log (agn.LV_VERBOSE, 'scan', 'Unparseable line: %s' % line)
			if count > 0:
				agn.log (agn.LV_DEBUG, 'scan', 'Processed %d line(s)' % count)
		finally:
			fp.close ()

class ScannerSendmail (Scanner):
	isstat = re.compile ('sendmail\\[[0-9]+\\]: *([0-9A-F]{6}[0-9A-Z]{3}[0-9A-F]{8})[G-Zg-z]?:.*stat=(.*)$')
	parser = re.compile ('^([a-z]{3} +[0-9]+ [0-9]{2}:[0-9]{2}:[0-9]{2}) +([^ ]+) +sendmail\\[[0-9]+\\]: *[0-9A-F]{6}([0-9A-Z]{3})[0-9A-F]{8}[G-Z]?:(.*)$', re.IGNORECASE)
	def __parseline (self, pline):
		rc = {
			'__line': pline
		}
		pmtch = self.parser.match (pline)
		if not pmtch is None:
			g = pmtch.groups ()
			rc['__timestamp'] = g[0]
			rc['__mailer'] = g[1]
			parms = g[3].split (',')
			for parm in parms:
				p = parm.split ('=', 1)
				if len (p) == 2:
					rc[p[0].strip ()] = p[1].strip ()
		return rc

	def parse (self, line):
		mtch = self.isstat.search (line)
		if not mtch is None:
			(qid, detail) = mtch.groups ()
			mailing = int (qid[:6], 16)
			if len (qid) == 17:
				customer = int (qid[9:], 16)
			else:
				customer = int (qid[10:], 16)
			details = self.__parseline (line)
			def get (key):
				try:
					return details[key]
				except KeyError:
					return ''
			try:
				self.writeBounce (details['dsn'], mailing, customer, 'stat=%s\trelay=%s' % (get ('stat'), get ('relay')))
			except KeyError, e:
				agn.log (agn.LV_VERBOSE, 'parse', 'Incomplete line ignored: %s: %s' % (line, str (e)))
		return mtch is not None

class Tracker (object):
	def __init__ (self, filename):
		self.filename = filename
		self.db = None
		self.decode = pickle.loads
		self.encode = pickle.dumps
	
	def open (self):
		if self.db is None:
			self.db = gdbm.open (self.filename, 'c')
	
	def close (self):
		if self.db is not None:
			self.db.close ()
			self.db = None

	def key (self, section, key):
		self.open ()
		return '%s:%s' % (section, key)
	
	def get (self, section, key):
		self.open ()
		try:
			return self.decode (self.db[self.key (section, key)])
		except KeyError:
			return {}
	
	def put (self, section, key, value):
		self.open ()
		self.db[self.key (section, key)] = self.encode (value)
	
	def update (self, section, key, **kws):
		if kws:
			self.open ()
			cur = self.get (section, key)
			for (var, val) in kws.items ():
				cur[var] = val
			self.put (section, key, cur)
	
	def delete (self, section, key):
		self.open ()
		try:
			del self.db[self.key (section, key)]
		except KeyError:
			pass
	
	def over (self, callback):
		self.open ()
		dbkey = self.db.firstkey ()
		while dbkey is not None:
			try:
				(section, key) = dbkey.split (':', 1)
				if not callback (section, key, self.get (section, key)):
					break
			except ValueError:
				pass
			dbkey = self.db.nextkey (dbkey)

class ScannerPostfix (Scanner):
	messageidLog = agn.mkpath (agn.base, 'var', 'spool', 'log', 'messageid.log')
	messageidTracker = agn.mkpath (agn.base, 'var', 'run', 'messageid.track')
	SEC_MESSAGEID = 'message-id'
	SEC_POSTFIXID = 'postfix-id'
	
	def __init__ (self):
		super (ScannerPostfix, self).__init__ ()
		self.mtrack = None
		self.last = 0

	def done (self):
		if self.mtrack is not None:
			self.mtrack.close ()
			self.mtrack = None
		super (ScannerPostfix, self).done ()

	def __handleMessageIDs (self):
		now = int (time.time ())
		check = now // (24 * 60 * 60)
		if self.last != check:
			expire = now - 7 * 24 * 60 * 60
			maxcount = 10000
			agn.log (agn.LV_INFO, 'expire', 'Expire old message-ids')
			while True:
				collect = []
				def callback (section, key, value):
					if section == self.SEC_MESSAGEID and value['created'] < expire:
						collect.append ((section, key))
						if len (collect) >= maxcount:
							return False
					return True
				self.mtrack.over (callback)
				if collect:
					agn.log (agn.LV_INFO, 'expire', 'Remove %d old message-ids' % len (collect))
					for (section, key) in collect:
						self.mtrack.delete (section, key)
						agn.log (agn.LV_DEBUG, 'expire', 'Removed message-id %s' % key)
				else:
					break
			agn.log (agn.LV_INFO, 'expire', 'Expiration finished')
			self.last = check
			now = int (time.time ())
		#
		if os.path.isfile (self.messageidLog):
			pfname = '%s.%d' % (self.messageidLog, int (time.time ()))
			nfname = pfname
			n = 0
			while os.path.isfile (nfname):
				n += 1
				nfname = '%s.%d' % (pfname, n)
			try:
				os.rename (self.messageidLog, nfname)
				time.sleep (2)
			except OSError, e:
				agn.log (agn.LV_ERROR, 'mid', 'Failed to rename %s to %s: %s' % (self.messageidLog, nfname, str (e)))
				return
			agn.log (agn.LV_DEBUG, 'mid', 'Scanning input file %s' % nfname)
			try:
				fdi = open (nfname)
				fdo = open (agn.logdataname ('messageid'), 'a')
				for line in fdi:
					fdo.write (line)
					line = line.strip ()
					try:
						parts = line.split (';', 4)
						if len (parts) == 5:
							rec = {
								'created': now,
								'companyID': int (parts[0]),
								'mailinglistID': int (parts[1]),
								'mailingID': int (parts[2]),
								'customerID': int (parts[3])
							}
							self.mtrack.put (self.SEC_MESSAGEID, parts[4], rec)
							agn.log (agn.LV_DEBUG, 'mid', 'Saved companyID=%s, mailinglistID=%s, mailingID=%s, customerID=%s for message-id %s' % (parts[0], parts[1], parts[2], parts[3], parts[4]))
						else:
							raise ValueError ('expect 5 elements, got only %d' % len (parts))
					except ValueError, e:
						agn.log (agn.LV_ERROR, 'mid', 'Failed to parse %s: %s' % (line, str (e)))
				fdo.close ()
				fdi.close ()
			except IOError, e:
				agn.log (agn.LV_ERROR, 'mid', 'Failed to write messagid file: %s' % str (e))
			os.unlink (nfname)
				
	def scan (self):
		try:
			self.mtrack = Tracker (self.messageidTracker)
			self.__handleMessageIDs ()
			super (ScannerPostfix, self).scan ()
		finally:
			if self.mtrack is not None:
				self.mtrack.close ()
				self.mtrack = None

	ignore = set (['statistics', 'NOQUEUE'])
	patternLine = re.compile ('^([a-z]{3} +[0-9]+ [0-9]+:[0-9]+:[0-9]+) +[^ ]+ +([a-z/]+)\\[[0-9]+\\]: +([^:]+): (.*)$', re.IGNORECASE)
	patternEnvelopeFrom = re.compile ('from=<([^>]*)>')
	patternMessageID = re.compile ('message-id=<([^>]+)>')
	def parse (self, line):
		mtch = self.patternLine.match (line)
		if mtch is not None:
			(date, part, id, data) = mtch.groups ()
			if id not in self.ignore:
				if part == 'postfix/pickup':
					mtch2 = self.patternEnvelopeFrom.search (data)
					if mtch2 is not None:
						envelopeFrom = mtch2.group (1)
						self.mtrack.update (self.SEC_POSTFIXID, id, envelopeFrom = envelopeFrom)
						agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Found envelopeFrom=%s' % envelopeFrom)
				elif part == 'postfix/cleanup':
					mtch2 = self.patternMessageID.search (data)
					if mtch2 is not None:
						messageID = mtch2.group (1)
						self.mtrack.update (self.SEC_POSTFIXID, id, messageID = messageID)
						agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Found messageID=%s' % messageID)
				elif part == 'postfix/qmgr' and data == 'removed':
					rec = self.mtrack.get (self.SEC_POSTFIXID, id)
					if rec:
						try:
							messageID = rec['messageID']
							try:
								self.mtrack.delete (self.SEC_MESSAGEID, messageID)
								agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Removed messageID=%s' % messageID)
							except KeyError:
								agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Got remove for messageID=%s, which is not under control' % messageID)
						except KeyError:
							agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Got remove without a stored messageID')
						self.mtrack.delete (self.SEC_POSTFIXID, id)
						agn.log (agn.LV_DEBUG, 'parse/%s' %id, 'Removed tracking entry')
				elif part in ('postfix/qmgr', 'postfix/smtp', 'postfix/error'):
					parsed = {}
					for p in data.split (', '):
						try:
							(var, val) = [_p.strip () for _p in p.split ('=', 1)]
							parsed[var] = val
						except ValueError:
							pass
					rec = self.mtrack.get (self.SEC_POSTFIXID, id)
					upd = {}
					if 'from' in parsed:
						upd['envelopeFrom'] = parsed['from']
					if 'to' in parsed:
						if 'envelopeTo' not in rec:
							upd['envelopeTo'] = parsed['to']
						if 'messageID' in rec:
							messageID = rec['messageID']
							midinfo = self.mtrack.get (self.SEC_MESSAGEID, messageID)
							if midinfo:
								try:
									reason = ['stat=%s' % parsed['status']]
									try:
										relay = parsed['relay']
										if relay and relay != 'none':
											reason += ['relay=%s' % relay]
									except KeyError:
										pass
									self.writeBounce (parsed['dsn'], midinfo['mailingID'], midinfo['customerID'], '\t'.join (reason))
								except KeyError, e:
									agn.log (agn.LV_VERBOSE, 'parse', 'Incomplete line ingored: %s: %s' % (line, str (e)))
					if upd:
						for (var, val) in upd.items ():
							rec[var] = val
						self.mtrack.put (self.SEC_POSTFIXID, id, rec)
						agn.log (agn.LV_DEBUG, 'parse/%s' % id, 'Update tracking entry: %s' % str (rec))
		#
		return mtch is not None
#
term = False
def handler (sig, stack):
	global	term
	term = True

def main ():
	global	term
	
	try:
		mta = os.environ['MTA']
	except KeyError:
		mta = None
	(opts, param) = getopt.getopt (sys.argv[1:], 'vsp')
	for opt in opts:
		if opt[0] == '-v':
			agn.outlevel = agn.LV_DEBUG
			agn.outstream = sys.stderr
		elif opt[0] == 's':
			mta = None
		elif opt[0] == '-p':
			mta = 'postfix'
	#
	scanners = {
		None:		ScannerSendmail,
		'postfix':	ScannerPostfix
	}
	try:
		scanner = scanners[mta] ()
	except KeyError:
		scanner = scanners[None] ()
	signal.signal (signal.SIGINT, handler)
	signal.signal (signal.SIGTERM, handler)
	signal.signal (signal.SIGHUP, signal.SIG_IGN)
	signal.signal (signal.SIGPIPE, signal.SIG_IGN)
	#
	agn.lock ()
	agn.log (agn.LV_INFO, 'main', 'Starting up')
	while not term:
		time.sleep (1)
		agn.mark (agn.LV_INFO, 'loop', 180)
		scanner.scan ()
	#
	scanner.done ()
	agn.log (agn.LV_INFO, 'main', 'Going down')
	agn.unlock ()

if __name__ == '__main__':
	main ()
