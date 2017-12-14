# ChainExpander example

## Table of Content

* [Overview](#overview)

* [Disclaimer](#disclaimer)

* [Prerequisites](#prerequisites)

* [Applications design](#application-design)

* [Building the _ChainExpander_ and the _ElektronObjects example SDK_](#building-the-chainexpander)

* [Running the _ChainExpander_](#running-the-chainexpander)

* [Running the _ChainStepByStepExample_](#running-the-chainstepbystepexample)

* [Troubleshooting](#troubleshooting)

* [Solution Code](#solution-code)

## <a id="overview"></a>Overview
This project is one of the many learning materials published by Thomson Reuters to help developers learning Thomson Reuters APIs. It contains two Java example applications and the _ValueAddObjectsForEMA_ example library that demonstrate the different concepts explained in the [Simple Chain objects for EMA](https://developers.thomsonreuters.com/article/simple-chain-objects-ema-part-1) article published on the [Thomson Reuters Developer Community portal](https://developers.thomsonreuters.com). These applications are based on the Java edition of the Elektron Message API that is one of the APIs of the Thomson Reuters Elektron SDK. Please consult this [Elektron SDK page](https://developers.thomsonreuters.com/elektron/elektron-sdk-java) for learning materials and documentation about this API.

For any question or comment related to this article please use the _ADD YOUR COMMENT_ section at the bottom of this page or post a question on the [EMA Q&A Forum](https://community.developers.thomsonreuters.com/spaces/72/index.html) of the Developer Community.

_**Note:** To be able to ask questions and to benefit from the full content available on the [TR Developer Community portal](https://developers.thomsonreuters.com) we recommend you to [register here](https://login.thomsonreuters.com/iamui/UI/createUser?app_id=DevPlatform&realm=DevPlatform) or [login here]( https://developers.thomsonreuters.com/iam/login?destination_path=Lw%3D%3D)._

## <a id="disclaimer"></a>Disclaimer
The example applications presented here and the _ValueAddObjectsForEMA_ example library have been written by Thomson Reuters for the only purpose of illustrating articles published on the Thomson Reuters Developer Community. These example applications and the _ValueAddObjectsForEMA_ example library have not been tested for a usage in production environments. Thomson Reuters cannot be held responsible for any issues that may happen if these example applications, the _ValueAddObjectsForEMA_ library or the related source code is used in production or any other client environment.

## <a id="prerequisites"></a>Prerequisites

Required software components:

* [Elektron Message API](https://developers.thomsonreuters.com/elektron/elektron-sdk-java) (1.1.0 or greater) - Thomson Reuters interface to the Elektron Market Data environment
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit - version 8

## <a id="application-design"></a> Applications design
The source code of these example applications has been designed for easy reuse in other example applications. It is made of three distinct parts:

### The *_ValueAddObjectsForEMA_* library
This module implements the complete logic and algorithms explained in the [Simple Chain objects for EMA - Part1](https://developers.thomsonreuters.com/article/simple-chain-objects-ema-part-1) article. These features are implemented by the _Chain_ objects defined by the _com.thomsonreuters.platformservices.elektron.objects.chain_ package. The source code of this package is reused by other Thomson Reuters example applications. The *_ValueAddObjectsForEMA_* example library provides other reusable objects presented by other articles published on the Thomson Reuters Developer Community.   

For more details about the _Chain_ objects usage, please refer to the [Simple Chain objects for EMA - Part2](https://developers.thomsonreuters.com/article/simple-chain-objects-ema-part-2) article and the *_ValueAddObjectsForEMA_* javadoc. 

### The _ChainStepByStepExample_ application
This example application that is part of the *_ValueAddObjectsForEMA_* library demonstrates the Chain objects capabilities and how to use them. The application starts by creating an EMA OmmConsumer and uses it in with Chain objects in several individual steps that demonstrate the implemented features. Before each step, explanatory text is displayed and you are prompted to press to start the step.

### The _ChainExpander_ tool

The _ChainExpander_ is a command line tool you can use to expand a flat chain from the command line. When the expansion is done, chain elements names are simply displayed on the output either in text or JSON format. The application accepts options and arguments that allow you to set the chain name, the service name and the DACS user name. You can also activate the optimization for long chains or even switch the application to a non verbose mode and redirect the output (the chain elements) to a file so that it can be processed by another application or script. This is a good example of a simple but real application that relies on the _ValueAddObjectsForEMA_ library and the _FlatChain_ class.

## <a id="building-the-chainexpander" name="building-the-chainexpander"></a>Building the _ChainExpander_ and the _ValueAddObjectsForEMA_

### Make sure the _ValueAddObjectsForEMA_ submodule is downloaded

Before you start building the project make sure the _ValueAddObjectsForEMA_ submodule is properly downloaded. To this aim list the files of the _./ValueAddObjectsForEMA_ directory. This directory should contain several script and source files. If it's empty, please run the following Git commands in the root directory of your _ChainExpander_ project: 

    git submodule init
    git submodule update

### Set the required environment variables

This package includes some convenient files which will enable the developer to quickly build and run the example application. These scripts depend on the *JAVA_HOME* and *ELEKTRON_JAVA_HOME* environment variables. These variables must be set appropriately before you run any of the *build* or *run* scripts.
* *JAVA_HOME* must be set with the root directory of your JDK 8 environment.
* *ELEKTRON_JAVA_HOME* must be set with the root directory of your (EMA) Elektron Java API installation

### Change the service name and DACS user name if need be

The *ChainStepByStepExample.java* file of the *_ValueAddObjectsForEMA_* contains two hardcoded values that you may want to change depending on your the TREP or Elektron platform you use. These values indicate:

* The **service name** used to subscribe to chains records: The hardcoded value is "ELEKTRON_DD". This value can be changed thanks to the *ChainStepByStepExample.serviceName* class member in the *ChainStepByStepExample.java* file.  
* The **DACS user name** used to connect the application to the infrastructure. If the Data Access Control System (DACS) is activated on your TREP and if your DACS username is different than your operating system user name, you will need to set it thanks to the *ChainStepByStepExample.dacsUserName* class member of the *ChainStepByStepExample.java* file.

The _ChainExpander_ application doesn't need these changes as the **service name** and the  **DACS user name** can be passed as command line arguments of the application.

### Run the *build* script

Once these environment variables setup and hardcoded values are properly set, you must run the *build.bat* or the *build.ksh* script to build the _ValueAddObjectsForEMA_ and the _ChainExpander_ application.

**Note:** Alternatively to the build scripts, you can use the NetBeans IDE to build the applications. NetBeans 8.2 project files are provided with the applications source code.    

## <a id="running-the-chainexpander"></a>Running the *ChainExpander*

**Before you start the application** you must configure the *EmaConfig.xml file* to specify the host name of the server (the TREP or Elektron platform) to which the EMA connects. This is set thanks to the value of the *\<ChannelGroup>\<ChannelList>\<Channel>\<Host>* node. This value can be a remote host name or IP address.

To start the *ChainExpander* run the *chain-expander.bat* or the *chain-expander.ksh* script. These scripts depend on the *JAVA_HOME* and *ELEKTRON_JAVA_HOME* environment variables that must have been defined for the build scripts.

### Usage

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
                               Default value: System user name
### Examples

    > chain-expander -s ELEKTRON_DD 0#.DJI

    > chain-expander -nv -s ELEKTRON_DD 0#.DJI

    > chain-expander -nv -j -s ELEKTRON_DD 0#.FTSE



### Expected output

This is an example of the *ChainExpander* output:

      >>> Input parameters:
            chain-name  : "0#.DJI"
            service-name: "ELEKTRON_DD"
            user-name   : ""
            optimization: disabled
            non-verbose : disabled
      >>> Connecting to the infrastructure...
      >>> Expanding the chain. Please wait...
            0#.DJI[0] = .DJI
            0#.DJI[1] = AAPL.OQ
            0#.DJI[2] = AXP.N
            0#.DJI[3] = BA.N
            0#.DJI[4] = CAT.N
            0#.DJI[5] = CSCO.OQ
            0#.DJI[6] = CVX.N
            0#.DJI[7] = DD.N
            0#.DJI[8] = DIS.N
            0#.DJI[9] = GE.N
            0#.DJI[10] = GS.N
            0#.DJI[11] = HD.N
            0#.DJI[12] = IBM.N
            0#.DJI[13] = INTC.OQ
            0#.DJI[14] = JNJ.N
            0#.DJI[15] = JPM.N
            0#.DJI[16] = KO.N
            0#.DJI[17] = MCD.N
            0#.DJI[18] = MMM.N
            0#.DJI[19] = MRK.N
            0#.DJI[20] = MSFT.OQ
            0#.DJI[21] = NKE.N
            0#.DJI[22] = PFE.N
            0#.DJI[23] = PG.N
            0#.DJI[24] = TRV.N
            0#.DJI[25] = UNH.N
            0#.DJI[26] = UTX.N
            0#.DJI[27] = V.N
            0#.DJI[28] = VZ.N
            0#.DJI[29] = WMT.N
            0#.DJI[30] = XOM.N


## <a id="running-the-chainstepbystepexample"></a>Running the _ChainStepByStepExample_

Please refer to the related [README](https://github.com/TR-API-Samples/Example.EMA.Java.ValueAddObjectsForEMA/blob/master/Chain-README.md) file of the _ValueAddObjectsForEMA_.

## <a id="troubleshooting"></a>Troubleshooting

**Q: When I build or run the application, it fails with an error like:**

    The system cannot find the path specified

**A:** The JAVA_HOME environment variable is not set, or set to the wrong path. See the [Building the ChainExpander](#building-the-chainexpander) section above.

<br>

**Q: When I build the application, I get "package ... does not exist" and "cannot find symbol" errors like:**

    Building the Chain Expander example...
    src\com\thomsonreuters\platformservices\ema\utils\chain\ChainRecord.java:3: error: package com.thomsonreuters.ema.access does not exist
    import com.thomsonreuters.ema.access.AckMsg;
                                        ^
    src\com\thomsonreuters\platformservices\ema\utils\chain\ChainRecord.java:4: error: package com.thomsonreuters.ema.access does not exist
    import com.thomsonreuters.ema.access.Data;
                                        ^
    src\com\thomsonreuters\platformservices\ema\utils\chain\ChainRecord.java:5: error: package com.thomsonreuters.ema.access does not exist
    import com.thomsonreuters.ema.access.DataType;
                                        ^
    src\com\thomsonreuters\platformservices\ema\utils\chain\ChainRecord.java:6: error: package com.thomsonreuters.ema.access.DataType does not exist
    import com.thomsonreuters.ema.access.DataType.DataTypes;
                                                 ^
    src\com\thomsonreuters\platformservices\ema\utils\chain\ChainRecord.java:7: error: package com.thomsonreuters.ema.access does not exist
    import com.thomsonreuters.ema.access.EmaFactory;
                                    ^
**A:** The ELEKTRON_JAVA_HOME environment variable is not set, or set to the wrong path.  See the [Building the ChainExpander](#building-the-chainexpander) section above.

<br>

**Q: When I run the application, I get a JNI error with a NoClassDefFoundError exception like:**

    Running the Chain Expander example...
    Error: A JNI error has occurred, please check your installation and try again
    Exception in thread "main" java.lang.NoClassDefFoundError: com/thomsonreuters/ema/access/OmmException
            at java.lang.Class.getDeclaredMethods0(Native Method)
            at java.lang.Class.privateGetDeclaredMethods(Class.java:2701)
            at java.lang.Class.privateGetMethodRecursive(Class.java:3048)
            at java.lang.Class.getMethod0(Class.java:3018)
            at java.lang.Class.getMethod(Class.java:1784)
            at sun.launcher.LauncherHelper.validateMainClass(LauncherHelper.java:544)
            at sun.launcher.LauncherHelper.checkAndLoadMain(LauncherHelper.java:526)

    Caused by: java.lang.ClassNotFoundException: com.thomsonreuters.ema.access.OmmException
            at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
            at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
            at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
            at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
            ... 7 more

**A:** The ELEKTRON_JAVA_HOME environment variable is not set, or set to the wrong path. See the [Building the ChainExpander](#building-the-chainexpander) section above.

<br>

**Q: The application is stuck after the *">>> Creating the OmmConsumer"* message is displayed.**

After a while the application displays an error like: 

      ERROR - Can't create the OmmConsumer because of the following error: login failed (timed out after waiting 45000 milliseconds) for 10.2.43.49:14002)

**A:** Verify that you properly set the *<host>* parameter in the EmaConfig.xml file (see [Running the ChainExpander](#running-the-chainexpander) for more). 
Ultimately, ask your TREP administrator to help you to investigate with TREP monitoring tools like adsmon.

 
## <a id="solution-code"></a>Solution Code

The ChainExpander was developed using the [Elektron SDK Java API](https://developers.thomsonreuters.com/elektron/elektron-sdk-java) that is available for download [here](https://developers.thomsonreuters.com/elektron/elektron-sdk-java/downloads).

### Built With

* [Elektron Message API](https://developers.thomsonreuters.com/elektron/elektron-sdk-java)
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [NetBeans 8.2](https://netbeans.org/) - IDE for Java development

### <a id="contributing"></a>Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

### <a id="authors"></a>Authors

* **Olivier Davant** - Release 1.1.  *ValueAddObjectsForEma Integration*

### <a id="license"></a>License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
