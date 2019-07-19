#!/bin/bash
db=mdtri.db
log=./ffff-log
/bin/rm -f $log
date
/bin/rm -f ${db}
touch ${db}
echo "Clean"
rm -f meshes/* textures/*
echo "Compile"
javac src/*.java && mv -f src/*class terrain
echo "Cook"
java terrain.TerrainCooker -d mandel_tri.png -i mandel_tri.png -x 64 -y 64 -z 0.125  -s 64 > $log
echo "DB Tex"
java -classpath "./sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f ${db} -d textures >> $log
echo "DB Ter"
java -classpath "./sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f ${db} -d meshes -t terrain >> $log 
echo "Copy"
cp -f ${db} /var/tmp/tiffy/
echo "Done"
date
