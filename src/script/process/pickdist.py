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
import	os, time, signal
import	shutil
import	agn
agn.require ('1.5.3')
agn.loglevel = agn.LV_DEBUG
if agn.iswin:
	import	subprocess
#
class Block:
	def __init__ (self, path):
		self.path = path
		self.dir = os.path.dirname (self.path)
		self.fname = os.path.basename (self.path)
		elem = self.fname.split ('=')
		self.ts = elem[1]
		cinfo = elem[2].split ('-')
		self.companyID = int (cinfo[0])
		self.mailingID = int (elem[3])
		try:
			self.blocknr = int (elem[4])
		except ValueError:
			self.blocknr = 0
		(base, ext) = self.fname.split ('.', 1)
		self.isdata = ext[:3] == 'xml'
		self.isfinal = ext == 'final'
		self.finalcount = 0
		self.stamp = base + '.stamp'
	
	def readyToSend (self, ts):
		return cmp (self.ts, ts) < 0

	def isFinalFor (self, other):
		if self.isfinal and \
		   self.companyID == other.companyID and \
		   self.mailingID == other.mailingID:
		   	return True
		return False
	
	def unpack (self, dest):
		if not self.isdata:
			agn.log (agn.LV_ERROR, 'block', 'Try to unpack non-data file %s' % self.path)
			return False
		agn.log (agn.LV_INFO, 'block', 'Unpacking %s' % self.path)
		stamp = self.dir + os.sep + self.stamp
		try:
			agn.log (agn.LV_VERBOSE, 'block', 'Removing stamp file %s' % stamp)
			os.unlink (stamp)
		except IOError, e:
			agn.log (agn.LV_ERROR, 'block', 'Failed to remove stamp file %s %s' % (stamp, `e.args`))
			return False
		if agn.iswin:
			aclog = os.path.sep.join ([agn.base, 'var', 'spool', 'log', 'account.log'])
			cmd = [os.path.sep.join ([agn.base, 'bin', 'xmlback.exe']), '-vlogenerate:media=email;path=%s;syslog=false;account-logfile=%s' % (dest.replace ('\\', '\\\\'), aclog.replace ('\\', '\\\\')),  self.path]
			agn.log (agn.LV_DEBUG, 'block', 'Calling %s' % `cmd`)
			n = subprocess.call (cmd)
		else:
			cmd = 'xmlback \'-vlogenerate:account-logfile=var/spool/log/account.log;media=email;path=%s\' \'%s\'' % (dest, self.path)
			agn.log (agn.LV_DEBUG, 'block', 'Calling %s' % cmd)
			n = os.system (cmd)
		if n:
			agn.log (agn.LV_ERROR, 'block', 'Failed to execute mail creator %s with %d' % (`cmd`, n))
			return False
		return True
	
	def moveTo (self, dest):
		try:
			shutil.move (self.path, dest)
			agn.log (agn.LV_DEBUG, 'block', 'Moved %s to %s' % (self.path, dest))
			return True
		except OSError, e:
			agn.log (agn.LV_ERROR, 'block', 'Failed to move %s to %s %s' % (self.path, dest, `e.args`))
		return False

	def __cmp__ (self, other):
		if self.blocknr != other.blocknr:
			return self.blocknr - other.blocknr
		return self.mailingID - other.mailingID

class Pickdist:
	def __init__ (self):
		self.spool = agn.base + os.sep + 'var' + os.sep + 'spool'
		self.incoming = self.spool + os.sep + 'META'
		self.archive = self.spool + os.sep + 'ARCHIVE'
		self.recover = self.spool + os.sep + 'RECOVER'
		self.deleted = self.spool + os.sep + 'DELETED'
		self.queue = self.spool + os.sep + 'QUEUE'
		self.data = []
	
	def scanForData (self):
		self.data = []
		files = [file for file in os.listdir (self.incoming) if file[:7] == 'AgnMail']
		if len (files) > 0:
			data = []
			finals = []
			db = agn.DBase ()
			if db is None:
				agn.log (agn.LV_ERROR, 'scan', 'Unable to get database instance')
				return 0
			inst = db.newInstance ()
			if inst is None:
				db.close ()
				agn.log (agn.LV_ERROR, 'scan', 'Unable to get database cursor')
				return 0
			deleted = {}
			now = time.localtime ()
			ts = 'D%04d%02d%02d%02d%02d%02d' % now[:6]
			for file in files:
				block = Block (self.incoming + os.sep + file)
				if not deleted.has_key (block.mailingID):
					r = inst.simpleQuery ('SELECT deleted FROM mailing_tbl WHERE mailing_id = %d' % block.mailingID)
					if not r is None:
						deleted[block.mailingID] = r[0]
				if deleted.has_key (block.mailingID):
					if deleted[block.mailingID] != 0:
						block.moveTo (agn.mkArchiveDirectory (self.deleted))
					elif block.readyToSend (ts):
						if block.isdata and block.stamp in files:
							data.append (block)
						elif block.isfinal:
							finals.append (block)
			inst.close ()
			db.close ()
			for block in data:
				for final in finals:
					if final.isFinalFor (block):
						final.finalcount += 1
						self.data.append (block)
						break
			for final in finals:
				if final.finalcount == 0:
					final.moveTo (agn.mkArchiveDirectory (self.archive))
			self.data.sort ()
		return len (self.data)

	def queueIsFree (self):
		return len ([file for file in os.listdir (self.queue) if file[:2] == 'qf']) < 5000
	
	def hasData (self):
		return len (self.data) > 0
	
	def getNextBlock (self):
		if self.data:
			block = self.data[0]
			self.data.remove (block)
		else:
			block = None
		return block

term = False
def handler (sig, stack):
	global	term
	
	term = True
signal.signal (signal.SIGINT, handler)
signal.signal (signal.SIGTERM, handler)

if not agn.iswin:
	signal.signal (signal.SIGHUP, signal.SIG_IGN)
	signal.signal (signal.SIGPIPE, signal.SIG_IGN)
#
agn.lock ()
agn.log (agn.LV_INFO, 'main', 'Starting up')
#
pd = Pickdist ()
while not term:
	time.sleep (1)
	agn.mark (agn.LV_INFO, 'loop', 180)
	if pd.scanForData () == 0:
		delay = 30
		agn.log (agn.LV_VERBOSE, 'loop', 'No ready to send data file found')
	else:
		delay = 0
		while not term and pd.hasData ():
			if not pd.queueIsFree ():
				agn.log (agn.LV_INFO, 'loop', 'Queue is already filled up')
				delay = 180
				break
			block = pd.getNextBlock ()
			if block.unpack (pd.queue):
				block.moveTo (agn.mkArchiveDirectory (pd.archive))
			else:
				block.moveTo (pd.recover)
	while not term and delay > 0:

		if agn.iswin and agn.winstop ():
			term = True
			break
		time.sleep (1)
		delay -= 1
#
agn.log (agn.LV_INFO, 'main', 'Going down')
agn.unlock ()
