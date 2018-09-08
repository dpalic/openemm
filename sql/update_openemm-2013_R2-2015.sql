DROP TABLE IF EXISTS `auto_import_tbl`;
SET @saved_cs_client = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `auto_import_tbl` (
  `auto_import_id` int(11) NOT NULL auto_increment,
  `import_profile_id` int(11) NOT NULL,
  `shortname` varchar(100) collate utf8_unicode_ci NOT NULL,
  `description` varchar(1000) collate utf8_unicode_ci NOT NULL,
  `file_path` varchar(400) collate utf8_unicode_ci NOT NULL,
  `file_server` varchar(400) collate utf8_unicode_ci NOT NULL,
  `one_time` int(1) NOT NULL,
  `auto_resume` int(1) NOT NULL,
  `executed` int(1) NOT NULL,
  `company_id` int(11) NOT NULL,
  `admin_id` int(11) NOT NULL,
  `active` int(1) DEFAULT 0 NOT NULL,
  `auto_activation_date` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`auto_import_id`),
  FOREIGN KEY (`company_id`) REFERENCES `company_tbl` (`company_id`),
  FOREIGN KEY (`admin_id`) REFERENCES `admin_tbl` (`admin_id`),
  FOREIGN KEY (`import_profile_id`) REFERENCES `import_profile_tbl` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `auto_import_mlist_bind_tbl`;
