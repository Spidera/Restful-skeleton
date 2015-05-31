#!/usr/bin/env bash
export PATH=$PATH:/usr/local/mysql/bin/
mysql --defaults-extra-file=./sqlScripts/mysql_config.cnf < ./sqlScripts/create_project_db.sql

#Add here table creation scripts
mysql --defaults-extra-file=./sqlScripts/mysql_config.cnf < ./sqlScripts/create_user_table.sql
