@echo off
setlocal

rem
rem The following batch file assumes the following environment:
rem 
rem		JAVA_HOME - Root directory of your JDK 8 environment
rem 	ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
rem		

set SCRIPT=%0
set BINDIR=build\classes
set JAVADOCDIR=dist\javadoc
set "EMA_HOME=%ELEKTRON_JAVA_HOME%\Ema"
set "ETA_HOME=%ELEKTRON_JAVA_HOME%\Eta"

rem Java Compiler.  Default compiler in path.
set JAVAC="%JAVA_HOME%\bin\javac"
set JAVADOC="%JAVA_HOME%\bin\javadoc"



set CLASSPATH=%BINDIR%;%EMA_HOME%\Libs\ema.jar;%EMA_HOME%\Libs\SLF4J\slf4j-1.7.12\slf4j-api-1.7.12.jar;%EMA_HOME%\Libs\SLF4J\slf4j-1.7.12\slf4j-jdk14-1.7.12.jar;%EMA_HOME%\Libs\apache\commons-configuration-1.10.jar;%EMA_HOME%\Libs\apache\commons-logging-1.2.jar;%EMA_HOME%\Libs\apache\commons-lang-2.6.jar;%EMA_HOME%\Libs\apache\org.apache.commons.collections.jar;%ETA_HOME%\Libs\upa.jar;%ETA_HOME%\Libs\upaValueAdd.jar;lib\commons-cli-1.4.jar;lib\json-20160810.jar

if not exist %BINDIR% (mkdir %BINDIR%)

echo Building the EMA Chain Toolkit...
%JAVAC% -d %BINDIR% src\com\thomsonreuters\platformservices\ema\utils\chain\*.java
if %errorlevel% neq 0 goto :ERROR

echo Building the EMA Chain Toolkit javadoc...
%JAVADOC% -d %JAVADOCDIR% -quiet -sourcepath src com.thomsonreuters.platformservices.ema.utils.chain
if %errorlevel% neq 0 goto :ERROR


echo Building the ChainExpander and EmaChainToolkitExample applications...
%JAVAC% -Xlint -d %BINDIR% src\com\thomsonreuters\platformservices\examples\*.java
if %errorlevel% neq 0 goto :ERROR

goto :DONE

:ERROR
echo.
echo Build failed.  Exiting.
goto :EOF

:DONE
echo.
echo Done.
