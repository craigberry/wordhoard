#!/bin/csh

###	Creates and signs the "wordhoard.jar" file for deployment with Web Start.

echo "Enter store password for keystore:"
set storepass = $<

echo "Enter key password for alias nujarsigner:"
set keypass = $<

echo 'Permissions: all-permissions' > manifest-attributes.txt
echo "Codebase: $CODEBASE" >> manifest-attributes.txt
echo 'Application-Name: WordHoard' >> manifest-attributes.txt

scripts/create-jnlp-file.csh
jar -cf client/wordhoard.jar -C bin .
jar ufm client/wordhoard.jar manifest-attributes.txt
jarsigner -keystore private/wordhoard.store -storepass $storepass \
	-keypass $keypass client/wordhoard.jar nujarsigner
