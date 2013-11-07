--
-- Table structure for openemm_cms database
--

--
-- Tables to simulate sequences in MySQL
--
create table cm_tbl_seq (value int not null) type=MYISAM;
create table cm_type_tbl_seq (value int not null) type=MYISAM;
create table cm_mailing_bind_tbl_seq (value int not null) type=MYISAM;
create table cm_text_version_tbl_seq (value int not null) type=MYISAM;
create table cm_location_tbl_seq (value int not null) type=MYISAM;
create table cm_content_tbl_seq (value int not null) type=MYISAM;
create table cm_template_tbl_seq (value int not null) type=MYISAM;
create table cm_template_mail_bind_tbl_seq (value int not null) type=MYISAM;
create table cm_media_file_tbl_seq (value int not null) type=MYISAM;

insert into cm_tbl_seq values(0);
insert into cm_type_tbl_seq values(0);
insert into cm_mailing_bind_tbl_seq values (0);
insert into cm_text_version_tbl_seq values(1);
insert into cm_location_tbl_seq values(0);
insert into cm_content_tbl_seq values(0);
insert into cm_template_tbl_seq values(0);
insert into cm_template_mail_bind_tbl_seq values(0);
insert into cm_media_file_tbl_seq values(0);

--
-- Table for storing CM Templates
--
DROP TABLE IF EXISTS `cm_template_tbl`;
CREATE TABLE  `cm_template_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL default '0',
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL default ' ',
  `description` varchar(255) collate utf8_unicode_ci NOT NULL default ' ',
  `content` longblob NOT NULL ,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for binding CM template to mailing
--
DROP TABLE IF EXISTS `cm_template_mailing_bind_tbl`;
CREATE TABLE  `cm_template_mailing_bind_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `mailing_id` int(10) unsigned NOT NULL,
  `cm_template_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for storing media files for CM templates and content modules
--
DROP TABLE IF EXISTS `cm_media_file_tbl`;
CREATE TABLE `cm_media_file_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `cm_template_id` int(10) unsigned NOT NULL default '0',
  `content_module_id` int(10) unsigned NOT NULL default '0',
  `company_id` int(10) unsigned NOT NULL default '0',
  `media_name` varchar(255) collate utf8_unicode_ci NOT NULL default '',
  `content` longblob,
  `media_type` int(10) unsigned NOT NULL default '0',
  `mime_type` varchar(255) collate utf8_unicode_ci default NULL,
  `cmtId` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for storing content module types
--
DROP TABLE IF EXISTS `cm_type_tbl`;
CREATE TABLE `cm_type_tbl` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `read_only` tinyint(1) NOT NULL,
  `is_public` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for storing content modules
--
DROP TABLE IF EXISTS `cm_content_module_tbl`;
CREATE TABLE `cm_content_module_tbl` (
  `id` int(10) unsigned NOT NULL,
  `company_id` int(10) unsigned NOT NULL,
  `shortname` varchar(255) collate utf8_unicode_ci NOT NULL,
  `description` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table structure for storing content modules placeholders content
--
DROP TABLE IF EXISTS `cm_content_tbl`;
CREATE TABLE  `cm_content_tbl` (
  `id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  `tag_name` varchar(255) collate utf8_unicode_ci NOT NULL,
  `content` longtext collate utf8_unicode_ci NOT NULL,
  `tag_type` int(11) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for binding content modules to mailings
--
DROP TABLE IF EXISTS `cm_mailing_bind_tbl`;
CREATE TABLE  `cm_mailing_bind_tbl` (
  `id` int(10) unsigned NOT NULL,
  `mailing_id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for storing CMs position inside CM template
-- (and for storing CM target group assignments)
--
DROP TABLE IF EXISTS `cm_location_tbl`;
CREATE TABLE  `cm_location_tbl` (
  `id` int(10) unsigned NOT NULL,
  `mailing_id` int(10) unsigned NOT NULL,
  `cm_template_id` int(10) unsigned NOT NULL,
  `content_module_id` int(10) unsigned NOT NULL,
  `dyn_name` varchar(100) collate utf8_unicode_ci NOT NULL,
  `dyn_order` int(10) unsigned NOT NULL,
  `target_group_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Table for storing text version alternative by admin
--

DROP TABLE IF EXISTS `cm_text_version_tbl`;
CREATE TABLE  `cm_text_version_tbl` (
  `id` int(10) unsigned NOT NULL,
  `admin_id` int(10) unsigned NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Default text version
--
LOCK TABLES `cm_text_version_tbl` WRITE;
/*!40000 ALTER TABLE `cm_text_version_tbl` DISABLE KEYS */;
INSERT INTO `cm_text_version_tbl` (`id`, `admin_id`, `text`) VALUES (1, 0,
'Dear Reader,

this e-mail was created in HTLM format only. Almost every e-mail client supports HTML mails, but it could be that your client is not permitted to display HTML mails.

However, you can view this e-mail in HTML format in your browser:

http://localhost:8080/form.do?agnCI=1&agnFN=fullview&agnUID=##AGNUID##

To unsubscribe from the list of this mailing please click this link:

http://localhost:8080/form.do?agnCI=1&agnFN=unsubscribe&agnUID=##AGNUID##'
);
/*!40000 ALTER TABLE `cm_text_version_tbl` ENABLE KEYS */;
UNLOCK TABLES;

GRANT DELETE, INSERT, UPDATE, LOCK TABLES, SELECT, ALTER, INDEX, CREATE
TEMPORARY TABLES, DROP, CREATE ON openemm_cms.* TO 'agnitas'@'localhost'
IDENTIFIED BY 'openemm';

FLUSH PRIVILEGES; 
