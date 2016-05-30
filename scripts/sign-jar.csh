#!/bin/csh

###	Signs a third-party library jar file for Web Start deployment.
#
#	sign-jar jarfile
#
#	jarfile = name of library file. E.g., "hibernate3.jar".

set jarfile=$1

echo "Enter store password for keystore:"
set storepass = $<

echo "Enter key password for alias nujarsigner:"
set keypass = $<

cp lib/$jarfile client
jarsigner -keystore private/wordhoard.store -storepass $storepass \
	-keypass $keypass client/$jarfile nujarsigner
