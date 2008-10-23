-- MySQL dump 10.9
--
-- Host: localhost    Database: openemm
-- ------------------------------------------------------
-- Server version	4.1.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_group_permission_tbl`
--

DROP TABLE IF EXISTS `admin_group_permission_tbl`;
CREATE TABLE `admin_group_permission_tbl` (
  `admin_group_id` int(11) NOT NULL default '4',
  `security_token` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  UNIQUE KEY `unique_admin_group_idx` (`admin_group_id`,`security_token`),
  KEY `admin_group_idx` (`admin_group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `admin_group_permission_tbl`
--


/*!40000 ALTER TABLE `admin_group_permission_tbl` DISABLE KEYS */;
LOCK TABLES `admin_group_permission_tbl` WRITE;
INSERT INTO `admin_group_permission_tbl` (`admin_group_id`, `security_token`) VALUES (4,'action.getcustomer'),(4,'action.op.ActivateDoubleOptIn'),(4,'action.op.ExecuteScript'),(4,'action.op.GetArchiveList'),(4,'action.op.GetArchiveMailing'),(4,'action.op.GetCustomer'),(4,'action.op.SendMailing'),(4,'action.op.SubscribeCustomer'),(4,'action.op.UnsubscribeCustomer'),(4,'action.op.UpdateCustomer'),(4,'actions.change'),(4,'actions.delete'),(4,'actions.set_usage'),(4,'actions.show'),(4,'admin.change'),(4,'admin.delete'),(4,'admin.new'),(4,'admin.show'),(4,'campaign.change'),(4,'campaign.delete'),(4,'campaign.new'),(4,'campaign.show'),(4,'campaign.stat'),(4,'charset.use.gb2312'),(4,'charset.use.iso_8859_1'),(4,'charset.use.iso_8859_15'),(4,'charset.use.utf_8'),(4,'forms.change'),(4,'forms.delete'),(4,'forms.view'),(4,'import.mode.add'),(4,'import.mode.add_update'),(4,'import.mode.bounce'),(4,'import.mode.doublechecking'),(4,'import.mode.null_values'),(4,'import.mode.only_update'),(4,'import.mode.remove_status'),(4,'import.mode.unsubscribe'),(4,'mailing.archived'),(4,'mailing.attachments.show'),(4,'mailing.change'),(4,'mailing.components.change'),(4,'mailing.components.show'),(4,'mailing.content.show'),(4,'mailing.copy'),(4,'mailing.default_action'),(4,'mailing.delete'),(4,'mailing.graphics_upload'),(4,'mailing.new'),(4,'mailing.send.admin'),(4,'mailing.send.show'),(4,'mailing.send.test'),(4,'mailing.send.world'),(4,'mailing.show'),(4,'mailing.show.charsets'),(4,'mailing.show.types'),(4,'mailinglist.change'),(4,'mailinglist.delete'),(4,'mailinglist.new'),(4,'mailinglist.show'),(4,'profileField.show'),(4,'recipient.change'),(4,'recipient.delete'),(4,'recipient.new'),(4,'recipient.show'),(4,'recipient.view'),(4,'settings.show'),(4,'stats.clean'),(4,'stats.domains'),(4,'stats.ip'),(4,'stats.mailing'),(4,'stats.rdir'),(4,'targets.createml'),(4,'targets.show'),(4,'template.change'),(4,'template.components.show'),(4,'template.delete'),(4,'template.new'),(4,'template.show'),(4,'update.show'),(4,'use_charset_iso_8859_1'),(4,'wizard.export'),(4,'wizard.import');
UNLOCK TABLES;
/*!40000 ALTER TABLE `admin_group_permission_tbl` ENABLE KEYS */;

--
-- Table structure for table `admin_group_tbl`
--

