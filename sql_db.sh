#!/bin/sh

adb_path=$ANDROID_HOME'/platform-tools/adb'

app_package=$1
db_name=$2

dest_path=/tmp/$db_name

while true
do
        rm -rf $dest_path
        $adb_path pull /data/data/$app_package/databases/$db_name $dest_path
        #sqlitebrowser $dest_path
        sqliteman $dest_path
done
