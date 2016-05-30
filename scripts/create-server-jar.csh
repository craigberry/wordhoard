#!/bin/csh

###	Creates the "wordhoardserver.jar" file for server deployment.

#	Rebuild the "misc/manifest-server" manifest file.

cat >misc/manifest-server <<eof
Main-Class: edu.northwestern.at.wordhoard.server.Server
Class-Path: wordhoardserver.jar
eof
cd lib
foreach jarfile (*.jar)
	echo "            $jarfile" >>../misc/manifest-server
end
cd ..

#	Create the "wordhoardserver.jar" file.

jar -cmf misc/manifest-server wordhoardserver.jar -C bin .
