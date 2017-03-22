#!/bin/ksh

#
# The following batch file assumes the following environment:
# 
#	JAVA_HOME - Root directory of your JDK 8 environment
# 	ELEKTRON_JAVA_HOME - Root directory of your (EMA) Elektron Java API installation
#

BINDIR=build/classes
if [ ! -d $BINDIR ]; then
  mkdir -p $BINDIR
fi

JAVAC=$JAVA_HOME/bin/javac
JAVADOC=$JAVA_HOME/bin/javadoc
JAVADOCDIR=dist/javadoc
EMA_HOME=$ELEKTRON_JAVA_HOME/Ema
ETA_HOME=$ELEKTRON_JAVA_HOME/Eta

CLASSPATH=./src:$EMA_HOME/Libs/ema.jar:$EMA_HOME/Libs/SLF4J/slf4j-1.7.12/slf4j-api-1.7.12.jar:$EMA_HOME/Libs/SLF4J/slf4j-1.7.12/slf4j-jdk14-1.7.12.jar:$EMA_HOME/Libs/apache/commons-configuration-1.10.jar:$EMA_HOME/Libs/apache/commons-logging-1.2.jar:$EMA_HOME/Libs/apache/commons-lang-2.6.jar:$EMA_HOME/Libs/apache/org.apache.commons.collections.jar:$ETA_HOME/Libs/upa.jar:$ETA_HOME/Libs/upaValueAdd.jar:lib/commons-cli-1.4.jar

function build
{
   printf "Building the EMA Chain Toolkit...\n"
   $JAVAC -d $BINDIR src/com/thomsonreuters/platformservices/ema/utils/chain/*.java; ret=$?
   if [ $ret != 0 ]; then
      printf "Build failed.  Exiting\n"
      exit $ret
   fi

   printf "Building the EMA Chain Toolkit javadoc...\n"
   $JAVADOC -d $JAVADOCDIR -quiet -sourcepath src com.thomsonreuters.platformservices.ema.utils.chain; ret=$?
   if [ $ret != 0 ]; then
      printf "Build failed.  Exiting\n"
      exit $ret
   fi

   printf "Building the ChainExpander and EmaChainToolkitExample applications...\n"
   $JAVAC -d $BINDIR src/com/thomsonreuters/platformservices/examples/*.java; ret=$?
   if [ $ret != 0 ]; then
      printf "Build failed.  Exiting\n"
      exit $ret
   fi
}

build

printf "\nDone.\n"
