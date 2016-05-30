#!/bin/csh

###	Imports all of the tables into the MySQL "martin" database.

chmod +r martin/*
scripts/import.csh wordclass NUPOS_WordClass
scripts/import.csh pos NUPOS_EnglishGreek
scripts/import.csh english NUPOSTrainingData
scripts/import.csh greek NUPOS_GreekData

