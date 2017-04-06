===================================

The ChainExpander command line tool

===================================



This application allows you to expand a flat chain from the command line. When

the expansion is done, chain elements names are simply displayed on the output

either in text or JSON format. The application accepts options and arguments 

that allow you to set the chain name, the service name and the DACS user name.
You can also activate the optimization for long chains or even switch the 
application to a non verbose mode and redirect the output (the chain

elements) to a file so that it can be processed by another application or

script.



Running the ChainExpander

-------------------------



Before you start the application you must configure the EmaConfig.xml file

to specify the host name of the server (the TREP or Elektron platform) to

which the EMA connects. This is set thanks to the value of the

<ChannelGroup><ChannelList><Channel><Host> node. This value can be a

remote host name or IP address.



The chain exapander application also requires the JAVA_HOME environment

variable to be set. The minimum java version required is 1.8.0.0.



usage: chain-expander [-nv] [-o] [-s service-name] [-u user-name] chain-name


options:

 -j,--json-output-mode     Outputs chain elements in JSON format.

 -nv,--non-verbose-mode    Enables the non verbose mode. Only the chain

                           elements are displayed.

 -o,--optimization         Enables the optimized algorithm for opening

                           long chains. This is not appropriate for short

                           chains (less than 300 elements).

 -s,--service-name <arg>   Elektron or TREP service name

                           Default value: ELEKTRON_DD

 -u,--user-name <arg>      DACS user name

                           Default value: System user name


examples:

 chain-expander -nv -s ELEKTRON_DD 0#.DJI

 chain-expander -nv -j -s ELEKTRON_DD 0#.FTSE

