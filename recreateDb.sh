#!/usr/bin/env bash
export PATH=$PATH:/usr/local/mysql/bin/
mysql --defaults-extra-file=./sqlScripts/mysql_config.cnf < ./sqlScripts/create_minyan_db.sql
mysql --defaults-extra-file=./sqlScripts/mysql_config.cnf < ./sqlScripts/create_user_table.sql
