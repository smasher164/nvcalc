:<<"::CMDLITERAL"
@ECHO OFF &SETLOCAL
GOTO :CMDSCRIPT
::CMDLITERAL
#|| goto :batch_part
  #!/bin/sh	#
  # get the directory of the sh file being run
  replSrc=`dirname $0 | while read a; do cd $a && pwd && break; done`/src #
  # recursively walk the src subdirectory for .java files #
  srcFiles=`find $replSrc -name '*.java'` #
  # call javac on that array as parameters #
  javac $srcFiles #
  ret=$? #
  if [ $ret -ne 0 ]; then #
  	exit $ret #
  fi #
  # call java -cp REPL under the src subdirectory #
  java -cp $replSrc REPL #
  ret=$? #
  exit $ret #
#exiting the bash part
exit
:CMDSCRIPT
 rem get the directory of the batch file being run
 SET replSrc=%~dp0src
 rem recursively walk the src subdirectory for .java files
 set walk=dir /s/b %replSrc%\*.java
 rem call javac on that array as parameters
 for /f %%i in ('%walk%') do call set "files=%%files%% %%%i"
 javac %files%
 if %errorlevel% NEQ 0 EXIT /b %errorlevel%
 rem call java -cp REPL under the src subdirectory
 java -cp %replSrc% REPL
 exit %errorlevel%