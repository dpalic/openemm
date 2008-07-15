-- phpMyAdmin SQL Dump
-- version 2.8.0.3
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Erstellungszeit: 23. Juni 2006 um 12:12
-- Server Version: 4.1.11
-- PHP-Version: 5.0.4
-- 
-- Datenbank: `openEMM`
-- 

-- --------------------------------------------------------

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `admin_group_permission_tbl`
-- 

CREATE TABLE `admin_group_permission_tbl` (
  `admin_group_id` int(11) NOT NULL default '4',
  `security_token` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  UNIQUE KEY `unique_admin_group_idx` (`admin_group_id`,`security_token`),
  KEY `admin_group_idx` (`admin_group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `admin_group_permission_tbl`
-- 

INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4, 'action.getcustomer'),
(4, 'actions.change'),
(4, 'actions.delete'),
(4, 'actions.set_usage'),
(4, 'actions.show'),
(4, 'action.op.ActivateDoubleOptIn'),
(4, 'action.op.GetCustomer'),
(4, 'action.op.SendMailing'),
(4, 'action.op.SubscribeCustomer'),
(4, 'action.op.UnsubscribeCustomer'),
(4, 'action.op.UpdateCustomer'),
(4, 'admin.change'),
(4, 'admin.delete'),
(4, 'admin.new'),
(4, 'admin.show'),
(4, 'campaign.change'),
(4, 'campaign.delete'),
(4, 'campaign.new'),
(4, 'campaign.show'),
(4, 'campaign.stat'),
(4, 'charset.use.gb2312'),
(4, 'charset.use.iso_8859_1'),
(4, 'charset.use.iso_8859_15'),
(4, 'forms.change'),
(4, 'forms.delete'),
(4, 'forms.view'),
(4, 'import.mode.add'),
(4, 'import.mode.add_update'),
(4, 'import.mode.bounce'),
(4, 'import.mode.doublechecking'),
(4, 'import.mode.null_values'),
(4, 'import.mode.only_update'),
(4, 'import.mode.remove_status'),
(4, 'import.mode.unsubscribe'),
(4, 'mailing.attachments.show'),
(4, 'mailing.change'),
(4, 'mailing.components.change'),
(4, 'mailing.components.show'),
(4, 'mailing.content.show'),
(4, 'mailing.copy'),
(4, 'mailing.default_action'),
(4, 'mailing.delete'),
(4, 'mailing.graphics_upload'),
(4, 'mailing.new'),
(4, 'mailing.send.admin'),
(4, 'mailing.send.show'),
(4, 'mailing.send.test'),
(4, 'mailing.send.world'),
(4, 'mailing.show'),
(4, 'mailing.show.charsets'),
(4, 'mailing.show.types'),
(4, 'mailinglist.change'),
(4, 'mailinglist.delete'),
(4, 'mailinglist.new'),
(4, 'mailinglist.show'),
(4, 'profileField.show'),
(4, 'recipient.change'),
(4, 'recipient.delete'),
(4, 'recipient.new'),
(4, 'recipient.show'),
(4, 'recipient.view'),
(4, 'settings.show'),
(4, 'stats.domains'),
(4, 'stats.ip'),
(4, 'stats.mailing'),
(4, 'stats.rdir'),
(4, 'stats.clean'),
(4, 'targets.show'),
(4, 'template.change'),
(4, 'template.components.show'),
(4, 'template.delete'),
(4, 'template.new'),
(4, 'template.show'),
(4, 'use_charset_iso_8859_1'),
(4, 'wizard.export'),
(4, 'wizard.import');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `admin_group_tbl`
-- 

CREATE TABLE `admin_group_tbl` (
  `admin_group_id` int(11) NOT NULL default '0',
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`admin_group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `admin_group_tbl`
-- 

INSERT INTO `admin_group_tbl` (`admin_group_id`, `company_id`, `shortname`, `description`) VALUES (4, 1, 'Standard', 'Standard'),
(0, 0, 'Dummy', 'Dummy');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `admin_permission_tbl`
-- 

CREATE TABLE `admin_permission_tbl` (
  `admin_id` int(11) NOT NULL default '0',
  `security_token` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  UNIQUE KEY `admin_permission_unique_idx` (`admin_id`,`security_token`),
  KEY `admin_idx` (`admin_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `admin_permission_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `admin_tbl`
-- 

CREATE TABLE `admin_tbl` (
  `admin_id` int(11) NOT NULL auto_increment,
  `username` varchar(20) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `company_id` int(11) NOT NULL default '0',
  `fullname` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `admin_country` varchar(2) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `admin_lang` varchar(2) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `admin_lang_variant` varchar(2) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `admin_timezone` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `layout_id` int(11) NOT NULL default '0',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `pwd_change` timestamp NOT NULL default '0000-00-00 00:00:00',
  `admin_group_id` int(11) NOT NULL default '0',
  `pwd_hash` varbinary(200) NOT NULL default '',
  PRIMARY KEY  (`admin_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=103 ;

-- 
-- Daten für Tabelle `admin_tbl`
-- 

INSERT INTO `admin_tbl` (`admin_id`, `username`, `company_id`, `fullname`, `timestamp`, `admin_country`, `admin_lang`, `admin_lang_variant`, `admin_timezone`, `layout_id`, `creation_date`, `pwd_change`, `admin_group_id`, `pwd_hash`) VALUES (1, 'admin', 1, 'Administrator', '2006-06-23 12:04:35', 'EN', 'en', '', 'Europe/Berlin', 0, current_timestamp, '0000-00-00 00:00:00', 4, 0x9bd796996fcdf40ad3d86025c03f2c9e);

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `bounce_tbl`
-- 

CREATE TABLE `bounce_tbl` (
  `bounce_id` int(10) NOT NULL auto_increment,
  `company_id` int(10) default NULL,
  `customer_id` int(10) default NULL,
  `detail` int(10) default NULL,
  `mailing_id` int(10) default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `dsn` int(10) default NULL,
  PRIMARY KEY  (`bounce_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `bounce_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `campaign_tbl`
-- 

CREATE TABLE `campaign_tbl` (
  `campaign_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) NOT NULL default '',
  `description` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`campaign_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `campaign_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `company_tbl`
-- 

CREATE TABLE `company_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `status` varchar(10) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `creator_company_id` int(11) NOT NULL default '0',
  `xor_key` varchar(20) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `notification_email` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `rdir_domain` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `mailloop_domain` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`company_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `company_tbl`
-- 

INSERT INTO `company_tbl` (`company_id`, `shortname`, `description`, `status`, `timestamp`, `creator_company_id`, `xor_key`, `creation_date`, `notification_email`, `rdir_domain`, `mailloop_domain`) VALUES (1, 'Agnitas Admin', 'Agnitas', 'active', '2006-04-18 11:09:47', 1, '', '0000-00-00 00:00:00', '', 'http://localhost:8080', '');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `component_tbl`
-- 

CREATE TABLE `component_tbl` (
  `component_id` int(10) unsigned NOT NULL auto_increment,
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `emmblock` longtext collate utf8_unicode_ci,
  `binblock` longblob,
  `comptype` int(10) unsigned NOT NULL default '0',
  `target_id` int(10) unsigned NOT NULL default '0',
  `mtype` varchar(200) collate utf8_unicode_ci default NULL,
  `compname` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`component_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `component_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `cust_ban_tbl`
-- 

CREATE TABLE `cust_ban_tbl` (
  `company_id` int(10) unsigned NOT NULL default '0',
  `email` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY (`company_id`,`email`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `cust_ban_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_1_binding_tbl`
-- 

CREATE TABLE `customer_1_binding_tbl` (
  `customer_id` int(10) unsigned NOT NULL default '0',
  `mailinglist_id` int(10) unsigned NOT NULL default '0',
  `user_type` char(1) collate utf8_unicode_ci default NULL,
  `user_status` int(10) unsigned default NULL,
  `user_remark` varchar(150) collate utf8_unicode_ci default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `exit_mailing_id` int(10) unsigned default NULL,
  `creation_date` timestamp NULL default NULL,
  `mediatype` int(10) unsigned NOT NULL default '0',
  KEY `customer_id` (`customer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 
-- Daten für Tabelle `customer_1_binding_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_1_tbl`
-- 

CREATE TABLE `customer_1_tbl` (
  `customer_id` int(11) NOT NULL auto_increment,
  `email` varchar(100) collate utf8_unicode_ci default NULL,
  `gender` int(11) NOT NULL default '0',
  `mailtype` int(11) default 0,
  `firstname` varchar(100) collate utf8_unicode_ci default NULL,
  `lastname` varchar(100) collate utf8_unicode_ci default NULL,
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `title` varchar(100) collate utf8_unicode_ci default NULL,
  `datasource_id` int(11) NOT NULL default '0',
  PRIMARY KEY  (`customer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `customer_1_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_1_tbl_seq`
-- 

CREATE TABLE `customer_1_tbl_seq` (
  `customer_id` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`customer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `customer_1_tbl_seq`
-- 

INSERT INTO `customer_1_tbl_seq` (`customer_id`) VALUES ( 0 );

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_field_tbl`
-- 

CREATE TABLE `customer_field_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `col_name` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `admin_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `default_value` varchar(255) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `mode_edit` int(11) NOT NULL default '0',
  `mode_insert` int(11) NOT NULL default '0',
  PRIMARY KEY  (`company_id`,`col_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `customer_field_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_import_errors_tbl`
-- 

CREATE TABLE `customer_import_errors_tbl` (
  `id` int(11) NOT NULL default '0',
  `error_id` varchar(20) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `count` int(11) NOT NULL default '0',
  UNIQUE KEY `customer_import_error_idx` (`id`,`error_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `customer_import_errors_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `customer_import_status_tbl`
-- 

CREATE TABLE `customer_import_status_tbl` (
  `id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `admin_id` int(11) NOT NULL default '0',
  `datasource_id` int(11) NOT NULL default '0',
  `mode` int(11) NOT NULL default '0',
  `double_check` int(11) NOT NULL default '0',
  `ignore_null` int(11) NOT NULL default '0',
  `field_separator` char(1) character set utf8 collate utf8_unicode_ci NOT NULL default '0',
  `delimiter` char(1) character set utf8 collate utf8_unicode_ci NOT NULL default '0',
  `keycolumn` varchar(80) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `charset` varchar(80) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `records_before` int(11) NOT NULL default '0',
  `inserted` int(11) NOT NULL default '0',
  `updated` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `customer_import_status_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `datasource_description_tbl`
-- 

CREATE TABLE `datasource_description_tbl` (
  `datasource_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `sourcegroup_id` int(11) NOT NULL default '0',
  `description` text,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`datasource_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `datasource_description_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `dyn_content_tbl`
-- 

CREATE TABLE `dyn_content_tbl` (
  `dyn_content_id` int(10) unsigned NOT NULL auto_increment,
  `dyn_name_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `dyn_content` longtext character set utf8 collate utf8_unicode_ci,
  `dyn_order` int(10) unsigned default NULL,
  `target_id` int(10) unsigned default NULL,
  PRIMARY KEY  (`dyn_content_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `dyn_content_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `dyn_name_tbl`
-- 

CREATE TABLE `dyn_name_tbl` (
  `dyn_name_id` int(10) unsigned NOT NULL auto_increment,
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `dyn_name` varchar(100) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`dyn_name_id`),
  KEY `mailing_id` (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `dyn_name_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `dyn_target_tbl`
-- 

CREATE TABLE `dyn_target_tbl` (
  `target_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `target_shortname` varchar(100) NOT NULL default '',
  `target_description` text,
  `target_sql` text,
  `target_representation` blob,
  PRIMARY KEY  (`target_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `dyn_target_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `emm_layout_tbl`
-- 

CREATE TABLE `emm_layout_tbl` (
  `layout_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `header_url` varchar(255) NOT NULL default '',
  `footer_url` varchar(255) NOT NULL default '',
  `base_url` varchar(255) NOT NULL default '',
  `normal_color` varchar(30) NOT NULL default '',
  `highlight_color` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`layout_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=0 ;

-- 
-- Daten für Tabelle `emm_layout_tbl`
-- 

INSERT INTO `emm_layout_tbl` (`layout_id`, `company_id`, `header_url`, `footer_url`, `base_url`, `normal_color`, `highlight_color`) VALUES (0, 0, 'header.jsp', 'footer.jsp', 'images/emm/', '#D2D7D2', '#73A2D0');
UPDATE emm_layout_tbl SET layout_id=0 WHERE layout_id=1;

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `export_predef_tbl`
-- 

CREATE TABLE `export_predef_tbl` (
  `id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `charset` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default 'ISO-8859-1',
  `column_names` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `deleted` int(11) NOT NULL default '0',
  `shortname` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `description` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `mailinglists` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `mailinglist_id` int(11) NOT NULL default '0',
  `delimiter_char` char(1) character set utf8 collate utf8_unicode_ci NOT NULL default '0',
  `separator_char` char(1) character set utf8 collate utf8_unicode_ci NOT NULL default '0',
  `target_id` int(11) NOT NULL default '0',
  `user_status` int(11) NOT NULL default '0',
  `user_type` char(1) character set utf8 collate utf8_unicode_ci NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

-- 
-- Daten für Tabelle `export_predef_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `maildrop_status_tbl`
-- 

CREATE TABLE `maildrop_status_tbl` (
  `status_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `status_field` varchar(10) NOT NULL default '',
  `mailing_id` int(11) NOT NULL default '0',
  `senddate` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `step` int(11) default NULL,
  `blocksize` int(11) default NULL,
  `gendate` timestamp NOT NULL default '0000-00-00 00:00:00',
  `genstatus` int(1) default NULL,
  `genchange` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`status_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `maildrop_status_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailing_account_tbl`
-- 

CREATE TABLE `mailing_account_tbl` (
  `mailing_id` int(11) NOT NULL default '0',
  `company_id` int(11) NOT NULL default '0',
  `mailtype` int(11) NOT NULL default '0',
  `no_of_mailings` int(11) NOT NULL default '0',
  `no_of_bytes` int(11) NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `maildrop_id` int(11) NOT NULL default '0',
  `mailing_account_id` int(11) NOT NULL default '0',
  `status_field` varchar(255) NOT NULL default '',
  `blocknr` int(11) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `mailing_account_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailing_backend_log_tbl`
-- 

CREATE TABLE `mailing_backend_log_tbl` (
  `mailing_id` int(10) default NULL,
  `current_mails` int(10) default NULL,
  `total_mails` int(10) default NULL,
  `change_date` timestamp NULL default NULL,
  `creation_date` timestamp NULL default NULL,
  `status_id` int(10) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `mailing_backend_log_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailing_mt_tbl`
-- 

CREATE TABLE `mailing_mt_tbl` (
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `param` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `mediatype` int(10) unsigned NOT NULL default '0',
  KEY `mailing_id` (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `mailing_mt_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailing_status_tbl`
-- 

CREATE TABLE `mailing_status_tbl` (
  `mailing_id` int(11) NOT NULL default '0',
  `status_text` text
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `mailing_status_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailing_tbl`
-- 

CREATE TABLE `mailing_tbl` (
  `mailing_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `campaign_id` int(11) unsigned NOT NULL default '0',
  `shortname` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `mailing_type` int(10) unsigned NOT NULL default '0',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mailtemplate_id` int(10) unsigned default '0',
  `is_template` int(10) unsigned NOT NULL default '0',
  `deleted` int(10) unsigned NOT NULL default '0',
  `target_expression` text character set utf8 collate utf8_unicode_ci,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `mailinglist_id` int(10) unsigned NOT NULL default '0',
  `needs_target` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `mailing_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailinglist_tbl`
-- 

CREATE TABLE `mailinglist_tbl` (
  `mailinglist_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned default NULL,
  `description` text,
  `shortname` varchar(100) NOT NULL default '',
  KEY `mailinglist_id` (`mailinglist_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `mailinglist_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailloop_tbl`
-- 

CREATE TABLE `mailloop_tbl` (
  `rid` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `description` text collate utf8_unicode_ci NOT NULL,
  `shortname` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `forward` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `forward_enable` int(10) unsigned NOT NULL default '0',
  `ar_enable` int(10) unsigned NOT NULL default '0',
  `ar_sender` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `ar_subject` text collate utf8_unicode_ci NOT NULL,
  `ar_text` longtext collate utf8_unicode_ci NOT NULL,
  `ar_html` longtext collate utf8_unicode_ci NOT NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`rid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `mailloop_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `mailtrack_tbl`
-- 

CREATE TABLE `mailtrack_tbl` (
  `mailtrack_id` int(10) NOT NULL auto_increment,
  `customer_id` int(10) default NULL,
  `mailing_id` int(10) default NULL,
  `company_id` int(10) default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `status_id` int(10) default NULL,
  PRIMARY KEY  (`mailtrack_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `mailtrack_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `onepixel_log_tbl`
-- 

CREATE TABLE `onepixel_log_tbl` (
  `company_id` int(10) unsigned NOT NULL default '0',
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `customer_id` int(10) unsigned NOT NULL default '0',
  `open_count` int(10) unsigned NOT NULL default '1',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `ip_adr` varchar(15) collate utf8_unicode_ci NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 
-- Daten für Tabelle `onepixel_log_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `rdir_action_tbl`
-- 

CREATE TABLE `rdir_action_tbl` (
  `action_id` int(10) unsigned NOT NULL auto_increment,
  `shortname` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` text character set utf8 collate utf8_unicode_ci,
  `action_type` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `operations` blob,
  PRIMARY KEY  (`action_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `rdir_action_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `rdir_log_tbl`
-- 

CREATE TABLE `rdir_log_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `customer_id` int(11) NOT NULL default '0',
  `mailing_id` int(11) NOT NULL default '0',
  `ip_adr` varchar(15) collate utf8_unicode_ci NOT NULL default '',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `url_id` int(11) NOT NULL default '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 
-- Daten für Tabelle `rdir_log_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `rdir_url_tbl`
-- 

CREATE TABLE `rdir_url_tbl` (
  `url_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `action_id` int(10) unsigned NOT NULL default '0',
  `measure_type` int(10) unsigned NOT NULL default '0',
  `full_url` text collate utf8_unicode_ci NOT NULL,
  `shortname` varchar(200) collate utf8_unicode_ci default NULL,
  `relevance` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`url_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `rdir_url_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `rulebased_sent_tbl`
-- 

CREATE TABLE `rulebased_sent_tbl` (
  `mailing_id` int(11) default NULL,
  `lastsent` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `rulebased_sent_tbl`
-- 


-- 
-- Tabellenstruktur für Tabelle `tag_tbl`
-- 

CREATE TABLE `tag_tbl` (
  `tag_id` int(10) unsigned NOT NULL auto_increment,
  `tagname` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `selectvalue` text collate utf8_unicode_ci NOT NULL,
  `type` varchar(10) collate utf8_unicode_ci NOT NULL default '',
  `company_id` int(10) NOT NULL default '0',
  `description` text collate utf8_unicode_ci,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=10 ;

-- 
-- Daten für Tabelle `tag_tbl`
-- 

INSERT INTO `tag_tbl` (`tag_id`, `tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES (1, 'agnCUSTOMERID', 'cust.customer_id', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(2, 'agnMAILTYPE', 'cust.mailtype', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(3, 'agnIMAGE', '''[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}''', 'COMPLEX', 0, NULL, '2006-07-10 09:58:25'),
(4, 'agnDB', 'cust.{column}', 'COMPLEX', 0, 'Display one Column', '2006-07-10 09:58:25'),
(5, 'agnTITLE', '''builtin''', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(6, 'agnFIRSTNAME', 'cust.firstname', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(7, 'agnLASTNAME', 'cust.lastname', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(8, 'agnEMAIL', 'cust.email', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25'),
(9, 'agnDATE', 'date_format(current_timestamp, ''%d.%m.%Y'')', 'SIMPLE', 0, NULL, '2006-07-10 09:58:25');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `timestamp_tbl`
-- 

CREATE TABLE `timestamp_tbl` (
  `timestamp_id` int(10) default NULL,
  `description` varchar(250) default NULL,
  `cur` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `prev` timestamp NOT NULL default '0000-00-00 00:00:00',
  `temp` timestamp NOT NULL default '0000-00-00 00:00:00'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `timestamp_tbl`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `title_tbl`
-- 

CREATE TABLE `title_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `title_id` int(11) NOT NULL auto_increment,
  `description` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`title_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `title_tbl`
-- 

INSERT INTO `title_tbl` (`company_id`, `description`) VALUES (1, 'Default'),
(1, 'German Default');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `title_gender_tbl`
-- 

CREATE TABLE `title_gender_tbl` (
  `title_id` int(11) NOT NULL default '0',
  `gender` int(11) NOT NULL default '0',
  `title` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`title_id`,`gender`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `title_gender_tbl`
-- 

INSERT INTO `title_gender_tbl` (`title_id`, `gender`, `title`) VALUES (1, 0, 'Mr.'),
(1, 1, 'Ms.'),
(1, 2, 'Company'),
(2, 0, 'Herr'),
(2, 1, 'Frau'),
(2, 2, 'Firma');

-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `userform_tbl`
-- 

CREATE TABLE `userform_tbl` (
  `form_id` int(10) unsigned NOT NULL auto_increment,
  `formname` varchar(200) character set utf8 collate utf8_unicode_ci NOT NULL default '',
  `description` text character set utf8 collate utf8_unicode_ci NOT NULL,
  `company_id` int(10) unsigned NOT NULL default '0',
  `startaction_id` int(10) unsigned NOT NULL default '0',
  `endaction_id` int(10) unsigned NOT NULL default '0',
  `success_template` longtext character set utf8 collate utf8_unicode_ci NOT NULL,
  `error_template` longtext character set utf8 collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`form_id`),
  KEY `formname` (`formname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `userform_tbl`
-- 

-- 
-- Tabellenstruktur für Tabelle `softbounce_email_tbl`
-- 

CREATE TABLE `softbounce_email_tbl` (
  `email` varchar(200) NOT NULL default '',
  `bnccnt` int(11) NOT NULL default '0',
  `mailing_id` int(11) NOT NULL default '0',
  `creation_date` timestamp NULL default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `company_id` int(11) NOT NULL default '0',
  KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- Daten für Tabelle `softbounce_email_tbl`
-- 

-- 
-- Tabellenstruktur für Tabelle `bounce_collect_tbl`
-- 
create table bounce_collect_tbl (
    `mailtrack_id` int(11) NOT NULL auto_increment,
    `customer_id` int(11),
    `mailing_id` int(11),
    `company_id` int(11),
    `change_date` timestamp,
    `status_id` int(11),
    PRIMARY KEY  (`mailtrack_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1; 

-- 
-- Daten für Tabelle `bounce_collect_tbl`
-- 

-- 
-- Tabellenstruktur für Tabelle `log_tbl`
-- 
create table `log_tbl` (
    `log_id` int(11) NOT NULL auto_increment,
    `company_id` int(11),
    `admin_id` int(11),
    `creation_date` timestamp not null default CURRENT_TIMESTAMP,
    `category` int(11),
    `ip_adr` varchar(20),
    `message` varchar(2000),
    PRIMARY KEY  (`log_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- 
-- geänderte Tabellenstruktur für Tabelle `mailing_tbl`
-- 
alter table `mailing_tbl` add `archived` int(11) unsigned NOT NULL default '0';

-- 
-- geänderte Tabellenstruktur für Tabelle `company_tbl`
-- 
alter table `company_tbl` add `mailtracking` int(11) unsigned NOT NULL default '0';

-- 
-- Daten für Tabelle `admin_group_permission_tbl`
-- 
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'action.op.GetArchiveList');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'action.op.GetArchiveMailing');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'targets.createml');
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) values (4, 'mailing.archived');

-- 
-- geänderte Daten für Tabelle `company_tbl`
-- 
UPDATE `company_tbl` set mailtracking=1 where company_id=1;


GRANT DELETE, INSERT, UPDATE, LOCK TABLES, SELECT, ALTER, INDEX, CREATE TEMPORARY TABLES, DROP, CREATE ON openemm.* TO 'agnitas'@'localhost' IDENTIFIED BY 'openemm';


FLUSH PRIVILEGES;

-- 
-- new tabels for webservices
-- 
CREATE TABLE `ws_admin_tbl` (
  `ws_admin_id` int(22),
  `username` varchar(50) character set utf8 collate utf8_unicode_ci,
  `password` varchar(50) character set utf8 collate utf8_unicode_ci,
  PRIMARY KEY  (`ws_admin_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
