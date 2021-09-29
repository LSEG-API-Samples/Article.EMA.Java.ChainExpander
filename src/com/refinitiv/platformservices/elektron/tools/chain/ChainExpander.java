/*
 * Copyright 2017 Thomson Reuters
 *
 * DISCLAIMER: This source code has been written by Thomson Reuters for the only 
 * purpose of illustrating articles published on the Thomson Reuters Developer 
 * Community. It has not been tested for usage in production environments. 
 * Thomson Reuters cannot be held responsible for any issues that may happen if 
 * these objects or the related source code is used in production or any other 
 * client environment.
 *
 * Thomson Reuters Developer Community: https://developers.thomsonreuters.com
 *
 * Related Articles:
 *   Simple Chain objects for EMA - Part 1: https://developers.thomsonreuters.com/article/simple-chain-objects-ema-part-1
 *   Simple Chain objects for EMA - Part 2: https://developers.thomsonreuters.com/article/simple-chain-objects-ema-part-2
 *
 */
package com.refinitiv.platformservices.elektron.tools.chain;

import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.OmmConsumer;
import com.refinitiv.ema.access.OmmConsumerConfig;
import static com.refinitiv.ema.access.OmmConsumerConfig.OperationModel.USER_DISPATCH;
import com.refinitiv.ema.access.OmmException;
import com.refinitiv.platformservices.elektron.objects.common.Dispatcher;
import com.refinitiv.platformservices.elektron.objects.chain.Chain;
import com.refinitiv.platformservices.elektron.objects.chain.FlatChain;
import static java.lang.System.exit;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

/**
 * Main application class that implements the main method and the whole 
 * application logic from the arguments interpretation to the data output 
 * in text or JSON format.
 */
class ChainExpander
{
    // TREP or Elektron Service name used to request chains and tiles
    private static String serviceName = "ELEKTRON_EDGE";
    
    // Name of the chain to expand
    private static String chainName = "";
    
    // Data Access Control System (DACS) username. 
    private static String dacsUserName = "";
    
    // Indicates if the optimized algorithm must be used. 
    private static int nbOfNamesToGuessForOptimization = 0;    
    
    // Indicates if the verbose mode is enabled. 
    private static boolean verboseMode = true;    
    
    // Indicates if chain elements must be displayed in JSON format. 
    private static boolean jsonOutputMode = false;    

    // The OmmConsumer used to request the chains
    private static OmmConsumer ommConsumer;
    
    // The OmmConsumer dispatcher
    private static Dispatcher dispatcher;    

    
    /**
     * Main application method
     * @param args application arguments
     */    
    public static void main(String[] args)
    {
        analyzeArguments(args);
        
        if(verboseMode)
            displayParameters();
        
        if(verboseMode) 
            System.out.println("  >>> Connecting to the infrastructure...");        
        
        createOmmConsumerAndDispatcher();
        
        if(verboseMode) 
            System.out.println("  >>> Expanding the chain <" + chainName + ">. Please wait...");        
        
        FlatChain theChain = new FlatChain.Builder()
                .withOmmConsumer(ommConsumer)
                .withChainName(chainName)
                .withServiceName(serviceName)
                .withNameGuessingOptimization(nbOfNamesToGuessForOptimization)
                .onComplete(
                    (chain) -> 
                        printChain(chain)
                )
                .onError(
                    (errorMessage, chain) -> 
                        System.out.println("\tError received for <" + chain.getName() + ">: " + errorMessage)
                )
                .build();
        
        theChain.open();
        
        dispatchEventsUntilTheChainIsComplete(theChain);
        
        theChain.close();                    
        
        uninitializeOmmConsumer();
    }
    
   /**
     * Creates the <code>OmmConsumer</code> and the <code>Dispatcher</code> used 
     * by the application
     */
    private static void createOmmConsumerAndDispatcher()
    {
        if(ommConsumer != null)
            return;
        
        try
        {
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig()
                    .operationModel(USER_DISPATCH);
            
            if(!dacsUserName.isEmpty())
            {
                config.username(dacsUserName);
            }
            
            ommConsumer = EmaFactory.createOmmConsumer(config);
            
            dispatcher = new Dispatcher.Builder()
                                .withOmmConsumer(ommConsumer)
                                .build();
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - Can't create the OmmConsumer because of the following error: " + exception.getMessage());
            exit(-1);
        }                        
    }    
    