DROP TABLE IF EXISTS `admin_group_tbl`;
CREATE TABLE `admin_group_tbl` (
  `admin_group_id` int(11) NOT NULL default '0',
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`admin_group_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `admin_group_tbl`
--


/*!40000 ALTER TABLE `admin_group_tbl` DISABLE KEYS */;
LOCK TABLES `admin_group_tbl` WRITE;
INSERT INTO `admin_group_tbl` (`admin_group_id`, `company_id`, `shortname`, `description`) VALUES (4,1,'Standard','Standard'),(0,0,'Dummy','Dummy');
UNLOCK TABLES;
/*!40000 ALTER TABLE `admin_group_tbl` ENABLE KEYS */;

--
-- Table structure for table `admin_permission_tbl`
--

DROP TABLE IF EXISTS `admin_permission_tbl`;
CREATE TABLE `admin_permission_tbl` (
  `admin_id` int(11) NOT NULL default '0',
  `security_token` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  UNIQUE KEY `admin_permission_unique_idx` (`admin_id`,`security_token`),
  KEY `admin_idx` (`admin_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `admin_permission_tbl`
--


/*!40000 ALTER TABLE `admin_permission_tbl` DISABLE KEYS */;
LOCK TABLES `admin_permission_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `admin_permission_tbl` ENABLE KEYS */;

--
-- Table structure for table `admin_tbl`
--

DROP TABLE IF EXISTS `admin_tbl`;
CREATE TABLE `admin_tbl` (
  `admin_id` int(11) NOT NULL auto_increment,
  `username` varchar(20) collate utf8_unicode_ci NOT NULL default '',
  `company_id` int(11) NOT NULL default '0',
  `fullname` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `admin_country` varchar(2) collate utf8_unicode_ci NOT NULL default '',
  `admin_lang` varchar(2) collate utf8_unicode_ci NOT NULL default '',
  `admin_lang_variant` varchar(2) collate utf8_unicode_ci NOT NULL default '',
  `admin_timezone` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `layout_id` int(11) NOT NULL default '0',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `pwd_change` timestamp NOT NULL default '0000-00-00 00:00:00',
  `admin_group_id` int(11) NOT NULL default '0',
  `pwd_hash` varbinary(200) NOT NULL default '',
  `preferred_list_size` smallint(5) unsigned NOT NULL default '0',
  PRIMARY KEY  (`admin_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `admin_tbl`
--


/*!40000 ALTER TABLE `admin_tbl` DISABLE KEYS */;
LOCK TABLES `admin_tbl` WRITE;
INSERT INTO `admin_tbl` (`admin_id`, `username`, `company_id`, `fullname`, `timestamp`, `admin_country`, `admin_lang`, `admin_lang_variant`, `admin_timezone`, `layout_id`, `creation_date`, `pwd_change`, `admin_group_id`, `pwd_hash`, `preferred_list_size`) VALUES (1,'admin',1,'Administrator','2006-06-23 12:04:35','EN','en','','Europe/Berlin',0,'2008-02-12 10:55:53','0000-00-00 00:00:00',4,'›×–™oÍô\nÓØ`%À?,',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `admin_tbl` ENABLE KEYS */;

--
-- Table structure for table `bounce_collect_tbl`
--

DROP TABLE IF EXISTS `bounce_collect_tbl`;
CREATE TABLE `bounce_collect_tbl` (
  `mailtrack_id` int(11) NOT NULL auto_increment,
  `customer_id` int(11) default NULL,
  `mailing_id` int(11) default NULL,
  `company_id` int(11) default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `status_id` int(11) default NULL,
  PRIMARY KEY  (`mailtrack_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `bounce_collect_tbl`
--


/*!40000 ALTER TABLE `bounce_collect_tbl` DISABLE KEYS */;
LOCK TABLES `bounce_collect_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `bounce_collect_tbl` ENABLE KEYS */;

--
-- Table structure for table `bounce_tbl`
--

DROP TABLE IF EXISTS `bounce_tbl`;
CREATE TABLE `bounce_tbl` (
  `bounce_id` int(10) NOT NULL auto_increment,
  `company_id` int(10) default NULL,
  `customer_id` int(10) default NULL,
  `detail` int(10) default NULL,
  `mailing_id` int(10) default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `dsn` int(10) default NULL,
  PRIMARY KEY  (`bounce_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `bounce_tbl`
--


/*!40000 ALTER TABLE `bounce_tbl` DISABLE KEYS */;
LOCK TABLES `bounce_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `bounce_tbl` ENABLE KEYS */;

--
-- Table structure for table `campaign_tbl`
--

DROP TABLE IF EXISTS `campaign_tbl`;
CREATE TABLE `campaign_tbl` (
  `campaign_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`campaign_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `campaign_tbl`
--


/*!40000 ALTER TABLE `campaign_tbl` DISABLE KEYS */;
LOCK TABLES `campaign_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `campaign_tbl` ENABLE KEYS */;

--
-- Table structure for table `company_tbl`
--

DROP TABLE IF EXISTS `company_tbl`;
CREATE TABLE `company_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `status` varchar(10) collate utf8_unicode_ci NOT NULL default '',
  `timestamp` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `creator_company_id` int(11) NOT NULL default '0',
  `xor_key` varchar(20) collate utf8_unicode_ci NOT NULL default '',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `notification_email` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `rdir_domain` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `mailloop_domain` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `mailtracking` int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (`company_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `company_tbl`
--


/*!40000 ALTER TABLE `company_tbl` DISABLE KEYS */;
LOCK TABLES `company_tbl` WRITE;
INSERT INTO `company_tbl` (`company_id`, `shortname`, `description`, `status`, `timestamp`, `creator_company_id`, `xor_key`, `creation_date`, `notification_email`, `rdir_domain`, `mailloop_domain`, `mailtracking`) VALUES (1,'Agnitas Admin','Agnitas','active','2008-02-12 10:55:53',1,'','0000-00-00 00:00:00','','http://localhost:8080','',1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `company_tbl` ENABLE KEYS */;

--
-- Table structure for table `component_tbl`
--

DROP TABLE IF EXISTS `component_tbl`;
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
  `url_id` int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (`component_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `component_tbl`
--


/*!40000 ALTER TABLE `component_tbl` DISABLE KEYS */;
LOCK TABLES `component_tbl` WRITE;
INSERT INTO `component_tbl` (`component_id`, `mailing_id`, `company_id`, `emmblock`, `binblock`, `comptype`, `target_id`, `mtype`, `compname`, `url_id`) VALUES (1,1,1,'**********************************************************************\r\n[agnDYN name=\"0.1.1 Header-Text\"/]\r\n**********************************************************************\r\n[agnDYN name=\"0.2 date\"/]\r\n\r\n[agnTITLE type=1],\r\n\r\n[agnDYN name=\"0.3 Intro-text\"/]\r\n[agnDYN name=\"0.4 Greeting\"/]\r\n\r\n----------------------------------------------------------------------[agnDYN name=\"1.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"1.0 Headline ****\"]\r\n\r\n[agnDYN name=\"1.1 Sub-headline\"][agnDVALUE name=\"1.1 Sub-headline\"/]\r\n[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/][agnDYN name=\"1.3 Link-URL\"]\r\n\r\n[agnDYN name=\"1.4 Link-Text\"/]\r\n[agnDVALUE name=\"1.3 Link-URL\"][/agnDYN name=\"1.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"2.0 Headline ****\"]\r\n\r\n[agnDYN name=\"2.1 Sub-headline\"][agnDVALUE name=\"2.1 Sub-headline\"/]\r\n[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/][agnDYN name=\"2.3 Link-URL\"]\r\n\r\n[agnDYN name=\"2.4 Link-Text\"/]\r\n[agnDVALUE name=\"2.3 Link-URL\"][/agnDYN name=\"2.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"3.0 Headline ****\"]\r\n\r\n[agnDYN name=\"3.1 Sub-headline\"][agnDVALUE name=\"3.1 Sub-headline\"/]\r\n[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/][agnDYN name=\"3.3 Link-URL\"]\r\n\r\n[agnDYN name=\"3.4 Link-Text\"/]\r\n[agnDVALUE name=\"3.3 Link-URL\"][/agnDYN name=\"3.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"3.0 Headline ****\"]\r\n\r\nImpressum\r\n\r\nSie mÃ¶chten Ihre Daten Ã¤ndern?\r\n[agnDYN name=\"9.0 change-profil-URL\"/]\r\n\r\nUm den Newsletter abzubestellen, klicken Sie bitte hier:\r\n[agnDYN name=\"9.1 unsubscribe-URL\"/]\r\n\r\n[agnDYN name=\"9.2 imprint\"/]',NULL,0,0,'text/plain','agnText',0),(2,1,1,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table bgcolor=\"#808080\" width=\"684\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr>\r\n    <td>[agnDYN name=\"0.1 Header-image\"]\r\n    	<table width=\"680\" border=\"0\"  bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td><img src=\"[agnDVALUE name=\"0.1 Header-image\"]\" width=\"680\" height=\"80\" alt=\"[agnDYN name=\"0.1.1 Header-Text\"/]\" border=\"0\"></td>\r\n            </tr>\r\n        </table>[/agnDYN name=\"0.1 Header-image\"]\r\n        <table width=\"680\" border=\"0\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td><td align=\"right\"><div style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 10px;\">[agnDYN name=\"0.2 date\"/]</div></td><td width=\"10\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td>\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n                 <tr><td><p><b>[agnTITLE type=1],</b></p><p>[agnDYN name=\"0.3 Intro-text\"/]</p><p>[agnDYN name=\"0.4 Greeting\"/]</p></td></tr>\r\n                 <tr><td><hr noshade></td></tr>\r\n              </table>[agnDYN name=\"1.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"1.5 Image-URL\"]<td>[agnDYN name=\"1.3 Link-URL\"]<a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[/agnDYN name=\"1.3 Link-URL\"]<img src=\"[agnDVALUE name=\"1.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"1.3 Link-URL\"]</a><!-- [agnDVALUE name=\"1.3 Link-URL\"] -->[/agnDYN name=\"1.3 Link-URL\"]</td>[/agnDYN name=\"1.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"1.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"1.1 Sub-headline\"]<b>[agnDVALUE name=\"1.1 Sub-headline\"/]</b><br>[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/]</p>[agnDYN name=\"1.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[agnDYN name=\"1.4 Link-Text\"/]</a></p>[/agnDYN name=\"1.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"1.7 Image-URL-1\"]<td>[agnDYN name=\"1.6 Link-URL\"]<a href=\"[agnDVALUE name=\"1.6 Link-URL\"]\">[/agnDYN name=\"1.6 Link-URL\"]<img src=\"[agnDVALUE name=\"1.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"1.6 Link-URL\"]</a><!-- [agnDVALUE name=\"1.6 Link-URL\"] -->[/agnDYN name=\"1.6 Link-URL\"]</td>[/agnDYN name=\"1.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"2.5 Image-URL\"]<td>[agnDYN name=\"2.3 Link-URL\"]<a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[/agnDYN name=\"2.3 Link-URL\"]<img src=\"[agnDVALUE name=\"2.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"2.3 Link-URL\"]</a><!-- [agnDVALUE name=\"2.3 Link-URL\"] -->[/agnDYN name=\"2.3 Link-URL\"]</td>[/agnDYN name=\"2.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"2.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"2.1 Sub-headline\"]<b>[agnDVALUE name=\"2.1 Sub-headline\"/]</b><br>[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/]</p>[agnDYN name=\"2.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[agnDYN name=\"2.4 Link-Text\"/]</a></p>[/agnDYN name=\"2.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"2.7 Image-URL-1\"]<td>[agnDYN name=\"2.6 Link-URL\"]<a href=\"[agnDVALUE name=\"2.6 Link-URL\"]\">[/agnDYN name=\"2.6 Link-URL\"]<img src=\"[agnDVALUE name=\"2.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"2.6 Link-URL\"]</a><!-- [agnDVALUE name=\"2.6 Link-URL\"] -->[/agnDYN name=\"2.6 Link-URL\"]</td>[/agnDYN name=\"2.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"3.5 Image-URL\"]<td>[agnDYN name=\"3.3 Link-URL\"]<a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[/agnDYN name=\"3.3 Link-URL\"]<img src=\"[agnDVALUE name=\"3.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"3.3 Link-URL\"]</a><!-- [agnDVALUE name=\"3.3 Link-URL\"] -->[/agnDYN name=\"3.3 Link-URL\"]</td>[/agnDYN name=\"3.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"3.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"3.1 Sub-headline\"]<b>[agnDVALUE name=\"3.1 Sub-headline\"/]</b><br>[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/]</p>[agnDYN name=\"3.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[agnDYN name=\"3.4 Link-Text\"/]</a></p>[/agnDYN name=\"3.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"3.7 Image-URL-1\"]<td>[agnDYN name=\"3.6 Link-URL\"]<a href=\"[agnDVALUE name=\"3.6 Link-URL\"]\">[/agnDYN name=\"3.6 Link-URL\"]<img src=\"[agnDVALUE name=\"3.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"3.6 Link-URL\"]</a><!-- [agnDVALUE name=\"3.6 Link-URL\"] -->[/agnDYN name=\"3.6 Link-URL\"]</td>[/agnDYN name=\"3.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" bgcolor=\"#D3D3D3\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr><td><h1>Impressum</h1>\r\n                 	 <p>Sie m&ouml;chten Ihre Daten &auml;ndern?<br><a href=\"[agnDYN name=\"9.0 change-profil-URL\"/]\">Newsletter-Profil &auml;ndern</a></p>\r\n                 	 <p>Um den Newsletter abzubestellen, klicken Sie bitte hier:<br><a href=\"[agnDYN name=\"9.1 unsubscribe-URL\"/]\">Newsletter abbestellen</a></p>\r\n                         <p>[agnDYN name=\"9.2 imprint\"/]</p></td></tr>\r\n              </table>              \r\n              </td>\r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\"><img src=\"[agnIMAGE name=\"clear.gif\"]\" width=\"8\" height=\"8\"></td>\r\n            </tr>            \r\n        </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,0,0,'text/html','agnHtml',0),(3,1,1,'R0lGODdhAQABAIgAAP///wAAACwAAAAAAQABAAACAkQBADs=','GIF87a\0\0ˆ\0\0ÿÿÿ\0\0\0,\0\0\0\0\0\0\0D\0;',5,0,'image/gif','clear.gif',0),(4,1,1,'/9j/4AAQSkZJRgABAQEAYABgAAD/4QBmRXhpZgAASUkqAAgAAAAEABoBBQABAAAAPgAAABsBBQAB\r\nAAAARgAAACgBAwABAAAAAgAAADEBAgAQAAAATgAAAAAAAABgAAAAAQAAAGAAAAABAAAAUGFpbnQu\r\nTkVUIHYzLjIyAP/bAEMAAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/bAEMBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/AABEIAFACqAMBIgACEQEDEQH/xAAf\r\nAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEF\r\nEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJ\r\nSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\r\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEB\r\nAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIy\r\ngQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNk\r\nZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfI\r\nycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AP3v+D/wf+Kf/BU74p/t\r\njfF/4v8A7Y37YfwK+E3wK/bD+PP7HnwF+Av7Hnx58Vfs0aJpWifs0eKv+FeeLvif8T/F3w7+weNP\r\niT45+JXjSw13VrCw1bXf+EY8HeGP7MsLDTLi/uHfSfoP/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVUf8A\r\nBGn/AJJ9/wAFAv8AtMN/wU9/9ad8VV+wjsqKzuyoiKWd3IVVVQSzMxICqoBJJIAAJJxQB+Pf/Dmn\r\n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\r\n+FPhvUYSdrWeqa38ZNY/aI8ZmznhZkd/DWr+EdatpgtxZa7AwVE+odJ+Lnw11zxevgDS/GOjXHjV\r\n9AtPFCeF2mkttak8P31pZX1rqkdjdxQTSW8lrqFpMditJGryCWNGt7hYiX4u/DSDxV4p8DS+MtFH\r\ni/wT4cm8X+KvDgnd9W0Twzb22nXk2s3lokbSCyS21bTZd8QkZhewKql22hPLMyjN03l+NVSOHWLl\r\nB4SuprCSnGnHFOLp8yw8qko01Wa9m5yjFS5mkP6zhuVz+sUOVVZUXL2tPlVaN+ak3zWVWNnzU2+Z\r\nWd0rM7+2t47S2t7WJp3itoIreNrm5ub25aOGNY0a4vLyWe8u52VQZbm6nmuZ5C0s8skru7flT/wV\r\n2/5JR+xt/wBpXf8Aglv/AOtpfCWv0f0H4p/DvxR4Hu/iV4d8Y6FrXgOws9Y1C98UadeJc6VaWnh9\r\nbh9ZmuJowXiGnR2s8lwjoJFiQSBGR42b8uf+ConjTwr8QvgJ+xL4w8E69p3ibwxq/wDwVd/4Jh/2\r\nZrelTfaLC9/s/wDbh+F+l3vkTYXf9m1Cyu7SXgbZreRf4c1FXBY2hGtKvhMVRjh8QsJXlVw9WnGh\r\ni3Gc1haznCKp4hxp1JKjNxqctOcuW0JNVCtRqez5K1KftqbrUuSpCXtaK5L1adm+emvaU71I3iva\r\nQ196N/2EooorlNQooooAKKKKACiiigAooooAKKKKACiiigAooooAKinghuYZra5hiuLe4ikgngnj\r\nSWGeGVDHLDNFIGSSKRGZJI3VkdGKsCCRUtFAHg3wp/ZX/Zh+A+va54q+B37OPwG+DPifxNZtp3iT\r\nxH8KfhB8Pvh3r3iDT2vE1FrHXNY8IeHtH1DVrNtQjjv2tr+4uITeRpdFPPVXHvNFFABRRRQAUUUU\r\nAFFFFABXK6L4E8D+G/EfjLxh4d8G+FNB8W/EW80XUfiD4p0Xw9pGl+I/HeoeHNEtPDPh6+8Za3Y2\r\ncGp+J7zQfDdhY+H9Fudbur6fS9EsrTSbF4LC2ht06qigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooAKKKKACiiigD+R/xF4b/aH8W/8ABIj/AILM6F+zJD8Rr74h3n/BXX/goA3iDRfg7JdR\r\nfFvxJ8GoP26ref48+Gvhs1iPt03ivXfg5H420+zsNOI1TVrae80jSRJql/ZxSc34K0r/AIJ5eL/2\r\n0f8Agmhc/wDBD34VXPw++JngP4uSTftmeKPhF8Jvir8IvBXhv9iIfDnxMPiV4I/a41nxXoHhrTPE\r\nvxI8Q+Lf+ELt/h7afEC48S/ESDxrpN/ex31lqJ068uv0F+Bnxd+In/BMH4t/tk/BL41fslftf/Fj\r\n4YfG/wDbD+Pv7Y/wG/aA/ZP/AGefiH+0/wCENf8ADX7S/ipfiJ4n+HHxD0H4RaT4i8Z/DP4gfDbx\r\ntqOv6DCniLw7b6H4q8ORaZq+k6nIVeXUPqL/AIe7fCj/AKM2/wCCrv8A4q3/AG0v/nS0AfiL/wAE\r\n3fhL/wAFcPjH8Bv2bf2vPEf7T3jLwv8ADuzHxt8YfGLVfiH+2b+0H8WvHXxj+Huhj4x+DbbwF/wy\r\n74w+Beh/Dn4S+Io9bsPDN7pPj7Qvjtq2seH7bw9Dr2nWt5qWsyWGm+Ff8E9v2p/H/jD9kzxT4p+N\r\nf7avxvuf2uD+w3+0F4q+HHgmy/bP/a8+IPi7xL8ZdH/Z3+K+v6pN8TPg346+Bnw6+GHwy8S+ENK0\r\n1vGnhLT/AAZ8SviMun+MtBtJdP12C+07S4da/ov/AOHu3wo/6M2/4Ku/+Kt/20v/AJ0tH/D3b4Uf\r\n9Gbf8FXf/FW/7aX/AM6WgD8ev2W/2if+CnHgX49/8EZf2Xv2sNW+JHjzwV8UNZ174u6T+1/4V1zX\r\ndM8O/tB/BrxJ+xd8S/Glv8Ef2q/D/wDabz2Pxt+D3xf1T4fW9tqWsG50L4oadb6DrtoLbxHF4ktt\r\nW/Vj9mP4p+Gvh74XsbHxj8Ybvw1Z2nxA8fapf/DtP2e/EniF7jS7/wAc67fQCXx5p3h3VXvP7bs5\r\notRt7+yl/wBCsru1sY4ibQyS9b/w92+FH/Rm3/BV3/xVv+2l/wDOlo/4e7fCj/ozb/gq7/4q3/bS\r\n/wDnS17GW5pHA4fGYadCdWGLq4Sq5U6tCnOEsJ9Z5VbEYPG0pxn9YbknSUk4RtKzkn8/m+QxzXHZ\r\nZjZV4Q/s2GMiqFSjWqUq8sVUwNWLquhjMJJwpSwK5qMnOlXVTlrRdOMoVOX+J+ga7q/7RPxk/aJ+\r\nEH26/wDF3we8DfAX4jeC9MtZb/S7P4g/D+48Pa1dfEfwY9kYEnuRqnhKbSLpbRLGXVbS8Gk2MFtD\r\nd6rDHLR8FfCnxp8Pfj58Vtd8ei4v/HXxG/YR+Jvj/wCJutWyXsnh+Px/4o+Jc0knh3SJpg9tZ23h\r\n3w3pmh6FZ6bDMVFtpBvIlMU5c9t/w92+FH/Rm3/BV3/xVv8Atpf/ADpaP+Hu3wo/6M2/4Ku/+Kt/\r\n20v/AJ0tfQR43xccu/s2OCoezll1PLJ15TviZYWjRw8KNH2qpJqhDEUq2NlR1jPE4jnk7UqaXhQ4\r\nHjGWIm8zm/rGa4vN3RjhKccJRxeLqV+epQoqs6kaqw1WGDVSeIqWoUnGnGmq1WMvLvg/8D/ic3g3\r\n4X/ArQXuIP2ff2gPAfgX4r/FjVL/AFHV49R8HS+ELPRYPjH8PtJmEhj0S48fa5c+GfMVmtrmyF14\r\nlsRbvaWF19i+ffjRp9/pf/BOX/gmxY6nZXenX0H/AAVd/wCCe/n2d9bTWl1D5v8AwUj8NzR+bb3C\r\nRzR+ZDJHKm9BvjdHXKspP2n/AMPdvhR/0Zt/wVd/8Vb/ALaX/wA6WvmH46/HX4m/8FLPib+yL8Bf\r\ngL+yL+2H8Lvh98Lv2w/2b/2svj/8f/2sv2b/AIjfsx+AvCvgL9mP4jab8XtM8C+BdM+L2m+FvF/x\r\nJ+I/xJ8X+FvD/ha0tPC3h++0nwvpN9f69r1/FbRKqY57xlis9wOIwFbCUaEMRmlDNqlSnO9Spi6N\r\nPM8O6tV+zh7WtWwmNwuHq1pWco5bRkop1JKPdkPCccir4Osswq4xYPL8bl1ONahGEo0sZWyzEuFO\r\ncKjVPD0cRgMTVo0HCbh9fqU41VSoU4P9+6KKK+NPrwooooAKKKKACiiigAooooAKKKKACiiigAoo\r\nooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiii\r\ngAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKA\r\nCiiigAooooA//9k=','ÿØÿà\0JFIF\0\0`\0`\0\0ÿá\0fExif\0\0II*\0\0\0\0\0\Z\0\0\0\0>\0\0\0\0\0\0\0F\0\0\0(\0\0\0\0\0\0\01\0\0\0\0N\0\0\0\0\0\0\0`\0\0\0\0\0\0`\0\0\0\0\0\0Paint.NET v3.22\0ÿÛ\0C\0ÿÛ\0CÿÀ\0\0P¨\"\0ÿÄ\0\0\0\0\0\0\0\0\0\0\0	\nÿÄ\0µ\0\0\0}\0!1AQa\"q2‘¡#B±ÁRÑğ$3br‚	\n\Z%&\'()*456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyzƒ„…†‡ˆ‰Š’“”•–—˜™š¢£¤¥¦§¨©ª²³´µ¶·¸¹ºÂÃÄÅÆÇÈÉÊÒÓÔÕÖ×ØÙÚáâãäåæçèéêñòóôõö÷øùúÿÄ\0\0\0\0\0\0\0\0	\nÿÄ\0µ\0\0w\0!1AQaq\"2B‘¡±Á	#3RğbrÑ\n$4á%ñ\Z&\'()*56789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz‚ƒ„…†‡ˆ‰Š’“”•–—˜™š¢£¤¥¦§¨©ª²³´µ¶·¸¹ºÂÃÄÅÆÇÈÉÊÒÓÔÕÖ×ØÙÚâãäåæçèéêòóôõö÷øùúÿÚ\0\0\0?\0ıïø?ğâŸü;âŸíñâÿ\0íûaü\nøMğ+öÃøóû|øû|yñWìÑ¢iZ\'ìÑâ¯øW.øŸñ?ÅßşÁãO‰>9ø•ãK\rwV°°ÕµßøF<áìË\r2âşáßIúşÓğûş’ÿ\0†ÿ\0Å~Ó¿üÕQÿ\0iÿ\0’}ÿ\0ÿ\0´ÃÁOõ§|U_°ÊŠÎì¨ˆ¥Ü…UU³3ª€I$€\0$œPãßü9§á÷ı$ş\rÿ\0‹<ı§ùª£şÓğûş’ÿ\0†ÿ\0Å~Ó¿üÕWÓÿ\0ÿ\0à¢Ÿ³GÃ©¯4íÿ\0âÇïX´ğİx{öSøñ‡ö¡¾²¾‡\nÚv«wğ/Á>;Ò4[ô¢·¸³Õµ+;»)\'‰¯ ‚7_˜àáŸ„_Zkß‹?²Ïí£ğÁğÊ‘·Ä?Ÿ±÷í3àO	ª1fË5ÿ\0Âû	’)£(bTógF	!$l\0Nÿ\0Ãš~Ò@¿à°ßø³ÏÚwÿ\0šª?áÍ?¿é _ğXoüYçí;ÿ\0ÍUp_³ïüCá×íOáÙ|Yû?xÏà¯ÅmÉ­ÿ\0µâğÎ¡®.½¡}¤Éöx|IáGT´ñ7…®nÖZÒèš|×40Í$ıqáÏÛº•cñw€¥ŠÑ»ğæ¨—¢ô™—NÔã¶I?<JuH‡ü³wãÌ 	ÿ\0iø}ÿ\0Iÿ\0‚ÃâÏ?ißşj¨ÿ\0‡4ü>ÿ\0¤Áa¿ñgŸ´ïÿ\05UúqğóãÃßŠ³xK_‚æö$2\\è·Šlu«XÃ”2I§Ï‰%€¤ÜÚ›eß\Z¼Ë#l¯N ÇŸøsOÃïúHüÿ\0yûNÿ\0óU^\rñwàgÅ¿ø&ÄOÙ+ãWÁ?Û\'öÃøßğÃâÇíû<şÉÿ\0´ÀoÛãïŠ¿i\rkşı§ş!é?´ˆ<Oñu|6øğÏÆ^\"ğïˆ’_‹Ã*Ğíõ=\'WÓÉ+ê¿õùEÿ\0vÿ\0’QûÚWà–ÿ\0úÚ_	hõvŠ( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( ÇŸø#Oü“ïø(ı¦ş\n{ÿ\0­;âªı. šÚæ®-®\"’‹yãI 	Ç,3E håŠXÙ’HİY«¤Šü~ÿ\0‚4ÿ\0É>ÿ\0‚Úa¿à§¿úÓ¾*¯Øj\0jª¢ª\"ª\"(TEUU@\nª \0ª \0\0\0\0\0OTL›MÔbÖ’Æ][¸õhõE·}6M1íä[ôÔRìW±{S*İ­È6índj½_ÿ\0ğV?úß…¼9àÏşÔn4ÖñÍµ÷‰¼q=¥ÃÛİ]x^ÎvÒô}š)w>—®jcUºÕ£xâó¿°lmVY­.u+f÷øc ¯ÄÙæ%¡QQxº’öµÚRöz0•lEe(sÊ¡\'\n|ÑsŸ,.¯sÅâïÃ¹>77ÄÓZxHC–6£:Õ«U…\n4”¥¤TªÔ<í\'NšE	òò¿æ«ş\n¿ÿ\0ïøáÚÇáÿ\0í‡ÿ\0cø¡ğ÷ö}ı¡ôÏ]ÃñÃÀ\Z=§ˆôÙ«Åút‘5ÌÚÇ‡“Â^Ö4şÙº°·Ò<qà\ré_¼icª[x–ÆïÃş(ÑµY¼MûğÛÄº§Œ~ø;Å\Zå†¥kºÖƒes¯éZ>£>­¤é!„5—ˆ4ı+S»±Òïµ\r*Ï[µ¿·Òõı\'I½Ôtèíoî4»ºû4›µöoìıâ-ÏÀ·–\Z¯ˆ<7¤^ÛøŸQ‘#Ö¼G¢èÓËaq¦è¦¶§{fòÂ—qêí‰7<²ˆcQúïøW•ä7~Kı§ŒÌpø¬5<G´’¯õŠ5£*u%=\Z1tÜk{:‘präƒœgÏu8~UÁ%æ9×<s,¿	‚Äá«Ë\r\ZT¥J4±4c\Z±Œñ±´\'BÜ¥QÉJ²„)ª|ê+éíYÕ|=ªØkz%ıÎ™«i—1İØ_ÚHcÚxÏÊèÜ†V’Xœ4SDÏÈñ;£~Îüø¶Ÿ¼\r¯t°Câ-*q¥xÖ1D/’–+ëx™œÇk¨BŞlJÕ&K˜¿r@şt|qûX~ÊßµÃáŸˆŸ´ïìëà‹K{öĞ¼UñÇá~…«¥•ÙZİ>Ÿ¨ø¦Şé ¸ò¤0HÑ•P²\\\ZıXÿ\0‚søªÓÇV:¯¼¬YxÓàÿ\0¼:÷şø‡á+û_ü<ñEÿ\0‡<Gs \\Ÿø¿G{ßkSé÷Ë¯iW£KÕ\'6×ºn¡etŸi±•-¿œ\'Js§R§Rpœ\'	Âqv”g%(Ê-4âÒi«5söÊu!V©Jp©Nkš§%8J/gE¸É>é´~£WåüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÕÚü¢ÿ\0‚»É(ı¿í+¿ğKım/„µ%Ÿ«´QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0~<ÿ\0Á\ZäŸÁ@¿í0ßğSßıißWì5~<ÿ\0Á\ZäŸÁ@¿í0ßğSßıißWì5\0~Iÿ\0ÁY<¥~Ñ¼_û |D[Èşütğ®„<[wáÛ—Ò|c×„~ hŞ1±m]’;Ë+TğÎ‚Ó	´»òöæê\"±yñÈ¿‰²ÿ\0ü[öxı’|	¬ü;ø_¨|K¿ğö¹âÛÏ\Z]Ÿø®Ã\\Ô\"Öoô}Cº×–¾ÒŒvocáí7m´‘Ê#f•|÷ZşŸjÿ\0…|	­¡Y½ç‰ü<×ÖVĞ(kCJ»G¬XÂKK:¬×öñîœ°Ä¯-Â©ü…®ì»3Ì2œTq¹f2¾Î¯‡¨éÔP¨¹g­ã%¼]Ó²vºMrc0,ÆÕñøL>7Ï\Z†*:ô\\ã~YJ•HÊq»qæ‹åvjÍ&¾@ø½ğ—ÂŞ\nğt ÑRmâm/G‘/.–â&·¾Òõû×uEdIt˜@?0+#ôÀÏÌÕú#ñ{G¿×>øÖÏFÑá!ñ\Z\rî©á½\Z9í,îµOhñÿ\0jiZE¡¨M†›q®ÜÚ.‚uÙ­¾©q<ÿ\0»BGÉŸğ@ÚKö>ÿ\0‚‚|Cø© üGÒuÿ\0‡_µOÀ?M5—ìÅñÿ\0KŒêŞÓ-íí.¼n,.tËÿ\0j~ñ”z¦‹â¯	ÄÏc¡Şø×P·ñ5”v_Ğ<â¶_—ğ¶\"\\E˜bñùæ‰ö8iª•q8Ú2)áÜqŸ°§MTœéIÔ¨êSTçQSœybÿ\0âÿ\0\r³Ç‰i¼ƒ/Á`òªøl/¶«KØá0˜:©Õ§U¼<\\eRN4£RQÂÑ¨Ûœ]E>y~‰|\'ÿ\0‚Á;i_†øÁûd~Êø¥ñ³Å~ŠòóÄ\ZÇŒş-xvşÏÂsŞŞŞx;FŸNğ§ü=¤Ç-®‡wmw>4›[¸noç´»3Íjn%ı—ı›ÿ\0fß‚_²/ÁşÏ_³§,şüø|ºêø;Àú~«â\rjËB_x—Xñ†º¶úŠu}s[™uøƒYÕ¥zÀâşd€E\0÷\Z+ğß3¯œæ˜ü×¢«ãñU±U#”`êÍÊ4ãd¯\Zqå§×3QNMÉ¶ÿ\0oÊ°ÊòÌ¿-§?i\rƒUT_«Ñ…\'UÁ9(Ê«‹©%Í+JOW¸WåüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÕÚü¢ÿ\0‚»É(ı¿í+¿ğKım/„µççêíQ@Q@~:ÿ\0Á]ÿ\0k_ÚËöP‡ö$½ı‘|)kñ+Å¿?jm[Ã~>ø<Únu¬üeø[àOÙÛãÆ¿ü4ğ~§ªÛÏ/‡¼sâ[?†BÛÁš®™$âa¥XJ.ì//4û¿Øªüşı±¾|Mø½û@Á6|{àM\ZÓTğÏìçû\\x¯âÇÅ‹ÛcKÓeĞ¼ª~Ê´OÂÛ-JÎÏPº·ºÖî$ñ§Äi­§i^_Çôº„-•Üğ€xN·û{Ş|Qı§ÿ\0àgösñí†«û4~Ü¾ı®¼_ã(DĞ¯5Ù|/ø¡xËÁ:eÕıÍ½ö¯áx/Æ7Z®â½JÔl®íõË\rGÃºúÌÚ|–Ñú‡Ãø*‡ìßñ7ÆŞĞô|{Ñ>üYñìŸ>\nşÓ/øA­øoöløÏñµ-_IÒ¼)ğóâíÉÔoÅš†ƒ«ÛxÄZï†t|BšÒ¼â]~]WEMKóÿ\0Å_ğJ~ÿ\0‚«üø¹ğnïH¶ÿ\0‚}êĞ~ÙŸ¼à}3X´ğçŠgÚöøwğÛâF·ğê/í+\rZçÁ?\ZµÛ\rxátÇw/‚¾%Âo®Âš‘®Ûù%û3Á0~0x@ı’ÿ\0fÿ\0~ÄÚŒ±~Î_¾MãoÚ?Æ¿ğPßÚ/ÇŸ²ÿ\0‰¾şÏŞ\'´ñ‚>#üı™4oÚcKñ‹ñÄğ¹áxËàŞ‹ğ«á—Œæ”ø‡Ãút$öR_ø)—ì­gàŸÙ[Ç:Æ½âíÓöÄı îÿ\0eÿ\0„\Z±àİR×Å‘übÓ<]ã‡ºîã=×±ğïü©x+\\×.}*ÇÄ:†jn^\rfÒå¨|Iÿ\0‚š~Ï¾ñOˆ~xÃŸ>4üSÑş2ø«à>ğ·àŸÃõñ—<uñáçÃ?|Xø¥ƒ£½Ö´-mág…¾!xfëúş½áí7Hñ\rÛøv	¯µTKy,ş5ÿ\0Á+iˆ_\Zà¡w\Zn™¤„pxGâ¿Ç¯ø\'•ì~-ğô\"‹öÌı õÿ\0Ùûã¿dºIæ†÷Áz†?hÙoE½Óõr{=6ãHøÓâ¿±ÌĞG©›nóÄß°Åé?d¿ÙGøÃû$ø/ö£ø˜>(|rı¥¿k-+áÇÆ	~~Ô??hÚb÷Åuï~Êß´ƒñGàÿ\0†ôÄğ/Œ|a¨ü4ñ’^|AÑíüyà}ÂWšd·\r¥28Ö7·Ö¡ñgö€ÿ\0‚léÿ\0.üaá†¿´ÆßÛáÇ¯‡ş\Z·ƒş!é~&ı~üN×n<â-+Å\Zdšß…µ¯|Oğ\\/s{áI´½~ÒkM_[ğıü2Ïèÿ\0?àª³ÄßxCÒ<-ñïDøcñgÇ²|,ø+ûNx¿á·á¿Ù³ã?ÄfÔµ}\'Jğ§ÃÏˆW·\'Q¼j\Z¯mà/k¾Ğ<ñ\nkHbğG‰uùu]5/„¾şÄ_·ˆüKûj´&¥ãkÂ_hoÛßÄ¿ÄºÄÿ\0Ÿ¿fÏß²ï~üğOÄ‹öÖš¯Åß‰V>7ñ.¥vŞ&ğÖ©ã+ÏèÚö‡£ÍâËÛ	Ç©\'Ïÿ\0³7üã€tÙ/öoñ×ìM¨Ëìåñ[ádŞ6ı£ükÿ\0\rı¢üyû/ø›áßìıâ{Ox#â?Áÿ\0Ù“Fı¦4¿h¿üAqáO	ëğ7Œ¾\rè¿\n¾xÉŞiOˆ|?§Ab@=>ø-7Ç¾şÔövñZjÿ\0	ÿ\0à«ß?b?é7Ÿ\nüA¤_|qñáŸÃ}@kp\'…İïÇ\r/IÔ¼P×öqİÚèZoÄü/ÑŸJºÒõY _Ñ?ˆ?ğU_€uïiü/ı¨¼g¥ü\"Ñ¼3¬şÓ4øqğ#Ä5ğ‡ì¤<KàÍ#â$š7Ç{í&ñõ+xOÀšæ—âßx_áî“ñ^ğFƒwÿ\0ˆ¬,ae\'ó›ÆŸ°×í•kğ‡ş\nğ³@ø%eâ‹ÏÁY>ÿ\0ÁGşk¶Ÿ¾iš_ÇOÚ~Ñş3øŸá}´:Æ¹g¨ü8ñ‡…ô_…Z†‘u«øú×LĞ5MSR‰´Iïí-„÷^ãø\'·í¡øÛöÆÖ¢ı‰ş\'~Ğ\ZŸíŸâûÿ\0ÚÀ\Z­—ü«â¿Àß…_¾\"|`øoáøûàwíEğ×á÷í\rğÊx/áŸ‹ôûãOŞø«ñáåİ·†¬ÿ\0°†“¥èà²~*ÿ\0‚|Ñ?h¯~ËşĞ>0üjøã¿|ø‰o7Àß†ÚÄŸxgáÇx“Ã^øÏãOé7iáï|)±Ô¼9¿Ä.¹¼’ÚÓOÖ´OM‡V±[ŸF­ÿ\0(ø÷ñ7örøà_ü\'Öm4/kŸµÇìOğŸQ½¼Ñô½r)¼ñŸö­øIğ·âš¶z½­å¬W\Z·‚üW­é¶ÚŒQ-ş—=Ìz†=µõµ¼ñøßì»ûxçà§ícñc[¾ğŞ‘áZüöı“<¨ü<ñ—ˆôøí<Gû?j?´~Ÿã=Âê¾0Öş0xwKğ×‡ü}àÖğŸŠüGâ[ïKÔ3ÿ\0Â[ªøLÔµám¯Ø+ÇzwìáÙpü}ı¢~\"[~Ô±_Ç#àµŸÄ‰—ú‡û9şÓş,x›Jğo‹iOˆúŞ…à{CÃÖ¼Ølõ-\"×\\¿‡K[õ¼šÊÁb\0ıKøİñ³áìåğ§ÆŸ>2x¢ßÁ¿\r~éqêŞ\'ñöz¤öÑ\\ßZi:m•†“£Új\ZÆµ¬ëZÎ¡§hš‡£Ø_júŞµ¨ØiZeÕõå¼|ğ·ş\nMğ#Ç~+ñÏ€¾!øKã‡ìµã¯ü!ñí	wá?ÚŸá…çÂGÄ¼v–0ø³á[èõ/è\Z¿†üw=Œ^2Ò›Xµñ¯ƒÆ«£Íâ\néj¶Ü|ÃûHé?·OíÕû?|CøIwû\Z^şÊ8ğv¹ğWã÷ÁïüMı¢¾x÷À>(şÏ_>|cğÿ\0ÂÚüÕüUâmÃŸ ğeş‡«øšãKŸOÑ­§7oo}:Ce?‰~Öß³çíû|?¾&şÓ_nÿ\0àŸß~~Á·Âhu¿|NğÅOˆÚÏ?j‡ÑüWñ(>ë:ğşğ‹à÷…~^êm-ş­mñÆÚ¦¥½¿ƒtm>Òv¹\0û—àÿ\0ü7à7Åÿ\0‰¾†µÂİKö–Oê_³ßˆ¾4|ñÃ¯|^ğ‡…>ëß5¯xoÄ:•ÌñiZ%·ƒ´%»şÈñ¤ñÉ}oA™¼ šeìº¬_?à«_³OÄÏxLÒ¼1ñ÷Ãÿ\0>0xú…iÏ|\Zñ…?gŒÿ\0oµmKCĞ¼#àêR¦±5Ç‹õ]S±ğ»â?\nøwÂ=¹¶†ø‹\\ŸSÑãÔ?1ï>-~Ğ´ÏÇÏø$¯Àßÿ\0\n>|#Ñ<G¢~Òwqøßá?íá3üXÑµø\'ÇÆÿ\0‡CãWÁk?	hğ\\xkàSIãMúf½ñô/^kŞ(ğ6‡„–z¥Ûú®û5~Şÿ\0fßØş	áñGösğÏ‚|û.|Pıî¾+~ÖÚWÆOß|7ñïÂïØ{Çñ·‚ïş\rü8´Ô5ÿ\0Œiãÿ\0Œ\rğ¯Â\Zv§¡üIğg„¼;à„ÖüGwÿ\0	¶Ö\Zm­ĞèOü³ö³ñìğÂ~!ğf¿ğçÁ^8øÃñÃáÀ?|JøÁ)áWÂEñİæ¥«xÛâ÷Pêz$WÚ/Ã…şñß-´‹­sF±Öõ½HÑïõ+{;éË|àÛÃöÑe_ÛÓâÏÁÚ3áü’_Ù×áMŸŒ>j–_\nµ„Ÿ¼\'ñ6ãşÑâ|_ı|%£è3øƒáN‹áİNø•ğßÄ¾Óü)â¯ˆÚ6Ÿã/\nh\rãK»{Øıåÿ\0&ı—<iûMü%ø?©|3Ò|7âÏ‰¿²ïíIğGö¼ğÃêVú…ş-kµ]Qõ?…Ú§Š.´½j\n\\xËÂ^\"ñ&áïİi—\Z]Š¿°—_{?O«ßÚşt~Ò¿°ßíkûx·í‹ñ·Yø5áßÙ³Äf_€ß³ÃoÙ»âçÄï\nø²?öÿ\0ÿ\0iÚWÆÚ‡íâz<á	üHÒíî?g¿Øé>\'øƒ«Áàïxß[ñU‘§j6¾»\0·û7ÁF¾ xoÄÿ\0µ©?íiğçş\nqû6üı€uoÛ?Ä¾|<ğgÃgøsñ\'ÃÒkz­—ìÿ\0}«ü6ºÖ|	tÿ\0<¡ø‹ÅŞĞõ»sñ+áïü!ZÅ§ŒÛZMVÅí½“á—Æ¿ÛÏà—¿a/~Ô?üñ‹áÇíë?€<kğ«Ãß4„Íû1ülñGÀŸü|øoáÿ\0…¾+ŸÅRkş3ğT’|?ñoÂ]jÏâµæ³ã-WY¼ğ¯Šl/ôX—]ĞÓÖÿ\0c¿¶oÆßxËÅß³.›ÿ\0îøk?üßöœıŠuèâñ·ÂO|EøÉª~Ó^Ó´o;ÃßüCâ?‡ğü\"ıŸ×Ã:çˆü\'uâ}wGñÖ¯â¯Åk§øoAĞ$×½—ƒ~~Ş|_ûøwö‚ı™mş\Zøö¸Ö~/üKñt_¾üJ›ö©øëàO€¿>ü\'ÓşØÇ¯\\xÃş×nş!ø‡â¾­âmğ³[Ñµxjt¾œk\"²\0ûoáOüOögø×®|ğÃsÅ\Z÷¾;ËñQtï‡CÂ×–:øcÁ»İâ¥ßÇ?]ËÏÂKx®ÒMÿ\0	S[O­xÃVÑô_Á­I|²§ã—ìáÿ\00ı§¼qñÇöb²×¿h_†?~4|{ı«>$|ı¡?à–şø]áâ_ìUğÃÁÒüF—_ø“¯xÆ×X¹ø§e{ğkJğ×€µ/x›â¹øñPøÎæÓáµ®•y©øJŞOwı–?b?Û3à/íE«şÛ&ğ÷Ã}Gâü+ş=öëøkà_‡ZEïì¯ofš÷ìÓâOƒ6•¼97Ä?…ºL²x?ö•µƒ]¾Õ~.øë^?|/§ëÚ†ƒ\r•ï’|\'ı‡?jë„Ÿ°/ìoyû\Z|6øEû~ÑŸ>&xûöñğ÷Å/†·şñÖ…ğ/Å«â¿øÿ\0á/†Vÿ\0ÅŸ5OŠ_µ=¬\Z†ƒñGøÅáè:cüAñµÎ©¯kğÛé(«|Sı°?m‹/‚Ÿ´¿üÁ?<¦~Íß²ßÇŞÓÿ\0dËÿ\0‚öoñ›àìÏñ“Vø1ñƒÇú÷Æ{QÓüyá/ˆšÌŞøã?j^³O‡º\r–á/Ä¾\Zñ\Z]øƒQ‡í‹ßğTß€?	~ ünøqÃ¿ÚOâÖ±û3EácöŠÕ>	|Ö>\"xsàß|cğßÃ<;ñÄÚ½•ı¢ë>Ôü#â;›»]+Àø¿Ç×Â^3¹·ğTÚV†u+Ÿ„ş#~Ê·ßjïø&„¾	iš¯ÀŸÚcãçÆMoÂ¶-ßÅŸŞø?á§ìİûTüYÖ¾3|iğ×~xŸ_ÿ\0…¯«|SğuÏ>$xÁúƒü3wğóÄÖÚÏ…<Iwâß}{LšWÄÚ¯Â¿¶ßüGá§ìÏû)h¿¿á+ñwìÍ¤øsÄ²|Røwğ³Ãÿ\0\r<u­şÂ¿ô\r6_‹Kâ›»ê?\na°†ÇT·o…šgüY¦Ë¥ø‹Oµğdrk–Zƒ\0~—xÏş\nAû%ü;“ÆøÛâøsBğ×ìÓ ~×\Z‹¯ôÉ„ş+|ñ\rÄz}§ˆşkï:xïX‡[Ô<1áÙ¼c>,¹×<sàk-+GÔá)Òå›ìø‘|eáx¹t/xa<SáíÄIá¿i2h.ĞZÓ­õÑ¼S Ë$Óh!ÓVäYë:Dò=Æ™¨Ãqe9ó p?œ_‰ğIÚ{Çşı>hzßƒ|áø$ŸìßğCXı’ş)k¶~\rñqı¨m_h~\Z•¯Äêú¾­àïÙ{K±øwiá=_@º“Ã-Ö|Mãˆ<caı¡ÂÏ	ŞİDüAã?ü9ğG‰>#xçágõ¿èÚ>]ëúŠåğWŠ.la}wÃ‰â_jZˆmtÍHÜÛXëZm×“©Ù%½ãÛXÏ4¶6àİQ@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@?ğFŸù\'ßğP/ûL7ü÷ÿ\0ZwÅUû\r_?ğFŸù\'ßğP/ûL7ü÷ÿ\0ZwÅUúá¦kúµ.¡­i:´úMÇØõHtÍFÎş]6ì†\"×PÖi^Îà„sä\\¬rá[åùN\05«ä¿Œ?²o„ş\"Ş]ø‹ÃwŸğˆx¦ët·F8>Ñ¡j×,ûšâöÉJMiu(ÊIwa\"ÆÇ÷ÓXÜN^GúÒŠ\0üzñì‹ñ¯B–Ag¡Xx–Ù„»Ğµ{&¿½!¾É©É¦ß	,¢ÙÆéc\ZG,ñÿ\0ö®ÿ\0‚i?´‡Äí\'öŠğG‡>6~ËµW‡5moDı¡?gûÇğ‹\'×¬84ícÄ–víZ®©al¯o¿¤^xkÅÓÀ`²¾ñ-Ş™gm§Çı‚×Í7¶?ì»i<ö·_<mum4–÷6×\Z£C=¼ğ»G4Ã$+$SE\"´rG\"«ÆêÊÊ]Ø,³2ÌHåÙv;0tT]U‚Âb1N’›j¢¡N£‚›Œ”\\­ÌâÒ½™ÏˆÆa0œŸZÅağŞÓ›“ëéQçå·7\'´”y¹y£ÍkÛ™^×GãÀ‚ÿ\0ğp×Ãkj?¶ïìûñ¿Ã–¥¥¯Š¿iØîÎ×ÆVö¶Ú¦£?ÂÚ\'á¡â+ë{Gv§ª5æ£yql‡R»’â{‰[õ;áŸÁ_ÛsPkkÿ\0Ú+öÎğüòÅ)yü-û0~Ï¾øSá½Fvµ©­üdÖ?h›9áfG\rjşÖ­¦qe®ÀÁQ>¡Ò~.|5×<^¾\0Òüc£\\xÕôO\'…Úi-µ©<?}ie}kªGcwIo%®¡i1Ø­$jò	cF·¸X‰~.ü4ƒÅ^)ğ4¾2ÑG‹üáÉ¼_â¯	İõmÃ6öÚuäÚÍå¢FÒ$¶Õ´ÙwÄ$f°*©vÚË3(ÍÓy~5Tbå„®¦°’œiÇâéó,<ªJ4Õf½›œ£.fş³†åsúÅUVT\\½­>UZ7æ¤ß5•XÙóSo™Yİ+3¿¶·ÒÚŞÖ&â¶‚+xÚæææöå£†5\ZâòòYï.çePe¹ºk™ä-,òÉ+»·åOüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÑıâŸÃ¿xïâW‡|c¡k^°³Ö5ßi×‰s¥ZZx}nYšâhÁx†¬ò\\# ‘bA FG›òçş\n‰ãO\nüBø	ûøÃÁ:öâojÿ\0ğUßø&öf·¥Mö‹ßìÿ\0Û‡á~—{äM…ßömBÊîÒ^Ù­ä_áÍE\\6„kJ¾F8|BÂW•\\=Zq¡‹qœÖ³œ\"©âiÔ’£7\Zœ´ç.[BMT+Q©ìù+RŸ¶¦ëRä©	{Z+’õiÙ¾zkÚS½HŞ+ÚC_z7ı„¢Š+”Ô(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0*)à†æ­®aŠâŞâ) 	ãIaPÇ,3E d’)™$Õ‘ÑŠ° ‘RÑ@\rğ§öWı˜~ëÚçŠ¾~Î?¾øŸÄÖm§x“Ä\n~|>øw¯xƒOkÄÔZÇ\\Ö<!áíPÕ¬ÛP;ö¶¿¸¸„ŞF—E<õWóE\0QE\0QE\0QE\0W+¢øÀşñŒ¼aáßøSAñoÄ[ÍQøƒâÃÚF—â?êÑ-<3áëïkv6pj~\'¼Ğ|7acáıç[º¾ŸKÑ,­4›‚ÂÚtê¨ Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( äÄ^ı¡ü[ÿ\0ˆÿ\0‚Ìè_³$?¯¾!ŞÁ]à \râ\ràì—Q|[ñ\'Á¨?n«yş<øká³X·Mâ½wàä~6Óìì4â5MZÚ{Í#Ij—öqIÍø+Jÿ\0‚yx¿öÑÿ\0‚h\\ÿ\0Á~\\ü>ø™à?‹’Mûfx£áÂoŠ¿¼á¿Øˆ|9ñ0ø•àÚãYñ^á­3Ä¿<CâßøBíşÚ|@¸ñ/ÄH<k¤ßŞÇ}e¨:òëôàgÅßˆŸğL‹¶OÁ/_²Wíñcá‡Æÿ\0Ûãïíğö€ı“ÿ\0gŸˆ´ÿ\0„5ÿ\0\r~Òş*_ˆ\'øqñAøE¤ø‹Æş |6ñ¶£¯è0§ˆ¼;o¡ø«Ã‘iš¾“©ÈUåÔ>¢ÿ\0‡»|(ÿ\0£6ÿ\0‚®ÿ\0â­ÿ\0m/şt´ø‹ÿ\0İøKÿ\0pøÇğömı¯<GûOxËÂÿ\0ìÇÆß|bÕ~!şÙ¿´Å¯|cø{¡Œ~\r¶ğü2ïŒ>è~øŠ=nÃÃ7ºO´/Ú¶±áûoC¯iÖ·š–³%†›á_ğOoÚŸÇş0ı“<SâŸ¶¯ÆûŸÚàşÃ´Š¾x&ËöÏı¯> ø»Ä¿tÙßâ¾¿ªMñ3àß¾|:øağËÄ¾Ò´Öñ§„´ÿ\0|JøŒºŒ´Itıví;K‡Zş‹ÿ\0áîß\n?èÍ¿à«¿ø«ÛKÿ\0-ğ÷o…ôfßğUßüU¿í¥ÿ\0Î–€?¿e¿Ú\'ş\nqà_ğF_Ù{ö°Õ¾$xóÁ_5{âî“û_øW\\×tÏşĞ¼Iû|Kñ¥¿ÁÚ¯Ãÿ\0Úo=ÆßƒßõO‡ÖöÚ–°nt/Š\Zu¾ƒ®ÚoÅâKm[õcöcø§á¯‡¾±±ñÆ¿\rYÚ|@ñö©ğí?g¿x…î4»ÿ\0ë·Ğ	|y§xwU{Ïí»9¢Ômïì¥ÿ\0B²»µ±\"m’õ¿ğ÷o…ôfßğUßüU¿í¥ÿ\0Î–ø{·Âú3oø*ïş*ßöÒÿ\0çK^Æ[šG‡Æa§Bua‹«„ªåN­\ns„°ŸYåVÄ`ñ´§ıa¹\'II8FÒ³’?›ä1ÍqÙf6UáìØc\"¨T£Z¥*òÅTÀÕ‹ªèc0’p¥,\næ£\':UÕNZÑtã(Tåş\'è\Zî¯ûDüdı¢~}ºÿ\0Åß¼\rğâ7‚ôËYoô»?ˆ?î<=­]|GğcÙ{‘ªxJm\"émÆ]VÒği60[CwªÃ´|ğ§ÆŸ~>|V×|z./üuñöø›ãÿ\0‰ºÕ²^Éáøüâ‰sI\'‡t‰¦mgmáß\réš…g¦Ã1Qm¤È”Å9sÛÃİ¾Ñ›ÁWñVÿ\0¶—ÿ\0:Z?áîß\n?èÍ¿à«¿ø«ÛKÿ\0-}xß»û68*ÎYu<²uå;âe…£G\n4}ª¤š¡E*ØÙQÖ3Äâ9äíJš^81–\"o3›úÆk‹Íİá)Ç	G‹©_¥\n*³©\Z«\rV5RxŠ–¡IÆœiªÕc/.ø?ğ?âsx7áÀ­î ıŸhøâ¿ÅRÿ\0QÕãÔ|/„,ôX>1ü>ÒfôKk—>óšÚæÈ]x–Ä[½¥…×Ø¾}øÑ§ßéğN_ø&Å§ew§_Aÿ\0]ÿ\0‚{ùöwÖÓZ]Cæÿ\0ÁHü74~m½ÂG4~d2G*oA¾7G\\«)?iÿ\0Ãİ¾Ñ›ÁWñVÿ\0¶—ÿ\0:Zù‡ã¯Ç_‰¿ğRÏ‰¿²/À_€¿²/í‡ğ»á÷ÂïÛöoı¬¾?üı¬¿fÿ\0ˆß³€¼+à/Ùâ6›ñ{Lğ/tÏ‹Úo…¼_ñ\'â?ÄŸø[Ãş´´ğ·‡ï´Ÿé7×úö½´J©{ÆX¬÷ˆÀVÂQ¡FiC6©RœïR¦.<ÏêÕ~ÎÖµl&7‡«ZVr[FJ)Ô’vCÂqÈ«àë,Â®1`òün]N5¨F,el³áNp¨Õ<=FV	¸}~¥8ÕT¨Sƒıû¢Š+ãO¯\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€?ÿÙ',5,0,'image/jpeg','logo.jpg',0),(5,2,1,'**********************************************************************\r\n[agnDYN name=\"0.1.1 Header-Text\"/]\r\n**********************************************************************\r\n[agnDYN name=\"0.2 date\"/]\r\n\r\n[agnTITLE type=1],\r\n\r\n[agnDYN name=\"0.3 Intro-text\"/]\r\n[agnDYN name=\"0.4 Greeting\"/]\r\n\r\n----------------------------------------------------------------------[agnDYN name=\"1.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"1.0 Headline ****\"]\r\n\r\n[agnDYN name=\"1.1 Sub-headline\"][agnDVALUE name=\"1.1 Sub-headline\"/]\r\n[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/][agnDYN name=\"1.3 Link-URL\"]\r\n\r\n[agnDYN name=\"1.4 Link-Text\"/]\r\n[agnDVALUE name=\"1.3 Link-URL\"][/agnDYN name=\"1.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"2.0 Headline ****\"]\r\n\r\n[agnDYN name=\"2.1 Sub-headline\"][agnDVALUE name=\"2.1 Sub-headline\"/]\r\n[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/][agnDYN name=\"2.3 Link-URL\"]\r\n\r\n[agnDYN name=\"2.4 Link-Text\"/]\r\n[agnDVALUE name=\"2.3 Link-URL\"][/agnDYN name=\"2.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n\r\n[agnDVALUE name=\"3.0 Headline ****\"]\r\n\r\n[agnDYN name=\"3.1 Sub-headline\"][agnDVALUE name=\"3.1 Sub-headline\"/]\r\n[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/][agnDYN name=\"3.3 Link-URL\"]\r\n\r\n[agnDYN name=\"3.4 Link-Text\"/]\r\n[agnDVALUE name=\"3.3 Link-URL\"][/agnDYN name=\"3.3 Link-URL\"]\r\n\r\n----------------------------------------------------------------------[/agnDYN name=\"3.0 Headline ****\"]\r\n\r\nImprint\r\n\r\nYou want to change your profile-data?\r\n[agnDYN name=\"9.0 change-profil-URL\"/]\r\n\r\nPlease click her to unsubscribe:\r\n[agnDYN name=\"9.1 unsubscribe-URL\"/]\r\n\r\n[agnDYN name=\"9.2 imprint\"/]',NULL,0,0,'text/plain','agnText',0),(6,2,1,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table bgcolor=\"#808080\" width=\"684\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr>\r\n    <td>[agnDYN name=\"0.1 Header-image\"]\r\n    	<table width=\"680\" border=\"0\"  bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td><img src=\"[agnDVALUE name=\"0.1 Header-image\"]\" width=\"680\" height=\"80\" alt=\"[agnDYN name=\"0.1.1 Header-Text\"/]\" border=\"0\"></td>\r\n            </tr>\r\n        </table>[/agnDYN name=\"0.1 Header-image\"]\r\n        <table width=\"680\" border=\"0\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td><td align=\"right\"><div style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 10px;\">[agnDYN name=\"0.2 date\"/]</div></td><td width=\"10\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td>\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n                 <tr><td><p><b>[agnTITLE type=1],</b></p><p>[agnDYN name=\"0.3 Intro-text\"/]</p><p>[agnDYN name=\"0.4 Greeting\"/]</p></td></tr>\r\n                 <tr><td><hr noshade></td></tr>\r\n              </table>[agnDYN name=\"1.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"1.5 Image-URL\"]<td>[agnDYN name=\"1.3 Link-URL\"]<a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[/agnDYN name=\"1.3 Link-URL\"]<img src=\"[agnDVALUE name=\"1.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"1.3 Link-URL\"]</a><!-- [agnDVALUE name=\"1.3 Link-URL\"] -->[/agnDYN name=\"1.3 Link-URL\"]</td>[/agnDYN name=\"1.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"1.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"1.1 Sub-headline\"]<b>[agnDVALUE name=\"1.1 Sub-headline\"/]</b><br>[/agnDYN name=\"1.1 Sub-headline\"][agnDYN name=\"1.2 Content\"/]</p>[agnDYN name=\"1.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"1.3 Link-URL\"]\">[agnDYN name=\"1.4 Link-Text\"/]</a></p>[/agnDYN name=\"1.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"1.7 Image-URL-1\"]<td>[agnDYN name=\"1.6 Link-URL\"]<a href=\"[agnDVALUE name=\"1.6 Link-URL\"]\">[/agnDYN name=\"1.6 Link-URL\"]<img src=\"[agnDVALUE name=\"1.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"1.6 Link-URL\"]</a><!-- [agnDVALUE name=\"1.6 Link-URL\"] -->[/agnDYN name=\"1.6 Link-URL\"]</td>[/agnDYN name=\"1.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"1.0 Headline ****\"][agnDYN name=\"2.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"2.5 Image-URL\"]<td>[agnDYN name=\"2.3 Link-URL\"]<a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[/agnDYN name=\"2.3 Link-URL\"]<img src=\"[agnDVALUE name=\"2.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"2.3 Link-URL\"]</a><!-- [agnDVALUE name=\"2.3 Link-URL\"] -->[/agnDYN name=\"2.3 Link-URL\"]</td>[/agnDYN name=\"2.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"2.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"2.1 Sub-headline\"]<b>[agnDVALUE name=\"2.1 Sub-headline\"/]</b><br>[/agnDYN name=\"2.1 Sub-headline\"][agnDYN name=\"2.2 Content\"/]</p>[agnDYN name=\"2.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"2.3 Link-URL\"]\">[agnDYN name=\"2.4 Link-Text\"/]</a></p>[/agnDYN name=\"2.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"2.7 Image-URL-1\"]<td>[agnDYN name=\"2.6 Link-URL\"]<a href=\"[agnDVALUE name=\"2.6 Link-URL\"]\">[/agnDYN name=\"2.6 Link-URL\"]<img src=\"[agnDVALUE name=\"2.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"2.6 Link-URL\"]</a><!-- [agnDVALUE name=\"2.6 Link-URL\"] -->[/agnDYN name=\"2.6 Link-URL\"]</td>[/agnDYN name=\"2.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"2.0 Headline ****\"][agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr>[agnDYN name=\"3.5 Image-URL\"]<td>[agnDYN name=\"3.3 Link-URL\"]<a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[/agnDYN name=\"3.3 Link-URL\"]<img src=\"[agnDVALUE name=\"3.5 Image-URL\"]\" alt=\"Picture_1\">[agnDYN name=\"3.3 Link-URL\"]</a><!-- [agnDVALUE name=\"3.3 Link-URL\"] -->[/agnDYN name=\"3.3 Link-URL\"]</td>[/agnDYN name=\"3.5 Image-URL\"]\r\n                     <td valign=\"top\" align=\"left\"><h1>[agnDVALUE name=\"3.0 Headline ****\"]</h1>\r\n                     <p>[agnDYN name=\"3.1 Sub-headline\"]<b>[agnDVALUE name=\"3.1 Sub-headline\"/]</b><br>[/agnDYN name=\"3.1 Sub-headline\"][agnDYN name=\"3.2 Content\"/]</p>[agnDYN name=\"3.3 Link-URL\"]\r\n                     <p><a href=\"[agnDVALUE name=\"3.3 Link-URL\"]\">[agnDYN name=\"3.4 Link-Text\"/]</a></p>[/agnDYN name=\"3.3 Link-URL\"]</td>\r\n                     [agnDYN name=\"3.7 Image-URL-1\"]<td>[agnDYN name=\"3.6 Link-URL\"]<a href=\"[agnDVALUE name=\"3.6 Link-URL\"]\">[/agnDYN name=\"3.6 Link-URL\"]<img src=\"[agnDVALUE name=\"3.7 Image-URL-1\"]\" alt=\"Picture_2\">[agnDYN name=\"3.6 Link-URL\"]</a><!-- [agnDVALUE name=\"3.6 Link-URL\"] -->[/agnDYN name=\"3.6 Link-URL\"]</td>[/agnDYN name=\"3.7 Image-URL-1\"]\r\n                 <tr><td colspan=\"3\"><hr noshade></td></tr>\r\n              </table>[/agnDYN name=\"3.0 Headline ****\"]\r\n              <table width=\"660\" bgcolor=\"#D3D3D3\" border=\"0\" cellspacing=\"0\" cellpadding=\"5\" style=\"font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;\">\r\n                 <tr><td><h1>Imprint</h1>\r\n                 	 <p>You want do change your profile-data?<br><a href=\"[agnDYN name=\"9.0 change-profil-URL\"/]\">change profile</a></p>\r\n                 	 <p>Please click her to unsubscribe:<br><a href=\"[agnDYN name=\"9.1 unsubscribe-URL\"/]\">unsubscribe newsletter</a></p>\r\n                         <p>[agnDYN name=\"9.2 imprint\"/]</p></td></tr>\r\n              </table>              \r\n              </td>\r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\"><img src=\"[agnIMAGE name=\"clear.gif\"]\" width=\"8\" height=\"8\"></td>\r\n            </tr>            \r\n        </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n',NULL,0,0,'text/html','agnHtml',0),(7,2,1,'R0lGODdhAQABAIgAAP///wAAACwAAAAAAQABAAACAkQBADs=','GIF87a\0\0ˆ\0\0ÿÿÿ\0\0\0,\0\0\0\0\0\0\0D\0;',5,0,'image/gif','clear.gif',0),(8,2,1,'/9j/4AAQSkZJRgABAQEAYABgAAD/4QBmRXhpZgAASUkqAAgAAAAEABoBBQABAAAAPgAAABsBBQAB\r\nAAAARgAAACgBAwABAAAAAgAAADEBAgAQAAAATgAAAAAAAABgAAAAAQAAAGAAAAABAAAAUGFpbnQu\r\nTkVUIHYzLjIyAP/bAEMAAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/bAEMBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\r\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAf/AABEIAFACqAMBIgACEQEDEQH/xAAf\r\nAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEF\r\nEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJ\r\nSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\r\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEB\r\nAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIy\r\ngQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNk\r\nZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfI\r\nycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/AP3v+D/wf+Kf/BU74p/t\r\njfF/4v8A7Y37YfwK+E3wK/bD+PP7HnwF+Av7Hnx58Vfs0aJpWifs0eKv+FeeLvif8T/F3w7+weNP\r\niT45+JXjSw13VrCw1bXf+EY8HeGP7MsLDTLi/uHfSfoP/hzT8Pv+kgX/AAWG/wDFnn7Tv/zVUf8A\r\nBGn/AJJ9/wAFAv8AtMN/wU9/9ad8VV+wjsqKzuyoiKWd3IVVVQSzMxICqoBJJIAAJJxQB+Pf/Dmn\r\n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\r\n+FPhvUYSdrWeqa38ZNY/aI8ZmznhZkd/DWr+EdatpgtxZa7AwVE+odJ+Lnw11zxevgDS/GOjXHjV\r\n9AtPFCeF2mkttak8P31pZX1rqkdjdxQTSW8lrqFpMditJGryCWNGt7hYiX4u/DSDxV4p8DS+MtFH\r\ni/wT4cm8X+KvDgnd9W0Twzb22nXk2s3lokbSCyS21bTZd8QkZhewKql22hPLMyjN03l+NVSOHWLl\r\nB4SuprCSnGnHFOLp8yw8qko01Wa9m5yjFS5mkP6zhuVz+sUOVVZUXL2tPlVaN+ak3zWVWNnzU2+Z\r\nWd0rM7+2t47S2t7WJp3itoIreNrm5ub25aOGNY0a4vLyWe8u52VQZbm6nmuZ5C0s8skru7flT/wV\r\n2/5JR+xt/wBpXf8Aglv/AOtpfCWv0f0H4p/DvxR4Hu/iV4d8Y6FrXgOws9Y1C98UadeJc6VaWnh9\r\nbh9ZmuJowXiGnR2s8lwjoJFiQSBGR42b8uf+ConjTwr8QvgJ+xL4w8E69p3ibwxq/wDwVd/4Jh/2\r\nZrelTfaLC9/s/wDbh+F+l3vkTYXf9m1Cyu7SXgbZreRf4c1FXBY2hGtKvhMVRjh8QsJXlVw9WnGh\r\ni3Gc1haznCKp4hxp1JKjNxqctOcuW0JNVCtRqez5K1KftqbrUuSpCXtaK5L1adm+emvaU71I3iva\r\nQ196N/2EooorlNQooooAKKKKACiiigAooooAKKKKACiiigAooooAKinghuYZra5hiuLe4ikgngnj\r\nSWGeGVDHLDNFIGSSKRGZJI3VkdGKsCCRUtFAHg3wp/ZX/Zh+A+va54q+B37OPwG+DPifxNZtp3iT\r\nxH8KfhB8Pvh3r3iDT2vE1FrHXNY8IeHtH1DVrNtQjjv2tr+4uITeRpdFPPVXHvNFFABRRRQAUUUU\r\nAFFFFABXK6L4E8D+G/EfjLxh4d8G+FNB8W/EW80XUfiD4p0Xw9pGl+I/HeoeHNEtPDPh6+8Za3Y2\r\ncGp+J7zQfDdhY+H9Fudbur6fS9EsrTSbF4LC2ht06qigAooooAKKKKACiiigAooooAKKKKACiiig\r\nAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKAC\r\niiigAooooAKKKKACiiigD+R/xF4b/aH8W/8ABIj/AILM6F+zJD8Rr74h3n/BXX/goA3iDRfg7JdR\r\nfFvxJ8GoP26ref48+Gvhs1iPt03ivXfg5H420+zsNOI1TVrae80jSRJql/ZxSc34K0r/AIJ5eL/2\r\n0f8Agmhc/wDBD34VXPw++JngP4uSTftmeKPhF8Jvir8IvBXhv9iIfDnxMPiV4I/a41nxXoHhrTPE\r\nvxI8Q+Lf+ELt/h7afEC48S/ESDxrpN/ex31lqJ068uv0F+Bnxd+In/BMH4t/tk/BL41fslftf/Fj\r\n4YfG/wDbD+Pv7Y/wG/aA/ZP/AGefiH+0/wCENf8ADX7S/ipfiJ4n+HHxD0H4RaT4i8Z/DP4gfDbx\r\ntqOv6DCniLw7b6H4q8ORaZq+k6nIVeXUPqL/AIe7fCj/AKM2/wCCrv8A4q3/AG0v/nS0AfiL/wAE\r\n3fhL/wAFcPjH8Bv2bf2vPEf7T3jLwv8ADuzHxt8YfGLVfiH+2b+0H8WvHXxj+Huhj4x+DbbwF/wy\r\n74w+Beh/Dn4S+Io9bsPDN7pPj7Qvjtq2seH7bw9Dr2nWt5qWsyWGm+Ff8E9v2p/H/jD9kzxT4p+N\r\nf7avxvuf2uD+w3+0F4q+HHgmy/bP/a8+IPi7xL8ZdH/Z3+K+v6pN8TPg346+Bnw6+GHwy8S+ENK0\r\n1vGnhLT/AAZ8SviMun+MtBtJdP12C+07S4da/ov/AOHu3wo/6M2/4Ku/+Kt/20v/AJ0tH/D3b4Uf\r\n9Gbf8FXf/FW/7aX/AM6WgD8ev2W/2if+CnHgX49/8EZf2Xv2sNW+JHjzwV8UNZ174u6T+1/4V1zX\r\ndM8O/tB/BrxJ+xd8S/Glv8Ef2q/D/wDabz2Pxt+D3xf1T4fW9tqWsG50L4oadb6DrtoLbxHF4ktt\r\nW/Vj9mP4p+Gvh74XsbHxj8Ybvw1Z2nxA8fapf/DtP2e/EniF7jS7/wAc67fQCXx5p3h3VXvP7bs5\r\notRt7+yl/wBCsru1sY4ibQyS9b/w92+FH/Rm3/BV3/xVv+2l/wDOlo/4e7fCj/ozb/gq7/4q3/bS\r\n/wDnS17GW5pHA4fGYadCdWGLq4Sq5U6tCnOEsJ9Z5VbEYPG0pxn9YbknSUk4RtKzkn8/m+QxzXHZ\r\nZjZV4Q/s2GMiqFSjWqUq8sVUwNWLquhjMJJwpSwK5qMnOlXVTlrRdOMoVOX+J+ga7q/7RPxk/aJ+\r\nEH26/wDF3we8DfAX4jeC9MtZb/S7P4g/D+48Pa1dfEfwY9kYEnuRqnhKbSLpbRLGXVbS8Gk2MFtD\r\nd6rDHLR8FfCnxp8Pfj58Vtd8ei4v/HXxG/YR+Jvj/wCJutWyXsnh+Px/4o+Jc0knh3SJpg9tZ23h\r\n3w3pmh6FZ6bDMVFtpBvIlMU5c9t/w92+FH/Rm3/BV3/xVv8Atpf/ADpaP+Hu3wo/6M2/4Ku/+Kt/\r\n20v/AJ0tfQR43xccu/s2OCoezll1PLJ15TviZYWjRw8KNH2qpJqhDEUq2NlR1jPE4jnk7UqaXhQ4\r\nHjGWIm8zm/rGa4vN3RjhKccJRxeLqV+epQoqs6kaqw1WGDVSeIqWoUnGnGmq1WMvLvg/8D/ic3g3\r\n4X/ArQXuIP2ff2gPAfgX4r/FjVL/AFHV49R8HS+ELPRYPjH8PtJmEhj0S48fa5c+GfMVmtrmyF14\r\nlsRbvaWF19i+ffjRp9/pf/BOX/gmxY6nZXenX0H/AAVd/wCCe/n2d9bTWl1D5v8AwUj8NzR+bb3C\r\nRzR+ZDJHKm9BvjdHXKspP2n/AMPdvhR/0Zt/wVd/8Vb/ALaX/wA6WvmH46/HX4m/8FLPib+yL8Bf\r\ngL+yL+2H8Lvh98Lv2w/2b/2svj/8f/2sv2b/AIjfsx+AvCvgL9mP4jab8XtM8C+BdM+L2m+FvF/x\r\nJ+I/xJ8X+FvD/ha0tPC3h++0nwvpN9f69r1/FbRKqY57xlis9wOIwFbCUaEMRmlDNqlSnO9Spi6N\r\nPM8O6tV+zh7WtWwmNwuHq1pWco5bRkop1JKPdkPCccir4Osswq4xYPL8bl1ONahGEo0sZWyzEuFO\r\ncKjVPD0cRgMTVo0HCbh9fqU41VSoU4P9+6KKK+NPrwooooAKKKKACiiigAooooAKKKKACiiigAoo\r\nooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiii\r\ngAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooAKKKKA\r\nCiiigAooooA//9k=','ÿØÿà\0JFIF\0\0`\0`\0\0ÿá\0fExif\0\0II*\0\0\0\0\0\Z\0\0\0\0>\0\0\0\0\0\0\0F\0\0\0(\0\0\0\0\0\0\01\0\0\0\0N\0\0\0\0\0\0\0`\0\0\0\0\0\0`\0\0\0\0\0\0Paint.NET v3.22\0ÿÛ\0C\0ÿÛ\0CÿÀ\0\0P¨\"\0ÿÄ\0\0\0\0\0\0\0\0\0\0\0	\nÿÄ\0µ\0\0\0}\0!1AQa\"q2‘¡#B±ÁRÑğ$3br‚	\n\Z%&\'()*456789:CDEFGHIJSTUVWXYZcdefghijstuvwxyzƒ„…†‡ˆ‰Š’“”•–—˜™š¢£¤¥¦§¨©ª²³´µ¶·¸¹ºÂÃÄÅÆÇÈÉÊÒÓÔÕÖ×ØÙÚáâãäåæçèéêñòóôõö÷øùúÿÄ\0\0\0\0\0\0\0\0	\nÿÄ\0µ\0\0w\0!1AQaq\"2B‘¡±Á	#3RğbrÑ\n$4á%ñ\Z&\'()*56789:CDEFGHIJSTUVWXYZcdefghijstuvwxyz‚ƒ„…†‡ˆ‰Š’“”•–—˜™š¢£¤¥¦§¨©ª²³´µ¶·¸¹ºÂÃÄÅÆÇÈÉÊÒÓÔÕÖ×ØÙÚâãäåæçèéêòóôõö÷øùúÿÚ\0\0\0?\0ıïø?ğâŸü;âŸíñâÿ\0íûaü\nøMğ+öÃøóû|øû|yñWìÑ¢iZ\'ìÑâ¯øW.øŸñ?ÅßşÁãO‰>9ø•ãK\rwV°°ÕµßøF<áìË\r2âşáßIúşÓğûş’ÿ\0†ÿ\0Å~Ó¿üÕQÿ\0iÿ\0’}ÿ\0ÿ\0´ÃÁOõ§|U_°ÊŠÎì¨ˆ¥Ü…UU³3ª€I$€\0$œPãßü9§á÷ı$ş\rÿ\0‹<ı§ùª£şÓğûş’ÿ\0†ÿ\0Å~Ó¿üÕWÓÿ\0ÿ\0à¢Ÿ³GÃ©¯4íÿ\0âÇïX´ğİx{öSøñ‡ö¡¾²¾‡\nÚv«wğ/Á>;Ò4[ô¢·¸³Õµ+;»)\'‰¯ ‚7_˜àáŸ„_Zkß‹?²Ïí£ğÁğÊ‘·Ä?Ÿ±÷í3àO	ª1fË5ÿ\0Âû	’)£(bTógF	!$l\0Nÿ\0Ãš~Ò@¿à°ßø³ÏÚwÿ\0šª?áÍ?¿é _ğXoüYçí;ÿ\0ÍUp_³ïüCá×íOáÙ|Yû?xÏà¯ÅmÉ­ÿ\0µâğÎ¡®.½¡}¤Éöx|IáGT´ñ7…®nÖZÒèš|×40Í$ıqáÏÛº•cñw€¥ŠÑ»ğæ¨—¢ô™—NÔã¶I?<JuH‡ü³wãÌ 	ÿ\0iø}ÿ\0Iÿ\0‚ÃâÏ?ißşj¨ÿ\0‡4ü>ÿ\0¤Áa¿ñgŸ´ïÿ\05UúqğóãÃßŠ³xK_‚æö$2\\è·Šlu«XÃ”2I§Ï‰%€¤ÜÚ›eß\Z¼Ë#l¯N ÇŸøsOÃïúHüÿ\0yûNÿ\0óU^\rñwàgÅ¿ø&ÄOÙ+ãWÁ?Û\'öÃøßğÃâÇíû<şÉÿ\0´ÀoÛãïŠ¿i\rkşı§ş!é?´ˆ<Oñu|6øğÏÆ^\"ğïˆ’_‹Ã*Ğíõ=\'WÓÉ+ê¿õùEÿ\0vÿ\0’QûÚWà–ÿ\0úÚ_	hõvŠ( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( ÇŸø#Oü“ïø(ı¦ş\n{ÿ\0­;âªı. šÚæ®-®\"’‹yãI 	Ç,3E håŠXÙ’HİY«¤Šü~ÿ\0‚4ÿ\0É>ÿ\0‚Úa¿à§¿úÓ¾*¯Øj\0jª¢ª\"ª\"(TEUU@\nª \0ª \0\0\0\0\0OTL›MÔbÖ’Æ][¸õhõE·}6M1íä[ôÔRìW±{S*İ­È6índj½_ÿ\0ğV?úß…¼9àÏşÔn4ÖñÍµ÷‰¼q=¥ÃÛİ]x^ÎvÒô}š)w>—®jcUºÕ£xâó¿°lmVY­.u+f÷øc ¯ÄÙæ%¡QQxº’öµÚRöz0•lEe(sÊ¡\'\n|ÑsŸ,.¯sÅâïÃ¹>77ÄÓZxHC–6£:Õ«U…\n4”¥¤TªÔ<í\'NšE	òò¿æ«ş\n¿ÿ\0ïøáÚÇáÿ\0í‡ÿ\0cø¡ğ÷ö}ı¡ôÏ]ÃñÃÀ\Z=§ˆôÙ«Åút‘5ÌÚÇ‡“Â^Ö4şÙº°·Ò<qà\ré_¼icª[x–ÆïÃş(ÑµY¼MûğÛÄº§Œ~ø;Å\Zå†¥kºÖƒes¯éZ>£>­¤é!„5—ˆ4ı+S»±Òïµ\r*Ï[µ¿·Òõı\'I½Ôtèíoî4»ºû4›µöoìıâ-ÏÀ·–\Z¯ˆ<7¤^ÛøŸQ‘#Ö¼G¢èÓËaq¦è¦¶§{fòÂ—qêí‰7<²ˆcQúïøW•ä7~Kı§ŒÌpø¬5<G´’¯õŠ5£*u%=\Z1tÜk{:‘präƒœgÏu8~UÁ%æ9×<s,¿	‚Äá«Ë\r\ZT¥J4±4c\Z±Œñ±´\'BÜ¥QÉJ²„)ª|ê+éíYÕ|=ªØkz%ıÎ™«i—1İØ_ÚHcÚxÏÊèÜ†V’Xœ4SDÏÈñ;£~Îüø¶Ÿ¼\r¯t°Câ-*q¥xÖ1D/’–+ëx™œÇk¨BŞlJÕ&K˜¿r@şt|qûX~ÊßµÃáŸˆŸ´ïìëà‹K{öĞ¼UñÇá~…«¥•ÙZİ>Ÿ¨ø¦Şé ¸ò¤0HÑ•P²\\\ZıXÿ\0‚søªÓÇV:¯¼¬YxÓàÿ\0¼:÷şø‡á+û_ü<ñEÿ\0‡<Gs \\Ÿø¿G{ßkSé÷Ë¯iW£KÕ\'6×ºn¡etŸi±•-¿œ\'Js§R§Rpœ\'	Âqv”g%(Ê-4âÒi«5söÊu!V©Jp©Nkš§%8J/gE¸É>é´~£WåüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÕÚü¢ÿ\0‚»É(ı¿í+¿ğKım/„µ%Ÿ«´QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0QE\0~<ÿ\0Á\ZäŸÁ@¿í0ßğSßıißWì5~<ÿ\0Á\ZäŸÁ@¿í0ßğSßıißWì5\0~Iÿ\0ÁY<¥~Ñ¼_û |D[Èşütğ®„<[wáÛ—Ò|c×„~ hŞ1±m]’;Ë+TğÎ‚Ó	´»òöæê\"±yñÈ¿‰²ÿ\0ü[öxı’|	¬ü;ø_¨|K¿ğö¹âÛÏ\Z]Ÿø®Ã\\Ô\"Öoô}Cº×–¾ÒŒvocáí7m´‘Ê#f•|÷ZşŸjÿ\0…|	­¡Y½ç‰ü<×ÖVĞ(kCJ»G¬XÂKK:¬×öñîœ°Ä¯-Â©ü…®ì»3Ì2œTq¹f2¾Î¯‡¨éÔP¨¹g­ã%¼]Ó²vºMrc0,ÆÕñøL>7Ï\Z†*:ô\\ã~YJ•HÊq»qæ‹åvjÍ&¾@ø½ğ—ÂŞ\nğt ÑRmâm/G‘/.–â&·¾Òõû×uEdIt˜@?0+#ôÀÏÌÕú#ñ{G¿×>øÖÏFÑá!ñ\Z\rî©á½\Z9í,îµOhñÿ\0jiZE¡¨M†›q®ÜÚ.‚uÙ­¾©q<ÿ\0»BGÉŸğ@ÚKö>ÿ\0‚‚|Cø© üGÒuÿ\0‡_µOÀ?M5—ìÅñÿ\0KŒêŞÓ-íí.¼n,.tËÿ\0j~ñ”z¦‹â¯	ÄÏc¡Şø×P·ñ5”v_Ğ<â¶_—ğ¶\"\\E˜bñùæ‰ö8iª•q8Ú2)áÜqŸ°§MTœéIÔ¨êSTçQSœybÿ\0âÿ\0\r³Ç‰i¼ƒ/Á`òªøl/¶«KØá0˜:©Õ§U¼<\\eRN4£RQÂÑ¨Ûœ]E>y~‰|\'ÿ\0‚Á;i_†øÁûd~Êø¥ñ³Å~ŠòóÄ\ZÇŒş-xvşÏÂsŞŞŞx;FŸNğ§ü=¤Ç-®‡wmw>4›[¸noç´»3Íjn%ı—ı›ÿ\0fß‚_²/ÁşÏ_³§,şüø|ºêø;Àú~«â\rjËB_x—Xñ†º¶úŠu}s[™uøƒYÕ¥zÀâşd€E\0÷\Z+ğß3¯œæ˜ü×¢«ãñU±U#”`êÍÊ4ãd¯\Zqå§×3QNMÉ¶ÿ\0oÊ°ÊòÌ¿-§?i\rƒUT_«Ñ…\'UÁ9(Ê«‹©%Í+JOW¸WåüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÕÚü¢ÿ\0‚»É(ı¿í+¿ğKım/„µççêíQ@Q@~:ÿ\0Á]ÿ\0k_ÚËöP‡ö$½ı‘|)kñ+Å¿?jm[Ã~>ø<Únu¬üeø[àOÙÛãÆ¿ü4ğ~§ªÛÏ/‡¼sâ[?†BÛÁš®™$âa¥XJ.ì//4û¿Øªüşı±¾|Mø½û@Á6|{àM\ZÓTğÏìçû\\x¯âÇÅ‹ÛcKÓeĞ¼ª~Ê´OÂÛ-JÎÏPº·ºÖî$ñ§Äi­§i^_Çôº„-•Üğ€xN·û{Ş|Qı§ÿ\0àgösñí†«û4~Ü¾ı®¼_ã(DĞ¯5Ù|/ø¡xËÁ:eÕıÍ½ö¯áx/Æ7Z®â½JÔl®íõË\rGÃºúÌÚ|–Ñú‡Ãø*‡ìßñ7ÆŞĞô|{Ñ>üYñìŸ>\nşÓ/øA­øoöløÏñµ-_IÒ¼)ğóâíÉÔoÅš†ƒ«ÛxÄZï†t|BšÒ¼â]~]WEMKóÿ\0Å_ğJ~ÿ\0‚«üø¹ğnïH¶ÿ\0‚}êĞ~ÙŸ¼à}3X´ğçŠgÚöøwğÛâF·ğê/í+\rZçÁ?\ZµÛ\rxátÇw/‚¾%Âo®Âš‘®Ûù%û3Á0~0x@ı’ÿ\0fÿ\0~ÄÚŒ±~Î_¾MãoÚ?Æ¿ğPßÚ/ÇŸ²ÿ\0‰¾şÏŞ\'´ñ‚>#üı™4oÚcKñ‹ñÄğ¹áxËàŞ‹ğ«á—Œæ”ø‡Ãút$öR_ø)—ì­gàŸÙ[Ç:Æ½âíÓöÄı îÿ\0eÿ\0„\Z±àİR×Å‘übÓ<]ã‡ºîã=×±ğïü©x+\\×.}*ÇÄ:†jn^\rfÒå¨|Iÿ\0‚š~Ï¾ñOˆ~xÃŸ>4üSÑş2ø«à>ğ·àŸÃõñ—<uñáçÃ?|Xø¥ƒ£½Ö´-mág…¾!xfëúş½áí7Hñ\rÛøv	¯µTKy,ş5ÿ\0Á+iˆ_\Zà¡w\Zn™¤„pxGâ¿Ç¯ø\'•ì~-ğô\"‹öÌı õÿ\0Ùûã¿dºIæ†÷Áz†?hÙoE½Óõr{=6ãHøÓâ¿±ÌĞG©›nóÄß°Åé?d¿ÙGøÃû$ø/ö£ø˜>(|rı¥¿k-+áÇÆ	~~Ô??hÚb÷Åuï~Êß´ƒñGàÿ\0†ôÄğ/Œ|a¨ü4ñ’^|AÑíüyà}ÂWšd·\r¥28Ö7·Ö¡ñgö€ÿ\0‚léÿ\0.üaá†¿´ÆßÛáÇ¯‡ş\Z·ƒş!é~&ı~üN×n<â-+Å\Zdšß…µ¯|Oğ\\/s{áI´½~ÒkM_[ğıü2Ïèÿ\0?àª³ÄßxCÒ<-ñïDøcñgÇ²|,ø+ûNx¿á·á¿Ù³ã?ÄfÔµ}\'Jğ§ÃÏˆW·\'Q¼j\Z¯mà/k¾Ğ<ñ\nkHbğG‰uùu]5/„¾şÄ_·ˆüKûj´&¥ãkÂ_hoÛßÄ¿ÄºÄÿ\0Ÿ¿fÏß²ï~üğOÄ‹öÖš¯Åß‰V>7ñ.¥vŞ&ğÖ©ã+ÏèÚö‡£ÍâËÛ	Ç©\'Ïÿ\0³7üã€tÙ/öoñ×ìM¨Ëìåñ[ádŞ6ı£ükÿ\0\rı¢üyû/ø›áßìıâ{Ox#â?Áÿ\0Ù“Fı¦4¿h¿üAqáO	ëğ7Œ¾\rè¿\n¾xÉŞiOˆ|?§Ab@=>ø-7Ç¾şÔövñZjÿ\0	ÿ\0à«ß?b?é7Ÿ\nüA¤_|qñáŸÃ}@kp\'…İïÇ\r/IÔ¼P×öqİÚèZoÄü/ÑŸJºÒõY _Ñ?ˆ?ğU_€uïiü/ı¨¼g¥ü\"Ñ¼3¬şÓ4øqğ#Ä5ğ‡ì¤<KàÍ#â$š7Ç{í&ñõ+xOÀšæ—âßx_áî“ñ^ğFƒwÿ\0ˆ¬,ae\'ó›ÆŸ°×í•kğ‡ş\nğ³@ø%eâ‹ÏÁY>ÿ\0ÁGşk¶Ÿ¾iš_ÇOÚ~Ñş3øŸá}´:Æ¹g¨ü8ñ‡…ô_…Z†‘u«øú×LĞ5MSR‰´Iïí-„÷^ãø\'·í¡øÛöÆÖ¢ı‰ş\'~Ğ\ZŸíŸâûÿ\0ÚÀ\Z­—ü«â¿Àß…_¾\"|`øoáøûàwíEğ×á÷í\rğÊx/áŸ‹ôûãOŞø«ñáåİ·†¬ÿ\0°†“¥èà²~*ÿ\0‚|Ñ?h¯~ËşĞ>0üjøã¿|ø‰o7Àß†ÚÄŸxgáÇx“Ã^øÏãOé7iáï|)±Ô¼9¿Ä.¹¼’ÚÓOÖ´OM‡V±[ŸF­ÿ\0(ø÷ñ7örøà_ü\'Öm4/kŸµÇìOğŸQ½¼Ñô½r)¼ñŸö­øIğ·âš¶z½­å¬W\Z·‚üW­é¶ÚŒQ-ş—=Ìz†=µõµ¼ñøßì»ûxçà§ícñc[¾ğŞ‘áZüöı“<¨ü<ñ—ˆôøí<Gû?j?´~Ÿã=Âê¾0Öş0xwKğ×‡ü}àÖğŸŠüGâ[ïKÔ3ÿ\0Â[ªøLÔµám¯Ø+ÇzwìáÙpü}ı¢~\"[~Ô±_Ç#àµŸÄ‰—ú‡û9şÓş,x›Jğo‹iOˆúŞ…à{CÃÖ¼Ølõ-\"×\\¿‡K[õ¼šÊÁb\0ıKøİñ³áìåğ§ÆŸ>2x¢ßÁ¿\r~éqêŞ\'ñöz¤öÑ\\ßZi:m•†“£Új\ZÆµ¬ëZÎ¡§hš‡£Ø_júŞµ¨ØiZeÕõå¼|ğ·ş\nMğ#Ç~+ñÏ€¾!øKã‡ìµã¯ü!ñí	wá?ÚŸá…çÂGÄ¼v–0ø³á[èõ/è\Z¿†üw=Œ^2Ò›Xµñ¯ƒÆ«£Íâ\néj¶Ü|ÃûHé?·OíÕû?|CøIwû\Z^şÊ8ğv¹ğWã÷ÁïüMı¢¾x÷À>(şÏ_>|cğÿ\0ÂÚüÕüUâmÃŸ ğeş‡«øšãKŸOÑ­§7oo}:Ce?‰~Öß³çíû|?¾&şÓ_nÿ\0àŸß~~Á·Âhu¿|NğÅOˆÚÏ?j‡ÑüWñ(>ë:ğşğ‹à÷…~^êm-ş­mñÆÚ¦¥½¿ƒtm>Òv¹\0û—àÿ\0ü7à7Åÿ\0‰¾†µÂİKö–Oê_³ßˆ¾4|ñÃ¯|^ğ‡…>ëß5¯xoÄ:•ÌñiZ%·ƒ´%»şÈñ¤ñÉ}oA™¼ šeìº¬_?à«_³OÄÏxLÒ¼1ñ÷Ãÿ\0>0xú…iÏ|\Zñ…?gŒÿ\0oµmKCĞ¼#àêR¦±5Ç‹õ]S±ğ»â?\nøwÂ=¹¶†ø‹\\ŸSÑãÔ?1ï>-~Ğ´ÏÇÏø$¯Àßÿ\0\n>|#Ñ<G¢~Òwqøßá?íá3üXÑµø\'ÇÆÿ\0‡CãWÁk?	hğ\\xkàSIãMúf½ñô/^kŞ(ğ6‡„–z¥Ûú®û5~Şÿ\0fßØş	áñGösğÏ‚|û.|Pıî¾+~ÖÚWÆOß|7ñïÂïØ{Çñ·‚ïş\rü8´Ô5ÿ\0Œiãÿ\0Œ\rğ¯Â\Zv§¡üIğg„¼;à„ÖüGwÿ\0	¶Ö\Zm­ĞèOü³ö³ñìğÂ~!ğf¿ğçÁ^8øÃñÃáÀ?|JøÁ)áWÂEñİæ¥«xÛâ÷Pêz$WÚ/Ã…şñß-´‹­sF±Öõ½HÑïõ+{;éË|àÛÃöÑe_ÛÓâÏÁÚ3áü’_Ù×áMŸŒ>j–_\nµ„Ÿ¼\'ñ6ãşÑâ|_ı|%£è3øƒáN‹áİNø•ğßÄ¾Óü)â¯ˆÚ6Ÿã/\nh\rãK»{Øıåÿ\0&ı—<iûMü%ø?©|3Ò|7âÏ‰¿²ïíIğGö¼ğÃêVú…ş-kµ]Qõ?…Ú§Š.´½j\n\\xËÂ^\"ñ&áïİi—\Z]Š¿°—_{?O«ßÚşt~Ò¿°ßíkûx·í‹ñ·Yø5áßÙ³Äf_€ß³ÃoÙ»âçÄï\nø²?öÿ\0ÿ\0iÚWÆÚ‡íâz<á	üHÒíî?g¿Øé>\'øƒ«Áàïxß[ñU‘§j6¾»\0·û7ÁF¾ xoÄÿ\0µ©?íiğçş\nqû6üı€uoÛ?Ä¾|<ğgÃgøsñ\'ÃÒkz­—ìÿ\0}«ü6ºÖ|	tÿ\0<¡ø‹ÅŞĞõ»sñ+áïü!ZÅ§ŒÛZMVÅí½“á—Æ¿ÛÏà—¿a/~Ô?üñ‹áÇíë?€<kğ«Ãß4„Íû1ülñGÀŸü|øoáÿ\0…¾+ŸÅRkş3ğT’|?ñoÂ]jÏâµæ³ã-WY¼ğ¯Šl/ôX—]ĞÓÖÿ\0c¿¶oÆßxËÅß³.›ÿ\0îøk?üßöœıŠuèâñ·ÂO|EøÉª~Ó^Ó´o;ÃßüCâ?‡ğü\"ıŸ×Ã:çˆü\'uâ}wGñÖ¯â¯Åk§øoAĞ$×½—ƒ~~Ş|_ûøwö‚ı™mş\Zøö¸Ö~/üKñt_¾üJ›ö©øëàO€¿>ü\'ÓşØÇ¯\\xÃş×nş!ø‡â¾­âmğ³[Ñµxjt¾œk\"²\0ûoáOüOögø×®|ğÃsÅ\Z÷¾;ËñQtï‡CÂ×–:øcÁ»İâ¥ßÇ?]ËÏÂKx®ÒMÿ\0	S[O­xÃVÑô_Á­I|²§ã—ìáÿ\00ı§¼qñÇöb²×¿h_†?~4|{ı«>$|ı¡?à–şø]áâ_ìUğÃÁÒüF—_ø“¯xÆ×X¹ø§e{ğkJğ×€µ/x›â¹øñPøÎæÓáµ®•y©øJŞOwı–?b?Û3à/íE«şÛ&ğ÷Ã}Gâü+ş=öëøkà_‡ZEïì¯ofš÷ìÓâOƒ6•¼97Ä?…ºL²x?ö•µƒ]¾Õ~.øë^?|/§ëÚ†ƒ\r•ï’|\'ı‡?jë„Ÿ°/ìoyû\Z|6øEû~ÑŸ>&xûöñğ÷Å/†·şñÖ…ğ/Å«â¿øÿ\0á/†Vÿ\0ÅŸ5OŠ_µ=¬\Z†ƒñGøÅáè:cüAñµÎ©¯kğÛé(«|Sı°?m‹/‚Ÿ´¿üÁ?<¦~Íß²ßÇŞÓÿ\0dËÿ\0‚öoñ›àìÏñ“Vø1ñƒÇú÷Æ{QÓüyá/ˆšÌŞøã?j^³O‡º\r–á/Ä¾\Zñ\Z]øƒQ‡í‹ßğTß€?	~ ünøqÃ¿ÚOâÖ±û3EácöŠÕ>	|Ö>\"xsàß|cğßÃ<;ñÄÚ½•ı¢ë>Ôü#â;›»]+Àø¿Ç×Â^3¹·ğTÚV†u+Ÿ„ş#~Ê·ßjïø&„¾	iš¯ÀŸÚcãçÆMoÂ¶-ßÅŸŞø?á§ìİûTüYÖ¾3|iğ×~xŸ_ÿ\0…¯«|SğuÏ>$xÁúƒü3wğóÄÖÚÏ…<Iwâß}{LšWÄÚ¯Â¿¶ßüGá§ìÏû)h¿¿á+ñwìÍ¤øsÄ²|Røwğ³Ãÿ\0\r<u­şÂ¿ô\r6_‹Kâ›»ê?\na°†ÇT·o…šgüY¦Ë¥ø‹Oµğdrk–Zƒ\0~—xÏş\nAû%ü;“ÆøÛâøsBğ×ìÓ ~×\Z‹¯ôÉ„ş+|ñ\rÄz}§ˆşkï:xïX‡[Ô<1áÙ¼c>,¹×<sàk-+GÔá)Òå›ìø‘|eáx¹t/xa<SáíÄIá¿i2h.ĞZÓ­õÑ¼S Ë$Óh!ÓVäYë:Dò=Æ™¨Ãqe9ó p?œ_‰ğIÚ{Çşı>hzßƒ|áø$ŸìßğCXı’ş)k¶~\rñqı¨m_h~\Z•¯Äêú¾­àïÙ{K±øwiá=_@º“Ã-Ö|Mãˆ<caı¡ÂÏ	ŞİDüAã?ü9ğG‰>#xçágõ¿èÚ>]ëúŠåğWŠ.la}wÃ‰â_jZˆmtÍHÜÛXëZm×“©Ù%½ãÛXÏ4¶6àİQ@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@Q@?ğFŸù\'ßğP/ûL7ü÷ÿ\0ZwÅUû\r_?ğFŸù\'ßğP/ûL7ü÷ÿ\0ZwÅUúá¦kúµ.¡­i:´úMÇØõHtÍFÎş]6ì†\"×PÖi^Îà„sä\\¬rá[åùN\05«ä¿Œ?²o„ş\"Ş]ø‹ÃwŸğˆx¦ët·F8>Ñ¡j×,ûšâöÉJMiu(ÊIwa\"ÆÇ÷ÓXÜN^GúÒŠ\0üzñì‹ñ¯B–Ag¡Xx–Ù„»Ğµ{&¿½!¾É©É¦ß	,¢ÙÆéc\ZG,ñÿ\0ö®ÿ\0‚i?´‡Äí\'öŠğG‡>6~ËµW‡5moDı¡?gûÇğ‹\'×¬84ícÄ–víZ®©al¯o¿¤^xkÅÓÀ`²¾ñ-Ş™gm§Çı‚×Í7¶?ì»i<ö·_<mum4–÷6×\Z£C=¼ğ»G4Ã$+$SE\"´rG\"«ÆêÊÊ]Ø,³2ÌHåÙv;0tT]U‚Âb1N’›j¢¡N£‚›Œ”\\­ÌâÒ½™ÏˆÆa0œŸZÅağŞÓ›“ëéQçå·7\'´”y¹y£ÍkÛ™^×GãÀ‚ÿ\0ğp×Ãkj?¶ïìûñ¿Ã–¥¥¯Š¿iØîÎ×ÆVö¶Ú¦£?ÂÚ\'á¡â+ë{Gv§ª5æ£yql‡R»’â{‰[õ;áŸÁ_ÛsPkkÿ\0Ú+öÎğüòÅ)yü-û0~Ï¾øSá½Fvµ©­üdÖ?h›9áfG\rjşÖ­¦qe®ÀÁQ>¡Ò~.|5×<^¾\0Òüc£\\xÕôO\'…Úi-µ©<?}ie}kªGcwIo%®¡i1Ø­$jò	cF·¸X‰~.ü4ƒÅ^)ğ4¾2ÑG‹üáÉ¼_â¯	İõmÃ6öÚuäÚÍå¢FÒ$¶Õ´ÙwÄ$f°*©vÚË3(ÍÓy~5Tbå„®¦°’œiÇâéó,<ªJ4Õf½›œ£.fş³†åsúÅUVT\\½­>UZ7æ¤ß5•XÙóSo™Yİ+3¿¶·ÒÚŞÖ&â¶‚+xÚæææöå£†5\ZâòòYï.çePe¹ºk™ä-,òÉ+»·åOüÛşIGìmÿ\0i]ÿ\0‚[ÿ\0ëi|%¯ÑıâŸÃ¿xïâW‡|c¡k^°³Ö5ßi×‰s¥ZZx}nYšâhÁx†¬ò\\# ‘bA FG›òçş\n‰ãO\nüBø	ûøÃÁ:öâojÿ\0ğUßø&öf·¥Mö‹ßìÿ\0Û‡á~—{äM…ßömBÊîÒ^Ù­ä_áÍE\\6„kJ¾F8|BÂW•\\=Zq¡‹qœÖ³œ\"©âiÔ’£7\Zœ´ç.[BMT+Q©ìù+RŸ¶¦ëRä©	{Z+’õiÙ¾zkÚS½HŞ+ÚC_z7ı„¢Š+”Ô(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0(¢Š\0*)à†æ­®aŠâŞâ) 	ãIaPÇ,3E d’)™$Õ‘ÑŠ° ‘RÑ@\rğ§öWı˜~ëÚçŠ¾~Î?¾øŸÄÖm§x“Ä\n~|>øw¯xƒOkÄÔZÇ\\Ö<!áíPÕ¬ÛP;ö¶¿¸¸„ŞF—E<õWóE\0QE\0QE\0QE\0W+¢øÀşñŒ¼aáßøSAñoÄ[ÍQøƒâÃÚF—â?êÑ-<3áëïkv6pj~\'¼Ğ|7acáıç[º¾ŸKÑ,­4›‚ÂÚtê¨ Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( Š( äÄ^ı¡ü[ÿ\0ˆÿ\0‚Ìè_³$?¯¾!ŞÁ]à \râ\ràì—Q|[ñ\'Á¨?n«yş<øká³X·Mâ½wàä~6Óìì4â5MZÚ{Í#Ij—öqIÍø+Jÿ\0‚yx¿öÑÿ\0‚h\\ÿ\0Á~\\ü>ø™à?‹’Mûfx£áÂoŠ¿¼á¿Øˆ|9ñ0ø•àÚãYñ^á­3Ä¿<CâßøBíşÚ|@¸ñ/ÄH<k¤ßŞÇ}e¨:òëôàgÅßˆŸğL‹¶OÁ/_²Wíñcá‡Æÿ\0Ûãïíğö€ı“ÿ\0gŸˆ´ÿ\0„5ÿ\0\r~Òş*_ˆ\'øqñAøE¤ø‹Æş |6ñ¶£¯è0§ˆ¼;o¡ø«Ã‘iš¾“©ÈUåÔ>¢ÿ\0‡»|(ÿ\0£6ÿ\0‚®ÿ\0â­ÿ\0m/şt´ø‹ÿ\0İøKÿ\0pøÇğömı¯<GûOxËÂÿ\0ìÇÆß|bÕ~!şÙ¿´Å¯|cø{¡Œ~\r¶ğü2ïŒ>è~øŠ=nÃÃ7ºO´/Ú¶±áûoC¯iÖ·š–³%†›á_ğOoÚŸÇş0ı“<SâŸ¶¯ÆûŸÚàşÃ´Š¾x&ËöÏı¯> ø»Ä¿tÙßâ¾¿ªMñ3àß¾|:øağËÄ¾Ò´Öñ§„´ÿ\0|JøŒºŒ´Itıví;K‡Zş‹ÿ\0áîß\n?èÍ¿à«¿ø«ÛKÿ\0-ğ÷o…ôfßğUßüU¿í¥ÿ\0Î–€?¿e¿Ú\'ş\nqà_ğF_Ù{ö°Õ¾$xóÁ_5{âî“û_øW\\×tÏşĞ¼Iû|Kñ¥¿ÁÚ¯Ãÿ\0Úo=ÆßƒßõO‡ÖöÚ–°nt/Š\Zu¾ƒ®ÚoÅâKm[õcöcø§á¯‡¾±±ñÆ¿\rYÚ|@ñö©ğí?g¿x…î4»ÿ\0ë·Ğ	|y§xwU{Ïí»9¢Ômïì¥ÿ\0B²»µ±\"m’õ¿ğ÷o…ôfßğUßüU¿í¥ÿ\0Î–ø{·Âú3oø*ïş*ßöÒÿ\0çK^Æ[šG‡Æa§Bua‹«„ªåN­\ns„°ŸYåVÄ`ñ´§ıa¹\'II8FÒ³’?›ä1ÍqÙf6UáìØc\"¨T£Z¥*òÅTÀÕ‹ªèc0’p¥,\næ£\':UÕNZÑtã(Tåş\'è\Zî¯ûDüdı¢~}ºÿ\0Åß¼\rğâ7‚ôËYoô»?ˆ?î<=­]|GğcÙ{‘ªxJm\"émÆ]VÒği60[CwªÃ´|ğ§ÆŸ~>|V×|z./üuñöø›ãÿ\0‰ºÕ²^Éáøüâ‰sI\'‡t‰¦mgmáß\réš…g¦Ã1Qm¤È”Å9sÛÃİ¾Ñ›ÁWñVÿ\0¶—ÿ\0:Z?áîß\n?èÍ¿à«¿ø«ÛKÿ\0-}xß»û68*ÎYu<²uå;âe…£G\n4}ª¤š¡E*ØÙQÖ3Äâ9äíJš^81–\"o3›úÆk‹Íİá)Ç	G‹©_¥\n*³©\Z«\rV5RxŠ–¡IÆœiªÕc/.ø?ğ?âsx7áÀ­î ıŸhøâ¿ÅRÿ\0QÕãÔ|/„,ôX>1ü>ÒfôKk—>óšÚæÈ]x–Ä[½¥…×Ø¾}øÑ§ßéğN_ø&Å§ew§_Aÿ\0]ÿ\0‚{ùöwÖÓZ]Cæÿ\0ÁHü74~m½ÂG4~d2G*oA¾7G\\«)?iÿ\0Ãİ¾Ñ›ÁWñVÿ\0¶—ÿ\0:Zù‡ã¯Ç_‰¿ğRÏ‰¿²/À_€¿²/í‡ğ»á÷ÂïÛöoı¬¾?üı¬¿fÿ\0ˆß³€¼+à/Ùâ6›ñ{Lğ/tÏ‹Úo…¼_ñ\'â?ÄŸø[Ãş´´ğ·‡ï´Ÿé7×úö½´J©{ÆX¬÷ˆÀVÂQ¡FiC6©RœïR¦.<ÏêÕ~ÎÖµl&7‡«ZVr[FJ)Ô’vCÂqÈ«àë,Â®1`òün]N5¨F,el³áNp¨Õ<=FV	¸}~¥8ÕT¨Sƒıû¢Š+ãO¯\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€\n(¢€?ÿÙ',5,0,'image/jpeg','logo.jpg',0),(9,3,1,'[agnDYN name=\"Text\"/]',NULL,0,0,'text/plain','agnText',0),(10,3,1,'[agnDYN name=\"HTML-Version\"/]',NULL,0,0,'text/html','agnHtml',0),(11,4,1,'[agnDYN name=\"Text\"/]',NULL,0,0,'text/plain','agnText',0),(12,4,1,'[agnDYN name=\"HTML-Version\"/]',NULL,0,0,'text/html','agnHtml',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `component_tbl` ENABLE KEYS */;

--
-- Table structure for table `cust_ban_tbl`
--

DROP TABLE IF EXISTS `cust_ban_tbl`;
CREATE TABLE `cust_ban_tbl` (
  `company_id` int(10) unsigned NOT NULL default '0',
  `email` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `creation_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`company_id`,`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `cust_ban_tbl`
--


/*!40000 ALTER TABLE `cust_ban_tbl` DISABLE KEYS */;
LOCK TABLES `cust_ban_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `cust_ban_tbl` ENABLE KEYS */;

--
-- Table structure for table `customer_1_binding_tbl`
--

DROP TABLE IF EXISTS `customer_1_binding_tbl`;
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
-- Dumping data for table `customer_1_binding_tbl`
--


/*!40000 ALTER TABLE `customer_1_binding_tbl` DISABLE KEYS */;
LOCK TABLES `customer_1_binding_tbl` WRITE;
INSERT INTO `customer_1_binding_tbl` (`customer_id`, `mailinglist_id`, `user_type`, `user_status`, `user_remark`, `change_date`, `exit_mailing_id`, `creation_date`, `mediatype`) VALUES (2,1,'A',1,'','2008-02-12 11:40:51',0,'2008-02-12 11:40:51',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_1_binding_tbl` ENABLE KEYS */;

--
-- Table structure for table `customer_1_tbl`
--

DROP TABLE IF EXISTS `customer_1_tbl`;
CREATE TABLE `customer_1_tbl` (
  `customer_id` int(11) NOT NULL auto_increment,
  `email` varchar(100) collate utf8_unicode_ci default NULL,
  `gender` int(11) NOT NULL default '0',
  `mailtype` int(11) default '0',
  `firstname` varchar(100) collate utf8_unicode_ci default NULL,
  `lastname` varchar(100) collate utf8_unicode_ci default NULL,
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `title` varchar(100) collate utf8_unicode_ci default NULL,
  `datasource_id` int(11) NOT NULL default '0',
  PRIMARY KEY  (`customer_id`),
  KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `customer_1_tbl`
--


/*!40000 ALTER TABLE `customer_1_tbl` DISABLE KEYS */;
LOCK TABLES `customer_1_tbl` WRITE;
INSERT INTO `customer_1_tbl` (`customer_id`, `email`, `gender`, `mailtype`, `firstname`, `lastname`, `creation_date`, `change_date`, `title`, `datasource_id`) VALUES (2,'mail@yourdomain.com',1,1,'Dagmar','Debug','2008-02-12 11:40:51','2008-02-12 11:40:51',NULL,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_1_tbl` ENABLE KEYS */;

--
-- Table structure for table `customer_1_tbl_seq`
--

DROP TABLE IF EXISTS `customer_1_tbl_seq`;
CREATE TABLE `customer_1_tbl_seq` (
  `customer_id` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`customer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `customer_1_tbl_seq`
--


/*!40000 ALTER TABLE `customer_1_tbl_seq` DISABLE KEYS */;
LOCK TABLES `customer_1_tbl_seq` WRITE;
INSERT INTO `customer_1_tbl_seq` (`customer_id`) VALUES (1),(2);
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_1_tbl_seq` ENABLE KEYS */;

--
-- Table structure for table `customer_field_tbl`
--

DROP TABLE IF EXISTS `customer_field_tbl`;
CREATE TABLE `customer_field_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `col_name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `admin_id` int(11) NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `default_value` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `mode_edit` int(11) NOT NULL default '0',
  `mode_insert` int(11) NOT NULL default '0',
  PRIMARY KEY  (`company_id`,`col_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `customer_field_tbl`
--


/*!40000 ALTER TABLE `customer_field_tbl` DISABLE KEYS */;
LOCK TABLES `customer_field_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_field_tbl` ENABLE KEYS */;

--
-- Table structure for table `customer_import_errors_tbl`
--

DROP TABLE IF EXISTS `customer_import_errors_tbl`;
CREATE TABLE `customer_import_errors_tbl` (
  `id` int(11) NOT NULL default '0',
  `error_id` varchar(20) collate utf8_unicode_ci NOT NULL default '',
  `count` int(11) NOT NULL default '0',
  UNIQUE KEY `customer_import_error_idx` (`id`,`error_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `customer_import_errors_tbl`
--


/*!40000 ALTER TABLE `customer_import_errors_tbl` DISABLE KEYS */;
LOCK TABLES `customer_import_errors_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_import_errors_tbl` ENABLE KEYS */;

--
-- Table structure for table `customer_import_status_tbl`
--

DROP TABLE IF EXISTS `customer_import_status_tbl`;
CREATE TABLE `customer_import_status_tbl` (
  `id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `admin_id` int(11) NOT NULL default '0',
  `datasource_id` int(11) NOT NULL default '0',
  `mode` int(11) NOT NULL default '0',
  `double_check` int(11) NOT NULL default '0',
  `ignore_null` int(11) NOT NULL default '0',
  `field_separator` char(1) collate utf8_unicode_ci NOT NULL default '0',
  `delimiter` char(1) collate utf8_unicode_ci NOT NULL default '0',
  `keycolumn` varchar(80) collate utf8_unicode_ci NOT NULL default '',
  `charset` varchar(80) collate utf8_unicode_ci NOT NULL default '',
  `records_before` int(11) NOT NULL default '0',
  `inserted` int(11) NOT NULL default '0',
  `updated` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `customer_import_status_tbl`
--


/*!40000 ALTER TABLE `customer_import_status_tbl` DISABLE KEYS */;
LOCK TABLES `customer_import_status_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `customer_import_status_tbl` ENABLE KEYS */;

--
-- Table structure for table `datasource_description_tbl`
--

DROP TABLE IF EXISTS `datasource_description_tbl`;
CREATE TABLE `datasource_description_tbl` (
  `datasource_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `sourcegroup_id` int(11) NOT NULL default '0',
  `description` text collate utf8_unicode_ci,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`datasource_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `datasource_description_tbl`
--


/*!40000 ALTER TABLE `datasource_description_tbl` DISABLE KEYS */;
LOCK TABLES `datasource_description_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `datasource_description_tbl` ENABLE KEYS */;

--
-- Table structure for table `date_tbl`
--

DROP TABLE IF EXISTS `date_tbl`;
CREATE TABLE `date_tbl` (
  `type` int(11) NOT NULL default '0',
  `format` varchar(100) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `date_tbl`
--


/*!40000 ALTER TABLE `date_tbl` DISABLE KEYS */;
LOCK TABLES `date_tbl` WRITE;
INSERT INTO `date_tbl` (`type`, `format`) VALUES (0,'d.M.yyyy'),(1,'MM/dd/yyyy'),(2,'EEEE d MMMM yyyy'),(3,'yyyy-MM-dd');
UNLOCK TABLES;
/*!40000 ALTER TABLE `date_tbl` ENABLE KEYS */;

--
-- Table structure for table `dyn_content_tbl`
--

DROP TABLE IF EXISTS `dyn_content_tbl`;
CREATE TABLE `dyn_content_tbl` (
  `dyn_content_id` int(10) unsigned NOT NULL auto_increment,
  `dyn_name_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `dyn_content` longtext collate utf8_unicode_ci,
  `dyn_order` int(10) unsigned default NULL,
  `target_id` int(10) unsigned default NULL,
  PRIMARY KEY  (`dyn_content_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `dyn_content_tbl`
--


/*!40000 ALTER TABLE `dyn_content_tbl` DISABLE KEYS */;
LOCK TABLES `dyn_content_tbl` WRITE;
INSERT INTO `dyn_content_tbl` (`dyn_content_id`, `dyn_name_id`, `company_id`, `dyn_content`, `dyn_order`, `target_id`) VALUES (1,23,1,'[agnIMAGE name=\"logo.jpg\"]',1,0),(2,1,1,'Firmenname fÃ¼r Textversion',1,0),(3,2,1,'[agnDATE]',1,0),(4,4,1,'Ihre<br>\r\nSuper-Firma',1,0),(5,20,1,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_profil&agnUID=##AGNUID##',1,0),(6,21,1,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_unsubscribe&agnUID=##AGNUID##',1,0),(7,22,1,'Ihre Firma, www.meine-firma.de<br>\r\nTelefon: xxx/12343567 ...<br><br>\r\nVorstand: xxx<br>\r\nRegistergericht ...',1,0),(8,55,1,'[agnIMAGE name=\"logo.jpg\"]',1,0),(9,33,1,'name of company for textversion',1,0),(10,34,1,'[agnDATE]',1,0),(11,36,1,'your<br>\r\nsuper company',1,0),(12,52,1,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_profil&agnUID=##AGNUID##',1,0),(13,53,1,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_unsubscribe&agnUID=##AGNUID##',1,0),(14,54,1,'Your company, www.my-company.de<br>\r\nFon: xxxx/12343567 ...<br>',1,0),(15,65,1,'**********************************************************************\r\n                       Newsletter Anmeldung\r\n**********************************************************************\r\n\r\n[agnTITLE type=2],\r\n\r\nvielen Dank, dass Sie sich fÃ¼r unseren Newsletter interessieren.\r\nUm Ihr Abonnement zu bestÃ¤tigen, klicken Sie bitte auf folgenden\r\nAktivierungslink:\r\n\r\nhttp://www.meine-firma.de/form.do?agnCI=1&agnFN=de_doi_welcome&agnUID=##AGNUID##\r\n(ACHTUNG: Bitte www.meine-firma.de auf Ihren rdir-Link anpassen\r\nund Link-Messung aktivieren!)\r\n\r\nHaben Sie unseren Newsletter nicht abonniert oder wurden fÃ¤lschlicher\r\nWeise bei uns angemeldet, mÃ¼ssen Sie nichts weiter unternehmen.\r\n\r\nMit freundlichen GrÃ¼ÃŸen\r\n\r\nIhr online-Team\r\n\r\n**********************************************************************\r\nImpressum:\r\nFirmenname\r\nStrasse ...',1,0),(16,67,1,'**********************************************************************\r\n                       newsletter registration\r\n**********************************************************************\r\n\r\n[agnTITLE type=1],\r\n\r\nthank you for your interest in our newsletter. To activate the\r\nabonnement please click the following link:\r\n\r\nhttp://www.my-company.de/form.do?agnCI=1&agnFN=en_doi_welcome&agnUID=##AGNUID##\r\n(WARNING: Please change www.my-company.de to your rdir-link and be\r\nsure that linktracking is activated!)\r\n\r\nGreetings\r\n\r\nyour online-team\r\n\r\n**********************************************************************\r\nImprint:\r\nname of company ...',1,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `dyn_content_tbl` ENABLE KEYS */;

--
-- Table structure for table `dyn_name_tbl`
--

DROP TABLE IF EXISTS `dyn_name_tbl`;
CREATE TABLE `dyn_name_tbl` (
  `dyn_name_id` int(10) unsigned NOT NULL auto_increment,
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `dyn_name` varchar(100) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`dyn_name_id`),
  KEY `mailing_id` (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `dyn_name_tbl`
--


/*!40000 ALTER TABLE `dyn_name_tbl` DISABLE KEYS */;
LOCK TABLES `dyn_name_tbl` WRITE;
INSERT INTO `dyn_name_tbl` (`dyn_name_id`, `mailing_id`, `company_id`, `dyn_name`) VALUES (1,1,1,'0.1.1 Header-Text'),(2,1,1,'0.2 date'),(3,1,1,'0.3 Intro-text'),(4,1,1,'0.4 Greeting'),(5,1,1,'1.0 Headline ****'),(6,1,1,'1.1 Sub-headline'),(7,1,1,'1.2 Content'),(8,1,1,'1.3 Link-URL'),(9,1,1,'1.4 Link-Text'),(10,1,1,'2.0 Headline ****'),(11,1,1,'2.1 Sub-headline'),(12,1,1,'2.2 Content'),(13,1,1,'2.3 Link-URL'),(14,1,1,'2.4 Link-Text'),(15,1,1,'3.0 Headline ****'),(16,1,1,'3.1 Sub-headline'),(17,1,1,'3.2 Content'),(18,1,1,'3.3 Link-URL'),(19,1,1,'3.4 Link-Text'),(20,1,1,'9.0 change-profil-URL'),(21,1,1,'9.1 unsubscribe-URL'),(22,1,1,'9.2 imprint'),(23,1,1,'0.1 Header-image'),(24,1,1,'1.5 Image-URL'),(25,1,1,'1.7 Image-URL-1'),(26,1,1,'1.6 Link-URL'),(27,1,1,'2.5 Image-URL'),(28,1,1,'2.7 Image-URL-1'),(29,1,1,'2.6 Link-URL'),(30,1,1,'3.5 Image-URL'),(31,1,1,'3.7 Image-URL-1'),(32,1,1,'3.6 Link-URL'),(33,2,1,'0.1.1 Header-Text'),(34,2,1,'0.2 date'),(35,2,1,'0.3 Intro-text'),(36,2,1,'0.4 Greeting'),(37,2,1,'1.0 Headline ****'),(38,2,1,'1.1 Sub-headline'),(39,2,1,'1.2 Content'),(40,2,1,'1.3 Link-URL'),(41,2,1,'1.4 Link-Text'),(42,2,1,'2.0 Headline ****'),(43,2,1,'2.1 Sub-headline'),(44,2,1,'2.2 Content'),(45,2,1,'2.3 Link-URL'),(46,2,1,'2.4 Link-Text'),(47,2,1,'3.0 Headline ****'),(48,2,1,'3.1 Sub-headline'),(49,2,1,'3.2 Content'),(50,2,1,'3.3 Link-URL'),(51,2,1,'3.4 Link-Text'),(52,2,1,'9.0 change-profil-URL'),(53,2,1,'9.1 unsubscribe-URL'),(54,2,1,'9.2 imprint'),(55,2,1,'0.1 Header-image'),(56,2,1,'1.5 Image-URL'),(57,2,1,'1.7 Image-URL-1'),(58,2,1,'1.6 Link-URL'),(59,2,1,'2.5 Image-URL'),(60,2,1,'2.7 Image-URL-1'),(61,2,1,'2.6 Link-URL'),(62,2,1,'3.5 Image-URL'),(63,2,1,'3.7 Image-URL-1'),(64,2,1,'3.6 Link-URL'),(65,3,1,'Text'),(66,3,1,'HTML-Version'),(67,4,1,'Text'),(68,4,1,'HTML-Version');
UNLOCK TABLES;
/*!40000 ALTER TABLE `dyn_name_tbl` ENABLE KEYS */;

--
-- Table structure for table `dyn_target_tbl`
--

DROP TABLE IF EXISTS `dyn_target_tbl`;
CREATE TABLE `dyn_target_tbl` (
  `target_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `target_shortname` varchar(100) collate utf8_unicode_ci NOT NULL default '',
  `target_description` text collate utf8_unicode_ci,
  `target_sql` text collate utf8_unicode_ci,
  `target_representation` blob,
  PRIMARY KEY  (`target_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `dyn_target_tbl`
--


/*!40000 ALTER TABLE `dyn_target_tbl` DISABLE KEYS */;
LOCK TABLES `dyn_target_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `dyn_target_tbl` ENABLE KEYS */;

--
-- Table structure for table `emm_layout_tbl`
--

DROP TABLE IF EXISTS `emm_layout_tbl`;
CREATE TABLE `emm_layout_tbl` (
  `layout_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `header_url` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `footer_url` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `base_url` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `normal_color` varchar(30) collate utf8_unicode_ci NOT NULL default '',
  `highlight_color` varchar(30) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`layout_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `emm_layout_tbl`
--


/*!40000 ALTER TABLE `emm_layout_tbl` DISABLE KEYS */;
LOCK TABLES `emm_layout_tbl` WRITE;
INSERT INTO `emm_layout_tbl` (`layout_id`, `company_id`, `header_url`, `footer_url`, `base_url`, `normal_color`, `highlight_color`) VALUES (0,0,'header.jsp','footer.jsp','images/emm/','#D2D7D2','#73A2D0');
UNLOCK TABLES;
/*!40000 ALTER TABLE `emm_layout_tbl` ENABLE KEYS */;

--
-- Table structure for table `export_predef_tbl`
--

DROP TABLE IF EXISTS `export_predef_tbl`;
CREATE TABLE `export_predef_tbl` (
  `id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `charset` varchar(200) collate utf8_unicode_ci NOT NULL default 'ISO-8859-1',
  `column_names` text collate utf8_unicode_ci NOT NULL,
  `deleted` int(11) NOT NULL default '0',
  `shortname` text collate utf8_unicode_ci NOT NULL,
  `description` text collate utf8_unicode_ci NOT NULL,
  `mailinglists` text collate utf8_unicode_ci NOT NULL,
  `mailinglist_id` int(11) NOT NULL default '0',
  `delimiter_char` char(1) collate utf8_unicode_ci NOT NULL default '0',
  `separator_char` char(1) collate utf8_unicode_ci NOT NULL default '0',
  `target_id` int(11) NOT NULL default '0',
  `user_status` int(11) NOT NULL default '0',
  `user_type` char(1) collate utf8_unicode_ci NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `export_predef_tbl`
--


/*!40000 ALTER TABLE `export_predef_tbl` DISABLE KEYS */;
LOCK TABLES `export_predef_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `export_predef_tbl` ENABLE KEYS */;

--
-- Table structure for table `log_tbl`
--

DROP TABLE IF EXISTS `log_tbl`;
CREATE TABLE `log_tbl` (
  `log_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) default NULL,
  `admin_id` int(11) default NULL,
  `creation_date` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `category` int(11) default NULL,
  `ip_adr` varchar(20) collate utf8_unicode_ci default NULL,
  `message` text collate utf8_unicode_ci,
  PRIMARY KEY  (`log_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `log_tbl`
--


/*!40000 ALTER TABLE `log_tbl` DISABLE KEYS */;
LOCK TABLES `log_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `log_tbl` ENABLE KEYS */;

--
-- Table structure for table `maildrop_status_tbl`
--

DROP TABLE IF EXISTS `maildrop_status_tbl`;
CREATE TABLE `maildrop_status_tbl` (
  `status_id` int(11) NOT NULL auto_increment,
  `company_id` int(11) NOT NULL default '0',
  `status_field` varchar(10) collate utf8_unicode_ci NOT NULL default '',
  `mailing_id` int(11) NOT NULL default '0',
  `senddate` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `step` int(11) default NULL,
  `blocksize` int(11) default NULL,
  `gendate` timestamp NOT NULL default '0000-00-00 00:00:00',
  `genstatus` int(1) default NULL,
  `genchange` timestamp NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`status_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `maildrop_status_tbl`
--


/*!40000 ALTER TABLE `maildrop_status_tbl` DISABLE KEYS */;
LOCK TABLES `maildrop_status_tbl` WRITE;
INSERT INTO `maildrop_status_tbl` (`status_id`, `company_id`, `status_field`, `mailing_id`, `senddate`, `step`, `blocksize`, `gendate`, `genstatus`, `genchange`) VALUES (1,1,'E',3,'2008-02-12 11:54:32',0,0,'2008-02-12 11:54:32',1,'2008-02-12 11:54:32'),(3,1,'E',4,'2008-02-26 11:53:32',0,0,'2008-02-26 11:53:32',1,'2008-02-26 11:53:33');
UNLOCK TABLES;
/*!40000 ALTER TABLE `maildrop_status_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailing_account_tbl`
--

DROP TABLE IF EXISTS `mailing_account_tbl`;
CREATE TABLE `mailing_account_tbl` (
  `mailing_id` int(11) NOT NULL default '0',
  `company_id` int(11) NOT NULL default '0',
  `mailtype` int(11) NOT NULL default '0',
  `no_of_mailings` int(11) NOT NULL default '0',
  `no_of_bytes` int(11) NOT NULL default '0',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `maildrop_id` int(11) NOT NULL default '0',
  `mailing_account_id` int(11) NOT NULL default '0',
  `status_field` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `blocknr` int(11) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailing_account_tbl`
--


/*!40000 ALTER TABLE `mailing_account_tbl` DISABLE KEYS */;
LOCK TABLES `mailing_account_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailing_account_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailing_backend_log_tbl`
--

DROP TABLE IF EXISTS `mailing_backend_log_tbl`;
CREATE TABLE `mailing_backend_log_tbl` (
  `mailing_id` int(10) default NULL,
  `current_mails` int(10) default NULL,
  `total_mails` int(10) default NULL,
  `change_date` timestamp NULL default NULL,
  `creation_date` timestamp NULL default NULL,
  `status_id` int(10) default NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailing_backend_log_tbl`
--


/*!40000 ALTER TABLE `mailing_backend_log_tbl` DISABLE KEYS */;
LOCK TABLES `mailing_backend_log_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailing_backend_log_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailing_mt_tbl`
--

DROP TABLE IF EXISTS `mailing_mt_tbl`;
CREATE TABLE `mailing_mt_tbl` (
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `param` text collate utf8_unicode_ci NOT NULL,
  `mediatype` int(10) unsigned NOT NULL default '0',
  KEY `mailing_id` (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailing_mt_tbl`
--


/*!40000 ALTER TABLE `mailing_mt_tbl` DISABLE KEYS */;
LOCK TABLES `mailing_mt_tbl` WRITE;
INSERT INTO `mailing_mt_tbl` (`mailing_id`, `param`, `mediatype`) VALUES (1,'from=\"Absender anpassen <noreply@openemm.org>\", subject=\"Bitte Betreff einfÃ¼gen!\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"2\", reply=\"Absender anpassen <noreply@openemm.org>\", onepixlog=\"bottom\", ',0),(2,'from=\"change sender name <noreply@openemm.org>\", subject=\"insert subject please!\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"2\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"bottom\", ',0),(3,'from=\"change sender name <noreply@openemm.org>\", subject=\"Bitte aktivieren: Ihre Anmeldung zum Newsletter\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"0\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"none\", ',0),(4,'from=\"change sender name <noreply@openemm.org>\", subject=\"please activate: your newsletter subscription\", charset=\"ISO-8859-1\", linefeed=\"72\", mailformat=\"0\", reply=\"change sender name <noreply@openemm.org>\", onepixlog=\"none\", ',0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailing_mt_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailing_status_tbl`
--

DROP TABLE IF EXISTS `mailing_status_tbl`;
CREATE TABLE `mailing_status_tbl` (
  `mailing_id` int(11) NOT NULL default '0',
  `status_text` text collate utf8_unicode_ci
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailing_status_tbl`
--


/*!40000 ALTER TABLE `mailing_status_tbl` DISABLE KEYS */;
LOCK TABLES `mailing_status_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailing_status_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailing_tbl`
--

DROP TABLE IF EXISTS `mailing_tbl`;
CREATE TABLE `mailing_tbl` (
  `mailing_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL default '0',
  `campaign_id` int(11) unsigned NOT NULL default '0',
  `shortname` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `description` text collate utf8_unicode_ci NOT NULL,
  `mailing_type` int(10) unsigned NOT NULL default '0',
  `creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
  `mailtemplate_id` int(10) unsigned default '0',
  `is_template` int(10) unsigned NOT NULL default '0',
  `deleted` int(10) unsigned NOT NULL default '0',
  `target_expression` text collate utf8_unicode_ci,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `mailinglist_id` int(10) unsigned NOT NULL default '0',
  `needs_target` int(10) unsigned NOT NULL default '0',
  `archived` int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (`mailing_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailing_tbl`
--


/*!40000 ALTER TABLE `mailing_tbl` DISABLE KEYS */;
LOCK TABLES `mailing_tbl` WRITE;
INSERT INTO `mailing_tbl` (`mailing_id`, `company_id`, `campaign_id`, `shortname`, `description`, `mailing_type`, `creation_date`, `mailtemplate_id`, `is_template`, `deleted`, `target_expression`, `change_date`, `mailinglist_id`, `needs_target`, `archived`) VALUES (1,1,0,'de_template','created by eMM-Xpress',0,'2008-02-12 11:30:14',0,1,0,'','2008-02-12 11:30:14',1,0,0),(2,1,0,'en_template','created by eMM-Xpress',0,'2008-02-12 11:47:21',0,1,0,'','2008-02-12 11:47:21',1,0,0),(3,1,0,'de_doi_mail','double-opt-in mail, subscribe link',1,'2008-02-12 11:54:06',0,0,0,'','2008-02-12 11:54:06',1,0,0),(4,1,0,'en_doi_mail','double-opt-in mail, subscribe link',1,'2008-02-12 11:57:13',0,0,0,'','2008-02-12 11:57:13',1,0,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailing_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailinglist_tbl`
--

DROP TABLE IF EXISTS `mailinglist_tbl`;
CREATE TABLE `mailinglist_tbl` (
  `mailinglist_id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned default NULL,
  `description` text collate utf8_unicode_ci,
  `shortname` varchar(100) collate utf8_unicode_ci NOT NULL default '',
  KEY `mailinglist_id` (`mailinglist_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailinglist_tbl`
--


/*!40000 ALTER TABLE `mailinglist_tbl` DISABLE KEYS */;
LOCK TABLES `mailinglist_tbl` WRITE;
INSERT INTO `mailinglist_tbl` (`mailinglist_id`, `company_id`, `description`, `shortname`) VALUES (1,1,'','mailinglist');
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailinglist_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailloop_tbl`
--

DROP TABLE IF EXISTS `mailloop_tbl`;
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
  `subscribe_enable` int(1) unsigned NOT NULL default '0',
  `mailinglist_id` int(11) unsigned NOT NULL default '0',
  `form_id` int(11) unsigned NOT NULL default '0',
  PRIMARY KEY  (`rid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailloop_tbl`
--


/*!40000 ALTER TABLE `mailloop_tbl` DISABLE KEYS */;
LOCK TABLES `mailloop_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailloop_tbl` ENABLE KEYS */;

--
-- Table structure for table `mailtrack_tbl`
--

DROP TABLE IF EXISTS `mailtrack_tbl`;
CREATE TABLE `mailtrack_tbl` (
  `mailtrack_id` int(10) NOT NULL auto_increment,
  `customer_id` int(10) default NULL,
  `mailing_id` int(10) default NULL,
  `company_id` int(10) default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `status_id` int(10) default NULL,
  PRIMARY KEY  (`mailtrack_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `mailtrack_tbl`
--


/*!40000 ALTER TABLE `mailtrack_tbl` DISABLE KEYS */;
LOCK TABLES `mailtrack_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `mailtrack_tbl` ENABLE KEYS */;

--
-- Table structure for table `onepixel_log_tbl`
--

DROP TABLE IF EXISTS `onepixel_log_tbl`;
CREATE TABLE `onepixel_log_tbl` (
  `company_id` int(10) unsigned NOT NULL default '0',
  `mailing_id` int(10) unsigned NOT NULL default '0',
  `customer_id` int(10) unsigned NOT NULL default '0',
  `open_count` int(10) unsigned NOT NULL default '1',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `ip_adr` varchar(15) collate utf8_unicode_ci NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `onepixel_log_tbl`
--


/*!40000 ALTER TABLE `onepixel_log_tbl` DISABLE KEYS */;
LOCK TABLES `onepixel_log_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `onepixel_log_tbl` ENABLE KEYS */;

--
-- Table structure for table `rdir_action_tbl`
--

DROP TABLE IF EXISTS `rdir_action_tbl`;
CREATE TABLE `rdir_action_tbl` (
  `action_id` int(10) unsigned NOT NULL auto_increment,
  `shortname` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `description` text collate utf8_unicode_ci,
  `action_type` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `operations` blob,
  PRIMARY KEY  (`action_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `rdir_action_tbl`
--


/*!40000 ALTER TABLE `rdir_action_tbl` DISABLE KEYS */;
LOCK TABLES `rdir_action_tbl` WRITE;
INSERT INTO `rdir_action_tbl` (`action_id`, `shortname`, `description`, `action_type`, `company_id`, `operations`) VALUES (1,'doi_user_confirm','step 1/2: confirm user',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0+org.agnitas.actions.ops.ActivateDoubleOptInl‹¥z2…Z\0\0xpx'),(2,'doi_user_register (de)','step 2/2: save user data, send doi-mail',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0)org.agnitas.actions.ops.SubscribeCustomer*Ö‘€V‡Qj\0Z\0doubleCheckZ\0doubleOptInL\0	keyColumnt\0Ljava/lang/String;xpt\0emailsr\0#org.agnitas.actions.ops.SendMailing	á¯š£N¾«\0I\0delayMinutesI\0	mailingIDxp\0\0\0\0\0\0\0x'),(3,'doi_user_register (en)','step 2/2: save user data, send doi-mail',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0)org.agnitas.actions.ops.SubscribeCustomer*Ö‘€V‡Qj\0Z\0doubleCheckZ\0doubleOptInL\0	keyColumnt\0Ljava/lang/String;xpt\0emailsr\0#org.agnitas.actions.ops.SendMailing	á¯š£N¾«\0I\0delayMinutesI\0	mailingIDxp\0\0\0\0\0\0\0x'),(4,'user_get_data','load data from database',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0#org.agnitas.actions.ops.GetCustomeršpºäş¼Ó\0Z\0\nloadAlwaysxp\0x'),(5,'user_subscribe','subscribe user',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0)org.agnitas.actions.ops.SubscribeCustomer*Ö‘€V‡Qj\0Z\0doubleCheckZ\0doubleOptInL\0	keyColumnt\0Ljava/lang/String;xp\0t\0emailx'),(6,'user_unsubscribe','unsubscribe user',1,1,'¬í\0sr\0java.util.ArrayListxÒ™Ça\0I\0sizexp\0\0\0w\0\0\0\nsr\0+org.agnitas.actions.ops.UnsubscribeCustomer»öÎàO±\0\0xpx');
UNLOCK TABLES;
/*!40000 ALTER TABLE `rdir_action_tbl` ENABLE KEYS */;

--
-- Table structure for table `rdir_log_tbl`
--

DROP TABLE IF EXISTS `rdir_log_tbl`;
CREATE TABLE `rdir_log_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `customer_id` int(11) NOT NULL default '0',
  `mailing_id` int(11) NOT NULL default '0',
  `ip_adr` varchar(15) collate utf8_unicode_ci NOT NULL default '',
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `url_id` int(11) NOT NULL default '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `rdir_log_tbl`
--


/*!40000 ALTER TABLE `rdir_log_tbl` DISABLE KEYS */;
LOCK TABLES `rdir_log_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rdir_log_tbl` ENABLE KEYS */;

--
-- Table structure for table `rdir_url_tbl`
--

DROP TABLE IF EXISTS `rdir_url_tbl`;
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `rdir_url_tbl`
--


/*!40000 ALTER TABLE `rdir_url_tbl` DISABLE KEYS */;
LOCK TABLES `rdir_url_tbl` WRITE;
INSERT INTO `rdir_url_tbl` (`url_id`, `company_id`, `mailing_id`, `action_id`, `measure_type`, `full_url`, `shortname`, `relevance`) VALUES (1,1,1,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_profil&agnUID=##AGNUID##',NULL,0),(2,1,1,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_unsubscribe&agnUID=##AGNUID##',NULL,0),(3,1,2,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_profil&agnUID=##AGNUID##',NULL,0),(4,1,2,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_unsubscribe&agnUID=##AGNUID##',NULL,0),(5,1,3,0,3,'http://www.meine-firma.de/form.do?agnCI=1&agnFN=de_doi_welcome&agnUID=##AGNUID##',NULL,0),(6,1,4,0,3,'http://www.my-company.de/form.do?agnCI=1&agnFN=en_doi_welcome&agnUID=##AGNUID##',NULL,0);
UNLOCK TABLES;
/*!40000 ALTER TABLE `rdir_url_tbl` ENABLE KEYS */;

--
-- Table structure for table `rulebased_sent_tbl`
--

DROP TABLE IF EXISTS `rulebased_sent_tbl`;
CREATE TABLE `rulebased_sent_tbl` (
  `mailing_id` int(11) default NULL,
  `lastsent` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `rulebased_sent_tbl`
--


/*!40000 ALTER TABLE `rulebased_sent_tbl` DISABLE KEYS */;
LOCK TABLES `rulebased_sent_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `rulebased_sent_tbl` ENABLE KEYS */;

--
-- Table structure for table `softbounce_email_tbl`
--

DROP TABLE IF EXISTS `softbounce_email_tbl`;
CREATE TABLE `softbounce_email_tbl` (
  `email` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `bnccnt` int(11) NOT NULL default '0',
  `mailing_id` int(11) NOT NULL default '0',
  `creation_date` timestamp NULL default NULL,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `company_id` int(11) NOT NULL default '0',
  KEY `email` (`email`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `softbounce_email_tbl`
--


/*!40000 ALTER TABLE `softbounce_email_tbl` DISABLE KEYS */;
LOCK TABLES `softbounce_email_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `softbounce_email_tbl` ENABLE KEYS */;

--
-- Table structure for table `tag_tbl`
--

DROP TABLE IF EXISTS `tag_tbl`;
CREATE TABLE `tag_tbl` (
  `tag_id` int(10) unsigned NOT NULL auto_increment,
  `tagname` varchar(64) collate utf8_unicode_ci NOT NULL default '',
  `selectvalue` text collate utf8_unicode_ci NOT NULL,
  `type` varchar(10) collate utf8_unicode_ci NOT NULL default '',
  `company_id` int(10) NOT NULL default '0',
  `description` text collate utf8_unicode_ci,
  `change_date` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  PRIMARY KEY  (`tag_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `tag_tbl`
--


/*!40000 ALTER TABLE `tag_tbl` DISABLE KEYS */;
LOCK TABLES `tag_tbl` WRITE;
INSERT INTO `tag_tbl` (`tag_id`, `tagname`, `selectvalue`, `type`, `company_id`, `description`, `change_date`) VALUES (1,'agnCUSTOMERID','cust.customer_id','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(2,'agnMAILTYPE','cust.mailtype','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(3,'agnIMAGE','\'[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}\'','COMPLEX',0,NULL,'2006-07-10 09:58:25'),(4,'agnDB','cust.{column}','COMPLEX',0,'Display one Column','2006-07-10 09:58:25'),(5,'agnTITLE','\'builtin\'','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(6,'agnFIRSTNAME','cust.firstname','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(7,'agnLASTNAME','cust.lastname','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(8,'agnEMAIL','cust.email','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(9,'agnDATE','date_format(current_timestamp, \'%d.%m.%Y\')','SIMPLE',0,NULL,'2006-07-10 09:58:25'),(10,'agnIMGLINK','\'<a href=\"[rdir-domain]/r.html?uid=[agnUID]\"><img src=\"[rdir-domain]/image?ci=[company-id]&mi=[mailing-id]&name={name}\" border=\"0\"></a>\'','COMPLEX',0,NULL,'2008-06-09 00:00:00');
UNLOCK TABLES;
/*!40000 ALTER TABLE `tag_tbl` ENABLE KEYS */;

--
-- Table structure for table `timestamp_tbl`
--

DROP TABLE IF EXISTS `timestamp_tbl`;
CREATE TABLE `timestamp_tbl` (
  `timestamp_id` int(10) default NULL,
  `description` varchar(250) collate utf8_unicode_ci default NULL,
  `cur` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `prev` timestamp NOT NULL default '0000-00-00 00:00:00',
  `temp` timestamp NOT NULL default '0000-00-00 00:00:00'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `timestamp_tbl`
--


/*!40000 ALTER TABLE `timestamp_tbl` DISABLE KEYS */;
LOCK TABLES `timestamp_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `timestamp_tbl` ENABLE KEYS */;

--
-- Table structure for table `title_gender_tbl`
--

DROP TABLE IF EXISTS `title_gender_tbl`;
CREATE TABLE `title_gender_tbl` (
  `title_id` int(11) NOT NULL default '0',
  `gender` int(11) NOT NULL default '0',
  `title` varchar(50) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`title_id`,`gender`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `title_gender_tbl`
--


/*!40000 ALTER TABLE `title_gender_tbl` DISABLE KEYS */;
LOCK TABLES `title_gender_tbl` WRITE;
INSERT INTO `title_gender_tbl` (`title_id`, `gender`, `title`) VALUES (1,0,'Mr.'),(1,1,'Ms.'),(1,2,'Company'),(2,0,'Herr'),(2,1,'Frau'),(2,2,'Firma');
UNLOCK TABLES;
/*!40000 ALTER TABLE `title_gender_tbl` ENABLE KEYS */;

--
-- Table structure for table `title_tbl`
--

DROP TABLE IF EXISTS `title_tbl`;
CREATE TABLE `title_tbl` (
  `company_id` int(11) NOT NULL default '0',
  `title_id` int(11) NOT NULL auto_increment,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  PRIMARY KEY  (`title_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `title_tbl`
--


/*!40000 ALTER TABLE `title_tbl` DISABLE KEYS */;
LOCK TABLES `title_tbl` WRITE;
INSERT INTO `title_tbl` (`company_id`, `title_id`, `description`) VALUES (1,1,'Default'),(1,2,'German Default');
UNLOCK TABLES;
/*!40000 ALTER TABLE `title_tbl` ENABLE KEYS */;

--
-- Table structure for table `userform_tbl`
--

DROP TABLE IF EXISTS `userform_tbl`;
CREATE TABLE `userform_tbl` (
  `form_id` int(10) unsigned NOT NULL auto_increment,
  `formname` varchar(200) collate utf8_unicode_ci NOT NULL default '',
  `description` text collate utf8_unicode_ci NOT NULL,
  `company_id` int(10) unsigned NOT NULL default '0',
  `startaction_id` int(10) unsigned NOT NULL default '0',
  `endaction_id` int(10) unsigned NOT NULL default '0',
  `success_template` longtext collate utf8_unicode_ci NOT NULL,
  `error_template` longtext collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`form_id`),
  KEY `formname` (`formname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `userform_tbl`
--


/*!40000 ALTER TABLE `userform_tbl` DISABLE KEYS */;
LOCK TABLES `userform_tbl` WRITE;
INSERT INTO `userform_tbl` (`form_id`, `formname`, `description`, `company_id`, `startaction_id`, `endaction_id`, `success_template`, `error_template`) VALUES (1,'de_doi','double-opt-in german 1/3',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_doi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 1/3</h1>\r\n                  <p>vielen Dank f&uuml;r Ihr Interesse an unserem Angebot.<br>\r\n                  Hier k&ouml;nnen Sie sich zum Newsletter registrieren:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unbekannt</option>\r\n                            <option value=\"1\">Frau</option>\r\n                            <option value=\"0\">Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Absenden\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(2,'de_doi_confirm','double-opt-in german 2/3',1,2,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 2/3</h1>\r\n                  <p>Ihre Daten wurden erfolgreich angenommen.<br><br>\r\n                  Bitte best&auml;tigen Sie Ihr Abonnement in der E-Mail,<br>die wir Ihnen in K&uuml;rze zustellen, um den Bestellprozess abzuschlie&szlig;en.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(3,'de_doi_welcome','double-opt-in german 3/3',1,1,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER ANMELDUNG 3/3</h1>\r\n                  <h1>Willkommen</h1>\r\n                  <p>Ihre Anmeldung ist abgeschlossen und wir freuen uns,<br>Sie in unserem Newsletterverteiler begr&uuml;&szlig;en zu d&uuml;rfen.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(4,'de_profil','profile german 1/2',1,4,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_profil_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">        \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN</h1>\r\n                  <p>Sie erhalten unseren Newsletter und m&ouml;chten Ihre Daten &auml;ndern:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" #if($!customerData.GENDER == \"2\") selected #end>unbekannt</option>\r\n                            <option value=\"1\" #if($!customerData.GENDER == \"1\") selected #end>Frau</option>\r\n                            <option value=\"0\" #if($!customerData.GENDER == \"0\") selected #end>Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\" value=\"$!customerData.FIRSTNAME\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\" value=\"$!customerData.LASTNAME\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\" value=\"$!customerData.EMAIL\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" #if($!customerData.MAILTYPE == \"1\") checked #end>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\" #if($!customerData.MAILTYPE == \"0\") checked #end>Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Speichern\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(5,'de_profil_confirm','profile german 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN</h1>\r\n                  <p>Ihre &Auml;nderungen wurden erfolgreich &uuml;bernommen.<br>\r\n                  Ab der n&auml;chsten Ausgabe ber&uuml;cksichitgen wir Ihre &Auml;nderungen.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Profil&auml;nderung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER DATEN &Auml;NDERN FEHLER</h1>\r\n                  <p>Leider konnten Ihre Daten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(6,'de_soi','single-opt-in german 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_soi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG 1/2</h1>\r\n                  <p>vielen Dank f&uuml;r Ihr Interesse an unserem Angebot.<br>\r\n                  Hier k&ouml;nnen Sie sich zum Newsletter registrieren:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">Anrede:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unbekannt</option>\r\n                            <option value=\"1\">Frau</option>\r\n                            <option value=\"0\">Herr</option>\r\n                          </select></td></tr>\r\n                  <tr><td>Vorname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>Nachname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>E-Mail-Adresse:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">Newsletterformat:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (mit Bildern)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (Plaintext)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Absenden\"></td>\r\n                      <td><input type=\"reset\" value=\"Abbrechen\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(7,'de_soi_confirm','single-opt-in german 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG 2/2</h1>\r\n                  <p>Wir konnten Ihre Anmeldung erfolgreich annehmen.<br>\r\n                  Ab der n&auml;chsten Ausgabe erhalten Sie unseren Newsletter.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Anmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ANMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Anmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden, bitte<br>\r\n                  &uuml;berpr&uuml;fen Sie Ihre Eingaben und versuchen es erneut.</p>\r\n                  <p>Vielen Dank f&uuml;r Ihr Verst&auml;ndnis.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(8,'de_unsub_confirm','unsubscribe german 2/2',1,6,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG 2/2</h1>\r\n                  <p>Ihre Abmeldung wurde erfolgreich entgegengenommen.<br>\r\n                  Sie erhalten keine weiteren Newsletterausgaben.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Abmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(9,'de_unsubscribe','unsubscribe german 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"de_unsub_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">       \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG 1/2</h1>\r\n                  <p>M&ouml;chten Sie den Newsletter wirklich abbestellen?</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\"><input type=\"submit\" value=\" Ja \"></td>\r\n                      <td><input type=\"reset\" value=\" Nein \" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>Newsletter-Abmeldung</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER-ABMELDUNG FEHLER</h1>\r\n                  <p>Leider ist bei Ihrer Abmeldung ein Fehler aufgetreten.<br>\r\n                  Ihre Daten konnten nicht gespeichert werden.</p>\r\n                  <p>Mit freundlichen Gr&uuml;&szlig;en<br>\r\n                  Ihr online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(10,'en_soi','single-opt-in english 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_soi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>SUBSCRIBE NEWSLETTER 1/2</h1>\r\n                  <p>Thank you for your interest!<br>\r\n                  Register here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unknown</option>\r\n                            <option value=\"1\">Ms.</option>\r\n                            <option value=\"0\">Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">mail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML (includes images)<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text (plaintext only)</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Send\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(11,'en_soi_confirm','single-opt-in english 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION 2/2</h1>\r\n                  <p>Your newsletter registration was successful.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter subscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER SUBSCRIPTION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(12,'en_unsubscribe','unsubscribe english 1/2',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscribe</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_unsub_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">       \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE NEWSLETTER 1/2</h1>\r\n                  <p>Do you really want to unsubscribe from our newsletter?</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\"><input type=\"submit\" value=\" Yes \"></td>\r\n                      <td><input type=\"reset\" value=\" No \" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(13,'en_unsub_confirm','unsubscribe english 2/2',1,6,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscribe</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE NEWSLETTER 2/2</h1>\r\n                  <p>Your newsletter unsubscription was successful.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter unsubscription</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>UNSUBSCRIBE ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>\r\n'),(14,'en_profil','profile english 1/2',1,4,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_profil_confirm\">\r\n          <input type=\"hidden\" name=\"agnUID\" value=\"$!agnUID\">        \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Please change your data here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" #if($!customerData.GENDER == \"2\") selected #end>unknown</option>\r\n                            <option value=\"1\" #if($!customerData.GENDER == \"1\") selected #end>Ms.</option>\r\n                            <option value=\"0\" #if($!customerData.GENDER == \"0\") selected #end>Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\" value=\"$!customerData.FIRSTNAME\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\" value=\"$!customerData.LASTNAME\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\" value=\"$!customerData.EMAIL\"></td></tr>\r\n                  <tr><td valign=\"top\">eMail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" #if($!customerData.MAILTYPE == \"1\") checked #end>HTML<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\" #if($!customerData.MAILTYPE == \"0\") checked #end>Text</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Save\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Sorry, your data could not be saved.<br>\r\n                  Please check your settings and try again.</p>\r\n                  <p>&nbsp;</p>\r\n                  <p>Greeting<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(15,'en_profil_confirm','profile english 2/2',1,5,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Your setting have been changed successfully.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter change profile</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>CHANGE DATA</h1>\r\n                  <p>Sorry, your data could not be saved.<br>\r\n                  Please check your settings and try again.</p>\r\n                  <p>&nbsp;</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(16,'en_doi','double-opt-in english 1/3',1,0,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 200px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <form action=\"form.do\">\r\n          <input type=\"hidden\" name=\"agnCI\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnFN\" value=\"en_doi_confirm\">\r\n          <input type=\"hidden\" name=\"agnSUBSCRIBE\" value=\"1\">\r\n          <input type=\"hidden\" name=\"agnMAILINGLIST\" value=\"1\">          \r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 1/3</h1>\r\n                  <p>Subscribe our newsletter here:</p>\r\n                  <table border=0>\r\n                  <tr><td width=\"120\">salutation:</td>\r\n                      <td><select name=\"GENDER\">\r\n                            <option value=\"2\" selected>unknown</option>\r\n                            <option value=\"1\">Ms.</option>\r\n                            <option value=\"0\">Mr.</option>\r\n                          </select></td></tr>\r\n                  <tr><td>firstname:</td>\r\n                      <td><input type=\"text\" name=\"FIRSTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>lastname:</td>\r\n                      <td><input type=\"text\" name=\"LASTNAME\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td>eMail:</td>\r\n                      <td><input type=\"text\" name=\"EMAIL\" style=\"width: 200px;\"></td></tr>\r\n                  <tr><td valign=\"top\">eMail format:</td>\r\n                      <td><input type=\"radio\" name=\"MAILTYPE\" value=\"1\" checked>HTML<br>\r\n                          <input type=\"radio\" name=\"MAILTYPE\" value=\"0\">Text</td></tr>\r\n                  <tr><td colspan=\"2\">&nbsp;</td></tr>\r\n                  <tr><td><input type=\"submit\" value=\"Send\"></td>\r\n                      <td><input type=\"reset\" value=\"Cancel\" onClick=\"javascript:history.back();\"></td></tr>\r\n                  </table>         \r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </form>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(17,'en_doi_confirm','double-opt-in english 2/3',1,3,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 2/3</h1>\r\n                  <p>Your data were saved successfully.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(18,'en_doi_welcome','double-opt-in english 3/3',1,1,0,'<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION 3/3</h1>\r\n                  <h1>Wellcome</h1>\r\n                  <p>Your registration was finished.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>','<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\r\n<HTML>\r\n<head>\r\n<title>newsletter registration</title>\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n<style type=\"text/css\">\r\n<!--\r\nbody, table { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px; }\r\nh1 { font-family: Tahoma, Helvetica, sans-serif; font-size: 16px; }\r\nselect, input { font-family: Tahoma, Helvetica, sans-serif; font-size: 12px;}      \r\nselect { width: 120px; }\r\n-->\r\n</style>\r\n</head>\r\n\r\n<body bgcolor=\"#C0C0C0\" link=\"#bb2233\" vlink=\"#bb2233\" alink=\"#bb2233\">\r\n<table width=\"480\" border=\"0\" align=\"center\" cellpadding=\"2\" cellspacing=\"0\">\r\n  <tr bgcolor=\"#808080\">\r\n    <td bgcolor=\"#808080\">\r\n      <table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" bgcolor=\"#FFFFFF\">\r\n        <tr>\r\n          <td>\r\n          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n            <tr>\r\n              <td width=\"10\">&nbsp;</td>\r\n              <td><h1>NEWSLETTER REGISTRATION ERROR</h1>\r\n                  <p>Sorry, an error occurred.</p>\r\n                  <p>Please try it again.</p>\r\n                  <p>Greetings<br>\r\n                  Your online-Team</p>\r\n                  </td>                                 \r\n              <td width=\"10\">&nbsp;</td>\r\n            </tr>\r\n            <tr>\r\n              <td colspan=\"3\">&nbsp;</td>\r\n            </tr>            \r\n          </table>\r\n          </td>\r\n        </tr>\r\n      </table>\r\n    </td>\r\n  </tr>\r\n</table>\r\n</body>\r\n</html>'),(19,'redirection_check','check if redirect answer\r\n(see explanation at error-form)',1,0,0,'database is ok','error!\r\n\r\nThis form may be used by a surveillance software like Nagios to check if OpenEMM is alive. To call this form use link\r\n\r\nhttp://your.domain.com/form.do?agnCI=1&agnFN=redirection_check\r\n\r\n(WARNING: Please change www.my-company.de to your rdir-link!)');
UNLOCK TABLES;
/*!40000 ALTER TABLE `userform_tbl` ENABLE KEYS */;

--
-- Table structure for table `ws_admin_tbl`
--

DROP TABLE IF EXISTS `ws_admin_tbl`;
CREATE TABLE `ws_admin_tbl` (
  `ws_admin_id` int(22) NOT NULL default '0',
  `username` varchar(50) collate utf8_unicode_ci default NULL,
  `password` varchar(50) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`ws_admin_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Dumping data for table `ws_admin_tbl`
--


/*!40000 ALTER TABLE `ws_admin_tbl` DISABLE KEYS */;
LOCK TABLES `ws_admin_tbl` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `ws_admin_tbl` ENABLE KEYS */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

GRANT DELETE, INSERT, UPDATE, LOCK TABLES, SELECT, ALTER, INDEX, CREATE
TEMPORARY TABLES, DROP, CREATE ON openemm.* TO 'agnitas'@'localhost'
IDENTIFIED BY 'openemm';

FLUSH PRIVILEGES; 

