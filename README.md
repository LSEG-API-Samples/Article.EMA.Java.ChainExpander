# ChainExpander example

## Table of Content

* [Overview](#overview)

* [Disclaimer](#disclaimer)

* [Prerequisites](#prerequisites)

* [Applications design](#application-design)

* [Building the _ChainExpander_ and the Refinitiv Real-Time Objects example SDK_](#building-the-chainexpander)

* [Running the _ChainExpander_](#running-the-chainexpander)

* [Running the _ChainStepByStepExample_](#running-the-chainstepbystepexample)

* [Troubleshooting](#troubleshooting)

* [Solution Code](#solution-code)

## <a id="overview"></a>Overview
This project is one of the many learning materials published by Refinitiv to help developers learning Refinitiv APIs. It contains two Java example applications and the _ValueAddObjectsForEMA_ example library that demonstrate the different concepts explained in the [Simple Chain objects for EMA](https://developers.refinitiv.com/article/simple-chain-objects-ema-part-1) article published on the [Refinitiv Developer Community portal](https://developers.refinitiv.com). These applications are based on the Java edition of the Enterprise Message API that is one of the APIs of the Refinitiv Real-Time SDK. Please consult this [Refinitiv Real-Time SDK page](https://developers.refinitiv.com/elektron/elektron-sdk-java) for learning materials and documentation about this API.

For any question or comment related to this article please use the _ADD YOUR COMMENT_ section at the bottom of this page or post a question on the [EMA Q&A Forum](https://community.developers.refinitiv.com/spaces/72/index.html) of the Developer Community.

_**Note:** To be able to ask questions and to benefit from the full content available on the [TR Developer Community portal](https://developers.refinitiv.com) we recommend you to [register here](https://login.refinitiv.com/iamui/UI/createUser?app_id=DevPlatform&realm=DevPlatform) or [login here]( https://developers.refinitiv.com/iam/login?destination_path=Lw%3D%3D)._

## <a id="disclaimer"></a>Disclaimer
The example applications presented here and the _ValueAddObjectsForEMA_ example library have been written by Refinitiv for the only purpose of illustrating articles published on the Refinitiv Developer Community. These example applications and the _ValueAddObjectsForEMA_ example library have not been tested for a usage in production environments. Refinitiv cannot be held responsible for any issues that may happen if these example applications, the _ValueAddObjectsForEMA_ library or the related source code is used in production or any other client environment.

## <a id="prerequisites"></a>Prerequisites

Required software components:

* [Enterprise Message API](https://developers.refinitiv.com/elektron/elektron-sdk-java) (2.0 or greater) - Refinitiv interface to the Refinitiv Real-Time Market Data environment
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit - version 8

## <a id="application-design"></a> Applications design
The source code of these example applications has been designed for easy reuse in other example applications. It is made of three distinct parts:

### The *_ValueAddObjectsForEMA_* library
This module implements the complete logic and algorithms explained in the [Simple Chain objects for EMA - Part1](https://developers.refinitiv.com/article/simple-chain-objects-ema-part-1) article. These features are implemented by the _Chain_ objects defined by the _com.refinitiv.platformservices.elektron.objects.chain_ package. The source code of this package is reused by other Refinitiv example applications. The *_ValueAddObjectsForEMA_* example library provides other reusable objects presented by other articles published on the Refinitiv Developer Community.   

For more details about the _Chain_ objects usage, please refer to the [Simple Chain objects for EMA - Part2](https://developers.refinitiv.com/article/simple-chain-objects-ema-part-2) article and the *_ValueAddObjectsForEMA_* javadoc. 

### The _ChainExpander_ tool

The _ChainExpander_ is a command line tool you can use to expand a flat chain from the command line. When the expansion is done, chain elements names are simply displayed on the output either in text or JSON format. The application accepts options and arguments that allow you to set the chain name, the service name and the DACS user name. You can also activate the optimization for long chains or even switch the application to a non verbose mode and redirect the output (the chain elements) to a file so that it can be processed by another application or script. This is a good example of a simple but real application that relies on the _ValueAddObjectsForEMA_ library and the _FlatChain_ class.

## <a id="building-the-chainexpander" name="building-the-chainexpander"></a>Building the _ChainExpander_ and the _ValueAddObjectsForEMA_

### Make sure the _ValueAddObjectsForEMA_ submodule is downloaded

Before you start building the project make sure the _ValueAddObjectsForEMA_ submodule is properly downloaded. To this aim list the files of the _./ValueAddObjectsForEMA_ directory. This directory should contain several script and source files. If it's empty, please run the following Git commands in the root directory of your _ChainExpander_ project: 

    git submodule init
    git submodule update

### Change the service name and DACS user name if need be

The *ChainStepByStepExample.java* file of the *_ValueAddObjectsForEMA_* contains two hardcoded values that you may want to change depending on your the TREP or Elektron platform you use. These values indicate:

* The **service name** used to subscribe to chains records: The hardcoded value is "ELEKTRON_DD". This value can be changed. 

_Alternatively to the build scripts, you can use the NetBeans IDE to build the applications. NetBeans 8.2 project files are provided with the applications source code._
 
## <a id="running-the-chainexpander"></a>Running the *ChainExpander*

**Before you start the application** you must configure the *EmaConfig.xml file* to specify the host name of the server (the RTDS (formerly TREP) or Refinitiv Real-Time platform) to which the EMA connects. This is set thanks to the value of the *\<ChannelGroup>\<ChannelList>\<Channel>\<Host>* node. This value can be a remote host name or IP address.

To start the *ChainExpander* run the *chain-expander.bat*. These scripts depend on the *JAVA_HOME* and *ELEKTRON_JAVA_HOME* environment variables that must have been defined for the build scripts.

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
