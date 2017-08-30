/*
 * Copyright 2017 Thomson Reuters
 *
 * DISCLAIMER: This source code has been written by Thomson Reuters for the only 
 * purpose of illustrating the "Decoding chains" article published on the 
 * Thomson Reuters Developer Community. It has not been tested for a usage in 
 * production environments.
 *
 * Thomson Reuters Developer Community: https://developers.thomsonreuters.com
 * Decoding chains - Part 1: https://developers.thomsonreuters.com/article/elektron-article-1
 * Decoding chains - Part 2: https://developers.thomsonreuters.com/article/elektron-article-2
 *
 */
package com.thomsonreuters.platformservices.examples;

import com.thomsonreuters.ema.access.EmaFactory;
import com.thomsonreuters.ema.access.OmmConsumer;
import com.thomsonreuters.ema.access.OmmConsumerConfig;
import static com.thomsonreuters.ema.access.OmmConsumerConfig.OperationModel.USER_DISPATCH;
import com.thomsonreuters.ema.access.OmmException;
import com.thomsonreuters.platformservices.ema.utils.chain.Chain;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain;
import com.thomsonreuters.platformservices.ema.utils.chain.RecursiveChain;
import com.thomsonreuters.platformservices.ema.utils.chain.SummaryLinksToSkipByDisplayTemplate;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.Date;
import java.util.List;
import java.util.Map;

class EmaChainToolkitExample
{
    // TREP or Elektron Service name used request chains and tiles
    // IMPORTANT NOTE:  You may need to change this value to match the
    // appropriate service name to be used in your environment
    private static final String SERVICE_NAME = "ELEKTRON_DD";
    
    // If the Data Access Control System (DACS) is activated on your TREP 
    // and if your DACS username is different than your operating system user 
    // name, you may need to hardcode your DACS user name in this application.
    // To do so, you just have to set it in the following field. 
    // Note: DACS user names are usualy provided by the TREP administration 
    // team of your company. 
    private static final String DACS_USER_NAME = "";

    // The OmmConsumer used to request the chains
    private static OmmConsumer ommConsumer;

    // Value of the timeout used by the event dispacthing loops
    private static final int DISPATCH_TIMEOUT_IN_MS = 200;
    
    // Values used to adjust the event dispatching duration of some steps
    private static final int THIRTY_SECONDS = 30;
    private static final int ONE_MINUTE = 60;
    private static final int TWO_MINUTES = 120;
    private static final int FIVE_MINUTE = 300;
    

    public static void main(String[] args)
    {
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("|                                                                             |");
        System.out.println("|                    EMA Chain toolkit example application                    |");
        System.out.println("|                                                                             |");
        System.out.println("| This example application illustrates the concepts explained in the          |");
        System.out.println("| \"Decoding chains with the Elektron Message API\" article published on the    |");
        System.out.println("| Thomson Reuters Developer Portal. More specifically, this application       |");
        System.out.println("| demonstrates how to use the EMA chain toolkit that implements the different |");        
        System.out.println("| concepts, algorithms and optimizations described in the article.            |");
        System.out.println("|                                                                             |");
        System.out.println("| The EMA chain toolkit is exposed by the com.thomsonreuters.platformservices |");
        System.out.println("| .ema.utils.chain package.                                                   |");        
        System.out.println("| The application starts by creating an EMA OmmConsumer and uses it with the  |");
        System.out.println("| toolkit to expand a number of different chains, demonstrating the           |");
        System.out.println("| implemented capabilities. Chain examples are executed one by one in 10      |");
        System.out.println("| individual steps.  Before each step, explanatory text is displayed and you  |");
        System.out.println("| are prompted to press <Enter> to start the step.                            |");
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println();
        createOmmConsumer();
        
        openAChainAndDisplayElementNamesWhenFinished_1();  
        openAChainAndDisplayElementNamesWhenFinished_2();
        openAChainAndDisplayElementsNamesAsSoonAsTheyAreDetected();
        openAChainAndSkipSummaryLinks();
        openAVeryLongChain();
        openAVeryLongChainWithTheOptimizedAlgorithm();
        openChainWithUpdates();
        openARecursiveChain();
        openARecursiveChainWithMaxDepth();
        openAChainThatDoesntExist();
                    
        uninitializeOmmConsumer();
 
        System.out.println("  >>> Exiting the application");
    }

