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
package com.thomsonreuters.platformservices.ema.utils.chain;

import com.thomsonreuters.ema.access.OmmConsumer;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ChainCompleteFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ChainErrorFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementAddedFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementChangedFunction;
import com.thomsonreuters.platformservices.ema.utils.chain.FlatChain.ElementRemovedFunction;

interface ChainRecordContext
{
    OmmConsumer getOmmConsumer();
            
    String getName();
    
    String getServiceName();
    
    boolean getWithUpdates();
    
    SummaryLinksToSkipByDisplayTemplate getSummaryLinksToSkipByDisplayTemplate();
    
    int getNameGuessesCount();

    ElementAddedFunction getElementAddedFunction();
    
    ElementChangedFunction getElementChangedFunction();
    
    ElementRemovedFunction getElementRemovedFunction();
    
    ChainCompleteFunction getChainCompleteFunction();
    
    ChainErrorFunction getChainErrorFunction();
    
}
