#!/bin/csh

###	Makes the "other-files" directory for the user manual web site.

#	Replace our MySQL root password by "my-password".

sed -e "s/$MYSQL_ROOT_PASSWORD/my-password/g" setup >temp
mv temp setup
cd misc
sed -e "s/$MYSQL_ROOT_PASSWORD/my-password/g" martin.properties >temp
mv temp martin.properties
cd ..

#	Create the "other-files" directory.

echo "" >server/data/log
rm -rf server/lib
rm -rf server/bin
rm -rf other-files
mkdir other-files

#	Create the directory contents.

echo "Copying build.xml"
cp build.xml other-files
echo "Copying full-build.txt"
cp misc/full-build.txt other-files
echo "Creating lib.zip"
zip -rq other-files/lib.zip lib
echo "Creating misc.zip"
zip -rq other-files/misc.zip misc
echo "Copying model.pdf"
cp misc/model.pdf other-files
echo "Creating scripts.zip"
zip -rq other-files/scripts.zip scripts
echo "Creating server.zip"
zip -rq other-files/server.zip server
echo "Copying setup"
cp setup other-files
echo "Copying XML files"
cp data/corpora.xml other-files
cp data/works/sha/ham.xml other-files
cp data/works/spe/faq.xml other-files
cp data/works/ege/IL.xml other-files
echo "Copying martin/report.txt"
cp martin/report.txt other-files
echo "Creating userman.zip"
zip -rq other-files/userman.zip userman
echo "Creating src.zip with license statements"
java edu.northwestern.at.wordhoard.tools.PreprocessDirectoryTree \
	src other-files/src private/licensetexts
cd other-files
zip -rq src.zip src
rm -rf src
cd ..

#	Change "my-password" back to our MySQL root password.

sed -e "s/my-password/$MYSQL_ROOT_PASSWORD/g" setup >temp
mv temp setup
cd misc
sed -e "s/my-password/$MYSQL_ROOT_PASSWORD/g" martin.properties >temp
mv temp martin.properties
cd ..

#	Zip it up.

zip -r other-files other-files
rm -rf other-files
