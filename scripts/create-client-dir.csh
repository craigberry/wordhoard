#!/bin/csh

###	Creates the "client" directory for deploying the Web Start client.

echo "Enter store password for keystore:"
set storepass = $<

echo "Enter key password for alias nujarsigner:"
set keypass = $<

rm -rf client
mkdir client

cp misc/icon.gif client

cp lib/*.jar client

echo 'Permissions: all-permissions' > manifest-attributes.txt
echo "Codebase: $CODEBASE" >> manifest-attributes.txt
echo 'Application-Name: WordHoard' >> manifest-attributes.txt

cd client
foreach jarfile (*.jar)
	echo "Adding manifest attributes to $jarfile"
	jar ufm $jarfile ../manifest-attributes.txt
	echo "Signing $jarfile"
	jarsigner -keystore ../private/wordhoard.store -storepass $storepass \
		-keypass $keypass $jarfile nujarsigner

end
cd ..

scripts/create-jnlp-file.csh
jar -cf client/wordhoard.jar -C bin .
jar ufm client/wordhoard.jar manifest-attributes.txt
jarsigner -keystore private/wordhoard.store -storepass $storepass \
	-keypass $keypass client/wordhoard.jar nujarsigner
