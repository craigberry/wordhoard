#!/bin/csh
cd /Users/wordhoard/server
java -Dlog4j.configurationFile=data/log4j2.xml -Xmx200m -jar lib/wordhoardserver.jar start data &
