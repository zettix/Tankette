#!/bin/bash
db=sin.db
log=/var/tmp/ffff-log
/bin/rm -f $log
date
/bin/rm -f ${db}
touch ${db}.db
echo "Clean"
rm -f meshes/* textures/*
echo "Compile"
javac src/*.java && mv -f src/*class terrain
echo "Cook"
java terrain.TerrainCooker -d sin -i mandel_tri.png -x 16 -y 16 -z 50.0  -s 256 > $log
echo "DB Tex"
java -classpath "../sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f ${db} -d textures >> $log
echo "DB Ter"
java -classpath "../sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f ${db} -d meshes -t terrain >> $log 
echo "Copy"
cp -f ${db} /var/tmp/tiffy/
echo "Done"
date
