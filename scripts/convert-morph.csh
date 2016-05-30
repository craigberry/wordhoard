#!/bin/csh

###	Runs ConvertMorph.
#
#	convert-morph.csh in rules data
#
#	in = Path to a MorphAdorner XML output file or a directory of such files. If a directory
#	path is specified, all files in the directory whose names end in ".xml" are processed.
#
#	rules = Path to a ConvertMorph XML rules file.
#
#	data = Path to WordHoard data directory.

java -Xmx2g edu.northwestern.at.wordhoard.tools.cm.ConvertMorph $1 $2 $3