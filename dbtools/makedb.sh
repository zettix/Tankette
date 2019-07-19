#!/bin/bash
date
/bin/rm -f sin.sb
touch sin.db
echo "Clean"
rm -f meshes/* textures/*
echo "Compile"
javac src/*.java && mv -f src/*class terrain
echo "Cook"
java terrain.TerrainCooker -d sin -i mandel_tri.png -x 16 -y 16 -z 100 -s 256 > /dev/null
echo "DB Tex"
java -classpath "../sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f sin.db -d textures > /dev/null
echo "DB Ter"
 java -classpath "../sqlite/sqlite-jdbc-3.19.3.jar:." terrain.SqlPusher -f sin.db -d meshes -t terrain > /dev/null
echo "Copy"
 cp -f sin.db /var/tmp/tiffy/
echo "Done"
date