    private static void openAChainAndDisplayElementNamesWhenFinished_1()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 1/10 - openAChainAndDisplayElementNamesWhenFinished_1()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the Dow Jones chain. When the chain decoding is");
        System.out.println("  . completed we display the names of all elements that constitute this chain.");
        System.out.println("  . We also display errors if any. In order to determine if the chain is");
        System.out.println("  . complete we poll the isComplete() method at regular intervals (after each");
        System.out.println("  . OMM consumer dispatch). In the next step we will see another technique to");
        System.out.println("  . detect chains completion.");
        System.out.println();              
        pressAnyKeyToContinue();
               
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#.DJI")
                .withServiceName(SERVICE_NAME)
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        // Display the chain elements after the dispatch loop exited because the 
        // chain is complete.   
        for (Map.Entry<Long, String> element : theChain.getElements().entrySet()){
            Long position = element.getKey();
            String name = element.getValue();
            
            System.out.println("\t" + theChain.getName() + "[" + position + "] = " + name);
        }
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }
        
    private static void openAChainAndDisplayElementNamesWhenFinished_2()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 2/10 - openAChainAndDisplayElementNamesWhenFinished_2()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the Dow Jones chain and display its elements names");
        System.out.println("  . when the chain is complete. This step displays the exact same information");
        System.out.println("  . than the previous one, but this time we use another technique to detect the");
        System.out.println("  . chain’s completion: we leverage the ChainCompleteFunction that is called as");
        System.out.println("  . soon as the chain is complete (no is polling required).");
        System.out.println();              
        pressAnyKeyToContinue();

        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#.DJI")
                .withServiceName(SERVICE_NAME)
                .onChainComplete(
                    new FlatChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(FlatChain chain) {
                            for (Map.Entry<Long, String> element : chain.getElements().entrySet()){
                                Long position = element.getKey();
                                String name = element.getValue();

                                System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                            }
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }
    
    private static void openAChainAndDisplayElementsNamesAsSoonAsTheyAreDetected()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 3/10 - openAChainAndDisplayElementsNamesAsSoonAsTheyAreDetected()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the Dow Jones chain and display the name of new");
        System.out.println("  . elements as soon as they are detected by the decoding algorithm. To this");
        System.out.println("  . aim we leverage the ElementAddedFunction functional interface.");
        System.out.println();              
        pressAnyKeyToContinue();

        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#.DJI")
                .withServiceName(SERVICE_NAME)
                .onElementAdded(
                    new FlatChain.ElementAddedFunction() {
                        @Override
                        public void onElementAdded(long position, String name, FlatChain chain) {
                            System.out.println("\tElement added to <" + chain.getName() + "> at position " + position + ": " + name);
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }
    
    private static void openAChainAndSkipSummaryLinks()
    {
                
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 4/10 - openAChainAndSkipSummaryLinks()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the Dow Jones chain once again, but this time we skip");
        System.out.println("  . the summary links. As the Dow Jones chain has one summary link, the chain ");
        System.out.println("  . will be made of 30 elements instead of 31. The number of summary links may");
        System.out.println("  . be different for other chains and depends on the display template used by");
        System.out.println("  . the chain. For example, it's 2 for the British FTSE 100 (0#.FTSE), 6 for");
        System.out.println("  . the Italian FTSE 100 (0#.FTMIB) and 6 for the French CAC40 (0#.FCHI).");
        System.out.println("  . The SummaryLinksToSkip object used in this method is setup for these 4");
        System.out.println("  . cases.");
        System.out.println();              
        pressAnyKeyToContinue();

        SummaryLinksToSkipByDisplayTemplate summaryLinksToSkip = new SummaryLinksToSkipByDisplayTemplate.Builder()
                .forDisplayTemplate(187).skip(1)  // e.g. 0#.DJI
                .forDisplayTemplate(205).skip(2)  // e.g. 0#.FTSE
                .forDisplayTemplate(1792).skip(6) // e.g. 0#.FTMIB
                .forDisplayTemplate(1098).skip(6) // e.g. 0#.FCHI
                .build();
        
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#.DJI")
                .withServiceName(SERVICE_NAME)
                .withSummaryLinksToSkip(summaryLinksToSkip)
                .onChainComplete(
                    new FlatChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(FlatChain chain) {
                            for (Map.Entry<Long, String> element : chain.getElements().entrySet()){
                                Long position = element.getKey();
                                String name = element.getValue();

                                System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                            }
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }    
    
    private static void openAVeryLongChain()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 5/10 - openAVeryLongChain()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the NASDAQ BASIC chain that contains more than 8000 ");
        System.out.println("  . elements. This kind of chain may take more than 20 seconds to open with the");
        System.out.println("  . normal decoding algorithm. For easier comparison with the optimized");
        System.out.println("  . algorithm, the time spent to decode the chain is displayed.");
        System.out.println();              
        pressAnyKeyToContinue();

        final Date startTime = new Date();
        
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#UNIVERSE.NB")
                .withServiceName(SERVICE_NAME)
                .onElementAdded(
                    new FlatChain.ElementAddedFunction() {
                        @Override
                        public void onElementAdded(long position, String name, FlatChain chain) {
                            if(position % 100 == 0)
                                System.out.println("\t" + (position+1) + " element(s) decoded for <" + chain.getName() + ">. Latest: " + name);
                        }
                    }
                )
                .onChainComplete(
                    new FlatChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(FlatChain chain) {
                            Date now = new Date();
                            long expansionTimeInSeconds = dateDifferenceInSeconds(startTime, now);

                            System.out.println("\tChain <" + chain.getName() + "> contains " + chain.getElements().size() + " elements and opened in "+ expansionTimeInSeconds + " seconds.");
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }    
    
    private static void openAVeryLongChainWithTheOptimizedAlgorithm()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 6/10 - openAVeryLongChainWithTheOptimizedAlgorithm()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the NASDAQ BASIC chain with the optimized decoding");
        System.out.println("  . algorithm. You should observe much better performance than with the normal");
        System.out.println("  . algorithm.");
        System.out.println();              
        pressAnyKeyToContinue();

        final Date startTime = new Date();
        
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#UNIVERSE.NB")
                .withServiceName(SERVICE_NAME)
                .withNameGuessingOptimization(50)
                .onElementAdded(
                    new FlatChain.ElementAddedFunction() {
                        @Override
                        public void onElementAdded(long position, String name, FlatChain chain) {
                            if(position % 100 == 0)
                                System.out.println("\t" + (position+1) + " element(s) decoded for <" + chain.getName() + ">. Latest: " + name);
                        }
                    }
                )                
                .onChainComplete(
                    new FlatChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(FlatChain chain) {
                            Date now = new Date();
                            long expansionTimeInSeconds = dateDifferenceInSeconds(startTime, now);

                            System.out.println("\tChain <" + chain.getName() + "> contains " + chain.getElements().size() + " elements and opened in "+ expansionTimeInSeconds + " seconds.");
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }        
    
    private static void openChainWithUpdates()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 7/10 - openChainWithUpdates()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the \"NYSE Active Volume leaders\" tile (.AV.O), this");
        System.out.println("  . type of chain that updates very frequently. Tiles follow the same naming");
        System.out.println("  . convention than classical chains, except for the name of their first chain");
        System.out.println("  . record that doesn't start by \"0#\". This example leverages the");
        System.out.println("  . ElementAddedFunction, ElementChangedFunction and ElementRemovedFunction");
        System.out.println("  . functional interfaces to display chain changes. For this step, EMA events");
        System.out.println("  . are displayed for 2 minutes. In order to help you visualizing the changes");
        System.out.println("  . that happened to the chain, the complete list of chain elements is");
        System.out.println("  . displayed when the chain is complete and just before it is closed, after");
        System.out.println("  . the 2 minutes wait. If this step is executed when the NYSE is opened, you");
        System.out.println("  . should observe changes in the chain.");
        System.out.println();              
        pressAnyKeyToContinue();

        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName(".AV.O")
                .withServiceName(SERVICE_NAME)
                .withUpdates(true)
                .onElementAdded(
                        new FlatChain.ElementAddedFunction() {
                            @Override
                            public void onElementAdded(long position, String name, FlatChain chain) {
                                System.out.println("\tElement added to <" + chain.getName() + "> at position " + position + ": " + name);
                            }
                        }
                )                
                .onElementRemoved(
                        new FlatChain.ElementRemovedFunction() {
                            @Override
                            public void onElementRemoved(long position, FlatChain chain) {
                                System.out.println("\tElement removed from <" + chain.getName() + "> at position " + position);
                            }
                        }
                )
                .onElementChanged(
                        new FlatChain.ElementChangedFunction() {
                            @Override
                            public void onElementChanged(long position, String previousName, String newName, FlatChain chain) {
                                System.out.println("\tElement changed in <" + chain.getName() + "> at position " + position);
                                System.out.println("\t\tPrevious name: " + previousName + " New name: " + newName);                                
                            }
                        }
                )               
                .onChainComplete(
                    new FlatChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(FlatChain chain) {
                            System.out.println("\n\tThe chain is complete and contains the following elements:");
                            for (Map.Entry<Long, String> element : chain.getElements().entrySet()){
                                Long position = element.getKey();
                                String name = element.getValue();

                                System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                            }
                            System.out.println("\tWaiting for updates...\n");
                        }
                    }
                )
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsFor(TWO_MINUTES);
        
        {// Prints the chain after 2 minutes
            System.out.println("\n\tThe chain is about to be closed. It now contains the following elements:");
            
            for (Map.Entry<Long, String> element : theChain.getElements().entrySet()){
                Long position = element.getKey();
                String name = element.getValue();

                System.out.println("\t\t" + theChain.getName() + "[" + position + "] = " + name);
            }
            System.out.println();
        }        
                
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }    
    
    private static void openARecursiveChain()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 8/10 - openARecursiveChain()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we open the chain for the Equity Japanese Contracts (0#JP-EQ).");
        System.out.println("  . This chain contains elements that are also chains. In this step we use a ");
        System.out.println("  . RecursivesChain object to open all elements of this chain of chains ");
        System.out.println("  . recursively. With recursive chains, the position is represented by a list");
        System.out.println("  . of numbers (Each number representing a position at a given depth).");
        System.out.println("  . The element name is made of a list of strings (Each string representing");
        System.out.println("  . the name of the element at a given level).");
        System.out.println();              
        pressAnyKeyToContinue();
        
        RecursiveChain theChain = new RecursiveChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#JP-EQ")
                .withServiceName(SERVICE_NAME)
                .onChainComplete(
                    new RecursiveChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(RecursiveChain chain) {
                            for (Map.Entry<List<Long>, List<String>> element : chain.getElements().entrySet()){
                                List<Long> position = element.getKey();
                                List<String> name = element.getValue();

                                System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                            }
                        }
                    }
                )
                .onChainError(
                        new RecursiveChain.ChainErrorFunction() {
                            @Override
                            public void onError(String errorMessage, RecursiveChain chain) {
                                if(!chain.isAChain())
                                {
                                    System.out.println("<" + chain.getName() + "> is not a chain");
                                }
                                System.out.println("Error received for <" + chain.getName() + ">: " + errorMessage);
                            }
                        }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }

    private static void openARecursiveChainWithMaxDepth()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 9/10 - openARecursiveChainWithMaxDepth()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we recursively open the chain for the Equity Japanese");
        System.out.println("  . Contracts (0#JP-EQ) and we limit the recursion depth to 2 levels.");
        System.out.println();              
        pressAnyKeyToContinue();
        
        RecursiveChain theChain = new RecursiveChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("0#JP-EQ")
                .withServiceName(SERVICE_NAME)
                .withMaxDepth(2)
                .onChainComplete(
                    new RecursiveChain.ChainCompleteFunction() {
                        @Override
                        public void onComplete(RecursiveChain chain) {
                            for (Map.Entry<List<Long>, List<String>> element : chain.getElements().entrySet()){
                                List<Long> position = element.getKey();
                                List<String> name = element.getValue();

                                System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                            }
                        }
                    }
                )
                .onChainError(
                        new RecursiveChain.ChainErrorFunction() {
                            @Override
                            public void onError(String errorMessage, RecursiveChain chain) {
                                if(!chain.isAChain())
                                {
                                    System.out.println("<" + chain.getName() + "> is not a chain");
                                }
                                System.out.println("Error received for <" + chain.getName() + ">: " + errorMessage);
                            }
                        }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }
    
    
    private static void openAChainThatDoesntExist()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  . 10/10 - openAChainThatDoesntExist()");
        System.out.println("  ..............................................................................");
        System.out.println("  . In this step we try to open a chain that doesn't exist and display the ");
        System.out.println("  . error detected by the decoding algorithm.");
        System.out.println();              
        pressAnyKeyToContinue();

        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName("THIS_CHAIN_DOESNT_EXIST")
                .withServiceName(SERVICE_NAME)
                .onChainError(
                    new FlatChain.ChainErrorFunction() {
                        @Override
                        public void onError(String errorMessage, FlatChain chain) {
                            System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage);
                        }
                    }
                )
                .build();
        
        System.out.println("    >>> Opening <" + theChain.getName() + ">");
        theChain.open();                                    
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        System.out.println("    >>> Closing <" + theChain.getName() + ">");
        theChain.close();        
    }
    
    private static void createOmmConsumer()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  >>> Creating the OmmConsumer");
        
        if(ommConsumer != null)
            return;
        
        try
        {
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig()
                    .consumerName("Consumer_1")
                    .operationModel(USER_DISPATCH);
            
            if(!DACS_USER_NAME.isEmpty())
            {
                config.username(DACS_USER_NAME);
            }
            
            ommConsumer = EmaFactory.createOmmConsumer(config);
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - Can't create the OmmConsumer because of the following error: " + exception.getMessage());
            System.out.println("  >>> Exiting the application");
            exit(-1);
        }                
            
    }
    
    private static void uninitializeOmmConsumer()
    {
        System.out.println();
        System.out.println("  ..............................................................................");
        System.out.println("  >>> Uninitializing the OmmConsumer");

        if(ommConsumer != null)
        {
            ommConsumer.uninitialize();        
            ommConsumer = null;
        }
    }

    private static void dispatchEventsUntilTheChainIsComplete(Chain chain)
    {
        System.out.println("    >>> Dispatching events until the chain is complete or in error");

        try
        {
            do
            {
                ommConsumer.dispatch(DISPATCH_TIMEOUT_IN_MS);
            } 
            while (!chain.isComplete());
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            System.out.println("  >>> Exiting the application");
            exit(-1);
        }                
    }
    
    private static void dispatchEventsFor(int durationInSeconds)
    {                
        System.out.println("    >>> Dispatching events for " + durationInSeconds + " seconds");
            
        try
        {
            Date now = new Date();
            Date endTime = new Date(now.getTime() + durationInSeconds * 1000);
            do
            {
                ommConsumer.dispatch(DISPATCH_TIMEOUT_IN_MS);
            } 
            while(new Date().before(endTime));            
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            System.out.println("  >>> Exiting the application");
            exit(-1);
        }                
    }
    
    private static void pressAnyKeyToContinue()
    { 
        System.out.println("    <<< Press <Enter> to continue...");
        try
        {
            do
            {
                ommConsumer.dispatch(DISPATCH_TIMEOUT_IN_MS);
            } 
            while (System.in.available() <= 0);
            do
            {
                System.in.read();
            }
            while (System.in.available() > 0);
        }  
        catch(OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            System.out.println("  >>> Exiting the application");
            exit(-1);
        } 
        catch (IOException exception)  
        {}  
    }    
    
    private static long dateDifferenceInSeconds(Date date1, Date date2)
    {
        long diff = date2.getTime() - date1.getTime();
        long diffInSeconds = diff / 1000 % 60;
        
        return diffInSeconds;
    }
}