    /**
     * Uninitialize the <code>OmmConsumer</code>.
     */
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
            dispatcher.dispatchEventsUntilComplete(chain);
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            exit(-1);
        }                        
    }
    
    /**
     * Prints the chain
     * @param chain the <code>FlatChain</code> to print
     */    
    private static void printChain(FlatChain chain)
    {
        if(jsonOutputMode)
        {
            printChainInJsonFormat(chain);
        }
        else
        {
            printChainInTextFormat(chain);
        }
    }
    
    /**
     * Print the <code>FlatChain</code> in text format.
     * @param chain the <code>FlatChain</code> to print
     */     
    private static void printChainInTextFormat(FlatChain chain)
    {
        chain.getElements().forEach(
            (position, name) ->
            {
                if(verboseMode) 
                    System.out.println("\t" + chain.getName() + "[" + position + "] = " + name);
                else
                    System.out.println(name);
            }
        );      
    }    
    
        
    /**
     * Print the <code>FlatChain</code> in JSON format.
     * @param chain the <code>FlatChain</code> to print
     */ 
    private static void printChainInJsonFormat(FlatChain chain)
    {
        JSONObject json = new JSONObject();
        json.put("name", chain.getName());        
        json.put("elements", new JSONObject(chain.getElements()));
        
        System.out.println(json);
    }
    
    /**
     * Analyze application's arguments
     * @param args the arguments to analyse
     */
    private static void analyzeArguments(String[] args)
    {        
        String syntax = "chain-expander [-nv] [-o] [-s service-name] [-u user-name] chain-name";
        String header = "options:";

        Options options = new Options();

        Option serviceNameOption = new Option("s", "service-name", true, "Elektron or TREP service name\nDefault value: ELEKTRON_EDGE");
        serviceNameOption.setRequired(false);
        options.addOption(serviceNameOption);

        Option dacsUserNameOption = new Option("u", "user-name", true, "DACS user name\nDefault value: System user name");
        dacsUserNameOption.setRequired(false);
        options.addOption(dacsUserNameOption);

        Option optimizationOption = new Option("o", "optimization", false, "Enables the optimized algorithm for opening long chains. This is not appropriate for short chains (less than 300 elements).");
        optimizationOption.setRequired(false);
        options.addOption(optimizationOption);

        Option nonVerboseOption = new Option("nv", "non-verbose-mode", false, "Enables the non verbose mode. Only the chain elements are displayed.");
        nonVerboseOption.setRequired(false);
        options.addOption(nonVerboseOption);

        Option jsonOutputOption = new Option("j", "json-output-mode", false, "Outputs chain elements in JSON format.");
        jsonOutputOption.setRequired(false);
        options.addOption(jsonOutputOption);

        String footer = "examples:\n" 
                      + " chain-expander -nv -s ELEKTRON_EDGE 0#.DJI\n"
                      + " chain-expander -nv -j -s ELEKTRON_EDGE 0#.FTSE";
        
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
            formatter.printHelp(syntax, header, options, footer);
            System.exit(1);
            return;
        }

        List<String> parsedArguments = cmd.getArgList();
        if(parsedArguments.size() != 1)
        {
            System.out.println("Missing required chain-name\n");
            formatter.printHelp(syntax, header, options, footer);
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
        if(cmd.hasOption("non-verbose-mode"))
        {        
            verboseMode = false;
        }
        if(cmd.hasOption("json-output-mode"))
        {        
            jsonOutputMode = true;
        }
    }    
    
    /**
     * Display the application's arguments and options.
     */
    private static void displayParameters()
    {
        System.out.println();
        System.out.println("  >>> Input parameters:");
        System.out.println("\tchain-name      : \"" + chainName + "\"");
        System.out.println("\tservice-name    : \"" + serviceName + "\"");
        System.out.println("\tuser-name       : \"" + dacsUserName + "\"");
        if(nbOfNamesToGuessForOptimization > 0)
        {
            System.out.println("\toptimization    : enabled");       
        }
        else
        {
            System.out.println("\toptimization    : disabled");    
        }
        if(verboseMode)
        {
            System.out.println("\tnon-verbose     : disabled");       
        }
        else
        {
            System.out.println("\tnon-verbose     : enabled");
        }
        if(jsonOutputMode)
        {
            System.out.println("\tjson-output-mode: enabled");       
        }
        else
        {
            System.out.println("\tjson-output-mode: disabled");    
        }
    }    
}
