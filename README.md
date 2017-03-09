# ChainExpander example
Created by Platform Services GitHub tool on Mon Mar 06 2017


## Table of Content

* [Overview](#overview)

* [Prerequisites](#prerequisites)

* [Application design](#application-design)

* [Demonstrated features](#demonstrated-features)

* [Using the EMA Chain Toolkit](#using-the-ema-chain-toolkit)

* [Building the ChainExpander](#building-the-chainexpander)

* [Running the ChainExpander](#running-the-chainexpander)

* [Troubleshooting](#troubleshooting)

* [Solution Code](#solution-code)

## <a id="overview"></a>Overview
The Chain Expander example demonstrates the different concepts explained in the [Decoding chains - Part 1](https://developers.thomsonreuters.com/article/elektron-article-1) article published on the [Thomson Reuters Developer Community](https://developers.thomsonreuters.com). This application is based on the Java edition of the Elektron Message API and is designed in a way that makes it easly reusable in your own source code application.

## <a id="prerequisites"></a>Prerequisites

Required software components:

* [Elektron Message API](https://developers.thomsonreuters.com/elektron/elektron-sdk-java) (1.0.8 or greater) - Thomson Reuters interface to the Elektron Market Data environment
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit - version 8

## <a id="application-design"></a> Application design
The source code of this example application has been designed for easy reuse. It is made of two distinct parts:

### The EMA Chain Toolkit
This is a reusable module that implements the complete chain decoding logic and algorithms explained in the [Decoding chains - Part 1](https://developers.thomsonreuters.com/article/elektron-article-1) article. These features have been isolated in the EMA Chain Toolkit contained in the *com.thomsonreuters.platformservices.ema.utils.chain* package. The source code of this package is available in GitHub and ready for reuse. It can be integrated in your application project as is. The toolkit also comes with a Javadoc that fully describes the exposed API. The [Decoding Chains - Part 2](https://developers.thomsonreuters.com/article/elektron-article-2) article explains how to use it.

### The ChainExpander
This is the example application itself. It is made of a single source file that demonstrates the EMA Chain Toolkit capabilities and how to use them. The application starts by creating an EMA *OmmConsumer* and uses it with the toolkit to expand different kind of chains. Chains are expanded one by one in 10 individual steps. Before each step an explanatory text is displayed and you are prompted to press *\<Enter>* to start the step.

The *ChainExpander* application also implements utility methods that are used to dispatch the *OmmConsumer* in different situations: until a chain is complete, until the user presses *\<Enter>* or until a certain amount of time is elapsed.

**Note:** If you do not know yet about the Elektron Message API (EMA) and how to program and EMA consumer application I recommend you to follow this [EMA Quick Start](https://developers.thomsonreuters.com/elektron/elektron-sdk-java/quick-start?content=8656&type=quick_start) and these [EMA Tutorials](https://developers.thomsonreuters.com/elektron/elektron-sdk-java/learning).

## <a id="demonstrated-features"></a>Demonstrated features
The Chain Expander application demonstrates the following features of the EMA Chain Toolkit:

* **Step 1:** Builds and opens the Dow Jones chain (0#.DJI). Waits for the chain to complete using the *isComplete()* method. Gets and display the chain elements.
* **Step 2:** Builds and opens the Dow Jones chain. Leverages the *ChainCompleteFunction* functional interface to wait for completion, to get the chain elements and to display them.
* **Step 3:** Builds and opens the Dow Jones chain. Leverages the *ElementAddedFunction* functional interface to display new chain elements as soon as they are decoded.
* **Step 4:** Same as Step 2 but skip the summary links (first link .DJI in the case of the Dow Jones).
* **Step 5:** Opens a very long chain (NASDAQ Basic) using the default algorithm. The NASDAQ Basic chain (0#UNIVERSE.NB) contains more than 8000 elements and may take more than 30 seconds to open with the default algorithm.
* **Step 6:** Same as Step 5 but with the "Name Guessing" optimized algorithm. This time the chain takes 1 second or so to open.
* **Step 7:** Opens the "NYSE Active Volume leaders" tile (.AV.O) with updates and display any change that happens thanks to the *ElementAddedFunction*, *ElementChangedFunction* and *ElementRemovedFunction* functional interfaces. Tiles are chains that updates very frequently. .AV.O updates often when the US market is opened.
* **Step 8:** Opens and displays the Japanese Equity Market recursive chain (0#JP-EQ).  This chain is a 3 levels depth chain of chains.
* **Step 9:** Same as Step 8 but with a maximum depth of 2 levels.
* **Step 10:** Opens a chain that doesn’t exit. Leverages the *ChainErrorFunction* functional interface to catch and display the error.

## <a id="using-the-ema-chain-toolkit"></a>Using the EMA Chain Toolkit
For more details about the EMA Chain Toolkit usage, please refer to the [Decoding Chains - Part 2](https://developers.thomsonreuters.com/article/elektron-article-2) article and the EMA Chain Toolkit javadoc. 

## <a id="building-the-chainexpander"></a>Building the ChainExpander

This package includes some convenient files which will enable the developer to quickly build and run the example application. These scripts depend on the *JAVA_HOME* and *ELEKTRON_JAVA_HOME* environment variables. These variables must be set appropriately before you run any of the *build* or *run* scripts.
* *JAVA_HOME* must be set with the root directory of your JDK 8 environment.
* *ELEKTRON_JAVA_HOME* must be set with the root directory of your (EMA) Elektron Java API installation

Once these variables setup, run the *build.bat* or the *build.ksh* script to build the application.

## <a id="running-the-chainexpander"></a>Running the ChainExpander
**Before you start the application** you must configure the *EmaConfig.xml file* to specify the host name of the server (the TREP or Elektron platform) to which the EMA connects. This is set thanks to the value of the *\<ChannelGroup>\<ChannelList>\<Channel>\<Host>* node. This value can be a remote host name or IP address.

To start the ChainExpander run the *run.bat* or the *run.ksh* script. These scripts depend on the *JAVA_HOME* and *ELEKTRON_JAVA_HOME* environment variables that must have been defined for the build scripts.

### Expected output

This is an example of the ChainExpander output for each step:

    -------------------------------------------------------------------------------
    |                                                                             |
    |                 Chain Expander toolkit example application                  |
    |                                                                             |
    | This example application illustrates the concepts explained in the          |
    | "Decoding chains with the Elektron Message API" article published on the    |
    | Thomson Reuters Developer Portal (URL). More specifically, this application |
    | demonstrates how to use the EMA chain toolkit that implements the different |
    | concepts, algorithms and optimizations described in the article.            |
    |                                                                             |
    | The EMA chain toolkit is exposed by the com.thomsonreuters.platformservices |
    | .ema.utils.chain package.                                                   |
    | The application starts by creating an EMA OmmConsumer and uses it with the  |
    | toolkit to expand a number of different chains, demonstrating the           |
    | implemented capabilities. The chains are expanded one by one in 10          |
    | individual steps. For each step an explanatory text is displayed and you    |
    | are prompted to press <Enter> to start the step.                            |
    -------------------------------------------------------------------------------


      ..............................................................................
      >>> Creating the OmmConsumer

      ..............................................................................
      . 1/10 - openAChainAndDisplayElementNamesWhenFinished_1()
      ..............................................................................
      . In this step we open the Dow Jones chain. When the chain decoding is
      . completed we display the names of all elements names that constitute this
      . chain. We also display errors if any.

        <<< Press <Enter> to continue...

        >>> Opening <0#.DJI>
        >>> Dispathing events until the chain is complete or in error
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
        >>> Closing <0#.DJI>

      ..............................................................................
      . 2/10 - openAChainAndDisplayElementNamesWhenFinished_2()
      ..............................................................................
      . In this step we open the same Dow Jones chain and also display its elements
      . names when the chain is complete. But this time we leverage the
      . onChainComplete() functional interface to detect when the chain is complete
      . and to display the elements.

        <<< Press <Enter> to continue...

        >>> Opening <0#.DJI>
        >>> Dispathing events until the chain is complete or in error
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
        >>> Closing <0#.DJI>

      ..............................................................................
      . 3/10 - openAChainAndDisplayElementsNamesAsSoonAsTheyAreDetected()
      ..............................................................................
      . In this step we open the Dow Jones chain and display the name of new
      . elements as soon as they are detected by the decoding algorithm. To this
      . aim we leverage the onElementAdded() functional interface.

        <<< Press <Enter> to continue...

        >>> Opening <0#.DJI>
        >>> Dispathing events until the chain is complete or in error
            Element added to <0#.DJI> at position 0: .DJI
            Element added to <0#.DJI> at position 1: AAPL.OQ
            Element added to <0#.DJI> at position 2: AXP.N
            Element added to <0#.DJI> at position 3: BA.N
            Element added to <0#.DJI> at position 4: CAT.N
            Element added to <0#.DJI> at position 5: CSCO.OQ
            Element added to <0#.DJI> at position 6: CVX.N
            Element added to <0#.DJI> at position 7: DD.N
            Element added to <0#.DJI> at position 8: DIS.N
            Element added to <0#.DJI> at position 9: GE.N
            Element added to <0#.DJI> at position 10: GS.N
            Element added to <0#.DJI> at position 11: HD.N
            Element added to <0#.DJI> at position 12: IBM.N
            Element added to <0#.DJI> at position 13: INTC.OQ
            Element added to <0#.DJI> at position 14: JNJ.N
            Element added to <0#.DJI> at position 15: JPM.N
            Element added to <0#.DJI> at position 16: KO.N
            Element added to <0#.DJI> at position 17: MCD.N
            Element added to <0#.DJI> at position 18: MMM.N
            Element added to <0#.DJI> at position 19: MRK.N
            Element added to <0#.DJI> at position 20: MSFT.OQ
            Element added to <0#.DJI> at position 21: NKE.N
            Element added to <0#.DJI> at position 22: PFE.N
            Element added to <0#.DJI> at position 23: PG.N
            Element added to <0#.DJI> at position 24: TRV.N
            Element added to <0#.DJI> at position 25: UNH.N
            Element added to <0#.DJI> at position 26: UTX.N
            Element added to <0#.DJI> at position 27: V.N
            Element added to <0#.DJI> at position 28: VZ.N
            Element added to <0#.DJI> at position 29: WMT.N
            Element added to <0#.DJI> at position 30: XOM.N
        >>> Closing <0#.DJI>

      ..............................................................................
      . 4/10 - openAChainAndSkipSummaryLinks()
      ..............................................................................
      . In this step we open the Dow Jones chain once again, but this time we skip
      . the summary links. As the Dow Jones chain as one summary link, the chain 
      . will be made of 30 elements instead of 31. The number of summary links may
      . be different for other chains and depends on the display template used by
      . the chain. For example, it's 2 for the British FTSE 100 (0#.FTSE), 6 for
      . the Italian FTSE 100 (0#.FTMIB) and 6 for the French CAC40 (0#.FCHI).
      . The SummaryLinksToSkip object used in this method is setup for these 4
      . cases.

        <<< Press <Enter> to continue...

        >>> Opening <0#.DJI>
        >>> Dispathing events until the chain is complete or in error
            0#.DJI[0] = AAPL.OQ
            0#.DJI[1] = AXP.N
            0#.DJI[2] = BA.N
            0#.DJI[3] = CAT.N
            0#.DJI[4] = CSCO.OQ
            0#.DJI[5] = CVX.N
            0#.DJI[6] = DD.N
            0#.DJI[7] = DIS.N
            0#.DJI[8] = GE.N
            0#.DJI[9] = GS.N
            0#.DJI[10] = HD.N
            0#.DJI[11] = IBM.N
            0#.DJI[12] = INTC.OQ
            0#.DJI[13] = JNJ.N
            0#.DJI[14] = JPM.N
            0#.DJI[15] = KO.N
            0#.DJI[16] = MCD.N
            0#.DJI[17] = MMM.N
            0#.DJI[18] = MRK.N
            0#.DJI[19] = MSFT.OQ
            0#.DJI[20] = NKE.N
            0#.DJI[21] = PFE.N
            0#.DJI[22] = PG.N
            0#.DJI[23] = TRV.N
            0#.DJI[24] = UNH.N
            0#.DJI[25] = UTX.N
            0#.DJI[26] = V.N
            0#.DJI[27] = VZ.N
            0#.DJI[28] = WMT.N
            0#.DJI[29] = XOM.N
        >>> Closing <0#.DJI>

      ..............................................................................
      . 5/10 - openAVeryLongChain()
      ..............................................................................
      . In this step we open the NASDAQ BASIC chain that contains more than 8000 
      . elements. This kind of chain may take more than 20 seconds to open with the
      . normal decoding algorithm. For easier comparison with the optimized
      . algorithm, the time spent to decode the chain is displayed.

        <<< Press <Enter> to continue...

        >>> Opening <0#UNIVERSE.NB>
        >>> Dispathing events until the chain is complete or in error
            1 elements decoded for <0#UNIVERSE.NB>. Latest: A.NB
            101 elements decoded for <0#UNIVERSE.NB>. Latest: ADMA.NB
            201 elements decoded for <0#UNIVERSE.NB>. Latest: AGN_pa.NB
            301 elements decoded for <0#UNIVERSE.NB>. Latest: ALL_pb.NB
                .
                .
                .
            8401 elements decoded for <0#UNIVERSE.NB>. Latest: ZLTQ.NB
            Chain <0#UNIVERSE.NB> contains 8436 elements and opened in 31 seconds.
        >>> Closing <0#UNIVERSE.NB>

      ..............................................................................
      . 6/10 - openAVeryLongChainWithTheOptimizedAlgorithm()
      ..............................................................................
      . In this step we open the NASDAQ BASIC chain with the optimized decoding
      . algorithm. You should observe much better performance than with the normal
      . algorithm.

        <<< Press <Enter> to continue...

        >>> Opening <0#UNIVERSE.NB>
        >>> Dispathing events until the chain is complete or in error
            1 elements decoded for <0#UNIVERSE.NB>. Latest: A.NB
            101 elements decoded for <0#UNIVERSE.NB>. Latest: ADMA.NB
            201 elements decoded for <0#UNIVERSE.NB>. Latest: AGN_pa.NB
            301 elements decoded for <0#UNIVERSE.NB>. Latest: ALL_pb.NB
                .
                .
                .
            8401 elements decoded for <0#UNIVERSE.NB>. Latest: ZLTQ.NB
            Chain <0#UNIVERSE.NB> contains 8436 elements and opened in 1 seconds.
        >>> Closing <0#UNIVERSE.NB>

            ..............................................................................
      . 7/10 - openChainWithUpdates()
      ..............................................................................
      . In this step we open the "NYSE Active Volume leaders" tile (.AV.O), this
      . type of chain that updates very frequently. Tiles follow the same naming
      . convention than classical chains, except for the name of their first chain
      . record that doesn't start by "0#". This example leverages the
      . onElementAdded, onElementChanged and onElementsRemoved functional
      . interfaces to display chain changes. For this step, EMA events are
      . displayed for 2 minutes. After this time the chain is close and the step
      . terminates. If this step is executed when the NYSE is opened, you should
      . observe changes in the chain.

        <<< Press <Enter> to continue...
        >>> Opening <.AV.O>
        >>> Dispathing events for 120 seconds
            Element added to <.AV.O> at position 0: GLBL.O
            Element added to <.AV.O> at position 1: MOMO.O
            Element added to <.AV.O> at position 2: AMD.O
            Element added to <.AV.O> at position 3: DISH.O
            Element added to <.AV.O> at position 4: TGTX.O
            Element added to <.AV.O> at position 5: MEET.O
            Element added to <.AV.O> at position 6: DRYS.O
            Element added to <.AV.O> at position 7: AUPH.O
            Element added to <.AV.O> at position 8: ETRM.O
            Element added to <.AV.O> at position 9: XIV.O
            Element added to <.AV.O> at position 10: TVIX.O
            Element added to <.AV.O> at position 11: PERI.O
            Element added to <.AV.O> at position 12: MYL.O
            Element added to <.AV.O> at position 13: ASNA.O
            Element added to <.AV.O> at position 14: OPK.O
            Element added to <.AV.O> at position 15: FTR.O
            Element added to <.AV.O> at position 16: HPJ.O
            Element added to <.AV.O> at position 17: WKHS.O
            Element added to <.AV.O> at position 18: MU.O
            Element added to <.AV.O> at position 19: QQQ.O
            Element added to <.AV.O> at position 20: AAPL.O
            Element added to <.AV.O> at position 21: IBB.O
            Element added to <.AV.O> at position 22: SQQQ.O
            Element added to <.AV.O> at position 23: SIRI.O
            Element added to <.AV.O> at position 24: LOGI.O
            Element changed in <.AV.O> at position 11
                Previous name: PERI.O New name: MYL.O
            Element changed in <.AV.O> at position 12
                Previous name: MYL.O New name: PERI.O
            Element changed in <.AV.O> at position 15
                Previous name: FTR.O New name: QQQ.O
            Element changed in <.AV.O> at position 16
                Previous name: HPJ.O New name: FTR.O
            Element changed in <.AV.O> at position 17
                Previous name: WKHS.O New name: HPJ.O
                .
                .
                .
            Element changed in <.AV.O> at position 18
                Previous name: HPJ.O New name: IBB.O
            Element changed in <.AV.O> at position 17
                Previous name: HPJ.O New name: IBB.O
            Element changed in <.AV.O> at position 18
                Previous name: IBB.O New name: HPJ.O
        >>> Closing <.AV.O>

      ..............................................................................
      >>> Creating the OmmConsumer

      ..............................................................................
      . 8/10 - openARecursiveChain()
      ..............................................................................
      . In this step we open the chain for the Equity Japanese Contracts (0#JP-EQ).
      . This chain contains elements that are also chains. In this step we use a 
      . RecursivesChain object to open all elements of this chain of chains 
      . recursively. With recursive chains, the position is represented by a list
      . of numbers (Each number representing a position at a given depth).
      . The element name is made of a list of strings (Each string representing
      . the name of the element at a given level).

        <<< Press <Enter> to continue...

        >>> Opening <0#JP-EQ>
        >>> Dispathing events until the chain is complete or in error
            0#JP-EQ[0] = [.TOPXC]
            0#JP-EQ[1, 0] = [.TSEI, .TOPX]
            0#JP-EQ[1, 1] = [.TSEI, .TSI2]
            0#JP-EQ[1, 2] = [.TSEI, .MTHR]
            0#JP-EQ[1, 3] = [.TSEI, .TSIL]
            0#JP-EQ[1, 4] = [.TSEI, .TSIM]
            0#JP-EQ[1, 5] = [.TSEI, .TSIS]
            0#JP-EQ[1, 6] = [.TSEI, .TOPXC]
            0#JP-EQ[1, 7] = [.TSEI, .TOPXL]
                .
                .
                .
            0#JP-EQ[5, 11, 27] = [0#JP-INDICES, .TSEK, .IBNKS.T]
            0#JP-EQ[5, 11, 28] = [0#JP-INDICES, .TSEK, .ISECU.T]
            0#JP-EQ[5, 11, 29] = [0#JP-INDICES, .TSEK, .IINSU.T]
            0#JP-EQ[5, 11, 30] = [0#JP-INDICES, .TSEK, .IFINS.T]
            0#JP-EQ[5, 11, 31] = [0#JP-INDICES, .TSEK, .IRLTY.T]
            0#JP-EQ[5, 11, 32] = [0#JP-INDICES, .TSEK, .ISVCS.T]
            0#JP-EQ[5, 12] = [0#JP-INDICES, .TSA1]
            0#JP-EQ[5, 13] = [0#JP-INDICES, .TSA2]
        >>> Closing <0#JP-EQ>

      ..............................................................................
      . 9/10 - openARecursiveChainWithMaxDepth()
      ..............................................................................
      . In this step we recursively open the chain for the Equity Japanese
      . Contracts (0#JP-EQ) and we limit the recursion depth to 2 levels.

        <<< Press <Enter> to continue...

        >>> Opening <0#JP-EQ>
        >>> Dispathing events until the chain is complete or in error
            0#JP-EQ[0] = [.TOPXC]
            0#JP-EQ[1, 0] = [.TSEI, .TOPX]
            0#JP-EQ[1, 1] = [.TSEI, .TSI2]
            0#JP-EQ[1, 2] = [.TSEI, .MTHR]
            0#JP-EQ[1, 3] = [.TSEI, .TSIL]
            0#JP-EQ[1, 4] = [.TSEI, .TSIM]
            0#JP-EQ[1, 5] = [.TSEI, .TSIS]
            0#JP-EQ[1, 6] = [.TSEI, .TOPXC]
            0#JP-EQ[1, 7] = [.TSEI, .TOPXL]
                .
                .
                .
            0#JP-EQ[5, 11] = [0#JP-INDICES, .TSEK]
            0#JP-EQ[5, 12] = [0#JP-INDICES, .TSA1]
            0#JP-EQ[5, 13] = [0#JP-INDICES, .TSA2]
        >>> Closing <0#JP-EQ>

      ..............................................................................
      . 10/10 - openAChainThatDoesntExist()
      ..............................................................................
      . In this step we try to open a chain that doesn't exist and display the 
      . error detected by the decoding algorithm.

        <<< Press <Enter> to continue...

        >>> Opening <THIS_CHAIN_DOESNT_EXIST>
        >>> Dispathing events until the chain is complete or in error
            Error received for <THIS_CHAIN_DOESNT_EXIST>: Invalid status received for <THIS_CHAIN_DOESNT_EXIST>: Closed / Suspect / Not found / '*The record could not be found'
        >>> Closing <THIS_CHAIN_DOESNT_EXIST>

      ..............................................................................
      >>> Uninitializing the OmmConsumer
      >>> Exiting the application

## <a id="troubleshooting"></a>Troubleshooting

**Q: When I build or run the application, it fails with an error like:**

    The system cannot find the path specified

**A:** The JAVA_HOME environment variable is not set, or set to the wrong path. See [Building the ChainExpander](#building-the-chainexpander) section above.

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
**A:** The ELEKTRON_JAVA_HOME environment variable is not set, or set to the wrong path.  See [Building the ChainExpander](#building-the-chainexpander) section above.

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

**A:** The ELEKTRON_JAVA_HOME environment variable is not set, or set to the wrong path.  See [Building the ChainExpander](#building-the-chainexpander) section above.

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

* **Olivier Davant** - Release 1.0.  *Initial version*

### <a id="license"></a>License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
