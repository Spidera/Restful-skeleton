# change 'projectDb' to desired database name
DROP DATABASE IF EXISTS projectDb;
CREATE DATABASE projectDb;
# change 'admin' and '1234' to desired user/password for this database
GRANT ALL ON projectDb.* to 'admin'@'localhost' identified by '1234';
