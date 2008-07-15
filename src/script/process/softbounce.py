#!/usr/bin/env python
#	-*- python -*-
"""**********************************************************************************
*  The contents of this file are subject to the OpenEMM Public License Version 1.1
*  ("License"); You may not use this file except in compliance with the License.
*  You may obtain a copy of the License at http://www.agnitas.org/openemm.
*  Software distributed under the License is distributed on an "AS IS" basis,
*  WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
*  the specific language governing rights and limitations under the License.
* 
*  The Original Code is OpenEMM.
*  The Initial Developer of the Original Code is AGNITAS AG. Portions created by
*  AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
* 
*  All copies of the Covered Code must include on each user interface screen,
*  visible to all users at all times
*     (a) the OpenEMM logo in the upper left corner and
*     (b) the OpenEMM copyright notice at the very bottom center
*  See full license, exhibit B for requirements.
**********************************************************************************

"""
#
import	time
import	agn
agn.require ('1.4.0')
agn.loglevel = agn.LV_INFO
#
agn.lock ()
db = agn.DBase ()
if db is None:
	agn.die (str = 'Failed to setup database interface')
curs = db.newInstance ()
if curs is None:
	agn.die (str = 'Failed to get database cursor')
#
#
# 1.) Kill old softbounces

old = time.localtime (time.time () - 179 * 24 * 60 * 60)
query = 'DELETE FROM softbounce_email_tbl WHERE creation_date < \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
try:
	rows = curs.update (query, commit = True)
	agn.log (agn.LV_INFO, 'kill', 'Removed %d address(es)' % rows)
except agn.error, e:
	agn.log (agn.LV_ERROR, 'kill', 'Failed to remove old entries using %s: %s' % (query, e.msg))
#
#
# 2.) Collect bounces to mailtrack_tbl
query = '*unset collect*'
try:
	query = 'UPDATE timestamp_tbl SET prev = cur, temp = current_timestamp WHERE timestamp_id = 2'
	rows = curs.update (query)
	if rows == 0:
		agn.log (agn.LV_INFO, 'collect', 'Missing entry in timestamp_tbl, try to create one')

		query = 'INSERT INTO timestamp_tbl (timestamp_id, description, cur, prev, temp) VALUES (2, \'Softbounce collection marker\', now(), \'1970-01-01\', now())'
		rows = curs.update (query)
	if rows != 1:
		raise agn.error ('Failed to set timestamp using %s' % query)
	db.commit ()
	agn.log (agn.LV_INFO, 'collect', 'Updated timestamps')
	time.sleep (1)

	iquery = 'INSERT INTO mailtrack_tbl (customer_id, company_id, mailing_id, status_id, change_date) '
	iquery += 'VALUES (:customer, :company, :mailing, 90, now())'
	insert = db.newInstance ()
	if insert is None:
		raise agn.error ('Failed to get new cursor for insertion')
	query =  'SELECT customer_id, company_id, mailing_id, detail '
	query += 'FROM bounce_tbl '

	query += 'WHERE change_date > (SELECT cur FROM timestamp_tbl WHERE timestamp_id = 2) AND change_date <= (SELECT temp FROM timestamp_tbl WHERE timestamp_id = 2) '
	query += 'ORDER BY company_id, customer_id'
	cur = [0, 0, 0, 0]
	(records, uniques, inserts) = (0, 0, 0)
	if curs.query (query) is None:
		raise agn.error ('Failed to query bounce_tbl using: %s' % query)
	while not cur is None:
		try:
			record = curs.next ()
			records += 1
		except StopIteration:
			record = None
		if record is None or cur[0] != record[0] or cur[1] != record[1]:
			if not record is None:
				uniques += 1
			if cur[0] > 0 and cur[3] < 510:
				map = {
					'customer': cur[0],
					'company': cur[1],
					'mailing': cur[2]
					}
				insert.update (iquery, map)
				inserts += 1
			cur = record
		elif record[3] > cur[3]:
			cur[2] = record[2]
			cur[3] = record[3]
	db.commit ()
	insert.close ()
	agn.log (agn.LV_INFO, 'collect', 'Read %d records (%d uniques) and inserted %d' % (records, uniques, inserts))
	query = 'UPDATE timestamp_tbl SET cur = temp WHERE timestamp_id = 2'
	curs.update (query)
	agn.log (agn.LV_INFO, 'collect', 'Timestamp updated')
except agn.error, e:
	agn.log (agn.LV_ERROR, 'collect', 'Failed: %s (last query %s)' % (e.msg, query))
#
#
# 3.) Merge them!

