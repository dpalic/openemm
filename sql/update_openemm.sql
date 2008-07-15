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