SET @saved_cs_client = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `auto_import_mlist_bind_tbl` (
  `auto_import_id` int(11) NOT NULL,
  `mailinglist_id` int(11) NOT NULL,
  `company_id` int(11) NOT NULL,
  PRIMARY KEY (`auto_import_id`, `mailinglist_id`),
  FOREIGN KEY (`company_id`) REFERENCES `company_tbl` (`company_id`),
  FOREIGN KEY (`auto_import_id`) REFERENCES `auto_import_tbl` (`auto_import_id`),
  FOREIGN KEY (`mailinglist_id`) REFERENCES `mailinglist_tbl` (`mailinglist_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `auto_import_time_tbl`;
SET @saved_cs_client = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `auto_import_time_tbl` (
  `auto_import_id` int(11) NOT NULL,
  `import_day_of_week` int(1) NOT NULL,
  `import_hour` int(2) NOT NULL,
  `company_id` int(11) NOT NULL,
  PRIMARY KEY (`auto_import_id`, `import_day_of_week`),
  FOREIGN KEY (`company_id`) REFERENCES `company_tbl` (`company_id`),
  FOREIGN KEY (`auto_import_id`) REFERENCES `auto_import_tbl` (`auto_import_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `auto_import_used_files_tbl`;
SET @saved_cs_client = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `auto_import_used_files_tbl` (
  `auto_import_used_file_id` int(11) NOT NULL auto_increment,
  `auto_import_id` int(11) NOT NULL,
  `file_size` int(15) NOT NULL,
  `file_date` datetime NOT NULL,
  `company_id` int(11) NOT NULL,
  PRIMARY KEY (`auto_import_used_file_id`),
  FOREIGN KEY (`company_id`) REFERENCES `company_tbl` (`company_id`),
  FOREIGN KEY (`auto_import_id`) REFERENCES `auto_import_tbl` (`auto_import_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

DROP TABLE IF EXISTS `userlog_tbl`;
SET @saved_cs_client = @@character_set_client;
SET character_set_client = utf8;
create table userlog_tbl (
	logtime TIMESTAMP NOT NULL,
	username VARCHAR(20) collate utf8_unicode_ci,
	action VARCHAR(100) collate utf8_unicode_ci,
	description VARCHAR(4000) collate utf8_unicode_ci
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE `rdir_action_tbl` ADD COLUMN `creation_date` timestamp default '0000-00-00 00:00:00';
ALTER TABLE `rdir_action_tbl` ADD COLUMN `change_date` timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP;

drop table if exists actop_execute_script_tbl;
drop table if exists actop_update_customer_tbl;
drop table if exists actop_get_customer_tbl;
drop table if exists actop_subscribe_customer_tbl;
drop table if exists actop_send_mailing_tbl;
drop table if exists actop_service_mail_tbl;
drop table if exists actop_get_archive_list_tbl;
drop table if exists actop_tbl;

create table actop_tbl (action_operation_id integer not null, company_id integer not null, type varchar(255) not null, action_id integer, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_execute_script_tbl (script longtext not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_update_customer_tbl (column_name varchar(255) not null, column_type varchar(255) not null, update_type integer not null, update_value varchar(255) not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_get_customer_tbl (load_always tinyint(1) not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_subscribe_customer_tbl (double_check tinyint(1) not null, key_column varchar(255) not null, double_opt_in tinyint(1) not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_send_mailing_tbl (mailing_id integer not null, delay_minutes integer not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_service_mail_tbl (text_mail text not null, subject_line varchar(255) not null, to_addr varchar(255) not null, mailtype integer not null, html_mail text not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
create table actop_get_archive_list_tbl (campaign_id integer not null, action_operation_id integer not null, primary key (action_operation_id)) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

alter table actop_tbl add index IDX_AO_A(action_id), add constraint IDX_AO_A foreign key (action_id) references rdir_action_tbl (action_id);
alter table actop_execute_script_tbl add index IDX_AO1_AO(action_operation_id), add constraint IDX_AO1_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_update_customer_tbl add index IDX_AO2_AO(action_operation_id), add constraint IDX_AO2_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_get_customer_tbl add index IDX_AO3_AO(action_operation_id), add constraint IDX_AO3_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_subscribe_customer_tbl add index IDX_AO4_AO(action_operation_id), add constraint IDX_AO4_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_send_mailing_tbl add index IDX_AO5_AO(action_operation_id), add constraint IDX_AO5_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_service_mail_tbl add index IDX_AO6_AO(action_operation_id), add constraint IDX_AO6_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);
alter table actop_get_archive_list_tbl add index IDX_AO7_AO(action_operation_id), add constraint IDX_AO7_AO foreign key (action_operation_id) references actop_tbl (action_operation_id);

CREATE TABLE `auto_export_tbl` (
  `auto_export_id` int(11) NOT NULL AUTO_INCREMENT,
  `export_profile_id` int(10) unsigned NOT NULL,
  `shortname` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `description` varchar(1000) COLLATE utf8_unicode_ci,
  `file_path` varchar(400) COLLATE utf8_unicode_ci NOT NULL,
  `file_server` varchar(400) COLLATE utf8_unicode_ci NOT NULL,
  `one_time` int(1) NOT NULL,
  `executed` int(1) NOT NULL,
  `company_id` int(11) unsigned NOT NULL,
  `admin_id` int(11) NOT NULL,
  active int(1) DEFAULT 0 NOT NULL,
  `auto_activation_date` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`auto_export_id`),
  KEY `company_id` (`company_id`),
  KEY `admin_id` (`admin_id`),
  KEY `export_profile_id` (`export_profile_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `auto_export_time_tbl` (
  `auto_export_id` int(11) NOT NULL,
  `export_day_of_week` int(1) NOT NULL,
  `export_hour` int(2) NOT NULL,
  `company_id` int(11) unsigned NOT NULL,
  PRIMARY KEY (`auto_export_id`,`export_day_of_week`),
  KEY `company_id` (`company_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE mailinglist_tbl ADD (
  `creation_date` timestamp default '0000-00-00 00:00:00',
  `change_date` timestamp default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

INSERT INTO admin_group_permission_tbl (admin_group_id, security_token) VALUES (4, 'recipient.import.auto');
INSERT INTO admin_group_permission_tbl (admin_group_id, security_token) VALUES (4, 'recipient.export.auto');
INSERT INTO admin_group_permission_tbl (admin_group_id, security_token) VALUES (4, 'targets.advancedRules.recipients');
DELETE FROM admin_group_permission_tbl WHERE security_token = 'action.getcustomer';
DELETE FROM admin_group_permission_tbl WHERE security_token = 'mailing.archived';

alter table rdir_action_tbl add deleted int(1) default 0;

-- drop existing tables in right order
DROP TABLE IF EXISTS `job_queue_parameter_tbl`;
DROP TABLE IF EXISTS `job_queue_tbl`;

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `job_queue_tbl` (
  `id` int(11) auto_increment,
  `description` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `created` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `lastStart` timestamp,
  `running` int(1) NOT NULL default '0',
  `lastResult` varchar(512) collate utf8_unicode_ci,
  `startAfterError` int(1) NOT NULL default '0',
  `lastDuration` int(10) NOT NULL default '0',
  `interval` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `nextStart` timestamp,
  `hostname` varchar(64) collate utf8_unicode_ci,
  `runClass` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  `runOnlyOnHosts` varchar(64) collate utf8_unicode_ci NULL default NULL,
  `emailOnError` varchar(64) collate utf8_unicode_ci NULL default NULL,
  `deleted` int(1) NOT NULL default '0',
  PRIMARY KEY (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `job_queue_parameter_tbl` (
  `job_id` int(11) auto_increment,
  `parameter_name` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `parameter_value` varchar(128) collate utf8_unicode_ci NOT NULL default '',
  FOREIGN KEY (`job_id`) REFERENCES `job_queue_tbl` (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
SET character_set_client = @saved_cs_client;

INSERT INTO job_queue_tbl (`DESCRIPTION`, `CREATED`, `LASTSTART`, `RUNNING`, `LASTRESULT`, `STARTAFTERERROR`, `LASTDURATION`, `INTERVAL`, `NEXTSTART`, `HOSTNAME`, `RUNCLASS`, `DELETED`)
	VALUES ('AutoImport', current_timestamp, null, 0, 'OK', 0, 0, '**00', current_timestamp, null, 'org.agnitas.emm.core.autoimport.service.AutoImportJobWorker', 0);
INSERT INTO job_queue_tbl (`DESCRIPTION`, `CREATED`, `LASTSTART`, `RUNNING`, `LASTRESULT`, `STARTAFTERERROR`, `LASTDURATION`, `INTERVAL`, `NEXTSTART`, `HOSTNAME`, `RUNCLASS`, `DELETED`)
	VALUES ('AutoExport', current_timestamp, null, 0, 'OK', 0, 0, '**00', current_timestamp, null, 'org.agnitas.emm.core.autoexport.service.AutoExportJobWorker', 0);
INSERT INTO job_queue_tbl (`DESCRIPTION`, `CREATED`, `LASTSTART`, `RUNNING`, `LASTRESULT`, `STARTAFTERERROR`, `LASTDURATION`, `INTERVAL`, `NEXTSTART`, `HOSTNAME`, `RUNCLASS`, `DELETED`)
	VALUES ('DBCleaner', CURRENT_TIMESTAMP, null, 0, 'OK', 0, 0, '0300', CURRENT_TIMESTAMP, null, 'org.agnitas.util.quartz.DBCleanerJobWorker', 0);
INSERT INTO job_queue_tbl (`DESCRIPTION`, `CREATED`, `LASTSTART`, `RUNNING`, `LASTRESULT`, `STARTAFTERERROR`, `LASTDURATION`, `INTERVAL`, `NEXTSTART`, `HOSTNAME`, `RUNCLASS`, `DELETED`)
	VALUES ('LoginTrackTableCleaner', CURRENT_TIMESTAMP, null, 0, 'OK', 0, 0, '0400', CURRENT_TIMESTAMP, null, 'org.agnitas.util.quartz.LoginTrackTableCleanerJobWorker', 0);

ALTER TABLE `webservice_user_tbl` ADD COLUMN `default_data_source_id` INT(11) NOT NULL DEFAULT 0  AFTER `company_id`;

INSERT INTO `admin_permission_tbl` (`admin_id`, `security_token`) (SELECT `admin_id`, 'recipient.list' FROM `admin_permission_tbl` WHERE security_token = 'recipient.show');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) (SELECT `admin_group_id`, 'recipient.list' FROM `admin_group_permission_tbl` WHERE security_token = 'recipient.show');
INSERT INTO `admin_permission_tbl` (`admin_id`, `security_token`) (SELECT `admin_id`, 'recipient.create' FROM `admin_permission_tbl` WHERE security_token = 'recipient.new');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) (SELECT `admin_group_id`, 'recipient.create' FROM `admin_group_permission_tbl` WHERE security_token = 'recipient.new');

ALTER TABLE import_profile_tbl ADD deleted int(1) DEFAULT 0;
ALTER TABLE import_column_mapping_tbl ADD deleted int(1) DEFAULT 0;
ALTER TABLE import_gender_mapping_tbl ADD deleted int(1) DEFAULT 0;

ALTER TABLE mailinglist_tbl ADD deleted int(1) DEFAULT 0;

-- new table to save user UI preferences
CREATE TABLE admin_pref_tbl (
	admin_id       int(11) DEFAULT '0',
	pref           varchar(40) collate utf8_unicode_ci NOT NULL,
	val            varchar(5) collate utf8_unicode_ci DEFAULT '0',
	KEY (admin_id)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- new table to store ActivateDOI action operation properties
CREATE TABLE actop_activate_doi_tbl (
    action_operation_id           INT(11) NOT NULL ,
    for_all_lists				  int(1) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE admin_tbl DROP preferred_list_size;

INSERT INTO tag_tbl (tagname, selectvalue, type, company_id, description, change_date) VALUES
	('agnDYN', 'agnDYN textComponent {name}', 'FLOW', 0, 'agnDYN tag works optionally with agnDVALUE', current_timestamp),
	('agnDVALUE', 'agnDVALUE textComponent {name}', 'FLOW', 0, 'agnDVALUE tag works only with agnDYN', current_timestamp),
	('agnALTERCALC', 'trunc(months_between(sysdate, cust.{column})/12, 0) {op} {value}', 'COMPLEX', 0, 'like agnALTER with operator and value', current_timestamp),
	('agnDATEDB_LANG', 'to_char(cust.{column}, ''{format}'', ''nls_date_language = {lang}'')', 'COMPLEX', 0, 'Returns date in column custom formatted in given language', current_timestamp),
	('agnYEARCALC', 'to_char (cust.{field}, ''YYYY'') {op} {value}', 'COMPLEX', 0,'to calculate with column', current_timestamp),
	('agnYEARCALC_F', 'to_char (cust.{column}, ''{format}'') {op} {value}', 'COMPLEX', 0,'like agnYEARCALC with formating the date', current_timestamp),
	('agnUID', '', 'SIMPLE', 0, 'agnUID', current_timestamp);
INSERT INTO tag_tbl (tagname, selectvalue, type, company_id, description, change_date) VALUES
    ('agnDBV','\'not available\'','COMPLEX',0,'Selects a virtual column not existing in DB', current_timestamp),
    ('agnTITLEFIRST','titlefirst({type}, [company-id], cust.customer_id)','COMPLEX',0,'shows title - print out firstname', current_timestamp),
    ('agnTITLEFULL','title2({type}, [company-id], cust.customer_id)','COMPLEX',0,'shows title - print out title, firstname, lastname', current_timestamp),
    ('agnSUBSCRIBERCOUNT','\'1\'','SIMPLE',0,'Dummy tag for preview', current_timestamp);
UPDATE tag_tbl SET selectvalue = '' WHERE tagname = 'agnSWYN';

ALTER TABLE auto_import_tbl ADD allow_unknown_hostkeys INTEGER DEFAULT NULL;
ALTER TABLE auto_export_tbl ADD allow_unknown_hostkeys INTEGER DEFAULT NULL;

CREATE TABLE job_queue_result_tbl (
	job_id                     int(11) NOT NULL,
	time                       timestamp DEFAULT CURRENT_TIMESTAMP,
	result                     varchar(512) DEFAULT NULL,
	duration                   int(10) DEFAULT '0',
	hostname                   varchar(64) DEFAULT NULL
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO doc_mapping_tbl (filename, pagekey) VALUES ('Plugin_Manager.htm', 'pluginmanagerList');