query = '*unset merge*'
try:
	query = 'SELECT MAX(mailtrack_id) FROM mailtrack_tbl'
	data = curs.simpleQuery (query)
	if data is None:
		raise agn.error ('Unable to fetch max mailtrack_id')
	if data[0] is None:
		max_mailtrack_id = 0
	else:
		max_mailtrack_id = data[0]
	iquery = 'INSERT INTO softbounce_email_tbl (company_id, email, bnccnt, mailing_id) VALUES (:company, :email, 1, :mailing)'
	icurs = db.newInstance ()

	uquery = 'UPDATE softbounce_email_tbl SET mailing_id = :mailing, change_date = now(), bnccnt=bnccnt+1 WHERE company_id = :company AND email = :email'
	ucurs = db.newInstance ()
	squery = 'SELECT count(*) FROM softbounce_email_tbl WHERE company_id = :company AND email = :email'
	scurs = db.newInstance ()
	dquery = 'DELETE FROM mailtrack_tbl WHERE mailtrack_id < %d AND status_id = 90 AND company_id = :company' % max_mailtrack_id
	dcurs = db.newInstance ()
	if None in [ icurs, ucurs, scurs, dcurs ]:
		raise agn.error ('Unable to setup curses for merging')
	
	query =  'SELECT distinct company_id FROM mailtrack_tbl '
	query += 'WHERE status_id = 90 '
	query += 'AND mailtrack_id < %d ' % max_mailtrack_id
	query += 'AND company_id IN (SELECT company_id FROM company_tbl WHERE status = \'active\')'
		
	for company in [c[0] for c in curs.queryc (query)]:
		agn.log (agn.LV_INFO, 'merge', 'Working on %d' % company)
		query =  'SELECT mt.customer_id, mt.mailing_id, cust.email '
		query += 'FROM mailtrack_tbl mt, customer_%d_tbl cust ' % company
		query += 'WHERE cust.customer_id = mt.customer_id '
		query += 'AND mt.company_id = %d ' % company
		query += 'AND mt.status_id = 90 '
		query += 'ORDER BY cust.email, mt.mailing_id'

		for record in curs.query (query):
			(cuid, mid, eml) = record
			map = {
				'company': company,
				'customer': cuid,
				'mailing': mid,
				'email': eml
			}
			date = scurs.simpleQuery (squery, map)
			if not data is None:
				if data[0] == 0:
					icurs.update (iquery, map)
				else:
					ucurs.update (uquery, map)
		map = {
			'company': company
		}
		dcurs.update (dquery, map)
	db.commit ()
	icurs.close ()
	ucurs.close ()
	scurs.close ()
	dcurs.close ()
except agn.error, e:
	agn.log (agn.LV_ERROR, 'merge', 'Failed: %s (last query %s)' % (e.msg, query))
#
#
# 4.) Make softbounce to hardbounce
query = '*unset unsub*'
try:
	query =  'SELECT distinct company_id FROM softbounce_email_tbl '
	query += 'WHERE company_id IN (SELECT company_id FROM company_tbl WHERE status = \'active\') '
	query += 'ORDER BY company_id'
	for company in [c[0] for c in curs.queryc (query)]:
		agn.log (agn.LV_INFO, 'unsub', 'Working on %d' % company)
		dquery = 'DELETE FROM softbounce_email_tbl WHERE company_id = %d AND email = :email' % company
		dcurs = db.newInstance ()

		uquery =  'UPDATE customer_%d_binding_tbl SET user_status = 2, user_remark = \'Softbounce\', exit_mailing_id = :mailing, change_date = now() ' % company
		uquery += 'WHERE customer_id IN (SELECT customer_id from customer_%d_tbl WHERE email = :email) and user_status = 1'
		ucurs = db.newInstance ()

		squery =  'SELECT email, mailing_id, bncnt, creation_date, change_date '
		squery += 'FROM softbounce_email_tbl '

		squery += 'WHERE company_id = %d AND bnccnt > 7 AND DATE_DIFF(change_date,creation_date) > 30' % company
		scurs = db.newInstance ()
		if None in [dcurs, ucurs, scurs]:
			raise agn.error ('Failed to setup curses')
		for record in scurs.query (squery):
			map = {
				'email': record[0],
				'mailing': record[1],
				'bouncecount': record[2],
				'creationdate': record[3],
				'timestamp': record[4]
			}
			query =  'SELECT max(customer_id) FROM customer_%d_tbl WHERE email = :email ' % company
			query += 'AND customer_id IN (SELECT customer_id from customer_%d_binding_tbl WHERE user_status = 1)' % company
			data = curs.simpleQuery (query, map)
			if data is None or data[0] <= 0:
				continue
			custid = data[0]

			query =  'SELECT count(*) FROM rdir_log_tbl WHERE customer_id = %d AND company_id = %d ' % (custid, company)
			old = time.localtime (time.time () - 30 * 24 * 60 * 60)
			query += 'AND change_date > \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
			data = curs.simpleQuery (query, map)
			if data is None:
				custid_klick = 0
			else:
				custid_klick = data[0]

			query =  'SELECT count(*) FROM onepixel_log_tbl WHERE customer_id = %d AND company_id = %d ' % (custid, company)
			query += 'AND change_date > \'%04d-%02d-%02d\'' % (old[0], old[1], old[2])
			data = curs.simpleQuery (query, map)
			if data is None:
				custid_onpx = 0
			else:
				custid_onpx = data[0]
			if custid_klick > 0 or custid_onpx > 0:
				agn.log (agn.LV_INFO, 'unsub', 'Email %s [%d] has %d kilick(s) and %d onepix(es) -> active' % (map['email'], custid, custid_klick, custid_onpx))
			else:
				agn.log (agn.LV_INFO, 'unsub', 'Email %s [%d] has no klicks and no onepixes -> hardbounce' % (map['email'], custid))
				ucurs.update (uquery, map)
			dcurs.update (dquery, map)
		scurs.close ()
		ucurs.close ()
		dcurs.close ()
except agn.error:
	agn.log (agn.LV_ERROR, 'unsub', 'Failed: %s (last query %s)' % (e.msg, query))
#
#
# X.) Cleanup
curs.close ()
db.close ()
agn.unlock ()
