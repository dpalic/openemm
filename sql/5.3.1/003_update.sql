von 5.3.0 auf 5.3.1
-- 
-- new tabels for webservices
-- 
CREATE TABLE `ws_admin_tbl` (
  `ws_admin_id` int(22),
  `username` varchar(50) character set utf8 collate utf8_unicode_ci,
  `password` varchar(50) character set utf8 collate utf8_unicode_ci,
  PRIMARY KEY  (`ws_admin_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;