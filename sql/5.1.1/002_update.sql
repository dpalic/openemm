von 5.1 auf 5.1.1
-- 
-- Neue Rechte für User agnitas
-- 
GRANT DELETE, INSERT, UPDATE, LOCK TABLES, SELECT, ALTER, INDEX, CREATE TEMPORARY TABLES, DROP, CREATE ON openemm.* TO 'agnitas'@'localhost' IDENTIFIED BY 'openemm';

FLUSH PRIVILEGES; 