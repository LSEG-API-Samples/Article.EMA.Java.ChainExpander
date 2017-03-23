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
import static java.lang.System.exit;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class ChainExpander
{
    // TREP or Elektron Service name used request chains and tiles
    private static String serviceName = "ELEKTRON_DD";
    
    // Name of the chain to expand
    private static String chainName = "";
    
    // Data Access Control System (DACS) username. 
    private static String dacsUserName = "";
    
    // Indicates if the optimized algorithm must be used. 
    private static int nbOfNamesToGuessForOptimization = 0;    
    
    // Indicates if the verbose mode is enabled. 
    private static boolean verboseMode = true;    

    // The OmmConsumer used to request the chains
    private static OmmConsumer ommConsumer;

    // Value of the timeout used by the event dispacthing loops
    private static final int DISPATCH_TIMEOUT_IN_MS = 200;
        
    public static void main(String[] args)
    {
        analyzeArguments(args);

        if(verboseMode)
        {
            System.out.println();
            System.out.println("  >>> Input parameters:");
            System.out.println("\tchain-name  : \"" + chainName + "\"");
            System.out.println("\tservice-name: \"" + serviceName + "\"");
            System.out.println("\tuser-name   : \"" + dacsUserName + "\"");
            if(nbOfNamesToGuessForOptimization > 0)
            {
                System.out.println("\toptimization: enabled");       
            }
            else
            {
                System.out.println("\toptimization: disabled");    
            }
            if(verboseMode)
            {
                System.out.println("\tnon-verbose : disabled");       
            }
        }
        
        if(verboseMode) System.out.println("  >>> Connecting to the infrastructure...");        
        createOmmConsumer();
        
        if(verboseMode) System.out.println("  >>> Expanding the chain. Please wait...");        
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName(chainName)
                .withServiceName(serviceName)
                .withNameGuessingOptimization(nbOfNamesToGuessForOptimization)
                .onChainComplete(
                    (chain) ->
                        chain.getElements().forEach(
                            (position, name) ->
                            {
                                if(verboseMode) 
                                    System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                                else
                                    System.out.println(name);
                                }
                        )
                )
                .onChainError(
                    (errorMessage, chain) -> 
                        System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage)
                )
                .build();
        
        theChain.open();
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        theChain.close();                    
        
        uninitializeOmmConsumer();
    }
    
    private static void analyzeArguments(String[] args)
    {        
        String syntax = "chain-expander [-nv] [-o] [-s service-name] [-u user-name] chain-name";
        Options options = new Options();

        Option serviceNameOption = new Option("s", "service-name", true, "Elektron or TREP service name\nDefault value: ELEKTRON_DD");
        serviceNameOption.setRequired(false);
        options.addOption(serviceNameOption);

        Option dacsUserNameOption = new Option("u", "user-name", true, "DACS user name\nDefault value: System user name");
        dacsUserNameOption.setRequired(false);
        options.addOption(dacsUserNameOption);

        Option optimizationOption = new Option("o", "optimization", false, "Enables the optimized algorithm for opening long chains. This is not appropriate for short chains (less than 300 elements).");
        optimizationOption.setRequired(false);
        options.addOption(optimizationOption);

        Option nonVerboseOption = new Option("nv", "non-verbose", false, "Enables the non verbose mode. Only the chain elements are displayed.");
        nonVerboseOption.setRequired(false);
        options.addOption(nonVerboseOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try 
        {
            cmd = parser.parse(options, args);
        } 
        catch (ParseException e) 
        {
            System.out.println(e.getMessage());
            formatter.printHelp(syntax, options);
            System.exit(1);
            return;
        }

        List<String> parsedArguments = cmd.getArgList();
        if(parsedArguments.size() != 1)
        {
            System.out.println("Missing required chain-name\n");
            formatter.printHelp(syntax, options);
            System.exit(1);
            return;
        }
        
        chainName = parsedArguments.get(0);
        if(cmd.hasOption("service-name"))
        {
            serviceName = cmd.getOptionValue("service-name");
        }
        if(cmd.hasOption("user-name"))
        {        
            dacsUserName = cmd.getOptionValue("user-name");
        }
        if(cmd.hasOption("optimization"))
        {        
            nbOfNamesToGuessForOptimization = 20;
        }
        if(cmd.hasOption("non-verbose"))
        {        
            verboseMode = false;
        }
    }
    
    private static void createOmmConsumer()
    {
        if(ommConsumer != null)
            return;
        
        try
        {
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig()
                    .consumerName("Consumer_1")
                    .operationModel(USER_DISPATCH);
            
            if(!dacsUserName.isEmpty())
            {
                config.username(dacsUserName);
            }
            
            ommConsumer = EmaFactory.createOmmConsumer(config);
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - Can't create the OmmConsumer because of the following error: " + exception.getMessage());
            exit(-1);
        }                
            
    }
    
    private static void uninitializeOmmConsumer()
    {
        if(ommConsumer != null)
        {
            ommConsumer.uninitialize();        
            ommConsumer = null;
        }
    }

    private static void dispatchEventsUntilTheChainIsComplete(Chain chain)
    {
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
            exit(-1);
        }                
    }
}
