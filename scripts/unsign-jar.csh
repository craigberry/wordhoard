#!/bin/csh

###	Unsigns jar files.
#
#	unsign-jar jar_file ...
#
#	jar_file ... = Path(s) to jar file(s) to unsign.

set tmpdir = "/tmp/unsign-jar-temp-dir"

foreach file ($argv[*])
	echo Unsigning $file
	rm -rf $tmpdir
	mkdir $tmpdir
	cp $file $tmpdir
	set saved_dir = $cwd
	cd $tmpdir
	jar -xf *.jar
	rm *.jar
	rm -rf META-INF
	jar -cf xxx.jar *
	cd $saved_dir
	mv $tmpdir/xxx.jar $file
end
