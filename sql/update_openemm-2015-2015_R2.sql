UPDATE doc_mapping_tbl set filename = 'create_trackable_and_non-track.htm' where pagekey = 'trackableLinks';
UPDATE doc_mapping_tbl set filename = 'using_trackable_links.htm' where pagekey = 'trackableLinkView';
UPDATE doc_mapping_tbl set filename = 'preview_-_for_in-depth_checkin.htm' where pagekey = 'preview';

UPDATE doc_mapping_tbl set filename = 'importing_the_csv-file.htm' where pagekey = 'importStep4';
UPDATE doc_mapping_tbl set filename = 'blacklist_-_do_not_mail.htm' where pagekey = 'blacklist';

UPDATE doc_mapping_tbl SET filename = 'comparing_mailings2.htm' WHERE pagekey = 'compareMailings';
UPDATE doc_mapping_tbl SET filename = 'ip_statistics.htm' WHERE pagekey = 'ipStatistics';

UPDATE doc_mapping_tbl SET filename = 'bounce-filter.htm' WHERE pagekey = 'bounceFilter';
UPDATE doc_mapping_tbl SET filename = 'the_plugins_at_a_glance.htm' WHERE pagekey = 'pluginmanagerList';

INSERT INTO doc_mapping_tbl (filename, pagekey) VALUES ('auto-import.htm', 'autoImport');
INSERT INTO doc_mapping_tbl (filename, pagekey) VALUES ('auto-export.htm', 'autoExport');