#!/bin/csh

###	Creates the "wordhoard.jnlp" file for deploying the Web Start client.

cat >client/wordhoard.jnlp <<eof
<?xml version="1.0" encoding="utf-8"?> 

<!-- WordHoard JNLP File --> 

<!--    DANGER WILL ROBINSON!!!!

        Thanks to the brain-dead XML parser Apple uses in their WebStart
        application, the spaces preceeding the "/>" you see below are
        REQUIRED!!!!! DO NOT FORGET THEM!!!!

-->

<jnlp spec="1.0+" 
    codebase="$CODEBASE"
    href="wordhoard.jnlp">
    <information>
        <title>WordHoard</title>
        <vendor>Northwestern University</vendor>
        <homepage href="$HOMEPAGE" />
        <description>WordHoard</description>
        <icon href="icon.gif" />
        <shortcut online="true">
            <desktop />
            <menu submenu="Northwestern University" />
        </shortcut>
    </information>
    <resources>
        <property name="apple.awt.application.name" value="WordHoard" />
        <property name="apple.laf.useScreenMenuBar" value="true" />
        <j2se version="1.8*" initial-heap-size="100m" max-heap-size="640m" />
        <jar href="wordhoard.jar" />
eof

cd client
foreach jarfile (*.jar)
	if ($jarfile != "wordhoard.jar") then
		echo '        <jar href="'$jarfile'" />' >>wordhoard.jnlp
	endif
end
cd ..

cat >>client/wordhoard.jnlp <<eof
    </resources>
    <security>
        <all-permissions />
    </security>
    <application-desc main-class="edu.northwestern.at.wordhoard.swing.WordHoard" />
</jnlp> 
eof

if (! -e bin/JNLP-INF) then
   mkdir bin/JNLP-INF
endif
cp client/wordhoard.jnlp bin/JNLP-INF/APPLICATION.JNLP
