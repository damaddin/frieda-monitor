#!/bin/bash

if [ ! -f maven-metadata.xml ]; then
    	echo -e "Local maven meta data file was not found!\nDownloading it from NEXUS...\n"
    	curl http://svn:8081/nexus/content/repositories/snapshots/com/mstsoft/frieda-monitor/1.0-SNAPSHOT//maven-metadata.xml > maven-metadata.xml
fi

echo -e "\nFetching our current version of jar\n";
OUR_CURRENT_VERSION=$(grep "<value>" maven-metadata.xml | sed 's/ //g' | sed 's/<value>//g' | sed 's/<\/value>//g' | tail -n 1)

echo -e "Deleting existing maven metadata file\n"
rm maven-metadata.xml

echo -e "Downloading new maven metadata file from nexus server...\n"
curl --request GET http://svn:8081/nexus/service/local/repositories/snapshots/content/com/mstsoft/frieda-monitor/1.0-SNAPSHOT/maven-metadata.xml > maven-metadata.xml


# fetching version of last jar
VERSION=$(grep "<value>" maven-metadata.xml | sed 's/ //g' | sed 's/<value>//g' | sed 's/<\/value>//g' | tail -n 1)

echo -e "\nChecking if our version of jar is up to date\n"
if [ "$OUR_CURRENT_VERSION" != "$VERSION" ]; then
		echo -e "Oh, no!\nBuild is old!\nDownloading new one...\n";
		curl http://svn:8081/nexus/content/repositories/snapshots/com/mstsoft/frieda-monitor/1.0-SNAPSHOT/frieda-monitor-$VERSION-jar-with-dependencies.jar > frieda-$VERSION.jar
else
    echo -e "Build is up to date!\n";
    
    if [ ! -f frieda-$OUR_CURRENT_VERSION.jar ]; then
    		echo -e "Local jar was not found!\nDownloading it from NEXUS...\n"
    		curl http://svn:8081/nexus/content/repositories/snapshots/com/mstsoft/frieda-monitor/1.0-SNAPSHOT/frieda-monitor-$OUR_CURRENT_VERSION-jar-with-dependencies.jar > frieda-$OUR_CURRENT_VERSION.jar
    fi
fi

echo -e "\nKilling frieda, if it is already running\n"
kill $(ps aux | grep '[f]rieda.*.jar' | awk '{print $2}')

echo "Running frieda-$VERSION.jar"
java -jar frieda-$VERSION.jar