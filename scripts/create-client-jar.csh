#!/bin/csh

###	Creates and signs the "wordhoard.jar" file for deployment with Web Start.

echo "Enter store password for keystore:"
set storepass = $<

echo "Enter key password for alias nujarsigner:"
set keypass = $<

scripts/create-jnlp-file.csh
jar -cf client/wordhoard.jar -C bin .
jarsigner -keystore private/wordhoard.store -storepass $storepass \
	-keypass $keypass client/wordhoard.jar nujarsigner
